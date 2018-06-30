package com.faradice.ocpp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.CoreDocumentImpl;
import com.sun.org.apache.xerces.internal.dom.TextImpl;
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
		try {
			SOAPMessage message = context.getMessage();
			Boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
			if (isOutbound) {
				SOAPMessage msg = ((SOAPMessageContext) context).getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope env = sp.getEnvelope();
				SOAPHeader soapHeader = env.getHeader();
				soapHeader.getChildElements();
				NodeList net = soapHeader.getElementsByTagName("Action");
				for (int i = 0; i < net.getLength(); i++) {
					Node n = net.item(i);
					if (n.getNodeName().equals("Action")) {
						NodeList childNodes = n.getChildNodes();
						Node cn = null;
						for (int l = 0; l < childNodes.getLength(); l++) {
							cn = childNodes.item(l);
							if (cn instanceof TextImpl) {
								String textNow = cn.getTextContent();
								System.out.println("Now:" + textNow);
								cn.setTextContent("Blablabla");
								textNow = cn.getTextContent();
								System.out.println("after:" + textNow);
							}
						}
						n.getNextSibling();
					}
				}
				message.saveChanges();
				ByteOutputStream bs = new ByteOutputStream();
				try {
					message.writeTo(bs);
				} catch (SOAPException | IOException e) {
					e.printStackTrace();
				}
				String s = new String(bs.getBytes(), 0, bs.size());
				System.out.println(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	public void close(MessageContext context) {
		System.out.println("Close");
	}

	public Set<QName> getHeaders() {
		Set<QName> qns = new HashSet<>();
		return qns;
	}

}
