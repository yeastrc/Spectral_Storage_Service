package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;

/**
 * Response object from Webservice GetScanData_AllScans_ExcludePeaks_Servlet
 *
 */
@XmlRootElement(name="get_ScanData_AllScans_ExcludePeaks_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanData_AllScans_ExcludePeaks_Response extends BaseGetDataWebserviceResponse {

	// Properties as XML elements

	@XmlElementWrapper(name="scans")
	@XmlElement(name="scan")
	private List<SingleScan_SubResponse> scans;


	public List<SingleScan_SubResponse> getScans() {
		return scans;
	}

	public void setScans(List<SingleScan_SubResponse> scans) {
		this.scans = scans;
	}
	
}
