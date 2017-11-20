package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;

/**
 * Response object from Webservice GetScanDataFromScanNumber_Servlet
 *
 */
@XmlRootElement(name="get_ScansDataFromRetentionTimeRange_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScansDataFromRetentionTimeRange_Response extends BaseWebserviceResponse {

	// Properties as XML elements

	/**
	 * Not populated if too many scans to return
	 */
	@XmlElementWrapper(name="scans")
	@XmlElement(name="scan")
	private List<SingleScan_SubResponse> scans;


	@XmlAttribute // attribute name is property name
	private Boolean tooManyScansToReturn;
	
	@XmlAttribute // attribute name is property name
	private Integer maxScansToReturn;

	/**
	 * Only populated if tooManyScansToReturn is true
	 */
	@XmlElementWrapper(name="scanNumbersRetentionTimes")
	@XmlElement(name="scnNmbRT")
	private List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanNumbersRetentionTimes;

	/**
	 * Not populated if too many scans to return
	 * @return
	 */
	public List<SingleScan_SubResponse> getScans() {
		return scans;
	}

	public void setScans(List<SingleScan_SubResponse> scans) {
		this.scans = scans;
	}

	/**
	 * Only populated if tooManyScansToReturn is true
	 * @return
	 */
	public List<Single_ScanRetentionTime_ScanNumber_SubResponse> getScanNumbersRetentionTimes() {
		return scanNumbersRetentionTimes;
	}

	/**
	 * Only populated if tooManyScansToReturn is true
	 * @param scanNumbersRetentionTimes
	 */
	public void setScanNumbersRetentionTimes(
			List<Single_ScanRetentionTime_ScanNumber_SubResponse> scanNumbersRetentionTimes) {
		this.scanNumbersRetentionTimes = scanNumbersRetentionTimes;
	}



	public Boolean getTooManyScansToReturn() {
		return tooManyScansToReturn;
	}

	public void setTooManyScansToReturn(Boolean tooManyScansToReturn) {
		this.tooManyScansToReturn = tooManyScansToReturn;
	}

	public Integer getMaxScansToReturn() {
		return maxScansToReturn;
	}

	public void setMaxScansToReturn(Integer maxScansToReturn) {
		this.maxScansToReturn = maxScansToReturn;
	}


}
