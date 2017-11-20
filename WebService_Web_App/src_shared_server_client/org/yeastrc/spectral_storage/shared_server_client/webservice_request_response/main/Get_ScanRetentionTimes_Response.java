package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;

/**
 * Response object from Webservice GetScanRetentionTimes_Servlet
 *
 */
@XmlRootElement(name="get_ScanRetentionTimes_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanRetentionTimes_Response extends BaseWebserviceResponse {

	// Properties as XML elements

	@XmlElementWrapper(name="scanParts")
	@XmlElement(name="scanPart")
	private List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanParts;

	public List<Single_ScanRetentionTime_ScanNumber_SubResponse> getScanParts() {
		return scanParts;
	}

	public void setScanParts(List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanParts) {
		this.scanParts = scanParts;
	}


}
