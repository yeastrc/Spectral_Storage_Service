package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;


import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice GetScanNumbers_Servlet
 *
 */
@XmlRootElement(name="get_ScanNumbers_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanNumbers_Request extends BaseGetDataWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;

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
