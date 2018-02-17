package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Request object for POST to Webservice GetScanPeakIntensityBinnedOn_RT_MZ_Servlet
 *
 */
@XmlRootElement(name="get_ScanPeakIntensityBinnedOn_RT_MZ_Request")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_ScanPeakIntensityBinnedOn_RT_MZ_Request extends BaseGetDataWebserviceRequest {

	@XmlAttribute // attribute name is property name
	private String scanFileAPIKey;
	
	@XmlAttribute // attribute name is property name
	private Long retentionTimeBinSize;
	
	@XmlAttribute // attribute name is property name
	private Long mzBinSize;

	public String getScanFileAPIKey() {
		return scanFileAPIKey;
	}

	public void setScanFileAPIKey(String scanFileAPIKey) {
		this.scanFileAPIKey = scanFileAPIKey;
	}

	public Long getRetentionTimeBinSize() {
		return retentionTimeBinSize;
	}

	public void setRetentionTimeBinSize(Long retentionTimeBinSize) {
		this.retentionTimeBinSize = retentionTimeBinSize;
	}

	public Long getMzBinSize() {
		return mzBinSize;
	}

	public void setMzBinSize(Long mzBinSize) {
		this.mzBinSize = mzBinSize;
	}
}
