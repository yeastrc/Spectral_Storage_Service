package org.yeastrc.spectral_storage.shared_server_client.constants;

/**
 * URL Paths to the servlets
 *
 */
public class WebserviceSpectralStoragePathConstants {

	
	//  Servlets - Processing Scan File Upload and Returning Status and API Key

	public static final String UPLOAD_SCAN_FILE_INIT_SERVLET_XML = "/uploadScanFile_Init_XML";
	
	public static final String UPLOAD_SCAN_FILE_UPLOAD_SCAN_FILE_SERVLET_XML = "/uploadScanFile_uploadScanFile_XML";
	
	public static final String UPLOAD_SCAN_FILE_SUBMIT_SERVLET_XML = "/uploadScanFile_Submit_XML";
	
	public static final String UPLOADED_SCAN_FILE_STATUS_API_KEY_SERVLET_XML = "/uploadedScanFile_Status_API_Key_XML";
	
	public static final String UPLOADED_SCAN_FILE_DELETE_FOR_SCAN_PROCESS_STATUS_KEY_SERVLET_XML = "/uploadedScanFile_Delete_For_ScanProcessStatusKey_XML";
		
	//  Servlets - Retrieving data from scan files using API Key and other parameters
	
	public static final String GET_SCAN_DATA_FROM_SCAN_NUMBERS_SERVLET_XML = "/getScanDataFromScanNumbers_XML";
	
	public static final String GET_SCAN_RETENTION_TIMES_SERVLET_XML = "/getScanRetentionTimes_XML";
	
	public static final String GET_SCAN_NUMBERS_FROM_RETENTION_TIME_RANGE_SERVLET_XML = "/getScanNumbersFromRetentionTimeRange_XML";

	public static final String GET_SCANS_DATA_FROM_RETENTION_TIME_RANGE_SERVLET_XML = "/getScansDataFromRetentionTimeRange_XML";

	

}