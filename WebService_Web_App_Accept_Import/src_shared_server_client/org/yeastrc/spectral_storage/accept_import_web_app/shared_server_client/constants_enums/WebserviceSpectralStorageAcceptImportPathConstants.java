package org.yeastrc.spectral_storage.accept_import_web_app.shared_server_client.constants_enums;

/**
 * URL Paths to the servlets
 *
 */
public class WebserviceSpectralStorageAcceptImportPathConstants {

	
	//  Servlets - Processing Scan File Upload and Returning Status and API Key

	public static final String UPLOAD_SCAN_FILE_INIT_SERVLET_XML = "/update/uploadScanFile_Init_XML";

	public static final String UPLOAD_SCAN_FILE_ADD_SCAN_FILE_IN_S3_BUCKET_SERVLET_XML = "/update/uploadScanFile_addScanFileInS3Bucket_XML";
	
	public static final String UPLOAD_SCAN_FILE_UPLOAD_SCAN_FILE_SERVLET_XML = "/update/uploadScanFile_uploadScanFile_XML";
	
	public static final String UPLOAD_SCAN_FILE_SUBMIT_SERVLET_XML = "/update/uploadScanFile_Submit_XML";
	
	public static final String UPLOADED_SCAN_FILE_STATUS_API_KEY_SERVLET_XML = "/update/uploadedScanFile_Status_API_Key_XML";
	
	public static final String UPLOADED_SCAN_FILE_DELETE_FOR_SCAN_PROCESS_STATUS_KEY_SERVLET_XML = "/update/uploadedScanFile_Delete_For_ScanProcessStatusKey_XML";
		
}
