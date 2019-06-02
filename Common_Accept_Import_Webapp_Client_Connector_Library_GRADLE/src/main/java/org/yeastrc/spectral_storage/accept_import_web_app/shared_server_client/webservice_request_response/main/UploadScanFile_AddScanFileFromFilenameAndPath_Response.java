package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_AddScanFileFromFilenameAndPath_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_AddScanFileFromFilenameAndPath_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_AddScanFileFromFilenameAndPath_Response {
	
	// Properties as XML elements
	
	@XmlAttribute // attribute name is property name
	private boolean statusSuccess;

	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_NotFound;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_Expired;

	@XmlAttribute // attribute name is property name
	private boolean filenameSuffixNotValid;

	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileWithPath_FilePathsAllowedNotConfigured;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileWithPath_FilePathNotAllowed;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileWithPath_FileNotFound;

	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileWithPath_NotMatch_SubmittedFileSize;
		
	//  These are populated for upload filesize exceeds allowed max
	@XmlAttribute // attribute name is property name
	private Boolean fileSizeLimitExceeded;
	@XmlAttribute // attribute name is property name
	private Long maxSize;
	@XmlAttribute // attribute name is property name
	private String maxSizeFormatted;
	
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}
	public boolean isUploadScanFileTempKey_NotFound() {
		return uploadScanFileTempKey_NotFound;
	}
	public void setUploadScanFileTempKey_NotFound(boolean uploadScanFileTempKey_NotFound) {
		this.uploadScanFileTempKey_NotFound = uploadScanFileTempKey_NotFound;
	}
	public boolean isFilenameSuffixNotValid() {
		return filenameSuffixNotValid;
	}
	public void setFilenameSuffixNotValid(boolean filenameSuffixNotValid) {
		this.filenameSuffixNotValid = filenameSuffixNotValid;
	}
	public Boolean getFileSizeLimitExceeded() {
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
	public boolean isUploadScanFileWithPath_NotMatch_SubmittedFileSize() {
		return uploadScanFileWithPath_NotMatch_SubmittedFileSize;
	}
	public void setUploadScanFileWithPath_NotMatch_SubmittedFileSize(
			boolean uploadScanFileWithPath_NotMatch_SubmittedFileSize) {
		this.uploadScanFileWithPath_NotMatch_SubmittedFileSize = uploadScanFileWithPath_NotMatch_SubmittedFileSize;
	}
	public boolean isUploadScanFileWithPath_FileNotFound() {
		return uploadScanFileWithPath_FileNotFound;
	}
	public void setUploadScanFileWithPath_FileNotFound(boolean uploadScanFileWithPath_FileNotFound) {
		this.uploadScanFileWithPath_FileNotFound = uploadScanFileWithPath_FileNotFound;
	}
	public boolean isUploadScanFileWithPath_FilePathsAllowedNotConfigured() {
		return uploadScanFileWithPath_FilePathsAllowedNotConfigured;
	}
	public void setUploadScanFileWithPath_FilePathsAllowedNotConfigured(
			boolean uploadScanFileWithPath_FilePathsAllowedNotConfigured) {
		this.uploadScanFileWithPath_FilePathsAllowedNotConfigured = uploadScanFileWithPath_FilePathsAllowedNotConfigured;
	}
	public boolean isUploadScanFileWithPath_FilePathNotAllowed() {
		return uploadScanFileWithPath_FilePathNotAllowed;
	}
	public void setUploadScanFileWithPath_FilePathNotAllowed(boolean uploadScanFileWithPath_FilePathNotAllowed) {
		this.uploadScanFileWithPath_FilePathNotAllowed = uploadScanFileWithPath_FilePathNotAllowed;
	}
	public boolean isUploadScanFileTempKey_Expired() {
		return uploadScanFileTempKey_Expired;
	}
	public void setUploadScanFileTempKey_Expired(boolean uploadScanFileTempKey_Expired) {
		this.uploadScanFileTempKey_Expired = uploadScanFileTempKey_Expired;
	}
	
	
}
