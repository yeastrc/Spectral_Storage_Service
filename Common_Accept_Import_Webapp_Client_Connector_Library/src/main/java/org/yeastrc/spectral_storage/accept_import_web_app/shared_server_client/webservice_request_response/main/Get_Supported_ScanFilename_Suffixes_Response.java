package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.webservice_request_response.main;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums.WebserviceSpectralStorageAcceptImport_ProcessStatusEnum;

/**
 * Response object from Webservice Get_Supported_ScanFilename_Suffixes
 *
 */
@XmlRootElement(name="get_Supported_ScanFilename_Suffixes_Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Get_Supported_ScanFilename_Suffixes_Response extends BaseAcceptImportWebserviceResponse {

	// Properties as XML attributes

	@XmlElementWrapper(name="scanFilenameSuffixes")
	@XmlElement(name="scanFilenameSuffix")
	private List<String> scanFilenameSuffixes;

	public List<String> getScanFilenameSuffixes() {
		return scanFilenameSuffixes;
	}

	public void setScanFilenameSuffixes(List<String> scanFilenameSuffixes) {
		this.scanFilenameSuffixes = scanFilenameSuffixes;
	}


}
