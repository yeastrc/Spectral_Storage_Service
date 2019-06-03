package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice Get_UploadedScanFileInfo
 *
 */
@XmlRootElement(name="get_UploadedScanFileInfo_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_UploadedScanFileInfo_Request extends BaseAcceptImportWebserviceRequest {

	// Properties as XML attributes
	
	@XmlAttribute // attribute name is property name
	private String scanProcessStatusKey;

	public String getScanProcessStatusKey() {
		return scanProcessStatusKey;
	}

	public void setScanProcessStatusKey(String scanProcessStatusKey) {
		this.scanProcessStatusKey = scanProcessStatusKey;
	}
}
