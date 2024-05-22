package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanDataFromScanNumbers_IncludeParentScans;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ExcludeReturnScanPeakData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnIonInjectionTimeData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_IncludeReturnScanLevelTotalIonCurrentData;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest;

/**
 * Request object for POST to Webservice GetScanDataFromScanNumbers_Servlet
 *
 */
@XmlRootElement(name="get_ScanDataFromScanNumbers_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanDataFromScanNumbers_Request extends BaseGetDataWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;
	
	/**
	 * Also return the parent scan(s) for each requested scan number
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanDataFromScanNumbers_IncludeParentScans includeParentScans;
	
	/**
	 * Indicates do not populate peaks list in SingleScan_SubResponse
	 *
	 * If null, assumed to be no
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanData_ExcludeReturnScanPeakData excludeReturnScanPeakData;

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
	
	/**
	 * Main Query element, list of scan numbers
	 */
	@XmlElementWrapper(name="scanNumbers")
	@XmlElement(name="scanNumber")
	private List<Integer> scanNumbers;

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 * 
	 * This cutoff is applied in Addition To the filters in property 'm_Over_Z_Range_Filters' 
	 */
	@XmlAttribute // attribute name is property name
	private Double mzLowCutoff;

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 * 
	 * This cutoff is applied in Addition To the filters in property 'm_Over_Z_Range_Filters'
	 */
	@XmlAttribute // attribute name is property name
	private Double mzHighCutoff;


	/**
	 * Main Query element, list of m/z filter cutoffs.  Each m/z range is OR with each other
	 */
	@XmlElementWrapper(name="m_Over_Z_Range_Filters")
	@XmlElement(name="m_Over_Z_Range_Filter")
	private List<Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest> m_Over_Z_Range_Filters;

	/**
	 * If populated and true, populate peak_WMxInty in class SingleScan_SubResponse
	 */
	@XmlAttribute // attribute name is property name
	private Boolean returnScanPeakWithMaxIntensityIgnoringSanPeakFilters;


	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	public Double getMzLowCutoff() {
		return mzLowCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	public void setMzLowCutoff(Double mzLowCutoff) {
		this.mzLowCutoff = mzLowCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	public Double getMzHighCutoff() {
		return mzHighCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	public void setMzHighCutoff(Double mzHighCutoff) {
		this.mzHighCutoff = mzHighCutoff;
	}


	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public List<Integer> getScanNumbers() {
		return scanNumbers;
	}

	public void setScanNumbers(List<Integer> scanNumbers) {
		this.scanNumbers = scanNumbers;
	}

	public Get_ScanDataFromScanNumbers_IncludeParentScans getIncludeParentScans() {
		return includeParentScans;
	}

	public void setIncludeParentScans(Get_ScanDataFromScanNumbers_IncludeParentScans includeParentScans) {
		this.includeParentScans = includeParentScans;
	}

	public Get_ScanData_ExcludeReturnScanPeakData getExcludeReturnScanPeakData() {
		return excludeReturnScanPeakData;
	}

	public void setExcludeReturnScanPeakData(Get_ScanData_ExcludeReturnScanPeakData excludeReturnScanPeakData) {
		this.excludeReturnScanPeakData = excludeReturnScanPeakData;
	}

	public Get_ScanData_IncludeReturnIonInjectionTimeData getIncludeReturnIonInjectionTimeData() {
		return includeReturnIonInjectionTimeData;
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

	public List<Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest> getM_Over_Z_Range_Filters() {
		return m_Over_Z_Range_Filters;
	}

	public void setM_Over_Z_Range_Filters(
			List<Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest> m_Over_Z_Range_Filters) {
		this.m_Over_Z_Range_Filters = m_Over_Z_Range_Filters;
	}

	public Boolean getReturnScanPeakWithMaxIntensityIgnoringSanPeakFilters() {
		return returnScanPeakWithMaxIntensityIgnoringSanPeakFilters;
	}

	public void setReturnScanPeakWithMaxIntensityIgnoringSanPeakFilters(
			Boolean returnScanPeakWithMaxIntensityIgnoringSanPeakFilters) {
		this.returnScanPeakWithMaxIntensityIgnoringSanPeakFilters = returnScanPeakWithMaxIntensityIgnoringSanPeakFilters;
	}


}
