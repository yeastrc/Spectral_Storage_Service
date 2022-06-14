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
	 * 
	 *
	 */
	public static class ProcessNextUploadedScanFile_Params {
		
		private File importScanFileProcsesingDirectory;
		private int scan_read_max_batch_size;
		private String converter_base_url;
		private String input_scan_filename;
		
		public void setImportScanFileProcsesingDirectory(File importScanFileProcsesingDirectory) {
			this.importScanFileProcsesingDirectory = importScanFileProcsesingDirectory;
		}
		public void setConverter_base_url(String converter_base_url) {
			this.converter_base_url = converter_base_url;
		}
		public void setInput_scan_filename(String input_scan_filename) {
			this.input_scan_filename = input_scan_filename;
		}
		public void setScan_read_max_batch_size(int scan_read_max_batch_size) {
			this.scan_read_max_batch_size = scan_read_max_batch_size;
		}
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
	public ProcessingSuccessFailKilled processNextUploadedScanFile( ProcessNextUploadedScanFile_Params methodParams ) throws Exception {
			
		File importScanFileProcsesingDirectory = methodParams.importScanFileProcsesingDirectory;
		int scan_read_max_batch_size = methodParams.scan_read_max_batch_size;
		String converter_base_url = methodParams.converter_base_url;
		String input_scan_filename = methodParams.input_scan_filename;
		
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
			String scan_read_max_batch_size_CommandString = "--scan_read_max_batch_size=" + scan_read_max_batch_size;
			commandAndItsArgumentsAsList.add( scan_read_max_batch_size_CommandString );
		}
		{
			String converter_base_url_CommandString = "--converter_base_url=" + converter_base_url;
			commandAndItsArgumentsAsList.add( converter_base_url_CommandString );
		}
		{
			String input_scan_filename_CommandString = "--input_scan_filename=" + input_scan_filename;
			commandAndItsArgumentsAsList.add( input_scan_filename_CommandString );
		}

		{
			File scanFileStorageBaseDir = configData_Directories_ProcessUploadInfo_InWorkDirectory.getScanStorageBaseDirectory();
			if ( scanFileStorageBaseDir != null ) {
				String scanFileStorageBaseDirString = "--output_base_dir=" + scanFileStorageBaseDir.getCanonicalPath();
				// writing to local filesystem: output dir
				commandAndItsArgumentsAsList.add( scanFileStorageBaseDirString );
			}
		}

		//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

//		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() ) ) {
//			commandAndItsArgumentsAsList.add( "--s3_output_bucket=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() );
//		}
//		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() ) ) {
//			commandAndItsArgumentsAsList.add( "--s3_output_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
//			commandAndItsArgumentsAsList.add( "--s3_input_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
//		}

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

//				ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessKilledEmail( importScanFileProcsesingDirectory );
				
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

//					ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessFailedEmail( importScanFileProcsesingDirectory );

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

//			ProcessUploadedScanFile_SendStatusEmail.getInstance().sendProcessFailedEmail( importScanFileProcsesingDirectory );
			
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
