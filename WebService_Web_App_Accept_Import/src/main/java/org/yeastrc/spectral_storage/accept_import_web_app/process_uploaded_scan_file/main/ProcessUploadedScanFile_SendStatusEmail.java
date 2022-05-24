package org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.main;

import java.io.File;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;
import org.yeastrc.spectral_storage.accept_import_web_app.send_email.SendEmail;
import org.yeastrc.spectral_storage.accept_import_web_app.send_email.SendEmailDTO;

/**
 * 
 *
 */
public class ProcessUploadedScanFile_SendStatusEmail {

	private static final Logger log = LoggerFactory.getLogger(ProcessUploadedScanFile_SendStatusEmail.class);

	private ProcessUploadedScanFile_SendStatusEmail() { }
	public static ProcessUploadedScanFile_SendStatusEmail getInstance() { 
		return new ProcessUploadedScanFile_SendStatusEmail(); 
	}
	
	/**
	 * 
	 */
	public void sendProcessFailedEmail( File scanFileDirectory ) {
		
		final String successFailString = "Failed";
		sendProcessEmail( scanFileDirectory, successFailString, true /* isFailed */ );
	}

	/**
	 * 
	 */
	public void sendProcessSuccessEmail( File scanFileDirectory ) {
		
		final String successFailString = "Success";
		sendProcessEmail( scanFileDirectory, successFailString, false /* isFailed */ );
	}

	/**
	 * 
	 */
	public void sendProcessKilledEmail( File scanFileDirectory ) {
		
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
				&& ( config.getEmailSmtpServerHost() != null  ) ) {
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
