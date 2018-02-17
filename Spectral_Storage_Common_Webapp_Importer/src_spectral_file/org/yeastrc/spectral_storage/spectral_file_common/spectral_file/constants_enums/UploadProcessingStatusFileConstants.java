package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums;

public class UploadProcessingStatusFileConstants {

	public static final String STATUS_FILENAME = "processing_status.txt";
	
	
	//  Strings written to status file:
	//  (Must not contain spaces or special characters)
	//  (Also appended to STATUS_FILENAME to create another filename)
	
	//	Written after dir created and all files put into it
	public static final String STATUS_PENDING = "pending"; 
	
	//  Written when Scan File Processor starts processing the directory
	public static final String STATUS_PROCESSING_STARTED = "processing_started";
	
	//  Written when Scan File Processor finished processing the directory successfully
	public static final String STATUS_PROCESSING_SUCCESSFUL = "processing_successful";
	
	//  Written when Scan File Processor finished processing the directory and failed
	public static final String STATUS_PROCESSING_FAILED = "processing_failed";

	//  Written when Scan File Processor processing the directory was killed
	public static final String STATUS_PROCESSING_KILLED = "processing_killed";

	
}
