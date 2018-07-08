package com.faradice.ocpp;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.AddressingFeature;

import ocpp.cs._2012._06.BootNotificationRequest;
import ocpp.cs._2012._06.BootNotificationResponse;
import ocpp.cs._2012._06.CentralSystemService;
import ocpp.cs._2012._06.RegistrationStatus;

/**
 * A simple tester to access Ocpp15 and send boot notification
 */

public class Ocpp15Tester {
	public final String urn = "urn://Ocpp/Cs/2012/06/";
		
	public final String chargePointId;
	public final String endpoint;
	public final String serviceName;
	public final String port;
	public final CentralSystemService centralService;

	public Ocpp15Tester(String chpId, String ePoint, String sName, String prt) {
		this.chargePointId = chpId;
		this.endpoint = ePoint;
		this.serviceName = sName;
		this.port = prt;
		centralService = initOcpp();
	}

	private CentralSystemService initOcpp() {
		CentralSystemService centralSystemService = null;
		QName serviceQName = new QName(urn, serviceName);
		QName portQName = new QName(urn, port);

		try {
			URL url = new URL(endpoint + "?wsdl");
			Service service = Service.create(url, serviceQName);
			AddressingFeature af = new AddressingFeature();
			centralSystemService = service.getPort(portQName, CentralSystemService.class, af);
		} catch (Exception e) {
			e.printStackTrace();
			centralSystemService = null;
		}
		return centralSystemService;
	}

	public void bootNotification() {
		try {
			BootNotificationResponse bootNotificationResponse = null;
			BootNotificationRequest breq = new BootNotificationRequest();
			breq.setChargeBoxSerialNumber("FD-X1-110042");
			breq.setChargePointVendor("Faradice");
			breq.setChargePointModel("FDP X1 S22");
			breq.setFirmwareVersion("Test Version");
			// Integrated Circuit Card Identifier (19 digit SIM ID)
			breq.setIccid("1234567891234567891");
			// International Mobile Subscription Identity (Mobile Carrier ID)
			breq.setImsi("27402"); // Vodafone
			breq.setMeterType("EVSE 1000Hz");
			breq.setMeterSerialNumber("CA-ME-12022");
			breq.setChargeBoxSerialNumber("FD-X1-110042");
			breq.setChargePointVendor("Faradice");
			breq.setChargePointModel("FDP X1 S22");
			breq.setFirmwareVersion("Faradice OCPP 1.5 Boot notification test for Intersoft");
			breq.setIccid("1234567891234567891");
			breq.setImsi("27402"); // Vodafone
			breq.setMeterType("EVSE 1000Hz");
			breq.setMeterSerialNumber("CA-ME-12022");
			bootNotificationResponse = centralService.bootNotification(breq);
			if (bootNotificationResponse == null) {
				throw new Exception("Result from bootnotifaction on server was NULL.  Check Server Log");
			}
			RegistrationStatus status = bootNotificationResponse.getStatus();
			if (status.equals(RegistrationStatus.ACCEPTED)) {
				System.out.println("Boot notification ACCEPTED!!!");
			} else {
				System.out.println("Boot notification had problems (:");
			}
		} catch (Throwable t) {
			t.printStackTrace();

		}
	}

	public static void main(String[] args) {
		String chargePointId = "Faradice1";
		String endpoint = "http://104.236.81.197:8088/Ocpp15WebAppDemo/CentralSystemService";
		String port = "CentralSystemServiceSoap12";
		String serviceName = "CentralSystemService";

		Ocpp15Tester ocpp15tester = new Ocpp15Tester(chargePointId, endpoint, serviceName, port);
		ocpp15tester.bootNotification();
	}

}
