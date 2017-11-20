package org.yeastrc.spectral_storage.web_app.servlet_response_factories;

//import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.sub_parts.Single_ScanRetentionTime_ScanNumber_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;

/**
 * Build Single_ScanRetentionTime_ScanNumber_SubResponse
 *
 */
public class Single_ScanRetentionTime_ScanNumber_SubResponse_Factory {

//	private static final Logger log = Logger.getLogger( Single_ScanRetentionTime_ScanNumber_SubResponse_Factory.class );

	//  private constructor
	private Single_ScanRetentionTime_ScanNumber_SubResponse_Factory() { }
	
	/**
	 * @return instance
	 */
	public static Single_ScanRetentionTime_ScanNumber_SubResponse_Factory getInstance() {
		return new Single_ScanRetentionTime_ScanNumber_SubResponse_Factory();
	}
	
	/**
	 * @param spectralFile_SingleScan_Common
	 * @return
	 */
	public Single_ScanRetentionTime_ScanNumber_SubResponse buildSingle_ScanRetentionTime_ScanNumber_SubResponse(
			SpectralFile_Result_RetentionTime_ScanNumber spectralFile_Result_RetentionTime_ScanNumber ) {
		
		Single_ScanRetentionTime_ScanNumber_SubResponse subresponse = new Single_ScanRetentionTime_ScanNumber_SubResponse();
		
		subresponse.setScanNumber( spectralFile_Result_RetentionTime_ScanNumber.getScanNumber() );
		subresponse.setLevel( spectralFile_Result_RetentionTime_ScanNumber.getLevel() );
		subresponse.setRetentionTime( spectralFile_Result_RetentionTime_ScanNumber.getRetentionTime() );
		
		return subresponse;
	}
}
