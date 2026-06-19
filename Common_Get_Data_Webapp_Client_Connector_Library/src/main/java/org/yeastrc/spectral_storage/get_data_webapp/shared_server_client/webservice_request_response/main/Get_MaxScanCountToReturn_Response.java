package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Response object from Webservice Get_MaxScanCountToReturn_Servlet
 *
 */
@XmlRootElement(name="get_MaxScanCountToReturn_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_MaxScanCountToReturn_Response extends BaseGetDataWebserviceResponse {

	/**
	 * 
	 */
	@XmlAttribute // attribute name is property name
	private Integer maxScanCountToReturn;

	public Integer getMaxScanCountToReturn() {
		return maxScanCountToReturn;
	}
	public void setMaxScanCountToReturn(Integer maxScanCountToReturn) {
		this.maxScanCountToReturn = maxScanCountToReturn;
	}
	
}
