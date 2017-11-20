package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response object from Webservice GetScanDataFromScanNumber_Servlet
 *
 */
@XmlRootElement(name="get_ScanNumbersFromRetentionTimeRange_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanNumbersFromRetentionTimeRange_Response extends BaseWebserviceResponse {

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
