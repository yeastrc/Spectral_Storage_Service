package org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_compute_api_key_store_in_file;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.background_thread.ProcessScanFile_Thread_Container;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.import_scan_filename_local_disk.ImportScanFilename_LocalDisk;
import org.yeastrc.spectral_storage.accept_import_web_app.process_import_request_api_key_value_in_file.ProcessImportRequest_APIKey_Value_InFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ComputeFromScanFileContentHashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;

/**
 * Compute the API Key for the Scan File and store in the file
 *
 */
public class Compute_APIKey_Value_StoreInFile_SingleProcessingDir {

	private static final Logger log = LoggerFactory.getLogger( Compute_APIKey_Value_StoreInFile_SingleProcessingDir.class );

	//  private constructor
	private Compute_APIKey_Value_StoreInFile_SingleProcessingDir() { }
	
	/**
	 * @return newly created instance
	 */
	public static Compute_APIKey_Value_StoreInFile_SingleProcessingDir getInstance() { 
		return new Compute_APIKey_Value_StoreInFile_SingleProcessingDir(); 
	}
	
	private volatile boolean shutdownReceived = false;
	
	public void shutdown() {
		shutdownReceived = true;
	}
	
	/**
	 * @param scanFileProcessingDir
	 * @throws Exception
	 */
	public void compute_APIKey_Value_StoreInFile_SingleProcessingDir( File scanFileProcessingDir ) throws Exception {
		
		//  TODO  Update this for Scan File on S3

		if ( StringUtils.isNotEmpty( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getS3Bucket() ) ) {

			//  Skip this option for now.  Additional configuration/init of class S3_AWS_InterfaceObjectHolder is required to use this.
			//									For configuration/init, need to handle the "Reload" option in the webapp web page
			
			//  S3 so directly trigger running Process Scan File process
			
			try {
				//  Create status file for pending
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( 
						UploadProcessingStatusFileConstants.STATUS_PENDING, 
						scanFileProcessingDir,
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
			} catch ( Exception e ) {
				String msg = "Failed to create status file, scanFileProcessingDir: " + scanFileProcessingDir.getAbsolutePath();
				log.error( msg, e );
				throw new SpectralStorageProcessingException( msg, e );
			}
					
			//  Awaken the thread that will process the process scan file directory 
			ProcessScanFile_Thread_Container.getSingletonInstance().awakenToProcessAScanFile();
			
			return;  // EARLY EXIT
		}
		
		compute_APIKey_Value_ScanFileLocalDisk( scanFileProcessingDir );

		//  API Key computed
		
		ProcessImportRequest_APIKey_Value_InFile.getInstance().
		processImportRequest_APIKey_Value_InFile( scanFileProcessingDir );
	}
	

	/**
	 * Called if Scan File on Local disk
	 * 
	 * @param scanFileProcessingDir
	 * @throws Exception
	 */
	public void compute_APIKey_Value_ScanFileLocalDisk( File scanFileProcessingDir ) throws Exception {

		//  Find the scan file 
		String scanFilename = ImportScanFilename_LocalDisk.getInstance().getImportScanFilename_LocalDisk( scanFileProcessingDir );
		
		File scanFile = new File( scanFileProcessingDir, scanFilename );

		
		Compute_Hashes compute_Hashes = Compute_File_Hashes.getInstance().compute_File_Hashes( scanFile );
		
		if ( shutdownReceived ) {
			return;
		}

		String apiKey_String = 
				ScanFileAPIKey_ComputeFromScanFileContentHashes.getInstance()
				.scanFileAPIKey_ComputeFromScanFileContentHashes( compute_Hashes );

		if ( shutdownReceived ) {
			return;
		}
		
		//  If get here and shutdown() called, have 0.5 seconds of join() on this thread to write the file properly to disk

		ScanFileAPIKey_ToFileReadWrite.getInstance().writeScanFileHashToInProcessFileInDir( apiKey_String, scanFileProcessingDir );

	}
}
