package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response object from Webservice Get_UploadedScanFileInfo
 *
 */
@XmlRootElement(name="get_UploadedScanFileInfo_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_UploadedScanFileInfo_Response extends BaseWebserviceResponse {

	// Properties as XML attributes

	@XmlAttribute
	private boolean scanProcessStatusKey_NotFound;

	@XmlAttribute
	private boolean statusPending;
	@XmlAttribute
	private boolean statusSuccess;
	@XmlAttribute
	private boolean statusFail;
	@XmlAttribute
	private boolean statusDeleted;
	
	@XmlAttribute
	private String scanFileAPIKey;

	public boolean isStatusPending() {
		return statusPending;
	}

	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusSuccess() {
		return statusSuccess;
	}

	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}

	public boolean isStatusFail() {
		return statusFail;
	}

	public void setStatusFail(boolean statusFail) {
		this.statusFail = statusFail;
	}

	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public boolean isScanProcessStatusKey_NotFound() {
		return scanProcessStatusKey_NotFound;
	}

	public void setScanProcessStatusKey_NotFound(boolean scanProcessStatusKey_NotFound) {
		this.scanProcessStatusKey_NotFound = scanProcessStatusKey_NotFound;
	}

	public boolean isStatusDeleted() {
		return statusDeleted;
	}

	public void setStatusDeleted(boolean statusDeleted) {
		this.statusDeleted = statusDeleted;
	}
}
