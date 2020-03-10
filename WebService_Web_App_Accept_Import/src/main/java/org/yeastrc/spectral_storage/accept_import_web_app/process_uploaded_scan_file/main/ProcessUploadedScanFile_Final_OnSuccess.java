package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;

import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;

/**
 * Processing when On Success (since will call from elsewhere as well)
 *
 */
public class ProcessUploadedScanFile_Final_OnSuccess {

	private ProcessUploadedScanFile_Final_OnSuccess() { }
	public static ProcessUploadedScanFile_Final_OnSuccess getInstance() { 
		return new ProcessUploadedScanFile_Final_OnSuccess(); 
	}
	
	/**
	 * @param apiKey
	 * @param importScanFileProcessingDirectory
	 * @throws Exception 
	 */
	public void processUploadedScanFile_Final_OnSuccess( String apiKey, File importScanFileProcessingDirectory ) throws Exception {

		ScanFileAPIKey_ToFileReadWrite.getInstance()
		.writeScanFileHashToFinalHashKeyFile( apiKey, importScanFileProcessingDirectory );

		UploadProcessingWriteOrUpdateStatusFile.getInstance()
		.uploadProcessingWriteOrUpdateStatusFile( 
				UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL, 
				importScanFileProcessingDirectory,
				UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );

		ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessSuccessEmail( importScanFileProcessingDirectory );
	}
}
