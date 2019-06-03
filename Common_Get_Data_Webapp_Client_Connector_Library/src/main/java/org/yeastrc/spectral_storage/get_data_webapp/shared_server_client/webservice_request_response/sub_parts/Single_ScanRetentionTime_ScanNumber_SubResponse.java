package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Single Scan Partial Data: scanNumber, level, retentionTime
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Single_ScanRetentionTime_ScanNumber_SubResponse {

	@XmlAttribute // attribute name is property name
	private int scanNumber;
	@XmlAttribute // attribute name is property name
	private int level;
	@XmlAttribute // attribute name is property name
	private float retentionTime;
	
	//  Constructors
	
	public Single_ScanRetentionTime_ScanNumber_SubResponse() {
		super();
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
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

}
