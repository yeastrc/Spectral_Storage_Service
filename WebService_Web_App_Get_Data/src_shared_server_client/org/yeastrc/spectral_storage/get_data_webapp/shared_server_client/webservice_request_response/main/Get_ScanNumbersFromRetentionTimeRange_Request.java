package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice Get_UploadedScanFileInfo
 *
 */
@XmlRootElement(name="get_ScanNumbersFromRetentionTimeRange_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanNumbersFromRetentionTimeRange_Request extends BaseGetDataWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

	@XmlAttribute // attribute name is property name
	private Float retentionTimeStart;
	
	@XmlAttribute // attribute name is property name
	private Float retentionTimeEnd;

	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public Float getRetentionTimeStart() {
		return retentionTimeStart;
	}

	public void setRetentionTimeStart(Float retentionTimeStart) {
		this.retentionTimeStart = retentionTimeStart;
	}

	public Float getRetentionTimeEnd() {
		return retentionTimeEnd;
	}

	public void setRetentionTimeEnd(Float retentionTimeEnd) {
		this.retentionTimeEnd = retentionTimeEnd;
	}


}
