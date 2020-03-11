package org.yeastrc.spectral_storage.accept_import_web_app.constants_enums;

public class UploadProcessingStatusFileConstants {

	public static final String STATUS_FILENAME = "processing_status.txt";
	
	
	//  Strings written to status file:
	//  (Must not contain spaces or special characters)
	//  (Also appended to STATUS_FILENAME to create another filename)
	
	//	1 of next 2 will be written after dir created and all files put into it

	//  Written when need to compute the API Key
	public static final String STATUS_COMPUTE_API_KEY = "compute_api_key"; 
	
	//  Written when ready to run the Importer on it
	public static final String STATUS_PENDING = "pending"; 
	
	//  Written when Scan File Processor starts processing the directory
	public static final String STATUS_PROCESSING_STARTED = "processing_started";
	
	//  Written when Scan File Processor finished processing the directory successfully, or the Accept Import Webapp finds the API Key in the already 
	public static final String STATUS_PROCESSING_SUCCESSFUL = "processing_successful";
	
	/**
	 * 
	 */
	public static final String STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP = "__accept_import";
	
	//  Written when Scan File Processor finished processing the directory and failed
	public static final String STATUS_PROCESSING_FAILED = "processing_failed";

	//  Written when Scan File Processor processing the directory was killed
	public static final String STATUS_PROCESSING_KILLED = "processing_killed";

	
}
