package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_Delete_UploadTempKey_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Delete_For_ScanProcessStatusKey_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Delete_For_ScanProcessStatusKey_Response {
	
	// Properties as XML elements
	
	private boolean statusSuccess;
	
	private boolean scanProcessStatusKey_NotFound;
	
	// Currently only allow delete after import complete or fail
	private boolean currentUploadProcessingStatusNotAllowDelete; 
	
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}
	public boolean isCurrentUploadProcessingStatusNotAllowDelete() {
		return currentUploadProcessingStatusNotAllowDelete;
	}
	public void setCurrentUploadProcessingStatusNotAllowDelete(boolean currentUploadProcessingStatusNotAllowDelete) {
		this.currentUploadProcessingStatusNotAllowDelete = currentUploadProcessingStatusNotAllowDelete;
	}
	public boolean isScanProcessStatusKey_NotFound() {
		return scanProcessStatusKey_NotFound;
	}
	public void setScanProcessStatusKey_NotFound(boolean scanProcessStatusKey_NotFound) {
		this.scanProcessStatusKey_NotFound = scanProcessStatusKey_NotFound;
	}

}
