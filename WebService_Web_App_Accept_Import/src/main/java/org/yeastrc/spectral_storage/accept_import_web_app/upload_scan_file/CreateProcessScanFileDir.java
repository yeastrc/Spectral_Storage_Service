package org.yeastrc.spectral_storage.accept_import_web_app.upload_scan_file;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileFileUploadFileSystemException;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;

/**
 * 
 *
 */
public class CreateProcessScanFileDir {

	private static final Logger log = LoggerFactory.getLogger(CreateProcessScanFileDir.class);

	//  private constructor
	private CreateProcessScanFileDir() { }
	
	/**
	 * @return newly created instance
	 */
	public static CreateProcessScanFileDir getInstance() { 
		return new CreateProcessScanFileDir(); 
	}
	
	/**
	 * @return
	 * @throws SpectralFileFileUploadFileSystemException 
	 * @throws IOException 
	 */
	public File createDirToProcessScanFile() throws SpectralFileFileUploadFileSystemException, IOException {
		
		File uploadBaseDir = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the File object for the Base Subdir used to store the scan file for processing 
		String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;
		File scanFilesToProcessBaseDir = new File( uploadBaseDir, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
//			boolean mkdirResult = 
			scanFilesToProcessBaseDir.mkdir();
		}
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			String msg = "scanFilesToProcessBaseDir does not exist after testing for it and attempting to create it.  scanFilesToProcessBaseDir: " 
					+ scanFilesToProcessBaseDir.getAbsolutePath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException(msg);
		}
		
		//  Create subdir for this specific scan file
		
		//  First part is YYYYMMDD_Hour_minute_second  Hour: 0-23 
		
		String currentDate_yyyymmdd_hhmmss = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss").format( new Date() );
		
		long processScanFileKey = System.currentTimeMillis();
		File createdSubDir = null;
		int retryCreateSubdirCount = 0;
		while ( createdSubDir == null ) {
			retryCreateSubdirCount++;
			if ( retryCreateSubdirCount > 4 ) {
				String msg = "Failed to create subdir after 4 attempts.";
				log.error( msg );
				throw new SpectralFileFileUploadFileSystemException(msg);
			}
			int uploadKeyIncrement = ( (int) ( Math.random() * 10 ) ) + 5;
			processScanFileKey += uploadKeyIncrement;
			createdSubDir =
					createSubDirForProcessScanFileDir( currentDate_yyyymmdd_hhmmss, processScanFileKey, scanFilesToProcessBaseDir );
		}
		
		//  Create a file in the directory to track the create date/time of the directory
		File createdDirFile = new File( createdSubDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_CREATE_TRACKING_FILE );
		if ( ! createdDirFile.createNewFile() ) {
			String msg = "Failed to create file in subdir: " + createdDirFile.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException(msg);
		}
		
		return createdSubDir;
	}
	

	/**
	 * @param processScanFileKey
	 * @param scanFilesToProcessBaseDir
	 * @return null if subdir already exists
	 * @throws SpectralFileFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File createSubDirForProcessScanFileDir( String currentDate_yyyymmdd_hhmmss, long processScanFileKey, File scanFilesToProcessBaseDir ) throws SpectralFileFileUploadFileSystemException, IOException {
		
		File subdir = getSubDirForUploadFileTempDir( currentDate_yyyymmdd_hhmmss, processScanFileKey, scanFilesToProcessBaseDir );
		if ( subdir.exists() ) {
			//  Subdir already exists so need new uploadKey to create unique subdir
			return null;
		}
		if ( ! subdir.mkdir() ) {
			String msg = "Failed to make temp upload subdir: " + subdir.getCanonicalPath();
			log.error( msg );
			throw new SpectralFileFileUploadFileSystemException( msg );
		}
		return subdir;
	}

	/**
	 * @param processScanFileKey
	 * @param scanFilesToProcessBaseDir
	 * @return
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File getSubDirForUploadFileTempDir( String currentDate_yyyymmdd_hhmmss, long processScanFileKey, File scanFilesToProcessBaseDir ) {
		
		long currTime = System.currentTimeMillis();
		
		double randomVal = Math.random();
		if ( randomVal < 0.5 ) {
			//  Make randomVal always >= 0.5
			randomVal += 0.5;
		}
		
		long processScanFileKeyAddition = ( (long) ( currTime * randomVal ) );
		
		String processScanFileKeyString = Long.toString( processScanFileKey );
		int processScanFileKeyStringLength = processScanFileKeyString.length();
		
		String processScanFileKeyAdditionString = Long.toString( processScanFileKeyAddition );
		int processScanFileKeyAdditionStringLength = processScanFileKeyAdditionString.length();
		
		int processScanFileKeyAdditionStringOutputLength = processScanFileKeyStringLength - 2;
		
		if ( processScanFileKeyAdditionStringOutputLength > processScanFileKeyAdditionStringLength ) {
			processScanFileKeyAdditionStringOutputLength = processScanFileKeyAdditionStringLength;
		}

		String uploadKeyAdditionFinalString = processScanFileKeyAdditionString.substring( processScanFileKeyStringLength - processScanFileKeyAdditionStringOutputLength );

		String subdirName = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_PREFIX 
				+ currentDate_yyyymmdd_hhmmss
				+ "_"
				+ processScanFileKeyString + uploadKeyAdditionFinalString;
		File subdir = new File( scanFilesToProcessBaseDir, subdirName );
		return subdir;
	}
	
}
