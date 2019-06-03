package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScanLevelSummaryData_SubResponse;

/**
 * Response object from Webservice GetSummaryDataPerScanLevel_Servlet
 *
 */
@XmlRootElement(name="get_SummaryDataPerScanLevel_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_SummaryDataPerScanLevel_Response extends BaseGetDataWebserviceResponse {

	// Properties as XML elements

	@XmlElementWrapper(name="scanSummaryPerScanLevelList")
	@XmlElement(name="scanSummaryPerScanLevel")
	private List<SingleScanLevelSummaryData_SubResponse> scanSummaryPerScanLevelList;

	public List<SingleScanLevelSummaryData_SubResponse> getScanSummaryPerScanLevelList() {
		return scanSummaryPerScanLevelList;
	}

	public void setScanSummaryPerScanLevelList(List<SingleScanLevelSummaryData_SubResponse> scanSummaryPerScanLevelList) {
		this.scanSummaryPerScanLevelList = scanSummaryPerScanLevelList;
	}


}
