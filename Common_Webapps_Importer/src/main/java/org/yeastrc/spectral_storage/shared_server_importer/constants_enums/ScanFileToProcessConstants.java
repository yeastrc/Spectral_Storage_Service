package org.yeastrc.spectral_storage.shared_server_importer.constants_enums;

/**
 * 
 * Files and directories for the upload and processing.
 * 
 * Shared between the web app and the scan file processing app
 */
public class ScanFileToProcessConstants {

	//  SUFFIXes also in WebserviceSpectralStorageScanFileAllowedSuffixesConstants
	
	public static final String UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML = ".mzML"; 
	public static final String UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML = ".mzXML";
	


	public static final String SCAN_FILES_TO_PROCESS_BASE_DIR = "scan_files_to_process_base_dir";


	public static final String SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR = "scan_files_processed_success_base_dir";
	public static final String SCAN_FILES_PROCESSED_FAILED_BASE_DIR = "scan_files_processed_failed_base_dir";
	
	
	/**
	 * Prefix for subdir per scan file to process
	 */
	public static final String SCAN_FILE_TO_PROCESS_SUB_DIR_PREFIX = "scan_file_";


	/**
	 * Key returned from upload servlet
	 */
	public static final String SCAN_PROCESS_STATUS_KEY_FILENAME = "b_scanProcessStatusKey.txt";
	
	/**
	 * scan file to process filename
	 */
	public static final String SCAN_FILE_TO_PROCESS_FILENAME_PREFIX = "a_scan_file_";

	/**
	 * Extra S3 object that is created when a scan file is "submitted" will have the scan filename and this suffix
	 */
	public static final String SCAN_FILE_TO_PROCESS_FILENAME_SUBMITTED_FILE_SUFFIX = ".submitted";

	/**
	 * Uploaded remote scan filename
	 */
	public static final String SCAN_FILE_TO_PROCESS_UPLOADED_FILENAME_FILE_FILENAME = "a_uploaded_scan_filename.txt";
	
	/**
	 * File created when the subdir is created for create date/time tracking 
	 */
	public static final String SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE = "c_dir_created_tracking.txt";

	/**
	 * Hash string (API Key) of the uploaded scan file 
	 */
	public static final String SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME = "process__scan_file_hash_string_api_key.txt";
	
	/**
	 * 
	 */
	public static final String ASSOCIATED_STORAGE_DIR__FILENAME = "x_Assoc_Storage_dir.txt";

	/**
	 * 
	 */
	public static final String DATA_ERROR_HUMAN_READABLE_FILENAME = "y_Data_Error_HumanReadable.txt";


	/**
	 * Hash string copied from file SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME 
	 */
	public static final String Z_FINAL_HASH_KEY = "z_hash_key.txt";
}
