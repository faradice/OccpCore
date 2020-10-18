package com.faradice.ocpp;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.faradice.faraUtil.FaraConfig;
import com.faradice.faraUtil.FaraDates;
import com.faradice.faraUtil.FaraFiles;
import com.faradice.faraUtil.FaraUtil;
import com.faradice.faraUtil.Log;
import com.faradice.faranet.FaraHttp;
import com.faradice.ocpp.Ocpp16Factory;

import ocpp.cs._2015._10.AuthorizationStatus;
import ocpp.cs._2015._10.AuthorizeRequest;
import ocpp.cs._2015._10.AuthorizeResponse;
import ocpp.cs._2015._10.BootNotificationRequest;
import ocpp.cs._2015._10.BootNotificationResponse;
import ocpp.cs._2015._10.CentralSystemService;
import ocpp.cs._2015._10.ChargePointErrorCode;
import ocpp.cs._2015._10.ChargePointStatus;
import ocpp.cs._2015._10.HeartbeatRequest;
import ocpp.cs._2015._10.HeartbeatResponse;
import ocpp.cs._2015._10.IdTagInfo;
import ocpp.cs._2015._10.Location;
import ocpp.cs._2015._10.Measurand;
import ocpp.cs._2015._10.MeterValue;
import ocpp.cs._2015._10.MeterValuesRequest;
import ocpp.cs._2015._10.Phase;
import ocpp.cs._2015._10.ReadingContext;
import ocpp.cs._2015._10.Reason;
import ocpp.cs._2015._10.RegistrationStatus;
import ocpp.cs._2015._10.SampledValue;
import ocpp.cs._2015._10.StartTransactionRequest;
import ocpp.cs._2015._10.StartTransactionResponse;
import ocpp.cs._2015._10.StatusNotificationRequest;
import ocpp.cs._2015._10.StopTransactionRequest;
import ocpp.cs._2015._10.StopTransactionResponse;
import ocpp.cs._2015._10.UnitOfMeasure;
import ocpp.cs._2015._10.ValueFormat;

public class OcppProvider {
	public final static String OPEN_CHARGER_RFID = "OpenCharger";
	public final static  long MIN_HEARTBEAT = 1000 * 30;

	public final String chargePointId;
	public final String endpoint;
	public final String serviceName;
	public final String port;

	private long heartbeatInterval = 1000 * 60 * 1;
	private long bootInterval = 1000 * 60 * 1;
	private long meterValueInterval = 1000 * 60 * 3;

	private long lastHeartbeat = -1;
	private long lastMetervalue = -1;
	private long bootRequestTime = -1;

	private boolean provideAccessWhenBootFailed = true;
	private boolean enabled = true;

	private String currentRFID = null;
	private CentralSystemService centralService = null;
	private BootNotificationResponse bootNotificationResponse = null;
	private AuthorizeResponse authorizeResponse = null;
	private IdTagInfo authorizeTag = null;
	private IdTagInfo stopTransactionTag = null;
	private StartTransactionResponse startTransactionResponse = null;
	private String firmwareVersion = "Unknown";

	public OcppProvider() {
		this(FaraHttp.getMacAddress() + ":" + FaraHttp.hostName(), FaraConfig.stringFromConfig(FaraConfig.END_POINT, null), FaraConfig.stringFromConfig(FaraConfig.SERVICE_NAME,null), FaraConfig.stringFromConfig(FaraConfig.PORT,null), "Unknown");
	}

	public OcppProvider(String chargePointId, String endpoint, String serviceName, String port, String firmwareVersion) {
		this.chargePointId = chargePointId;
		this.endpoint = endpoint;
		this.serviceName = serviceName;
		this.port = port;
		this.firmwareVersion = firmwareVersion;
		if (chargePointId == null || endpoint == null || serviceName == null || port == null) {
			enabled = false;
		}
	}

	public static OcppProvider buildOnTest() {
		String endpoint = "http://oncore-test.on.is/ocpp-server/OcppServices/Ocpp16/Ocpp16Service.svc";
		String serviceName = "Ocpp16Service";
		String port = "CentralSystemServiceSoap12";
		String chargePointId = FaraHttp.getMacAddress() + ":" + FaraHttp.hostName();
		return new OcppProvider(chargePointId, endpoint, serviceName, port, "Test Firmware");
	}

	public void setEnabled(boolean enabled) {
		if (chargePointId != null) {
			this.enabled = enabled;
		} else {
			this.enabled = false;
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void bootup() {
		if (!enabled) {
			return;
		}

		if (booted()) {
			return;
		}

		long now = System.currentTimeMillis();
		if (bootRequestTime > now - bootInterval) {
			return;
		}

		bootRequestTime = System.currentTimeMillis();

		try {
			Log.info("Boot Request");
			centralService = Ocpp16Factory.init(chargePointId, endpoint, serviceName, port);
			if (centralService == null) {
				return;
			}
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Bootup");
			bootNotificationResponse = null;
			BootNotificationRequest breq = new BootNotificationRequest();
			breq.setChargeBoxSerialNumber("FD-X1-110042");
			breq.setChargePointVendor("Faradice");
			breq.setChargePointModel("FDP X1 S22");
			breq.setFirmwareVersion(firmwareVersion);
			// Integrated Circuit Card Identifier (19 digit SIM ID)
			breq.setIccid("1234567891234567891");
			// International Mobile Subscription Identity (Mobile Carrier ID)
			breq.setImsi("27402"); // Vodafone
			breq.setMeterType("EVSE 1000Hz");
			breq.setMeterSerialNumber("CA-ME-12022");
			bootNotificationResponse = centralService.bootNotification(breq);
			if (bootNotificationResponse == null) {
				throw new Exception("Result from bootnotifaction on server was NULL.  Check Server Log");
			}
			XMLGregorianCalendar time = bootNotificationResponse.getCurrentTime();
			updateSystemTime(time);
			bootRequestTime = System.currentTimeMillis();
			RegistrationStatus status = bootNotificationResponse.getStatus();
			bootInterval = bootNotificationResponse.getInterval();
			if (status.equals(RegistrationStatus.ACCEPTED)) {
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Bootup accepted");
				heartbeatInterval = Math.max(bootNotificationResponse.getInterval(), MIN_HEARTBEAT);
			} else {
				close();
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP not accepted");
				heartbeatInterval = bootNotificationResponse.getInterval();
			}
		} catch (Throwable t) {
			Log.error(t.getMessage(), t);
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Bootup error: " + t.getMessage());
			close();
		}
	}

	public boolean authenticate(String rfid) {
		if (!enabled) {
			return true;
		}

		bootup();
		boolean authenticated = false;
		authorizeTag = null;
		authorizeResponse = null;
		currentRFID = rfid;
		try {
			Log.info("authenticate " + rfid);
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Authenticating " + rfid);

			if (!booted()) {
				return provideAccessWhenBootFailed;
			}

			AuthorizeRequest aur = new AuthorizeRequest();
			aur.setIdTag(rfid);
			authorizeResponse = centralService.authorize(aur);
			if (authorizeResponse == null) {
				throw new Exception("Result from authenticate on server was NULL.  Check Server Log");
			}
			authorizeTag = authorizeResponse.getIdTagInfo();
			Log.info("Authentication from server: " + authorizeTag.getStatus().value());
			if (authorizeTag.getStatus().equals(AuthorizationStatus.ACCEPTED)) {
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Authenticate accepted for " + rfid);
				authenticated = true;
			} else {
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Authenticate NOT accepted for " + rfid);
				authenticated = false;
			}
		} catch (Throwable t) {
			Log.error(t);
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Authenticate error: " + t.getMessage());
			authenticated = provideAccessWhenBootFailed;
		}
		Log.debug("Authenticate result for " + rfid + " " + authenticated);
		return authenticated;
	}

	public boolean startTransaction(String rfid, int connectorId) {

		startTransactionResponse = null;
		if (!enabled) {
			return true;
		}

		bootup();
		if (rfid == null || rfid.equalsIgnoreCase(OPEN_CHARGER_RFID)) {
			this.currentRFID = rfid;
		}
		boolean accepted = false;
		try {
			Log.info("Start transaction for " + rfid);
			if (!booted()) {
				return false;
			}

			long now = System.currentTimeMillis();
			if (currentRFID == null) {
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Start Transaction failed. RFID not set when authenticating");
				Log.error(rfid + " was not authenticated.");
			}

			if (currentRFID == null || !currentRFID.equals(rfid)) {
				Log.error("Trying to start transaction for " + rfid + " but authenticated tag is " + currentRFID);
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "Trying to start transaction for " + rfid + " but authenticated tag is " + currentRFID);
			}

			if (authorizeTag == null && !(rfid.equalsIgnoreCase(OPEN_CHARGER_RFID))) {
				Log.error("Trying to start transaction for " + rfid + " but id Tag from authentication is not set. Check if authenticate has been called");
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "Trying to start transaction for " + rfid + " but id Tag from authentication is not set. Check if authenticate has been called");
			}

			if (authorizeTag != null && now > authorizeTag.getExpiryDate().toGregorianCalendar().getTimeInMillis()) {
				Log.error("The Authentication expired " + FaraDates.getDateTime(authorizeTag.getExpiryDate().toGregorianCalendar().getTimeInMillis()));
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "The Authentication expired " + FaraDates.getDateTime(authorizeTag.getExpiryDate().toGregorianCalendar().getTimeInMillis()));
			}
			StartTransactionRequest streq = new StartTransactionRequest();
			streq.setConnectorId(connectorId);
			streq.setIdTag(rfid);
			streq.setMeterStart(0);
			streq.setTimestamp(FaraDates.getXMLDateTime());
			startTransactionResponse = centralService.startTransaction(streq);
			if (startTransactionResponse == null) {
				throw new Exception("Result from startTransaction on server was NULL.  Check Server Log");
			}
			updateSystemTime(streq.getTimestamp());
			accepted = true;
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCCPP Start Transaction accepted for RFID "+currentRFID);
		} catch (Throwable t) {
			t.printStackTrace();
			Log.error(t);
			accepted = false;
		}
		return accepted;
	}

	public void stopTransaction(String rfid, long whInSession, String log) {
		if (!enabled) {
			return;
		}

		bootup();
		if (OPEN_CHARGER_RFID.equalsIgnoreCase(currentRFID)) {
			rfid = OPEN_CHARGER_RFID;
		}

		try {
			stopTransactionTag = null;
			Log.info("Stop transaction for " + rfid);
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Stop transaction for " + rfid);

			if (!booted()) {
				return;
			}

			if (rfid == null || !rfid.equals(currentRFID)) {
				Log.error("Invalid rfid when stopping transaction for " + rfid + " current id is " + currentRFID);
				FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Invalid rfid when stopping transaction for " + rfid + " current id is " + currentRFID);
			}

			StopTransactionRequest streq = new StopTransactionRequest();
			updateSystemTime(streq.getTimestamp());
			streq.setTimestamp(FaraDates.getXMLDateTime());
			if (startTransactionResponse != null) {
				streq.setTransactionId(startTransactionResponse.getTransactionId());
			} else {
				streq.setTransactionId(-1);
			}
			streq.setIdTag(rfid);
			int meterStop = (int) whInSession;
			streq.setMeterStop(meterStop);

			// TODO Get reason from log line
			Reason ocppReason = Reason.EV_DISCONNECTED;
			streq.setReason(ocppReason);
			streq.setIdTag(rfid);
			streq.setTransactionId(startTransactionResponse.getTransactionId());
			streq.getTransactionData().addAll(createMeterValues(log));
			StopTransactionResponse stopTransactionResponse = centralService.stopTransaction(streq);
			if (stopTransactionResponse == null) {
				throw new Exception("Result from stopTransaction on server was NULL.  Check Server Log");
			}
			stopTransactionTag = stopTransactionResponse.getIdTagInfo();
			AuthorizationStatus status = stopTransactionTag.getStatus();
			Log.debug("Stop Transaction status " + status);
			startTransactionResponse = null;
		} catch (Throwable t) {
			Log.error(t);
			FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Error wehn stopping chargaction for " + rfid + " "+t.getMessage());
		}
		currentRFID = null;
	}

	public void heartbeat() {
		if (!enabled) {
			return;
		}

		bootup();
		try {
			if (!booted()) {
				return;
			}

			long now = System.currentTimeMillis();
			if (lastHeartbeat > now - heartbeatInterval) {
				return;
			}
			lastHeartbeat = System.currentTimeMillis();
			lastHeartbeat = now;
			HeartbeatRequest hbreq = new HeartbeatRequest();
			HeartbeatResponse hres = centralService.heartbeat(hbreq);
			updateSystemTime(hres.getCurrentTime());
			Log.debug(FaraDates.getDateTime(hres.getCurrentTime().toGregorianCalendar().getTimeInMillis()));
		} catch (Throwable t) {
			Log.error(t);
		}
	}

	public void meterValues(int connectorId, String log, boolean forceSend) {
		if (!enabled) {
			return;
		}
		bootup();
		if (startTransactionResponse == null) {
			return;
		}
		if (!booted()) {
			return;
		}

		long now = System.currentTimeMillis();
		if (!forceSend || (lastMetervalue > now - meterValueInterval)) {
			return;
		}
		lastMetervalue = now;
		MeterValuesRequest mvreq = new MeterValuesRequest();
		mvreq.setConnectorId(connectorId);
		mvreq.setTransactionId(startTransactionResponse.getTransactionId());
		mvreq.getMeterValue().addAll(createMeterValues(log));
		centralService.meterValues(mvreq);
		String valStr = "";
		for (SampledValue sm : mvreq.getMeterValue().get(0).getSampledValue()) {
			valStr+=sm.getMeasurand().name()+"="+ sm.getMeasurand().value();
		}
		FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP Meter Value: " + valStr);;
	}

	public void meterValue(int connectorId, String eventLine) {
		if (!enabled) {
			return;
		}
		bootup();

		if (!booted()) {
			return;
		}

		String logRFID = (currentRFID == null) ? "None" : currentRFID;
		
		String info = FaraHttp.hostName() + ", " + eventLine;
		StatusNotificationRequest snreq = new StatusNotificationRequest();
		snreq.setConnectorId(connectorId);
		snreq.setInfo(info);
		snreq.setTimestamp(FaraDates.getXMLDateTime());
		snreq.setVendorId("Faradice");
		snreq.setVendorErrorCode("");
		snreq.setErrorCode(cpErrorCode(eventLine));
		ChargePointStatus cps = cpStatus(eventLine);
		snreq.setStatus(cps);
		FaraFiles.appendRowToCSVFile(FaraFiles.LOG_FILE, "OCPP MeterValue for " + logRFID + " "+cps.name());
		centralService.statusNotification(snreq);
	}

	private ChargePointErrorCode cpErrorCode(String logRow) {
		if (cpStatus(logRow).equals(ChargePointStatus.FAULTED)) {
			return ChargePointErrorCode.OTHER_ERROR;
		} else {
			return ChargePointErrorCode.NO_ERROR;
		}
	}

	ChargePointStatus cpStatus(String logRow) {
		String rowlc = logRow.toLowerCase();
		if (rowlc.contains("fully")) {
			return ChargePointStatus.FINISHING;
		} else if (rowlc.contains("uncon")) {
			return ChargePointStatus.AVAILABLE;
		} else if (rowlc.contains("charging")) {
			return ChargePointStatus.CHARGING;
		} else if (rowlc.contains("auth")) {
			return ChargePointStatus.PREPARING;
		} else if (rowlc.contains("err")) {
			return ChargePointStatus.FAULTED;
		} else {
			return ChargePointStatus.AVAILABLE;
		}
	}

	private void updateSystemTime(XMLGregorianCalendar cal) {
		if (cal == null) {
			return;
		}
		long nowmillis = System.currentTimeMillis();
		long calmillis = cal.toGregorianCalendar().getTimeInMillis();
		if (Math.abs(calmillis - nowmillis) > (20 * 1000)) {
			FaraUtil.setSystemTime(calmillis);
		}
	}

	private List<MeterValue> createMeterValues(String log) {
		ArrayList<MeterValue> meterValues = new ArrayList<>();
		MeterValue mv = meterValueOfLog(log);
		if (mv != null) {
			meterValues.add(mv);
		}
		return meterValues;
	}

	private MeterValue meterValueOfLog(String line) {
		String[] cols = line.split(",");
		MeterValue mv = new MeterValue();
		// todo create values from log
		XMLGregorianCalendar tsFromLog = FaraDates.getXMLDateTime();
		mv.setTimestamp(tsFromLog);
		String whFromLog = "Unkown";
		String tempFromLog = "Unknown";
		String iMaxaFromLog = "Unknown";

		// Get Temp
		String tmp = valueOf(cols, "temp");
		if (tmp != null) {
			int tBPos = tmp.indexOf(":", 2);
			if (tBPos > 1) {
				tempFromLog = tmp.substring(tBPos + 2).trim();
			}
		}

		// Get wh
		String whStr = valueOf(cols, "wh");
		if (whStr != null) {
			whFromLog = whStr.substring(0, whStr.indexOf("w"));
			System.out.println(whFromLog);
		}

		// Get iMax
		String imStr = valueOf(cols, "imax");
		if (imStr != null) {
			iMaxaFromLog = imStr.substring(imStr.indexOf(":") + 1);
			System.out.println(iMaxaFromLog);
		}

		// Get amp now

		// wh
		SampledValue wh = new SampledValue();
		wh.setPhase(Phase.L_1);
		wh.setContext(ReadingContext.SAMPLE_PERIODIC);
		wh.setFormat(ValueFormat.RAW);
		wh.setLocation(Location.EV);
		wh.setMeasurand(Measurand.CURRENT_EXPORT);
		wh.setUnit(UnitOfMeasure.WH);
		wh.setValue(whFromLog);
		mv.getSampledValue().add(wh);

		// temp
		SampledValue temp = new SampledValue();
		temp.setContext(ReadingContext.SAMPLE_PERIODIC);
		temp.setFormat(ValueFormat.RAW);
		temp.setLocation(Location.EV);
		temp.setMeasurand(Measurand.TEMPERATURE);
		temp.setUnit(UnitOfMeasure.CELSIUS);
		temp.setValue(tempFromLog);
		mv.getSampledValue().add(temp);

		// MAX Amp
		SampledValue maxa = new SampledValue();
		maxa.setContext(ReadingContext.SAMPLE_PERIODIC);
		maxa.setFormat(ValueFormat.RAW);
		maxa.setLocation(Location.EV);
		maxa.setMeasurand(Measurand.CURRENT_OFFERED);
		maxa.setUnit(UnitOfMeasure.A);
		maxa.setValue(iMaxaFromLog);
		mv.getSampledValue().add(maxa);
		return mv;
	}

	private String valueOf(String[] cols, String value) {
		for (String col : cols) {
			if (col.toLowerCase().contains(value.toLowerCase())) {
				return col;
			}
		}
		return null;
	}

	private boolean booted() {
		return (centralService != null);
	}

	private void close() {
		Log.info("Closing OCPP connection");
		bootNotificationResponse = null;
		centralService = null;
		currentRFID = null;
		bootNotificationResponse = null;
		authorizeResponse = null;
		authorizeTag = null;
		stopTransactionTag = null;
		startTransactionResponse = null;
	}

}
