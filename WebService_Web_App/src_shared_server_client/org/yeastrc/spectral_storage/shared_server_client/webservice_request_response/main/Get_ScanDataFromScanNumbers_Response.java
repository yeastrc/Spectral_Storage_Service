package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;

/**
 * Response object from Webservice GetScanDataFromScanNumbers_Servlet
 *
 */
@XmlRootElement(name="get_ScanDataFromScanNumbers_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanDataFromScanNumbers_Response extends BaseWebserviceResponse {

	// Properties as XML elements

	@XmlElementWrapper(name="scans")
	@XmlElement(name="scan")
	private List<SingleScan_SubResponse> scans;

	/**
	 * Not populated if number of scan numbers > maxScansToReturn 
	 * 
	 * Only populated if includeParentScans is not null and is not NO 
	 */
	@XmlAttribute // attribute name is property name
	private Byte maxScanLevelFound;
	
	@XmlAttribute // attribute name is property name
	private Boolean tooManyScansToReturn;
	
	@XmlAttribute // attribute name is property name
	private Integer maxScansToReturn;

	/**
	 * Not populated if number of scan numbers > maxScansToReturn
	 * 
	 * Only populated if includeParentScans is not null and is not NO
	 */
	@XmlAttribute // attribute name is property name
	private Integer maxScanNumbersAllowedForMaxScanLevelFound;
	
	

	public List<SingleScan_SubResponse> getScans() {
		return scans;
	}

	public void setScans(List<SingleScan_SubResponse> scans) {
		this.scans = scans;
	}

	/**
	 * Not populated if number of scan numbers > maxScansToReturn
	 * @return
	 */
	public Byte getMaxScanLevelFound() {
		return maxScanLevelFound;
	}

	public void setMaxScanLevelFound(Byte maxScanLevelFound) {
		this.maxScanLevelFound = maxScanLevelFound;
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

	/**
	 * Not populated if number of scan numbers > maxScansToReturn
	 * @return
	 */
	public Integer getMaxScanNumbersAllowedForMaxScanLevelFound() {
		return maxScanNumbersAllowedForMaxScanLevelFound;
	}

	public void setMaxScanNumbersAllowedForMaxScanLevelFound(Integer maxScanNumbersAllowedForMaxScanLevelFound) {
		this.maxScanNumbersAllowedForMaxScanLevelFound = maxScanNumbersAllowedForMaxScanLevelFound;
	}

	
}
