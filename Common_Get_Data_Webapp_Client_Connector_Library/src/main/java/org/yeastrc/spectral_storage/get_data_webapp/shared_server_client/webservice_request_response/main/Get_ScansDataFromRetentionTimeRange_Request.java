package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ExcludeScansWithoutPeaks;

/**
 * Request object for POST to Webservice 
 *
 */
@XmlRootElement(name="get_ScansDataFromRetentionTimeRange_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScansDataFromRetentionTimeRange_Request extends BaseGetDataWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

	/**
	 * Primarily for when filtering the scans
	 */
	@XmlAttribute // attribute name is property name
	private Get_ScanData_ExcludeScansWithoutPeaks excludeScansWithoutPeaks;

	@XmlAttribute // attribute name is property name
	private float retentionTimeStart;
	
	@XmlAttribute // attribute name is property name
	private float retentionTimeEnd;

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	@XmlAttribute // attribute name is property name
	private Double mzLowCutoff;

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	@XmlAttribute // attribute name is property name
	private Double mzHighCutoff;
	
	/**
	 * Only return scans for this scan level
	 */
	@XmlAttribute // attribute name is property name
	private Byte scanLevel;
	


	/**
	 * Primarily for when filtering the scans
	 * @return
	 */
	public Get_ScanData_ExcludeScansWithoutPeaks getExcludeScansWithoutPeaks() {
		return excludeScansWithoutPeaks;
	}

	/**
	 * Primarily for when filtering the scans
	 * @param excludeScansWithoutPeaks
	 */
	public void setExcludeScansWithoutPeaks(Get_ScanData_ExcludeScansWithoutPeaks excludeScansWithoutPeaks) {
		this.excludeScansWithoutPeaks = excludeScansWithoutPeaks;
	}

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

	public float getRetentionTimeStart() {
		return retentionTimeStart;
	}

	public void setRetentionTimeStart(float retentionTimeStart) {
		this.retentionTimeStart = retentionTimeStart;
	}

	public float getRetentionTimeEnd() {
		return retentionTimeEnd;
	}

	public void setRetentionTimeEnd(float retentionTimeEnd) {
		this.retentionTimeEnd = retentionTimeEnd;
	}

	public Byte getScanLevel() {
		return scanLevel;
	}

	public void setScanLevel(Byte scanLevel) {
		this.scanLevel = scanLevel;
	}

}
