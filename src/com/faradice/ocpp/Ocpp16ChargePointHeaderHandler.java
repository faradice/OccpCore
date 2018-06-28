package com.faradice.ocpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class Ocpp16ChargePointHeaderHandler implements SOAPHandler<SOAPMessageContext> {
	final static Logger log = Logger.getLogger(Ocpp16ChargePointHeaderHandler.class.getName());

	public Ocpp16ChargePointHeaderHandler() {
	}

	public boolean handleMessage(SOAPMessageContext context) {
		SOAPMessage message = context.getMessage();
		Boolean outbound = (Boolean) context.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			System.out.println("Outbound");
		} else {
			System.out.println("Inbound");
		}
		
		try {
			if (outbound) {
				SOAPHeader sh = message.getSOAPHeader();
				List<Node> nodesToRemove = new ArrayList<>();
				Iterator it = sh.getAllAttributes();
				while (it.hasNext()) {
					Object o = it.next();
					System.out.println(o);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ByteOutputStream bs = new ByteOutputStream();
		int size = -1;
		try {
//			message.setContentDescription("application/soap+xml; charset=utf-8");
			message.writeTo(bs);
//			message.saveChanges();
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
		qns.add(new QName("urn://Ocpp/Cs/2015/10/","chargeBoxIdentity"));
		return qns;
	}

}
