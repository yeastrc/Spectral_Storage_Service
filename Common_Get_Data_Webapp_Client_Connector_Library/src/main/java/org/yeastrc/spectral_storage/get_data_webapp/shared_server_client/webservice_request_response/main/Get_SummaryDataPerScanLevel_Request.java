package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice GetSummaryDataPerScanLevel_Servlet
 *
 */
@XmlRootElement(name="get_SummaryDataPerScanLevel_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_SummaryDataPerScanLevel_Request extends BaseGetDataWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

}
