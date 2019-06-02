package org.yeastrc.spectral_storage.get_data_webapp.constants_enums;

import java.text.NumberFormat;

/**
 * 
 *
 */
public class FileUploadConstants {


	 public static final long MAX_FILE_UPLOAD_SIZE = ( 20L * 1000L * 1000L * 1000L ); // 20GB max

//	public static final int MAX_FILE_UPLOAD_SIZE = ( 2 * 10 * 1000  ); // temp smaller max of 20KB

	
	public static final String MAX_FILE_UPLOAD_SIZE_FORMATTED = NumberFormat.getInstance().format( MAX_FILE_UPLOAD_SIZE );
	
	
	public static long get_MAX_FILE_UPLOAD_SIZE() {
		
		return MAX_FILE_UPLOAD_SIZE;
	}
	
	
	public static String get_MAX_FILE_UPLOAD_SIZE_FORMATTED() {
		
		return MAX_FILE_UPLOAD_SIZE_FORMATTED;
	}
	
	
	public static final String UPLOAD_SCAN_FILE_FIELD_NAME = "uploadScanFile";
	


	public static final String UPLOAD_FILE_TEMP_BASE_DIR = "upload_file_temp_base_dir";
	
	
	/**
	 * Prefix for temp subdir per request
	 */
	public static final String UPLOAD_FILE_TEMP_SUB_DIR_PREFIX = "up_tmp_";
	
	/**
	 * Allowed access time after creation of UPLOAD_FILE_TEMP_SUB_DIR_PREFIX
	 */
	public static final long UPLOAD_FILE_TEMP_SUB_DIR_ALLOWED_ACCESS_TIME = 5 * 60 * 1000; // 5 minutes

	public static final String UPLOAD_FILE_DATA_FILE_PREFIX = "uploaded_file__data_file_";
	public static final String UPLOAD_FILE_DATA_FILE_SUFFIX = ".xml";
	

	public static final String UPLOAD_SCAN_FILE_TEMP_FILENAME_PREFIX = "uploaded_scan_file";
	
	
	
	////////////////////
	
	//  Contents to add to import work dir

	/**
	 * File created when the submit is for the same machine
	 * 
	 *  This file contains the list of files to be imported, since they are not copied to the import dir
	 */
	public static final String IMPORT_FILE_LIST_FILE = "import_file_list.txt";
}
