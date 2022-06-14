package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.constants_enums;

/**
 * Object prefixes used for S3 objects for uploaded scan files
 *
 */
public class S3_ObjectKey_Prefixes_UploadedScanFile {

	public static final String S3_PATH_SEPARATOR = "/"; 

	//  Change to have a single S3 object path for uploaded scan files 
	//  since cannot change the S3 object key for a S3 object

	/**
	 * When scan file initially uploaded
	 */
	public static final String UPLOADED_SCAN_FILE = "spectral_storage_scan_file_uploaded";

//	/**
//	 * When scan file initially uploaded
//	 */
//	public static final String TEMP_UPLOADED_SCAN_FILE = "spectral_storage_scan_file_temp_upload";
//	
//	/**
//	 * Once scan file upload has been submitted, it is renamed to this prefix
//	 */
//	public static final String SCAN_FILE_TO_IMPORT_PREFIX = "spectral_storage_scan_file_to_import";
}
