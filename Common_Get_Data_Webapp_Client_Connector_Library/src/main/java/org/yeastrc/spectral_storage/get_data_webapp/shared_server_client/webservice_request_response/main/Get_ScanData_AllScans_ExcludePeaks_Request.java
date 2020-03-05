package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnIonInjectionTimeData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData;

/**
 * Request object for POST to Webservice GetScanData_AllScans_ExcludePeaks_Servlet
 *
 */
@XmlRootElement(name="get_ScanData_AllScans_ExcludePeaks_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanData_AllScans_ExcludePeaks_Request extends BaseGetDataWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;
	
	/**
	 * Indicates Do populate Ion Injection Time SingleScan_SubResponse
	 *
	 * If null, assumed to be no
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData;
	
	/**
	 * Indicates Do populate Scan Level Total Ion Current in SingleScan_SubResponse
	 * 
	 * If null, assumed to be no
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData;
	
	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public void setIncludeReturnIonInjectionTimeData(
			Get_ScanData_IncludeReturnIonInjectionTimeData includeReturnIonInjectionTimeData) {
		this.includeReturnIonInjectionTimeData = includeReturnIonInjectionTimeData;
	}

	public Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData getIncludeReturnScanLevelTotalIonCurrentData() {
		return includeReturnScanLevelTotalIonCurrentData;
	}

	public void setIncludeReturnScanLevelTotalIonCurrentData(
			Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData includeReturnScanLevelTotalIonCurrentData) {
		this.includeReturnScanLevelTotalIonCurrentData = includeReturnScanLevelTotalIonCurrentData;
	}

	public Get_ScanData_IncludeReturnIonInjectionTimeData getIncludeReturnIonInjectionTimeData() {
		return includeReturnIonInjectionTimeData;
	}


}
