package org.yeastrc.spectral_storage.web_app.upload_scan_file;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.web_app.exceptions.SpectralFileFileUploadFileSystemException;

/**
 * 
 *
 */
public class CreateProcessScanFileDir {

	private static final Logger log = Logger.getLogger(CreateProcessScanFileDir.class);

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
		
		long uploadKey = System.currentTimeMillis();
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
			uploadKey += uploadKeyIncrement;
			createdSubDir =
					createSubDirForUploadFileTempDir( uploadKey, scanFilesToProcessBaseDir );
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
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return null if subdir already exists
	 * @throws SpectralFileFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File createSubDirForUploadFileTempDir( long uploadKey, File uploadTempBase ) throws SpectralFileFileUploadFileSystemException, IOException {
		
		File subdir = getSubDirForUploadFileTempDir( uploadKey, uploadTempBase );
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
	 * @param uploadKey
	 * @param uploadTempBase
	 * @return
	 * @throws ProxlWebappFileUploadFileSystemException 
	 * @throws IOException 
	 */
	private File getSubDirForUploadFileTempDir( long uploadKey, File uploadTempBase ) {
		
		long currTime = System.currentTimeMillis();
		
		double randomVal = Math.random();
		if ( randomVal < 0.5 ) {
			//  Make randomVal always >= 0.5
			randomVal += 0.5;
		}
		
		long uploadKeyAddition = ( (long) ( currTime * randomVal ) );
		
		String uploadKeyString = Long.toString( uploadKey );
		int uploadKeyStringLength = uploadKeyString.length();
		
		String uploadKeyAdditionString = Long.toString( uploadKeyAddition );
		int uploadKeyAdditionStringLength = uploadKeyAdditionString.length();
		
		int uploadKeyAdditionStringOutputLength = uploadKeyStringLength - 2;
		
		if ( uploadKeyAdditionStringOutputLength > uploadKeyAdditionStringLength ) {
			uploadKeyAdditionStringOutputLength = uploadKeyAdditionStringLength;
		}
		

		String uploadKeyAdditionFinalString = uploadKeyAdditionString.substring( uploadKeyStringLength - uploadKeyAdditionStringOutputLength );


		String subdirName = ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_SUB_DIR_PREFIX 
				+ uploadKeyString + uploadKeyAdditionFinalString;
		File subdir = new File( uploadTempBase, subdirName );
		return subdir;
	}
	
}
