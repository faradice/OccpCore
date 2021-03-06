
package ocpp.cp._2012._06;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ChargePointService", targetNamespace = "urn://Ocpp/Cp/2012/06/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ChargePointService {


    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.UnlockConnectorResponse
     */
    @WebMethod(operationName = "UnlockConnector", action = "/UnlockConnector")
    @WebResult(name = "unlockConnectorResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public UnlockConnectorResponse unlockConnector(
        @WebParam(name = "unlockConnectorRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        UnlockConnectorRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.ResetResponse
     */
    @WebMethod(operationName = "Reset", action = "/Reset")
    @WebResult(name = "resetResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public ResetResponse reset(
        @WebParam(name = "resetRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        ResetRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.ChangeAvailabilityResponse
     */
    @WebMethod(operationName = "ChangeAvailability", action = "/ChangeAvailability")
    @WebResult(name = "changeAvailabilityResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public ChangeAvailabilityResponse changeAvailability(
        @WebParam(name = "changeAvailabilityRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        ChangeAvailabilityRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.GetDiagnosticsResponse
     */
    @WebMethod(operationName = "GetDiagnostics", action = "/GetDiagnostics")
    @WebResult(name = "getDiagnosticsResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public GetDiagnosticsResponse getDiagnostics(
        @WebParam(name = "getDiagnosticsRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        GetDiagnosticsRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.ClearCacheResponse
     */
    @WebMethod(operationName = "ClearCache", action = "/ClearCache")
    @WebResult(name = "clearCacheResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public ClearCacheResponse clearCache(
        @WebParam(name = "clearCacheRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        ClearCacheRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.UpdateFirmwareResponse
     */
    @WebMethod(operationName = "UpdateFirmware", action = "/UpdateFirmware")
    @WebResult(name = "updateFirmwareResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public UpdateFirmwareResponse updateFirmware(
        @WebParam(name = "updateFirmwareRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        UpdateFirmwareRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.ChangeConfigurationResponse
     */
    @WebMethod(operationName = "ChangeConfiguration", action = "/ChangeConfiguration")
    @WebResult(name = "changeConfigurationResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public ChangeConfigurationResponse changeConfiguration(
        @WebParam(name = "changeConfigurationRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        ChangeConfigurationRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.RemoteStartTransactionResponse
     */
    @WebMethod(operationName = "RemoteStartTransaction", action = "/RemoteStartTransaction")
    @WebResult(name = "remoteStartTransactionResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public RemoteStartTransactionResponse remoteStartTransaction(
        @WebParam(name = "remoteStartTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        RemoteStartTransactionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.RemoteStopTransactionResponse
     */
    @WebMethod(operationName = "RemoteStopTransaction", action = "/RemoteStopTransaction")
    @WebResult(name = "remoteStopTransactionResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public RemoteStopTransactionResponse remoteStopTransaction(
        @WebParam(name = "remoteStopTransactionRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        RemoteStopTransactionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.CancelReservationResponse
     */
    @WebMethod(operationName = "CancelReservation", action = "/CancelReservation")
    @WebResult(name = "cancelReservationResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public CancelReservationResponse cancelReservation(
        @WebParam(name = "cancelReservationRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        CancelReservationRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.DataTransferResponse
     */
    @WebMethod(operationName = "DataTransfer", action = "/DataTransfer")
    @WebResult(name = "dataTransferResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public DataTransferResponse dataTransfer(
        @WebParam(name = "dataTransferRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        DataTransferRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.GetConfigurationResponse
     */
    @WebMethod(operationName = "GetConfiguration", action = "/GetConfiguration")
    @WebResult(name = "getConfigurationResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public GetConfigurationResponse getConfiguration(
        @WebParam(name = "getConfigurationRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        GetConfigurationRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.GetLocalListVersionResponse
     */
    @WebMethod(operationName = "GetLocalListVersion", action = "/GetLocalListVersion")
    @WebResult(name = "getLocalListVersionResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public GetLocalListVersionResponse getLocalListVersion(
        @WebParam(name = "getLocalListVersionRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        GetLocalListVersionRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.ReserveNowResponse
     */
    @WebMethod(operationName = "ReserveNow", action = "/ReserveNow")
    @WebResult(name = "reserveNowResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public ReserveNowResponse reserveNow(
        @WebParam(name = "reserveNowRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        ReserveNowRequest parameters);

    /**
     * 
     * @param parameters
     * @return
     *     returns ocpp.cp._2012._06.SendLocalListResponse
     */
    @WebMethod(operationName = "SendLocalList", action = "/SendLocalList")
    @WebResult(name = "sendLocalListResponse", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
    public SendLocalListResponse sendLocalList(
        @WebParam(name = "sendLocalListRequest", targetNamespace = "urn://Ocpp/Cp/2012/06/", partName = "parameters")
        SendLocalListRequest parameters);

}
