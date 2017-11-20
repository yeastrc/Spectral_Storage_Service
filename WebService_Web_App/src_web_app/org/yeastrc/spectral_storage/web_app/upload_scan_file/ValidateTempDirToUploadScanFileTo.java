package org.yeastrc.spectral_storage.web_app.upload_scan_file;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.web_app.constants_enums.FileUploadConstants;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileFileUploadFileSystemException;

/**
 * 
 *
 */
public class ValidateTempDirToUploadScanFileTo {

	private static final Logger log = Logger.getLogger(ValidateTempDirToUploadScanFileTo.class);

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
	public boolean validateTempDirToUploadScanFileTo( File uploadScanFileTempKey_Dir ) throws SpectralFileFileUploadFileSystemException, IOException {
		
		File c_dir_created_trackingFile = new File( uploadScanFileTempKey_Dir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE );
		
		if ( ! c_dir_created_trackingFile.exists() ) {
			return false;
		}
		
		long lastModified = c_dir_created_trackingFile.lastModified();
		
		long now = System.currentTimeMillis();
		
		if ( lastModified + FileUploadConstants.UPLOAD_FILE_TEMP_SUB_DIR_ALLOWED_ACCESS_TIME < now ) {
			return false;
		}
		
		return true;
		
	}
}
