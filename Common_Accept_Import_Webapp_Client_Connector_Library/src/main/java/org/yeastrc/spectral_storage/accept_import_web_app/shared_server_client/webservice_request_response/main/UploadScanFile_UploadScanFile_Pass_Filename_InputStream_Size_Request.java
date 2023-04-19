package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import java.io.InputStream;

/**
 * Request to UploadScanFile_UploadScanFile_Servlet via CallSpectralStorageWebservice
 * 
 * Not serialized to bytes (XML, JSON, ?)
 *
 * Used only in call to CallSpectralStorageWebservice.call_UploadScanFile_Pass_InputStream_Size_Service(...)
 */
public class UploadScanFile_UploadScanFile_Pass_Filename_InputStream_Size_Request {
	
	//  ALL must be populated
	private String scanFilename;
	private InputStream scanFile_InputStream;
	private Long scanFile_Size;
	private String uploadScanFileTempKey; // assigned temp key for rest of Upload Scan File process

	public String getUploadScanFileTempKey() {
		return uploadScanFileTempKey;
	}

	public void setUploadScanFileTempKey(String uploadScanFileTempKey) {
		this.uploadScanFileTempKey = uploadScanFileTempKey;
	}

	public InputStream getScanFile_InputStream() {
		return scanFile_InputStream;
	}

	public void setScanFile_InputStream(InputStream scanFile_InputStream) {
		this.scanFile_InputStream = scanFile_InputStream;
	}

	public Long getScanFile_Size() {
		return scanFile_Size;
	}

	public void setScanFile_Size(Long scanFile_Size) {
		this.scanFile_Size = scanFile_Size;
	}
	public String getScanFilename() {
		return scanFilename;
	}

	public void setScanFilename(String scanFilename) {
		this.scanFilename = scanFilename;
	}
	
}
