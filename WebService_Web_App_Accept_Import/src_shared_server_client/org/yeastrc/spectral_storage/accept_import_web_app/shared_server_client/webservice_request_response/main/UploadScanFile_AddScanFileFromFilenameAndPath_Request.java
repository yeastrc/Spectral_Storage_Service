package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice UploadScanFile_AddScanFileFromFilenameAndPath_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_AddScanFileFromFilenameAndPath_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_AddScanFileFromFilenameAndPath_Request extends BaseAcceptImportWebserviceRequest {

	// Properties as XML attributes

	@XmlAttribute
	private String uploadScanFileTempKey; // assigned temp key for rest of Upload Scan File process

	@XmlAttribute // attribute name is property name
	private String filenameWithPath;

	@XmlAttribute // attribute name is property name
	private BigInteger fileSize;

	public BigInteger getFileSize() {
		return fileSize;
	}

	public void setFileSize(BigInteger fileSize) {
		this.fileSize = fileSize;
	}

	public String getUploadScanFileTempKey() {
		return uploadScanFileTempKey;
	}

	public void setUploadScanFileTempKey(String uploadScanFileTempKey) {
		this.uploadScanFileTempKey = uploadScanFileTempKey;
	}

	public String getFilenameWithPath() {
		return filenameWithPath;
	}

	public void setFilenameWithPath(String filenameWithPath) {
		this.filenameWithPath = filenameWithPath;
	}

}
