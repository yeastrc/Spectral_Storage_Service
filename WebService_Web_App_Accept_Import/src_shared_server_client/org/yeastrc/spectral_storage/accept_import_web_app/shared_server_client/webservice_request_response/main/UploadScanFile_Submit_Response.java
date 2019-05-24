package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_Submit_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Submit_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Submit_Response {
	
	// Properties as XML elements
	
	@XmlAttribute // attribute name is property name
	private boolean statusSuccess;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_NotFound;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_Expired;
	
	@XmlAttribute // attribute name is property name
	private boolean noUploadedScanFile;
	
	@XmlAttribute // attribute name is property name
	private String scanProcessStatusKey; // assigned temp status key (used to get perm API Key), null if submit failed
	
	
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}
	public String getScanProcessStatusKey() {
		return scanProcessStatusKey;
	}
	public void setScanProcessStatusKey(String scanProcessStatusKey) {
		this.scanProcessStatusKey = scanProcessStatusKey;
	}
	public boolean isUploadScanFileTempKey_NotFound() {
		return uploadScanFileTempKey_NotFound;
	}
	public void setUploadScanFileTempKey_NotFound(boolean uploadScanFileTempKey_NotFound) {
		this.uploadScanFileTempKey_NotFound = uploadScanFileTempKey_NotFound;
	}
	public boolean isNoUploadedScanFile() {
		return noUploadedScanFile;
	}
	public void setNoUploadedScanFile(boolean noUploadedScanFile) {
		this.noUploadedScanFile = noUploadedScanFile;
	}
	public boolean isUploadScanFileTempKey_Expired() {
		return uploadScanFileTempKey_Expired;
	}
	public void setUploadScanFileTempKey_Expired(boolean uploadScanFileTempKey_Expired) {
		this.uploadScanFileTempKey_Expired = uploadScanFileTempKey_Expired;
	}

}
