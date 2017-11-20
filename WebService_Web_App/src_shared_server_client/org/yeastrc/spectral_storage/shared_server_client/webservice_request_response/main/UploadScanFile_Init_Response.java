package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response from UploadScanFile_Init_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Init_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Init_Response {
	
	// Properties as XML elements
	
	private boolean statusSuccess;
	private String uploadScanFileTempKey; // assigned temp key for rest of Upload Scan File process, null if failed
	
	private Long maxUploadFileSize;
	private String maxUploadFileSizeFormatted;
	
	public boolean isStatusSuccess() {
		return statusSuccess;
	}
	public void setStatusSuccess(boolean statusSuccess) {
		this.statusSuccess = statusSuccess;
	}
	public String getUploadScanFileTempKey() {
		return uploadScanFileTempKey;
	}
	public void setUploadScanFileTempKey(String uploadScanFileTempKey) {
		this.uploadScanFileTempKey = uploadScanFileTempKey;
	}
	public Long getMaxUploadFileSize() {
		return maxUploadFileSize;
	}
	public void setMaxUploadFileSize(Long maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
	}
	public String getMaxUploadFileSizeFormatted() {
		return maxUploadFileSizeFormatted;
	}
	public void setMaxUploadFileSizeFormatted(String maxUploadFileSizeFormatted) {
		this.maxUploadFileSizeFormatted = maxUploadFileSizeFormatted;
	}


}
