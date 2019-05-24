package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_Init_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_AddScanFileInS3Bucket_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_AddScanFileInS3Bucket_Response {
	
	// Properties as XML elements
	
	@XmlAttribute // attribute name is property name
	private boolean statusSuccess;

	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_NotFound;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileTempKey_Expired;

	@XmlAttribute // attribute name is property name
	private boolean objectKeyOrFilenameSuffixNotValid;
	
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileS3BucketOrObjectKey_NotFound;
	
	/**
	 * S3 returned an permission error when trying to determine if object exists or unable to determine the size
	 */
	@XmlAttribute // attribute name is property name
	private boolean uploadScanFileS3BucketOrObjectKey_PermissionError;
		
	
	//  These are populated for upload filesize exceeds allowed max
	@XmlAttribute // attribute name is property name
	private Boolean fileSizeLimitExceeded;
	@XmlAttribute // attribute name is property name
	private Long maxSize;
	@XmlAttribute // attribute name is property name
	private String maxSizeFormatted;
	public boolean isUploadScanFileTempKey_NotFound() {
		return uploadScanFileTempKey_NotFound;
	}
	public void setUploadScanFileTempKey_NotFound(boolean uploadScanFileTempKey_NotFound) {
		this.uploadScanFileTempKey_NotFound = uploadScanFileTempKey_NotFound;
	}
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}
	public boolean isObjectKeyOrFilenameSuffixNotValid() {
		return objectKeyOrFilenameSuffixNotValid;
	}
	public void setObjectKeyOrFilenameSuffixNotValid(boolean objectKeyOrFilenameSuffixNotValid) {
		this.objectKeyOrFilenameSuffixNotValid = objectKeyOrFilenameSuffixNotValid;
	}
	public boolean isUploadScanFileS3BucketOrObjectKey_NotFound() {
		return uploadScanFileS3BucketOrObjectKey_NotFound;
	}
	public void setUploadScanFileS3BucketOrObjectKey_NotFound(boolean uploadScanFileS3BucketOrObjectKey_NotFound) {
		this.uploadScanFileS3BucketOrObjectKey_NotFound = uploadScanFileS3BucketOrObjectKey_NotFound;
	}
	public boolean isUploadScanFileS3BucketOrObjectKey_PermissionError() {
		return uploadScanFileS3BucketOrObjectKey_PermissionError;
	}
	public void setUploadScanFileS3BucketOrObjectKey_PermissionError(
			boolean uploadScanFileS3BucketOrObjectKey_PermissionError) {
		this.uploadScanFileS3BucketOrObjectKey_PermissionError = uploadScanFileS3BucketOrObjectKey_PermissionError;
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
	public boolean isUploadScanFileTempKey_Expired() {
		return uploadScanFileTempKey_Expired;
	}
	public void setUploadScanFileTempKey_Expired(boolean uploadScanFileTempKey_Expired) {
		this.uploadScanFileTempKey_Expired = uploadScanFileTempKey_Expired;
	}
	
}
