package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice UploadScanFile_Delete_UploadTempKey_Servlet
 *
 */
@XmlRootElement(name="uploadScanFile_Delete_For_ScanProcessStatusKey_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadScanFile_Delete_For_ScanProcessStatusKey_Request extends BaseWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute
	private String scanProcessStatusKey; // assigned key After Upload Submit called

	public String getScanProcessStatusKey() {
		return scanProcessStatusKey;
	}

	public void setScanProcessStatusKey(String scanProcessStatusKey) {
		this.scanProcessStatusKey = scanProcessStatusKey;
	}


}
