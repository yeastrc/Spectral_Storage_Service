package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts;

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
	private double mz;
	@XmlAttribute // attribute name is property name
	private float intensity;
	
	//  Constructors
	
	public SingleScanPeak_SubResponse() {
		super();
	}
	

	public double getMz() {
		return mz;
	}

	public void setMz(double mz) {
		this.mz = mz;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
}
