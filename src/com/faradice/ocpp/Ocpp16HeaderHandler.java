package com.faradice.ocpp;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class Ocpp16HeaderHandler implements SOAPHandler<SOAPMessageContext> {
	final static Logger log = Logger.getLogger(Ocpp16HeaderHandler.class.getName());

	public final String chargePointID;

	public Ocpp16HeaderHandler() {
		this("Server");
	}

	
	public Ocpp16HeaderHandler(String cpi) {
		this.chargePointID = cpi;
	}

	public boolean handleMessage(SOAPMessageContext context) {
		Boolean outboundProperty = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		ByteOutputStream bs = new ByteOutputStream();
		SOAPMessage message = context.getMessage();
		try {
			if (outboundProperty) {
				message.writeTo(bs);
//				String s1 = new String(bs.getBytes());
//DEBUG				System.out.println(s1);

				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				String ns = envelope.getNamespaceURI("ns");
				if (ns == null) {
					envelope.addNamespaceDeclaration("ns", "urn://Ocpp/Cs/2015/10/");
				}

				SOAPHeader header = envelope.getHeader(); // addHeader();
				if (header == null) {
					header = envelope.addHeader();
				}

				// ns:chargeBoxIdentity
				SOAPElement chargeBoxIdentityElement = header.addChildElement("chargeBoxIdentity", "ns");
				chargeBoxIdentityElement.addTextNode(chargePointID);

				message.saveChanges();
				message.writeTo(bs);
				String s = new String(bs.getBytes());
//DEBUG				System.out.println(s);
			} else {
//DEBUG				System.out.println("Inbound");
				message.writeTo(bs);
				String s = new String(bs.getBytes());
      			System.out.println(s);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.severe(ex.getMessage());
		}
		return outboundProperty;
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
