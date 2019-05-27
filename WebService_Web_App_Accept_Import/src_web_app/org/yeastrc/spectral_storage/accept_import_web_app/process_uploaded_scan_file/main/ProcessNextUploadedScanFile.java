package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappInternalException;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.run_system_command.RunSystemCommand;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.run_system_command.RunSystemCommandResponse;
import org.yeastrc.spectral_storage.accept_import_web_app.send_email.SendEmail;
import org.yeastrc.spectral_storage.accept_import_web_app.send_email.SendEmailDTO;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.a_upload_processing_status_file.UploadProcessingWriteOrUpdateStatusFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.UploadProcessingStatusFileConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.ScanFileHashToFileReadWrite;

/**
 * 
 *
 */
public class ProcessNextUploadedScanFile {

	private static final Logger log = Logger.getLogger(ProcessNextUploadedScanFile.class);


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
	public ProcessingSuccessFailKilled processNextUploadedScanFile( File scanFileDirectory ) throws Exception {
		
		if ( log.isInfoEnabled() ) {
			log.info( "processNextUploadedScanFile(..): Processing Scan File in Directory: " + scanFileDirectory );
		}
		
		ProcessingSuccessFailKilled processingSuccessFail_Result = null;
		
		UploadProcessingWriteOrUpdateStatusFile.getInstance()
		.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_STARTED, scanFileDirectory );
		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadInfo_InWorkDirectory = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();
		
		String javaExecutable = 
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getJavaExecutable();

		List<String> javaExecutableParameters =
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getJavaExecutableParameters();

		String processScanUploadJarFile = 
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getProcessScanUploadJarFile();

		String scanFileStorageBaseDirString = null;

		File scanFileStorageBaseDir = 
				configData_Directories_ProcessUploadInfo_InWorkDirectory.getScanStorageBaseDirectory();
		if ( scanFileStorageBaseDir != null ) {
			scanFileStorageBaseDirString = "--output_base_dir=" + scanFileStorageBaseDir.getCanonicalPath();
		}


		List<String> commandAndItsArgumentsAsList = new ArrayList<>( 20 );
		commandAndItsArgumentsAsList.add( javaExecutable );
		
		if ( javaExecutableParameters != null && ( ! javaExecutableParameters.isEmpty() ) ) {
			for ( String javaExecutableParameter : javaExecutableParameters ) {
				commandAndItsArgumentsAsList.add( javaExecutableParameter );
			}
		}
		
		commandAndItsArgumentsAsList.add( "-jar" );
		commandAndItsArgumentsAsList.add( processScanUploadJarFile );
		
		if ( scanFileStorageBaseDirString != null ) {
			// writing to local filesystem: output dir
			commandAndItsArgumentsAsList.add( scanFileStorageBaseDirString );
		}
		
		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() ) ) {
			commandAndItsArgumentsAsList.add( "--s3_output_bucket=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Bucket() );
		}
		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() ) ) {
			commandAndItsArgumentsAsList.add( "--s3_output_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
			commandAndItsArgumentsAsList.add( "--s3_input_region=" + configData_Directories_ProcessUploadInfo_InWorkDirectory.getS3Region() );
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

				sendProcessKilledEmail( scanFileDirectory );
				
				processingSuccessFail_Result = ProcessingSuccessFailKilled.KILLED;
				
			} else {
				if ( ! runSystemCommandResponse.isCommandSuccessful() ) {
					log.error( "command failed: exit code: "
							+ runSystemCommandResponse.getCommandExitCode()
							+ ", command: "
							+ commandAndItsArgumentsAsList
							+ ", scanFileDirectory:  " + scanFileDirectory.getCanonicalPath() );
					
					UploadProcessingWriteOrUpdateStatusFile.getInstance()
					.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, scanFileDirectory );

					sendProcessFailedEmail( scanFileDirectory );

					processingSuccessFail_Result = ProcessingSuccessFailKilled.FAIL;
					
				} else {
					
					String scanFileHashKey =
							ScanFileHashToFileReadWrite.getInstance()
							.readScanFileHashFromInProcessFile( scanFileDirectory );
					
					ScanFileHashToFileReadWrite.getInstance()
					.writeScanFileHashToFinalHashKeyFile( scanFileHashKey, scanFileDirectory );
					
					UploadProcessingWriteOrUpdateStatusFile.getInstance()
					.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_SUCCESSFUL, scanFileDirectory );

					sendProcessSuccessEmail( scanFileDirectory );

					processingSuccessFail_Result = ProcessingSuccessFailKilled.SUCCESS;
				}

			}
		} catch (Throwable e) {
			log.error( "command failed: " + commandAndItsArgumentsAsList
					+ ", scanFileDirectory:  " + scanFileDirectory.getCanonicalPath() );

			UploadProcessingWriteOrUpdateStatusFile.getInstance()
			.uploadProcessingWriteOrUpdateStatusFile( UploadProcessingStatusFileConstants.STATUS_PROCESSING_FAILED, scanFileDirectory );

			sendProcessFailedEmail( scanFileDirectory );
			
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
	

	/**
	 * 
	 */
	private void sendProcessFailedEmail( File scanFileDirectory ) {
		
		final String successFailString = "Failed";
		sendProcessEmail( scanFileDirectory, successFailString, true /* isFailed */ );
	}

	/**
	 * 
	 */
	private void sendProcessSuccessEmail( File scanFileDirectory ) {
		
		final String successFailString = "Success";
		sendProcessEmail( scanFileDirectory, successFailString, false /* isFailed */ );
	}

	/**
	 * 
	 */
	private void sendProcessKilledEmail( File scanFileDirectory ) {
		
		final String successFailString = "Killed";
		sendProcessEmail( scanFileDirectory, successFailString, false /* isFailed */ );
	}
	
	/**
	 * 
	 */
	private void sendProcessEmail( File scanFileDirectory, String successFailString, boolean isFailed ) {

		try {
			if ( ! isSendEmailConfigured() ) {
				return;  // EARLY EXIT
			}

			ConfigData_Directories_ProcessUploadInfo_InWorkDirectory config = 
					ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();

			String machineNameForSubject = "";
			String machineNameForBody = "";

			if ( config.getEmailMachineName() != null ) {
				machineNameForSubject = "Machine name: " + config.getEmailMachineName() + ".  ";
				machineNameForBody = "Machine name: " + config.getEmailMachineName() + ".\n\n";
			}

			String emailSubject = "Processing of scan file in spectral storage app: " + successFailString + ".  "
					+ machineNameForSubject
					+ "Processing dir: " + scanFileDirectory.getAbsolutePath();

			String emailBody = "Processing of scan file in spectral storage app " + successFailString + ".\n\n"
					+ machineNameForBody
					+ "Processing dir: " + scanFileDirectory.getAbsolutePath()
					+ "\n\n";

			SendEmailDTO sendEmailDTO = new SendEmailDTO();
			sendEmailDTO.setEmailSubject( emailSubject );
			sendEmailDTO.setEmailBody( emailBody );

			sendEmailDTO.setFromEmailAddress( config.getEmailFromEmailAddress() );
			
			if ( config.getEmailToEmailAddresses() != null ) {
				for ( String toEmailAddress : config.getEmailToEmailAddresses() ) {
	
					sendEmailDTO.setToEmailAddress( toEmailAddress );
					
					sendSendEmailDTO( sendEmailDTO );
				}
			}
			
			if ( isFailed && config.getEmailToEmailAddresses_FailedOnly() != null ) {
				for ( String toEmailAddress : config.getEmailToEmailAddresses_FailedOnly() ) {
					
					sendEmailDTO.setToEmailAddress( toEmailAddress );
					
					sendSendEmailDTO( sendEmailDTO );
				}
			}
			
		} catch (Throwable t) {
			String msg = "Failed to send email";
			log.error( msg, t );
			//  Eat exception
		}
	}

	/**
	 * @return
	 */
	private boolean isSendEmailConfigured() {
		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory config = 
				ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance();
		
		if ( config.getEmailFromEmailAddress() != null 
				&& ( config.getEmailToEmailAddresses() != null
					|| config.getEmailToEmailAddresses_FailedOnly() != null )
				&& ( config.getEmailSmtpServerHost() != null 
						|| config.getEmailWebserviceURL() != null ) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param sendEmailDTO
	 */
	private void sendSendEmailDTO( SendEmailDTO sendEmailDTO ) {

		try {
			SendEmail.getInstance().sendEmail( sendEmailDTO );
		} catch (Throwable t) {
			String msg = "Failed to send email";
			log.error( msg, t );
			//  Eat exception
		}
	}
	
}
