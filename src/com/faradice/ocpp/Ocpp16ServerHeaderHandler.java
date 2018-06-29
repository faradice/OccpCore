package com.faradice.ocpp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class Ocpp16ServerHeaderHandler implements SOAPHandler<SOAPMessageContext> {
	final static Logger log = Logger.getLogger(Ocpp16ServerHeaderHandler.class.getName());

	public final String chargePointID;

	public Ocpp16ServerHeaderHandler() {
		this("Server");
	}

	
	public Ocpp16ServerHeaderHandler(String cpi) {
		this.chargePointID = cpi;
	}

	public boolean handleMessage(SOAPMessageContext context) {
		SOAPMessage message = context.getMessage();
		
		ByteOutputStream bs = new ByteOutputStream();
		try {
			message.writeTo(bs);
		} catch (SOAPException | IOException e) {
			e.printStackTrace();
		}
		String s = new String(bs.getBytes(), 0, bs.size());
		System.out.println(s);
		return true;
	}


	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	public void close(MessageContext context) {
	}

	public Set<QName> getHeaders() {
		Set<QName> qns = new HashSet<>();
		return qns;
	}

}
