package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.import_processing_status_file__read_write.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.run_system_command.RunSystemCommand;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.run_system_command.RunSystemCommandResponse;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing.ScanFileAPIKey_ToFileReadWrite;

/**
 * 
 *
 */
public class ProcessNextUploadedScanFile {

	private static final Logger log = LoggerFactory.getLogger(ProcessNextUploadedScanFile.class);


	private static final String CMD_LINE_PARAM_DELETE_ON_SUCCESS = "--delete-scan-file-on-successful-processing";

	public enum ProcessingSuccessFailKilled { SUCCESS, FAIL, KILLED }
	
//	private volatile boolean shutdownRequested = false;
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
//		shutdownRequested = true;
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
	 * Process next uploaded scan file
	 * 
	 * Return after processing next uploaded scan file or shutdown() has been called
	 * @throws Exception 
	 */
	public ProcessingSuccessFailKilled processNextUploadedScanFile( File importScanFileProcsesingDirectory ) throws Exception {
		
		if ( log.isInfoEnabled() ) {
			log.info( "processNextUploadedScanFile(..): Processing Scan File in Directory: " + importScanFileProcsesingDirectory );
		}
		
		ProcessingSuccessFailKilled processingSuccessFail_Result = null;
		
		UploadProcessingWriteOrUpdateStatusFile.getInstance()
		.uploadProcessingWriteOrUpdateStatusFile( 
				UploadProcessingStatusFileConstants.STATUS_PROCESSING_STARTED, 
				importScanFileProcsesingDirectory,
				UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );
		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadInfo_InWorkDirectory = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();
		
		String javaExecutable = 
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getJavaExecutable();

		List<String> javaExecutableParameters =
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getJavaExecutableParameters();

		String processScanUploadJarFile = 
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getProcessScanUploadJarFile();

		List<String> commandAndItsArgumentsAsList = new ArrayList<>( 20 );
		commandAndItsArgumentsAsList.add( javaExecutable );
		
		if ( javaExecutableParameters != null && ( ! javaExecutableParameters.isEmpty() ) ) {
			for ( String javaExecutableParameter : javaExecutableParameters ) {
				commandAndItsArgumentsAsList.add( javaExecutableParameter );
			}
		}
		
		commandAndItsArgumentsAsList.add( "-jar" );
		commandAndItsArgumentsAsList.add( processScanUploadJarFile );

		{
			File scanFileStorageBaseDir = configData_Directories_ProcessUploadInfo_InWorkDirectory.getScanStorageBaseDirectory();
			if ( scanFileStorageBaseDir != null ) {
				String scanFileStorageBaseDirString = "--output_base_dir=" + scanFileStorageBaseDir.getCanonicalPath();
				// writing to local filesystem: output dir
				commandAndItsArgumentsAsList.add( scanFileStorageBaseDirString );
			}
		}
		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() ) ) {
			commandAndItsArgumentsAsList.add( "--s3_output_bucket=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() );
		}
		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() ) ) {
			commandAndItsArgumentsAsList.add( "--s3_output_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
			commandAndItsArgumentsAsList.add( "--s3_input_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
		}

		{
			File importerTempOutputBaseDirectoryFile = configData_Directories_ProcessUploadInfo_InWorkDirectory.getImporterTempOutputBaseDirectory();
			if ( importerTempOutputBaseDirectoryFile != null ) {
				String importerTempOutputBaseDirectoryString = "--temp_output_base_dir=" + importerTempOutputBaseDirectoryFile.getCanonicalPath();
				// importer temp output dir to write files to
				commandAndItsArgumentsAsList.add( importerTempOutputBaseDirectoryString );
			}
		}
		{

			File backupOldBaseDir = 
					configData_Directories_ProcessUploadInfo_InWorkDirectory.getBackupOldBaseDirectory();
			if ( backupOldBaseDir != null ) {
				String backupOldBaseDirString = "--backup_old_base_dir=" + backupOldBaseDir.getCanonicalPath();
				// writing to local filesystem: backup old dir
				commandAndItsArgumentsAsList.add( backupOldBaseDirString );
			}
		}

		if ( ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().isDeleteUploadedScanFileOnSuccessfulImport() ) {
			//  Configured to delete uploaded scan file on successful import so pass to import program
			commandAndItsArgumentsAsList.add( CMD_LINE_PARAM_DELETE_ON_SUCCESS );
		}
		
		if ( log.isInfoEnabled() ) {
			String commandAndItsArgumentsAsList_Space_Delimited = StringUtils.join( commandAndItsArgumentsAsList, " " );
			log.info( "Command to run to import scan file (space delimited): " + commandAndItsArgumentsAsList_Space_Delimited );
		}

		
		String filenameToWriteSysoutTo = "processScanUploadCommand.sysout.txt";
		String filenameToWriteSyserrTo = "processScanUploadCommand.syserr.txt";
		File fileToWriteSysoutTo = new File( importScanFileProcsesingDirectory, filenameToWriteSysoutTo );
		File fileToWriteSyserrTo = new File( importScanFileProcsesingDirectory, filenameToWriteSyserrTo );
		runSystemCommand = RunSystemCommand.getInstance();
		try {
			RunSystemCommandResponse runSystemCommandResponse = 
					runSystemCommand.runCmd( 
							commandAndItsArgumentsAsList, 
							importScanFileProcsesingDirectory /* dirToRunCommandIn*/, 
							fileToWriteSysoutTo /* fileToWriteSysoutTo*/,
							fileToWriteSyserrTo /* fileToWriteSyserrTo*/,
							false /* throwExceptionOnCommandFailure */ );
			
			{  //  Write Importer Exit code to file
				int commandExitCode = runSystemCommandResponse.getCommandExitCode();
				
				File codeFile = new File( importScanFileProcsesingDirectory, ScanFileToProcessConstants.IMPORTER_PROGRAM_EXIT_CODE_FILENAME );
				
				try ( BufferedWriter writer = new BufferedWriter( new FileWriter(codeFile ) ) ) {
					writer.write( "Importer Program Exit Code: " );
					writer.write( String.valueOf( commandExitCode ) );
					writer.newLine();
				}
			}
			
			if ( runSystemCommandResponse.isShutdownRequested() ) {
				log.warn( "command was aborted for run importer program shutdown: " + commandAndItsArgumentsAsList
						+ ", scanFileDirectory:  " + importScanFileProcsesingDirectory.getCanonicalPath() );
				
				UploadProcessingWriteOrUpdateStatusFile.getInstance()
				.uploadProcessingWriteOrUpdateStatusFile( 
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_KILLED, 
						importScanFileProcsesingDirectory,
						UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );

				ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessKilledEmail( importScanFileProcsesingDirectory );
				
				processingSuccessFail_Result = ProcessingSuccessFailKilled.KILLED;
				
			} else {
				if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
					log.error( "command failed: exit code: "
							+ runSystemCommandResponse.getCommandExitCode()
							+ ", command: "
							+ commandAndItsArgumentsAsList
							+ ", scanFileDirectory:  " + importScanFileProcsesingDirectory.getCanonicalPath() );
					
					UploadProcessingWriteOrUpdateStatusFile.getInstance()
					.uploadProcessingWriteOrUpdateStatusFile( 
							UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, 
							importScanFileProcsesingDirectory,
							UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );

					ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessFailedEmail( importScanFileProcsesingDirectory );

					processingSuccessFail_Result = ProcessingSuccessFailKilled.FAIL;
					
				} else {
					
					String scanFileHashKey =
							ScanFileAPIKey_ToFileReadWrite.getInstance()
							.readScanFileHashFromInProcessFile( importScanFileProcsesingDirectory );
					
					ProcessUploadedScanFile_Final_OnSuccess.getInstance().processUploadedScanFile_Final_OnSuccess( scanFileHashKey, importScanFileProcsesingDirectory );

					processingSuccessFail_Result = ProcessingSuccessFailKilled.SUCCESS;
				}

			}
		} catch (Throwable e) {
			log.error( "command failed: " + commandAndItsArgumentsAsList
					+ ", scanFileDirectory:  " + importScanFileProcsesingDirectory.getCanonicalPath() );

			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( 
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, 
					importScanFileProcsesingDirectory,
					UploadProcessingStatusFileConstants.STATUS_PROCESSING_CALLER_LABEL__ACCEPT_IMPORT_WEBAPP );

			ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessFailedEmail( importScanFileProcsesingDirectory );
			
			throw new SpectralFileWebappInternalException( e );
			
		} finally {
			runSystemCommand = null;
			
		}
		
		if ( processingSuccessFail_Result == null ) {
			//  Assume fail
			processingSuccessFail_Result = ProcessingSuccessFailKilled.FAIL;
		}
		
		return processingSuccessFail_Result;
	}
	

	
}
