package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import java.io.File;

/**
 * Request to UploadScanFile_UploadScanFile_Servlet via CallSpectralStorageWebservice
 * 
 * Not serialized to bytes (XML, JSON, ?)
 *
 * Used only in call to CallSpectralStorageWebservice.call_UploadScanFile_UploadScanFile_Service(...)
 */
public class UploadScanFile_UploadScanFile_Request {
	
	private File scanFile;
	private String uploadScanFileTempKey; // assigned temp key for rest of Upload Scan File process

	public String getUploadScanFileTempKey() {
		return uploadScanFileTempKey;
	}

	public void setUploadScanFileTempKey(String uploadScanFileTempKey) {
		this.uploadScanFileTempKey = uploadScanFileTempKey;
	}
	public File getScanFile() {
		return scanFile;
	}

	public void setScanFile(File scanFile) {
		this.scanFile = scanFile;
	}
	
}
