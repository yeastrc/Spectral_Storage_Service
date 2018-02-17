package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_UploadScanFile_Servlet
 *
 */
@XmlRootElement(name="UploadScanFile_UploadScanFile_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_UploadScanFile_Response {
	
	// Properties as XML elements
	
	private boolean statusSuccess;

	private boolean uploadScanFileTempKey_NotFound;
		
	private boolean uploadedFileHasNoFilename;
	private boolean uploadedFileSuffixNotValid;
	
	//  These are populated for upload filesize exceeds allowed max
	private Boolean fileSizeLimitExceeded;
	private Long maxSize;
	private String maxSizeFormatted;
	
	
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}

	public Boolean isFileSizeLimitExceeded() {
		return fileSizeLimitExceeded;
	}
	public void setFileSizeLimitExceeded(Boolean fileSizeLimitExceeded) {
		this.fileSizeLimitExceeded = fileSizeLimitExceeded;
	}
	public Long getMaxSize() {
		return maxSize;
	}
	public void setMaxSize(Long maxSize) {
		this.maxSize = maxSize;
	}
	public String getMaxSizeFormatted() {
		return maxSizeFormatted;
	}
	public void setMaxSizeFormatted(String maxSizeFormatted) {
		this.maxSizeFormatted = maxSizeFormatted;
	}
	public boolean isUploadedFileSuffixNotValid() {
		return uploadedFileSuffixNotValid;
	}
	public void setUploadedFileSuffixNotValid(boolean uploadedFileSuffixNotValid) {
		this.uploadedFileSuffixNotValid = uploadedFileSuffixNotValid;
	}
	public boolean isUploadedFileHasNoFilename() {
		return uploadedFileHasNoFilename;
	}
	public void setUploadedFileHasNoFilename(boolean uploadedFileHasNoFilename) {
		this.uploadedFileHasNoFilename = uploadedFileHasNoFilename;
	}
	public boolean isUploadScanFileTempKey_NotFound() {
		return uploadScanFileTempKey_NotFound;
	}
	public void setUploadScanFileTempKey_NotFound(boolean uploadScanFileTempKey_NotFound) {
		this.uploadScanFileTempKey_NotFound = uploadScanFileTempKey_NotFound;
	}


}
