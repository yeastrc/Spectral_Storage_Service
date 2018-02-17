package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;

/**
 * Response object from Webservice GetScanNumbers_Servlet
 *
 */
@XmlRootElement(name="get_ScanNumbers_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanNumbers_Response extends BaseGetDataWebserviceResponse {

	// Properties as XML elements

	@XmlElementWrapper(name="scanNumbers")
	@XmlElement(name="scanNumber")
	private List<Integer> scanNumbers;

	public List<Integer> getScanNumbers() {
		return scanNumbers;
	}

	public void setScanNumbers(List<Integer> scanNumbers) {
		this.scanNumbers = scanNumbers;
	}

}
