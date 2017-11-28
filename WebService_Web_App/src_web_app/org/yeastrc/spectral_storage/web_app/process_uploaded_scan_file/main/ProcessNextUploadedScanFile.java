package org.yeastrc.spectral_storage.web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.ScanFileHashToFileReadWrite;
import org.yeastrc.spectral_storage.web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.web_app.process_uploaded_scan_file.run_system_command.RunSystemCommand;
import org.yeastrc.spectral_storage.web_app.process_uploaded_scan_file.run_system_command.RunSystemCommandResponse;

/**
 * 
 *
 */
public class ProcessNextUploadedScanFile {

	private static final Logger log = Logger.getLogger(ProcessNextUploadedScanFile.class);


	private static final String CMD_LINE_PARAM_DELETE_ON_SUCCESS = "--delete-scan-file-on-successful-processing";

	
	
	private volatile boolean shutdownRequested = false;
	private volatile RunSystemCommand runSystemCommand;

	private ProcessNextUploadedScanFile() { }
	public static ProcessNextUploadedScanFile getInstance() { 
		return new ProcessNextUploadedScanFile(); 
	}
	

	/**
	 * awaken thread to process request, calls "notify()"
	 */
	public void awaken() {

		if ( log.isDebugEnabled() ) {
			log.debug("awaken() called:  " );
		}

		synchronized (this) {
			notify();
		}
	}


	/**
	 * Called on a separate thread when a shutdown request comes from the operating system.
	 * If this is not heeded, the process may be killed by the operating system after some time has passed ( controlled by the operating system )
	 */
	public void shutdown() {
		log.error( "shutdown() called. Calling runSystemCommand.shutdown() then calling awaken()");
		shutdownRequested = true;
		try {
			if ( runSystemCommand != null ) {
				runSystemCommand.shutdown();
			}
		} catch ( NullPointerException e ) {
			//  Eat the NullPointerException since that meant that nothing had to be done.
		}
		log.error( "shutdown() called. Called runSystemCommand.shutdown() Now calling awaken()");
		awaken();
	}
	
	/**
	 * Process all uploaded scan files that have not been fully processed
	 * 
	 * Return after processing all uploaded scan files or shutdown() has been called
	 * @throws Exception 
	 */
	public void processNextUploadedScanFile( File scanFileDirectory ) throws Exception {
		
		UploadProcessingWriteOrUpdateStatusFile.getInstance()
		.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_STARTED, scanFileDirectory );
		
		String javaExecutable = 
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getJavaExecutable();

		List<String> javaExecutableParameters =
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getJavaExecutableParameters();

		String processScanUploadJarFile = 
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getProcessScanUploadJarFile();

		File scanFileStorageBaseDir = 
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getScanStorageBaseDirectory();
		
		String scanFileStorageBaseDirString = scanFileStorageBaseDir.getCanonicalPath();


		List<String> commandAndItsArgumentsAsList = new ArrayList<>( 20 );
		commandAndItsArgumentsAsList.add( javaExecutable );
		
		if ( javaExecutableParameters != null && ( ! javaExecutableParameters.isEmpty() ) ) {
			for ( String javaExecutableParameter : javaExecutableParameters ) {
				commandAndItsArgumentsAsList.add( javaExecutableParameter );
			}
		}
		
		commandAndItsArgumentsAsList.add( "-jar" );
		commandAndItsArgumentsAsList.add( processScanUploadJarFile );
		
		//  output dir
		commandAndItsArgumentsAsList.add( scanFileStorageBaseDirString );
		
		if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {
			//  Configured to delete uploaded scan file on successful import so pass to import program
			commandAndItsArgumentsAsList.add( CMD_LINE_PARAM_DELETE_ON_SUCCESS );
		}

		
		String filenameToWriteSysoutTo = "processScanUploadCommand.sysout.txt";
		String filenameToWriteSyserrTo = "processScanUploadCommand.syserr.txt";
		File fileToWriteSysoutTo = new File( scanFileDirectory, filenameToWriteSysoutTo );
		File fileToWriteSyserrTo = new File( scanFileDirectory, filenameToWriteSyserrTo );
		runSystemCommand = RunSystemCommand.getInstance();
		try {
			RunSystemCommandResponse runSystemCommandResponse = 
					runSystemCommand.runCmd( 
							commandAndItsArgumentsAsList, 
							scanFileDirectory /* dirToRunCommandIn*/, 
							fileToWriteSysoutTo /* fileToWriteSysoutTo*/,
							fileToWriteSyserrTo /* fileToWriteSyserrTo*/,
							false /* throwExceptionOnCommandFailure */ );
			if ( runSystemCommandResponse.isShutdownRequested() ) {
				log.warn( "command was aborted for run importer program shutdown: " + commandAndItsArgumentsAsList
						+ ", scanFileDirectory:  " + scanFileDirectory.getCanonicalPath() );
				
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED, scanFileDirectory );

			} else {
				if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
					log.error( "command failed: exit code: "
							+ runSystemCommandResponse.getCommandExitCode()
							+ ", command: "
							+ commandAndItsArgumentsAsList
							+ ", scanFileDirectory:  " + scanFileDirectory.getCanonicalPath() );
					
					UploadProcessingWriteOrUpdateStatusFile.getInstance()
					.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, scanFileDirectory );

				} else {
					
					String scanFileHashKey =
							ScanFileHashToFileReadWrite.getInstance()
							.readScanFileHashFromInProcessFile( scanFileDirectory );
					
					ScanFileHashToFileReadWrite.getInstance()
					.writeScanFileHashToFinalHashKeyFile( scanFileHashKey, scanFileDirectory );
					
					UploadProcessingWriteOrUpdateStatusFile.getInstance()
					.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL, scanFileDirectory );

				}

			}
		} catch (Throwable e) {
			log.error( "command failed: " + commandAndItsArgumentsAsList
					+ ", scanFileDirectory:  " + scanFileDirectory.getCanonicalPath() );

			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, scanFileDirectory );

			throw new Exception( e );
			
		} finally {
			runSystemCommand = null;
			
		}
		
	}

	
}