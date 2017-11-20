package org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Single Peak Data in single Scan
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SingleScanPeak_SubResponse {

	@XmlAttribute // attribute name is property name
	private float mz;
	@XmlAttribute // attribute name is property name
	private float intensity;
	
	//  Constructors
	
	public SingleScanPeak_SubResponse() {
		super();
	}
	

	public float getMz() {
		return mz;
	}

	public void setMz(float mz) {
		this.mz = mz;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
}
