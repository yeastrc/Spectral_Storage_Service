package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

/**
 * Single Scan Data
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SingleScan_SubResponse {

	@XmlAttribute // attribute name is property name
	private byte level;
	@XmlAttribute // attribute name is property name
	private int scanNumber;
	@XmlAttribute // attribute name is property name
	private float retentionTime;
	@XmlAttribute // attribute name is property name
	private Float totalIonCurrent_ForScan;
	
	/**
	 * Not Populated when Data file is version < 5 since not stored in those data files
	 */
	@XmlAttribute // attribute name is property name
	private Float ionInjectionTime;  // In Milliseconds
	
	/**
	 * Not populated if request other than peaks and scan file contains more than one unique value
	 */
	@XmlAttribute // attribute name is property name
	private Byte isCentroid;
	
	//  Only applicable where level > 1
	
	@XmlAttribute // attribute name is property name
	private Integer parentScanNumber;
	@XmlAttribute // attribute name is property name
	private Byte precursorCharge;
	@XmlAttribute // attribute name is property name
	private Double precursor_M_Over_Z;
	
	// Peaks
	@XmlElementWrapper(name="peaks")
	@XmlElement(name="peak")
	private List<SingleScanPeak_SubResponse> peaks;
	
	//  Constructors
	
	public SingleScan_SubResponse() {
		super();
	}
	
	/**
	 * Not populated if request other than peaks and scan file contains more than one unique value
	 * @return null if not populated
	 */
	public Byte getIsCentroid() {
		return isCentroid;
	}
	public void setIsCentroid(Byte isCentroid) {
		this.isCentroid = isCentroid;
	}

	/**
	 * Not Populated when Data file is version < 5 since not stored in those data files
	 * @return null if not populated
	 */
	public Float getIonInjectionTime() {
		return ionInjectionTime;
	}
	public void setIonInjectionTime(Float ionInjectionTime) {
		this.ionInjectionTime = ionInjectionTime;
	}
	
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public float getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(float retentionTime) {
		this.retentionTime = retentionTime;
	}
	public Integer getParentScanNumber() {
		return parentScanNumber;
	}
	public void setParentScanNumber(Integer parentScanNumber) {
		this.parentScanNumber = parentScanNumber;
	}
	public Byte getPrecursorCharge() {
		return precursorCharge;
	}
	public void setPrecursorCharge(Byte precursorCharge) {
		this.precursorCharge = precursorCharge;
	}
	public Double getPrecursor_M_Over_Z() {
		return precursor_M_Over_Z;
	}
	public void setPrecursor_M_Over_Z(Double precursor_M_Over_Z) {
		this.precursor_M_Over_Z = precursor_M_Over_Z;
	}

	public List<SingleScanPeak_SubResponse> getPeaks() {
		return peaks;
	}

	public void setPeaks(List<SingleScanPeak_SubResponse> peaks) {
		this.peaks = peaks;
	}

	public Float getTotalIonCurrent_ForScan() {
		return totalIonCurrent_ForScan;
	}

	public void setTotalIonCurrent_ForScan(Float totalIonCurrent_ForScan) {
		this.totalIonCurrent_ForScan = totalIonCurrent_ForScan;
	}

	
}
