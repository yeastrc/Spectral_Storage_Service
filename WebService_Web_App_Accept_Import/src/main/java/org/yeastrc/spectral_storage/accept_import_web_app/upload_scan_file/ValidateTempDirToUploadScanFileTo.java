package org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file;

import java.io.File;
import java.io.IOException;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * 
 *
 */
public class ValidateTempDirToUploadScanFileTo {

	private static final Logger log = LoggerFactory.getLogger(ValidateTempDirToUploadScanFileTo.class);
	
	public enum ValidationResponse { VALID, KEY_NOT_FOUND, KEY_EXPIRED }

	//  private constructor
	private ValidateTempDirToUploadScanFileTo() { }
	
	/**
	 * @return newly created instance
	 */
	public static ValidateTempDirToUploadScanFileTo getInstance() { 
		return new ValidateTempDirToUploadScanFileTo(); 
	}
	
	/**
	 * @param uploadScanFileTempKey_Dir
	 * @return
	 * @throws SpectralFileFileUploadFileSystemException
	 * @throws IOException
	 */
	public ValidationResponse validateTempDirToUploadScanFileTo( File uploadScanFileTempKey_Dir ) throws SpectralFileFileUploadFileSystemException, IOException {
		
		File c_dir_created_trackingFile = new File( uploadScanFileTempKey_Dir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE );
		
		if ( ! c_dir_created_trackingFile.exists() ) {

			log.warn( "Validation of submit of dir " 
					+ uploadScanFileTempKey_Dir.getAbsolutePath()
					
					+ " Failed. The tracking file is missing: " + ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE );
			return ValidationResponse.KEY_NOT_FOUND;
		}
		
		long lastModified = c_dir_created_trackingFile.lastModified();
		
		long now = System.currentTimeMillis();
		
		if ( lastModified + FileUploadConstants.UPLOAD_FILE_TEMP_SUB_DIR_ALLOWED_ACCESS_TIME < now ) {
			
			log.warn( "Validation of submit of dir " 
					+ uploadScanFileTempKey_Dir.getAbsolutePath()
					+ " Failed. The time from create directory (tracked by last modified on file '"
					+ ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE
					+ "' to time of submit (aka now) exceeds time allowed (in milliseconds): " 
					+ FileUploadConstants.UPLOAD_FILE_TEMP_SUB_DIR_ALLOWED_ACCESS_TIME );
			return ValidationResponse.KEY_EXPIRED;
		}
		
		return ValidationResponse.VALID;
		
	}
}
