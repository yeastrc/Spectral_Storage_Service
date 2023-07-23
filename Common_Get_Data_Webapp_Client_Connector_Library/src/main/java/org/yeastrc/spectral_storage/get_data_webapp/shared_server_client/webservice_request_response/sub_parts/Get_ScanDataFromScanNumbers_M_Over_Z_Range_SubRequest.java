package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Sub Part for class Get_ScanDataFromScanNumbers_Request
 * 
 * A Single m/z range to filter the returned scan peaks
 * 
 * Each m/z range is OR with each other
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanDataFromScanNumbers_M_Over_Z_Range_SubRequest {


	/**
	 * A Single m/z range to filter the returned scan peaks
	 * 
	 * Each m/z range is OR with each other 
	 */
	@XmlAttribute // attribute name is property name
	private Double mzLowCutoff;

	/**
	 * A Single m/z range to filter the returned scan peaks
	 * 
	 * Each m/z range is OR with each other 
	 */
	@XmlAttribute // attribute name is property name
	private Double mzHighCutoff;
	
	

	public Double getMzLowCutoff() {
		return mzLowCutoff;
	}

	public void setMzLowCutoff(Double mzLowCutoff) {
		this.mzLowCutoff = mzLowCutoff;
	}

	public Double getMzHighCutoff() {
		return mzHighCutoff;
	}

	public void setMzHighCutoff(Double mzHighCutoff) {
		this.mzHighCutoff = mzHighCutoff;
	}


	
}
