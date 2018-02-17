package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingReadStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;

/**
 * 
 *
 */
public class GetNextScanFileDirToProcess {

	private static final Logger log = Logger.getLogger(GetNextScanFileDirToProcess.class);

	private GetNextScanFileDirToProcess() { }
	public static GetNextScanFileDirToProcess getInstance() { 
		return new GetNextScanFileDirToProcess(); 
	}
	
	/**
	 * Get oldest directory with status of pending 
	 * 
	 * @return directory to process, null if no directory to process
	 * @throws Exception 
	 */
	public File getNextScanFileDirToProcess() throws Exception {
		
		File tempScanUploadBaseDirectoryFile =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getTempScanUploadBaseDirectory();
		
		//  Get the File object for the Base Subdir used to store the scan file for processing 
		String scanFilesToProcessBaseDirString = ScanFileToProcessConstants.SCAN_FILES_TO_PROCESS_BASE_DIR;
		
		File scanFilesToProcessBaseDir = new File( tempScanUploadBaseDirectoryFile, scanFilesToProcessBaseDirString );
		if ( ! scanFilesToProcessBaseDir.exists() ) {
			//  Scan Files Uploaded Base Dir does not exist.  Nothing uploaded yet
			return null;  //  EARLY EXIT
		}
		
		//  Get oldest directory with a scan file to process 
		
		File[] scanFilesToProcessBaseDirContents = scanFilesToProcessBaseDir.listFiles();
		
		//  sort oldest to newest
		Arrays.sort( scanFilesToProcessBaseDirContents, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				if (  o1.lastModified() < o2.lastModified() ) {
					return -1;
				}
				if (  o1.lastModified() > o2.lastModified() ) {
					return 1;
				}
				return 0;
			}
		});
		
		for ( File scanFileDir : scanFilesToProcessBaseDirContents ) {
			if ( isNextScanFileDirToProcess( scanFileDir ) ) {
				return scanFileDir;  //  EARLY EXIT
			}
		}
		
		return null;

	}
	
	/**
	 * @param scanFilesToProcessBaseDirContent
	 * @return
	 * @throws Exception 
	 */
	private boolean isNextScanFileDirToProcess( File scanFileDir ) throws Exception {
		
		if ( ! scanFileDir.isDirectory() ) {
			//  Must be a directory
			return false;  //  EARLY EXIT
		}
		
		String status =
				UploadProcessingReadStatusFile.getInstance().uploadProcessingReadStatusFile( scanFileDir );
		
		if ( status == null ) {
			//  No status file
			
		}
		
		if ( UploadProcessingStatusFileConstants.STATUS_PENDING.equals( status ) ) {
			//  status is pending so process it next
			return true;
		}
		
		return false;
	}
	
}
