package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_Submit_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Submit_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Submit_Response {
	
	// Properties as XML elements
	
	private boolean statusSuccess;
	
	private boolean uploadScanFileTempKey_NotFound;
	
	private boolean noUploadedScanFile;
	
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

}
