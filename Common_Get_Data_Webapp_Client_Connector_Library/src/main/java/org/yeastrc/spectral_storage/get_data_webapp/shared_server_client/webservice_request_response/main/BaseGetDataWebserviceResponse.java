package org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.enums.Get_ScanData_ScanFileAPI_Key_NotFound;

/**
 * Base class for all Webservice Response Classes
 *
 */
public class BaseGetDataWebserviceResponse {

	private Get_ScanData_ScanFileAPI_Key_NotFound status_scanFileAPIKeyNotFound;

	public Get_ScanData_ScanFileAPI_Key_NotFound getStatus_scanFileAPIKeyNotFound() {
		return status_scanFileAPIKeyNotFound;
	}

	public void setStatus_scanFileAPIKeyNotFound(Get_ScanData_ScanFileAPI_Key_NotFound status_scanFileAPIKeyNotFound) {
		this.status_scanFileAPIKeyNotFound = status_scanFileAPIKeyNotFound;
	}

}
