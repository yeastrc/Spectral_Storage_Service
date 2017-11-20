package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice 
 *
 */
@XmlRootElement(name="get_ScansDataFromRetentionTimeRange_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScansDataFromRetentionTimeRange_Request extends BaseWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

	@XmlAttribute // attribute name is property name
	private float retentionTimeStart;
	
	@XmlAttribute // attribute name is property name
	private float retentionTimeEnd;
	
	/**
	 * Only return scans for this scan level
	 */
	@XmlAttribute // attribute name is property name
	private Byte scanLevel;
	
	

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
