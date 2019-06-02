package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Single Summary Data Entry per Scan Level
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SingleScanLevelSummaryData_SubResponse {
	/**
	 * Scan level for this summary data entry
	 */
	@XmlAttribute // attribute name is property name
	byte scanLevel;
	/**
	 * number of scans with this scan level
	 */
	@XmlAttribute // attribute name is property name
	int numberOfScans;
	/**
	 * Sum of intensity of all peaks for all scans with this scan level
	 */
	@XmlAttribute // attribute name is property name
	double totalIonCurrent;
	public byte getScanLevel() {
		return scanLevel;
	}
	public void setScanLevel(byte scanLevel) {
		this.scanLevel = scanLevel;
	}
	public int getNumberOfScans() {
		return numberOfScans;
	}
	public void setNumberOfScans(int numberOfScans) {
		this.numberOfScans = numberOfScans;
	}
	public double getTotalIonCurrent() {
		return totalIonCurrent;
	}
	public void setTotalIonCurrent(double totalIonCurrent) {
		this.totalIonCurrent = totalIonCurrent;
	}
		
}
