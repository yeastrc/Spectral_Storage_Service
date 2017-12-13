package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.enums.Get_ScanDataFromScanNumbers_IncludeParentScans;

/**
 * Request object for POST to Webservice GetScanDataFromScanNumbers_Servlet
 *
 */
@XmlRootElement(name="get_ScanDataFromScanNumbers_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanDataFromScanNumbers_Request extends BaseWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;
	
	/**
	 * Also return the parent scan(s) for each requested scan number
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanDataFromScanNumbers_IncludeParentScans includeParentScans;
	
	
	/**
	 * Main Query element, list of scan numbers
	 */
	@XmlElementWrapper(name="scanNumbers")
	@XmlElement(name="scanNumber")
	private List<Integer> scanNumbers;

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	@XmlAttribute // attribute name is property name
	private Float mzLowCutoff;

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	@XmlAttribute // attribute name is property name
	private Float mzHighCutoff;


	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	public Float getMzLowCutoff() {
		return mzLowCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	public void setMzLowCutoff(Float mzLowCutoff) {
		this.mzLowCutoff = mzLowCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	public Float getMzHighCutoff() {
		return mzHighCutoff;
	}

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	public void setMzHighCutoff(Float mzHighCutoff) {
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


}
