package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice GetScanRetentionTimes_Servlet
 *
 */
@XmlRootElement(name="get_ScanRetentionTimes_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanRetentionTimes_Request extends BaseWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

	/**
	 * Scan numbers to get data for.
	 * 
	 * If this is populated, No other options are allowed.
	 */
	@XmlElementWrapper(name="scanNumbers")
	@XmlElement(name="scanNumber")
	private List<Integer> scanNumbers;
	
	/**
	 * Scan Levels to Include from results list
	 */
	@XmlElementWrapper(name="scanLevelsToInclude")
	@XmlElement(name="scanLevelToInclude")
	private List<Integer> scanLevelsToInclude;

	/**
	 * Scan Levels to Exclude from results list
	 */
	@XmlElementWrapper(name="scanLevelsToExclude")
	@XmlElement(name="scanLevelToExclude")
	private List<Integer> scanLevelsToExclude;
	
	/**
	 * Scan numbers to get data for.
	 * 
	 * If this is populated, No other options are allowed.
	 * 
	 * @return
	 */
	public List<Integer> getScanNumbers() {
		return scanNumbers;
	}

	/**
	 * Scan numbers to get data for.
	 * 
	 * If this is populated, No other options are allowed.
	 * 
	 * @param scanNumbers
	 */
	public void setScanNumbers(List<Integer> scanNumbers) {
		this.scanNumbers = scanNumbers;
	}


	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	/**
	 * Scan Levels to Include from results list
	 * @return
	 */
	public List<Integer> getScanLevelsToInclude() {
		return scanLevelsToInclude;
	}

	/**
	 * Scan Levels to Include from results list
	 * @param scanLevelsToInclude
	 */
	public void setScanLevelsToInclude(List<Integer> scanLevelsToInclude) {
		this.scanLevelsToInclude = scanLevelsToInclude;
	}

	/**
	 * Scan Levels to Exclude from results list
	 * @return
	 */
	public List<Integer> getScanLevelsToExclude() {
		return scanLevelsToExclude;
	}

	/**
	 * Scan Levels to Exclude from results list
	 * @param scanLevelsToExclude
	 */
	public void setScanLevelsToExclude(List<Integer> scanLevelsToExclude) {
		this.scanLevelsToExclude = scanLevelsToExclude;
	}

}
