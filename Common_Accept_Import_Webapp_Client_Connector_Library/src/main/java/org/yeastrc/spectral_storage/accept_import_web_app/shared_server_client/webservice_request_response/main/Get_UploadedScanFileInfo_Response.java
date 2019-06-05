package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImport_ProcessStatusEnum;

/**
 * Response object from Webservice Get_UploadedScanFileInfo
 *
 */
@XmlRootElement(name="get_UploadedScanFileInfo_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_UploadedScanFileInfo_Response extends BaseAcceptImportWebserviceResponse {

	// Properties as XML attributes

	@XmlAttribute
	private boolean scanProcessStatusKey_NotFound;

	@XmlAttribute
	private WebserviceSpectralStorageAcceptImport_ProcessStatusEnum status;
	
	/**
	 * Populated if status success
	 */
	@XmlAttribute
	private String scanFileAPIKey;

	/**
	 * Populated if status fail with Data Error
	 */
	@XmlAttribute
	private String dataErrorFailMessage;

	public boolean isScanProcessStatusKey_NotFound() {
		return scanProcessStatusKey_NotFound;
	}

	public void setScanProcessStatusKey_NotFound(boolean scanProcessStatusKey_NotFound) {
		this.scanProcessStatusKey_NotFound = scanProcessStatusKey_NotFound;
	}

	public WebserviceSpectralStorageAcceptImport_ProcessStatusEnum getStatus() {
		return status;
	}

	public void setStatus(WebserviceSpectralStorageAcceptImport_ProcessStatusEnum status) {
		this.status = status;
	}

	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public String getDataErrorFailMessage() {
		return dataErrorFailMessage;
	}

	public void setDataErrorFailMessage(String dataErrorFailMessage) {
		this.dataErrorFailMessage = dataErrorFailMessage;
	}

}
