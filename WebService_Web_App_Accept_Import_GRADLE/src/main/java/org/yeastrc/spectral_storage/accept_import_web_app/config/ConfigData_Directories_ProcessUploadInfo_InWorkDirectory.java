package org.yeastrc.spectral_storage.accept_import_web_app.config;

import java.io.File;
import java.util.List;

/**
 * Config data for config file in web app Work Directory
 * 
 * The Secondary configuration for the web app
 * 
 * 
 * Singleton Instance
 *
 */
public class ConfigData_Directories_ProcessUploadInfo_InWorkDirectory {
	
	private static volatile ConfigData_Directories_ProcessUploadInfo_InWorkDirectory instance;

	//  package private constructor
	ConfigData_Directories_ProcessUploadInfo_InWorkDirectory() { }
	
	/**
	 * @return Singleton instance
	 */
	public synchronized static ConfigData_Directories_ProcessUploadInfo_InWorkDirectory getSingletonInstance() { 
		return instance; 
	}
	public static void setInstance(ConfigData_Directories_ProcessUploadInfo_InWorkDirectory instanceNew) {
		instance = instanceNew;
	}


	/**
	 * The Base Directory that the scans are written to for perm storage
	 */
	private File scanStorageBaseDirectory;

	/**
	 * The 'temp' Base directory that scan files are uploaded into
	 */
	private File tempScanUploadBaseDirectory;
	

	/**
	 * The S3 bucket that the scan data is written to for perm storage
	 */
	private String s3Bucket;

	/**
	 * The S3 region that the scan data is written to for perm storage
	 */
	private String s3Region;

	
	/**
	 * Path for passed in Filename must start with one of these values
	 */
	private List<String> submittedScanFilePathRestrictions;
	
	
	/**
	 * Java Jar File to run in the directory the scan file has been uploaded into
	 */
	private String processScanUploadJarFile;
	
	/**
	 * command (filename and path to Java executable)
	 */
	private String javaExecutable;

	/**
	 * Parameters to pass to java executable
	 */
	private List<String> javaExecutableParameters;
	
	/**
	 * Delete the uploaded scan file on successful import
	 */
	private boolean deleteUploadedScanFileOnSuccessfulImport;
	
	//////////////////////////////////
	//  Email on error config

	//  Probably used.  SMTP Server Host
	private String emailSmtpServerHost;
	
	//  Probably not used.  Special service
	private String emailWebserviceURL;
	
	private String emailFromEmailAddress;

	private List<String> emailToEmailAddresses;

	/**
	 * Only send to these email addresses if processing failed
	 */
	private List<String> emailToEmailAddresses_FailedOnly;

	private String emailMachineName;
	
	/**
	 * The Base Directory that the scans are written to for perm storage
	 * @return
	 */
	public File getScanStorageBaseDirectory() {
		return scanStorageBaseDirectory;
	}

	/**
	 * The Base Directory that the scans are written to for perm storage
	 * @param scanStorageDirectory
	 */
	public void setScanStorageBaseDirectory(File scanStorageDirectory) {
		this.scanStorageBaseDirectory = scanStorageDirectory;
	}

	/**
	 * The 'temp' Base directory that scan files are uploaded into
	 * @return
	 */
	public File getTempScanUploadBaseDirectory() {
		return tempScanUploadBaseDirectory;
	}

	/**
	 * The 'temp' Base directory that scan files are uploaded into
	 * @param tempScanUploadDirectory
	 */
	public void setTempScanUploadBaseDirectory(File tempScanUploadDirectory) {
		this.tempScanUploadBaseDirectory = tempScanUploadDirectory;
	}

	/**
	 * command (filename and path to Java executable)
	 * @return
	 */
	public String getJavaExecutable() {
		return javaExecutable;
	}

	/**
	 * command (filename and path to Java executable)
	 * @param javaExecutable
	 */
	public void setJavaExecutable(String javaExecutable) {
		this.javaExecutable = javaExecutable;
	}

	/**
	 * Java Jar File to run in the directory the scan file has been uploaded into
	 * @return
	 */
	public String getProcessScanUploadJarFile() {
		return processScanUploadJarFile;
	}

	/**
	 * Java Jar File to run in the directory the scan file has been uploaded into
	 * @param processScanUploadJarFile
	 */
	public void setProcessScanUploadJarFile(String processScanUploadJarFile) {
		this.processScanUploadJarFile = processScanUploadJarFile;
	}

	/**
	 * Delete the uploaded scan file on successful import
	 * @return
	 */
	public boolean isDeleteUploadedScanFileOnSuccessfulImport() {
		return deleteUploadedScanFileOnSuccessfulImport;
	}

	/**Delete the uploaded scan file on successful import
	 * @param deleteUploadedScanFileOnSuccessfulImportr
	 */
	public void setDeleteUploadedScanFileOnSuccessfulImport(boolean deleteUploadedScanFileOnSuccessfulImport) {
		this.deleteUploadedScanFileOnSuccessfulImport = deleteUploadedScanFileOnSuccessfulImport;
	}

	/**
	 * Parameters to pass to java executable
	 * @return
	 */
	public List<String> getJavaExecutableParameters() {
		return javaExecutableParameters;
	}

	/**
	 * Parameters to pass to java executable
	 * @param javaExecutableParameters
	 */
	public void setJavaExecutableParameters(List<String> javaExecutableParameters) {
		this.javaExecutableParameters = javaExecutableParameters;
	}

	public String getEmailWebserviceURL() {
		return emailWebserviceURL;
	}

	public void setEmailWebserviceURL(String emailWebserviceURL) {
		this.emailWebserviceURL = emailWebserviceURL;
	}

	public String getEmailSmtpServerHost() {
		return emailSmtpServerHost;
	}

	public void setEmailSmtpServerHost(String emailSmtpServerHost) {
		this.emailSmtpServerHost = emailSmtpServerHost;
	}

	public String getEmailFromEmailAddress() {
		return emailFromEmailAddress;
	}

	public void setEmailFromEmailAddress(String emailFromEmailAddress) {
		this.emailFromEmailAddress = emailFromEmailAddress;
	}

	public List<String> getEmailToEmailAddresses() {
		return emailToEmailAddresses;
	}

	public void setEmailToEmailAddresses(List<String> emailToEmailAddresses) {
		this.emailToEmailAddresses = emailToEmailAddresses;
	}

	public String getEmailMachineName() {
		return emailMachineName;
	}

	public void setEmailMachineName(String emailMachineName) {
		this.emailMachineName = emailMachineName;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	public void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	public String getS3Region() {
		return s3Region;
	}

	public void setS3Region(String s3Region) {
		this.s3Region = s3Region;
	}

	public List<String> getSubmittedScanFilePathRestrictions() {
		return submittedScanFilePathRestrictions;
	}

	public void setSubmittedScanFilePathRestrictions(List<String> submittedScanFilePathRestrictions) {
		this.submittedScanFilePathRestrictions = submittedScanFilePathRestrictions;
	}

	public List<String> getEmailToEmailAddresses_FailedOnly() {
		return emailToEmailAddresses_FailedOnly;
	}

	public void setEmailToEmailAddresses_FailedOnly(List<String> emailToEmailAddresses_FailedOnly) {
		this.emailToEmailAddresses_FailedOnly = emailToEmailAddresses_FailedOnly;
	}


		
}
