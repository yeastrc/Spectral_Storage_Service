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
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
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
			message = createSMTPMailMessageToSend( sendEmailDTO );
			// send the message
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
	
	/**
	 * @param sendEmailDTO
	 * @return
	 * @throws Exception 
	 */
	private MimeMessage createSMTPMailMessageToSend( SendEmailDTO sendEmailDTO ) throws Exception {
		// set the SMTP host property value
		Properties properties = System.getProperties();
		properties.put( "mail.smtp.host", ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.getSingletonInstance().getEmailSmtpServerHost() );
		// create a JavaMail session
		javax.mail.Session mSession = javax.mail.Session.getInstance(properties, null);
		// create a new MIME message
		MimeMessage message = new MimeMessage(mSession);
		// set the from address
		Address fromAddress = new InternetAddress( sendEmailDTO.getFromEmailAddress() );
		message.setFrom(fromAddress);
		// set the to address
		Address[] toAddress = InternetAddress.parse( sendEmailDTO.getToEmailAddress() );
		message.setRecipients(Message.RecipientType.TO, toAddress);
		// set the subject
		message.setSubject( sendEmailDTO.getEmailSubject() );
		message.setText( sendEmailDTO.getEmailBody() );
		return message;
	}
}
