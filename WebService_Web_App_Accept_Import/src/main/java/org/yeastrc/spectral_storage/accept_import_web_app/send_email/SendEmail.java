package org.yeastrc.spectral_storage.accept_import_web_app.send_email;

import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.config.ConfigData_Directories_ProcessUploadInfo_InWorkDirectory;

/**
 * 
 *
 */
public class SendEmail {
	
	private static final SendEmail instance = new SendEmail();
	private SendEmail() { }
	public static SendEmail getInstance() { return instance; }
	private static final Logger log = LoggerFactory.getLogger(SendEmail.class);
	
	/**
	 * @param sendEmailDTO
	 * @throws Exception
	 */
	public void sendEmail( SendEmailDTO sendEmailDTO ) throws Exception  {

		MimeMessage message = null;
		// Generate and send the email to the user.
		try {
			// set the SMTP host property value
			Properties properties = System.getProperties();
			properties.put( "mail.smtp.host", ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerHost() );


			{
				String smtpServerPort = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerPort();
				if ( StringUtils.isNotEmpty(smtpServerPort) ) {
					properties.put( "mail.smtp.port", smtpServerPort );
				}
			}


			//				properties.put("mail.smtp.timeout", SMTP_TIMEOUT);    
			//				properties.put("mail.smtp.connectiontimeout", SMTP_TIMEOUT); 


			// create a JavaMail session
			javax.mail.Session mailSession = javax.mail.Session.getInstance(properties, null);
			// create a new MIME message
			message = new MimeMessage(mailSession);
			// set the from address
			Address fromAddress = new InternetAddress( sendEmailDTO.getFromEmailAddress() );
			message.setFrom(fromAddress);
			// set the to address
			Address[] toAddress = InternetAddress.parse( sendEmailDTO.getToEmailAddress() );
			message.setRecipients(Message.RecipientType.TO, toAddress);
			// set the subject
			message.setSubject( sendEmailDTO.getEmailSubject() );
			message.setText( sendEmailDTO.getEmailBody() );
			
			// send the message

			Transport mailTransport = null;
			try {
				mailTransport = mailSession.getTransport("smtp");
				
				String smtpAuthUsername = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerAuthUsername();
				String smtpAuthPassword = ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerAuthPassword();
				
				if ( StringUtils.isNotEmpty(smtpAuthUsername) && StringUtils.isNotEmpty(smtpAuthPassword) ) {
					//  YES SMTP Username/Password
					mailTransport.connect( smtpAuthUsername, smtpAuthPassword );
				} else {
					//  NO SMTP Username/Password
					mailTransport.connect();	
				}
				
				message.saveChanges();      // don't forget this
				
				mailTransport.sendMessage(message, message.getAllRecipients());
				
			} finally {
				if ( mailTransport != null ) {
					mailTransport.close();
				}
			}
			
			Transport.send(message);
		}
		catch (AddressException e) {
			// Invalid email address format
			//				errors.add("email", new ActionMessage("error.resetpassword.sendmailerror"));
			log.warn( "AddressException: to email address: " + sendEmailDTO.getToEmailAddress(), e );
			throw e; 
		}
		catch (SendFailedException e) {
			// Invalid email address format
			log.error( "SendFailedException: to email address: " + sendEmailDTO.getToEmailAddress()
			+ ", Smtp Server Host: " + ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerHost(), e );
			throw e; 
		}
		catch (MessagingException e) {
			// Invalid email address format
			log.error( "MessagingException: to email address: " + sendEmailDTO.getToEmailAddress()
			+ ", Smtp Server Host: " + ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerHost(), e );
			throw e; 
		}
		catch (Exception e) {
			// Invalid email address format
			log.error( "Exception: to email address: " + sendEmailDTO.getToEmailAddress()
			+ ", Smtp Server Host: " + ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerHost(), e );
			throw e; 
		}
	}
	
}
