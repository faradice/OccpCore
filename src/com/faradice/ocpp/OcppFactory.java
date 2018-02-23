package com.faradice.ocpp;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.AddressingFeature;

import ocpp.cs._2015._10.CentralSystemService;

public class OcppFactory {
	private static CentralSystemService centalSystemService;

	public static CentralSystemService centralService(String endpoint, String urn, String name) {
		if (centalSystemService != null) {
			return centalSystemService;
		}
		
		try {
			URL url = new URL(endpoint+"?wsdl");
			QName qName = new QName(urn, name);
			Service service = Service.create(url, qName);
			AddressingFeature af = new AddressingFeature();
			centalSystemService = service.getPort(CentralSystemService.class, af);
			addHandler((BindingProvider) centalSystemService);
			return centalSystemService;
		} catch (Exception e) {
			e.printStackTrace();
			centalSystemService = null;
			return null;
		}
	}
	
	public static CentralSystemService centralService16(String endpoint) {
		return centralService(endpoint, "urn://Ocpp/Cs/2015/10/", "CentralSystemService");
	}
	
	public static CentralSystemService centralService15(String endpoint) {
		return centralService(endpoint, "urn://Ocpp/Cs/2012/06/", "CentralSystemService");
	}

	public static CentralSystemService digoCentralService15() {
		String endpoint = "http://104.236.81.197:8088/Ocpp15WebAppDemo/CentralSystemService";
		return centralService(endpoint, "urn://Ocpp/Cs/2012/06/", "CentralSystemService");
	}
	
	public static CentralSystemService digoCentralService16() {
		String endpoint = "http://104.236.81.197:8088/cs_ocpp16/CentralSystemService";
		return centralService(endpoint, "urn://Ocpp/Cs/2015/10/", "CentralSystemService");
	}
	
	public static CentralSystemService localCentralService15() {
		String endpoint = "http://localhost:8085/Fara_occp/CentralSystemService";
		return centralService(endpoint, "urn://Ocpp/Cs/2012/06/", "CentralSystemService");
	}

	public static CentralSystemService directCentralService() {
		String endpoint = "http://localhost:8085/Fara_occp/CentralSystemService";
		return centralService(endpoint, "urn://Ocpp/Cs/2015/10/", "CentralSystemService");
	}
		
	public static CentralSystemService faraCentralService() {
		String endpoint =  "http://localhost:8079/FaraCentralSystem";
		return centralService(endpoint, "http://centralsystem.ocpp.faradice.com/", "CentralSystemService");
	}
		
	public static CentralSystemService localDemo15CentralService() {
		String endpoint =  "http://localhost:8080/Ocpp15WebAppDemo/CentralSystemService";
		return centralService(endpoint, "urn://Ocpp/Cs/2012/06/", "CentralSystemService");
	}
	
	public static void addHandler(BindingProvider bindingProvider) {
		OcppHeaderHandler handler = new OcppHeaderHandler("Faradice1");
		List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
		handlerChain.add(handler);
		bindingProvider.getBinding().setHandlerChain(handlerChain);
		System.out.println("Handler added: "+handler.getClass().getName());
	}
	
}
