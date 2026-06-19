package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice UploadScanFile_Submit_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Submit_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Submit_Request extends BaseAcceptImportWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute
	private String uploadScanFileTempKey; // assigned temp key for rest of Upload Scan File process

	public String getUploadScanFileTempKey() {
		return uploadScanFileTempKey;
	}

	public void setUploadScanFileTempKey(String uploadScanFileTempKey) {
		this.uploadScanFileTempKey = uploadScanFileTempKey;
	}


}
