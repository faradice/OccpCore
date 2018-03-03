package com.faradice.ocpp;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

import ocpp.cs._2015._10.CentralSystemService;

public class Ocpp16Factory {
	private static CentralSystemService centalSystemService;

	public static CentralSystemService centralService(String chargePointId, String endpoint, String urn, String serviceName, String portName) {
		if (centalSystemService != null) {
			return centalSystemService;
		}
	
		QName serviceQName = new QName(urn, serviceName);
		QName portQName = new QName(urn, portName);

		try {
			URL url = new URL(endpoint+"?wsdl");
			Service service = Service.create(url, serviceQName);
			AddressingFeature af = new AddressingFeature();
			centalSystemService = service.getPort(portQName, CentralSystemService.class, af);
			addHandler(chargePointId, (BindingProvider) centalSystemService);
			return centalSystemService;
		} catch (Exception e) {
			e.printStackTrace();
			centalSystemService = null;
			return null;
		}
	}
	
	public static CentralSystemService centralService(String chargePointId, String endpoint) {
		return centralService(chargePointId, endpoint, "urn://Ocpp/Cs/2015/10/", "CentralSystemService",  "CentralSystemService");
	}
		
	public static CentralSystemService loalChargePointCentralMockup(String chargePointId) {
		// see end of WSDL: soap:address location="http://localhost:8079/FaraCentralSystem"
		String endpoint = "http://localhost:8079/FaraCentralSystem";
		
		// see WSDL: targetNamespace="http://centralsystem.ocpp.faradice.com/"
		String urn = "http://centralsystem.ocpp.faradice.com/";
		
		// See WSDL: name="CentralSystemService"
		String serviceName = "CentralSystemService";

		// See port list when connected to server or 
		// in end of WSDL: port name="CentralSystemPort"
		String portName = "CentralSystemPort";
		return centralService(chargePointId, endpoint, urn, serviceName, portName);
	}	

	public static CentralSystemService digoCentralService16(String chargePointId) {
		String endpoint = "http://104.236.81.197:8088/cs_ocpp16/CentralSystemService";
		String port = "CentralSystemServiceSoap12";
		String serviceName = "CentralSystemService";
		return centralService(chargePointId, endpoint, "urn://Ocpp/Cs/2015/10/", serviceName, port);
	}
	
	public static CentralSystemService directCentralService() {
		String endpoint = "http://localhost:8085/Fara_occp/CentralSystemService";
		return centralService("Faradice", endpoint);
	}
		
	public static CentralSystemService faraCentralService() {
		String endpoint =  "http://localhost:8079/FaraCentralSystem";
		return centralService("Faradice", endpoint, "http://centralsystem.ocpp.faradice.com/", "CentralSystemService", "CentralSystemService");
	}
		
	public static void addHandler(String chargPointId, BindingProvider bindingProvider) {
		Ocpp16HeaderHandler handler = new Ocpp16HeaderHandler(chargPointId);
		List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
		handlerChain.add(handler);
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		System.out.println("Handler added: "+handler.getClass().getName());
	}
		
}
