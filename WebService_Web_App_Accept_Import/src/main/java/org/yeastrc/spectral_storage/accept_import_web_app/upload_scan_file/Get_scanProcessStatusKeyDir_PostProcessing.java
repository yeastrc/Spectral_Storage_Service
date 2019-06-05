package org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * For servlet calls after Processing is started/completed
 * 
 * For:  
 *
 */
public class Get_scanProcessStatusKeyDir_PostProcessing {

	private static final Logger log = Logger.getLogger(Get_scanProcessStatusKeyDir_PostProcessing.class);

	//  private constructor
	private Get_scanProcessStatusKeyDir_PostProcessing() { }
	
	/**
	 * @return newly created instance
	 */
	public static Get_scanProcessStatusKeyDir_PostProcessing getInstance() { 
		return new Get_scanProcessStatusKeyDir_PostProcessing(); 
	}
	
	
	/**
	 * @param scanProcessStatusKey
	 * @return null if not found or any errors
	 * @throws SpectralFileFileUploadFileSystemException 
	 */
	public File get_scanProcessStatusKeyDir_PostProcessing( String scanProcessStatusKey ) throws SpectralFileFileUploadFileSystemException {

		File uploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		if ( ! uploadBaseDir.exists() ) {
			String msg = "uploadBaseDir does not exist.  uploadBaseDir: " 
					+ uploadBaseDir.getAbsolutePath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException(msg);
		}		

		File resultDir = null;
		
		resultDir = get_scanProcessStatusKeyDir_SpecificDirectory(
				scanProcessStatusKey, ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR, uploadBaseDir );
		
		if ( resultDir == null ) {
			resultDir = get_scanProcessStatusKeyDir_SpecificDirectory(
					scanProcessStatusKey, ScanFileToProcessConstants.SCAN_FILES_PROCESSED_SUCCESS_BASE_DIR, uploadBaseDir );
		}
		if ( resultDir == null ) {
			resultDir = get_scanProcessStatusKeyDir_SpecificDirectory(
					scanProcessStatusKey, ScanFileToProcessConstants.SCAN_FILES_PROCESSED_FAILED_BASE_DIR, uploadBaseDir );
		}

		return resultDir;
	}
	
	/**
	 * @param scanProcessStatusKey
	 * @param scanFilesToProcessBaseDirString
	 * @param uploadBaseDir
	 * @return
	 * @throws SpectralFileFileUploadFileSystemException
	 */
	private File get_scanProcessStatusKeyDir_SpecificDirectory( 
			String scanProcessStatusKey, 
			String scanFilesToProcessBaseDirString,
			File uploadBaseDir ) throws SpectralFileFileUploadFileSystemException {
		
		File scanFilesToProcessBaseDir = new File( uploadBaseDir, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			
			if ( scanFilesToProcessBaseDirString.equals( ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR ) ) {
				String msg = "scanFilesToProcessBaseDir does not exist.  scanFilesToProcessBaseDir: " 
						+ scanFilesToProcessBaseDir.getAbsolutePath();
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException(msg);
			}
			
			//  One of new directories so may not have been created yet so just return null
			return null;  // EARLY EXIT
		}

		File scanProcessStatusKeyDir = new File( scanFilesToProcessBaseDir, scanProcessStatusKey );
		if ( ! scanProcessStatusKeyDir.exists() ) {
			if ( log.isInfoEnabled() ) {
				String msg = "scanProcessStatusKeyDir does not exist.  scanProcessStatusKeyDir: " 
						+ scanProcessStatusKeyDir.getAbsolutePath();
				log.info( msg );
			}

			return null;  // EARLY EXIT
		} 

		return scanProcessStatusKeyDir;
	}

}
