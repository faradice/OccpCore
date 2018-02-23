
package ocpp.cp._2015._10;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * The ChargePoint Service for the Open Charge Point Protocol
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ChargePointService", targetNamespace = "urn://Ocpp/Cp/2015/10/", wsdlLocation = "file:/Users/ragnar/OcppCore/wsdl/OCPP_ChargePointService_1.6.wsdl")
public class ChargePointService_Service
    extends Service
{

    private final static URL CHARGEPOINTSERVICE_WSDL_LOCATION;
    private final static WebServiceException CHARGEPOINTSERVICE_EXCEPTION;
    private final static QName CHARGEPOINTSERVICE_QNAME = new QName("urn://Ocpp/Cp/2015/10/", "ChargePointService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/Users/ragnar/OcppCore/wsdl/OCPP_ChargePointService_1.6.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        CHARGEPOINTSERVICE_WSDL_LOCATION = url;
        CHARGEPOINTSERVICE_EXCEPTION = e;
    }

    public ChargePointService_Service() {
        super(__getWsdlLocation(), CHARGEPOINTSERVICE_QNAME);
    }

    public ChargePointService_Service(WebServiceFeature... features) {
        super(__getWsdlLocation(), CHARGEPOINTSERVICE_QNAME, features);
    }

    public ChargePointService_Service(URL wsdlLocation) {
        super(wsdlLocation, CHARGEPOINTSERVICE_QNAME);
    }

    public ChargePointService_Service(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, CHARGEPOINTSERVICE_QNAME, features);
    }

    public ChargePointService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ChargePointService_Service(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ChargePointService
     */
    @WebEndpoint(name = "ChargePointServiceSoap12")
    public ChargePointService getChargePointServiceSoap12() {
        return super.getPort(new QName("urn://Ocpp/Cp/2015/10/", "ChargePointServiceSoap12"), ChargePointService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ChargePointService
     */
    @WebEndpoint(name = "ChargePointServiceSoap12")
    public ChargePointService getChargePointServiceSoap12(WebServiceFeature... features) {
        return super.getPort(new QName("urn://Ocpp/Cp/2015/10/", "ChargePointServiceSoap12"), ChargePointService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (CHARGEPOINTSERVICE_EXCEPTION!= null) {
            throw CHARGEPOINTSERVICE_EXCEPTION;
        }
        return CHARGEPOINTSERVICE_WSDL_LOCATION;
    }

}
