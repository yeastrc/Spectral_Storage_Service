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
	 * The 'temp' Base directory that scan files are uploaded into
	 */
	private File tempScanUploadBaseDirectory;
	

	/**
	 * The Base Directory that the importer writes the data files to 
	 * before either moving them to under scanStorageBaseDirectory or copying them to S3
	 * 
	 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
	 */
	private File importerTempOutputBaseDirectory;

	/**
	 * The Base Directory that the scans are written to for perm storage
	 */
	private File scanStorageBaseDirectory;

	/**
	 * The Base Directory that the Old scan Files are moved to When there is a new Version of the File Format
	 * Only applicable when scanStorageBaseDirectory is populated.
	 */
	private File backupOldBaseDirectory;
	

	// AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.
	
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
	
	/**
	 * Max Number of Import execution directories to keep for Successful Import
	 * 
	 * Has a default.  Maybe 2.
	 */
	private int max_ImportScanFilesToKeep_SuccessfulImport;
	
	/**
	 * Max Number of Import execution directories to keep for Failed Import
	 * 
	 * Has a default.  Maybe 2.
	 */
	private int max_ImportScanFilesToKeep_FailedImport;

	/**
	 * Max Number of Days to keep Import execution directories for Successful Import
	 * 
	 * Has a default.  Maybe 2.
	 */
	private int max_DaysToKeep_ImportScanFiles_SuccessfulImport;

	/**
	 * Max Number of Days to keep Import execution directories for Failed Import
	 * 
	 * Has a default.  Maybe 2.
	 */
	private int max_DaysToKeep_ImportScanFiles_FailedImport;
	
	
	
	
	//////////////////////////////////
	//  Email on error config

	//  Probably used.  SMTP Server Host
	private String emailSmtpServerHost;
	
	private String emailFromEmailAddress;

	private List<String> emailToEmailAddresses;

	/**
	 * Only send to these email addresses if processing failed
	 */
	private List<String> emailToEmailAddresses_FailedOnly;

	private String emailMachineName;
	
	
	//////////////////////////////////////////////////

	/**
	 * The Base Directory that the importer writes the data files to 
	 * before either moving them to under scanStorageBaseDirectory or copying them to S3
	 * 
	 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
	 * 
	 * @return
	 */
	public File getImporterTempOutputBaseDirectory() {
		return importerTempOutputBaseDirectory;
	}

	/**
	 * The Base Directory that the importer writes the data files to 
	 * before either moving them to under scanStorageBaseDirectory or copying them to S3
	 * 
	 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
	 * 
	 * @param importerTempOutputBaseDirectory
	 */
	public void setImporterTempOutputBaseDirectory(File importerTempOutputBaseDirectory) {
		this.importerTempOutputBaseDirectory = importerTempOutputBaseDirectory;
	}

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
	 * The Base Directory that the Old scan Files are moved to When there is a new Version of the File Format
	 * @return
	 */
	public File getBackupOldBaseDirectory() {
		return backupOldBaseDirectory;
	}
	/**
	 * The Base Directory that the Old scan Files are moved to When there is a new Version of the File Format
	 * @param backupOldBaseDirectory
	 */
	public void setBackupOldBaseDirectory(File backupOldBaseDirectory) {
		this.backupOldBaseDirectory = backupOldBaseDirectory;
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

	public int getMax_ImportScanFilesToKeep_SuccessfulImport() {
		return max_ImportScanFilesToKeep_SuccessfulImport;
	}

	public void setMax_ImportScanFilesToKeep_SuccessfulImport(
			int max_ImportScanFilesToKeep_SuccessfulImport) {
		this.max_ImportScanFilesToKeep_SuccessfulImport = max_ImportScanFilesToKeep_SuccessfulImport;
	}

	public int getMax_ImportScanFilesToKeep_FailedImport() {
		return max_ImportScanFilesToKeep_FailedImport;
	}

	public void setMax_ImportScanFilesToKeep_FailedImport(
			int max_ImportScanFilesToKeep_FailedImport) {
		this.max_ImportScanFilesToKeep_FailedImport = max_ImportScanFilesToKeep_FailedImport;
	}

	public int getMax_DaysToKeep_ImportScanFiles_SuccessfulImport() {
		return max_DaysToKeep_ImportScanFiles_SuccessfulImport;
	}

	public void setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(
			int max_DaysToKeep_ImportScanFiles_SuccessfulImport) {
		this.max_DaysToKeep_ImportScanFiles_SuccessfulImport = max_DaysToKeep_ImportScanFiles_SuccessfulImport;
	}

	public int getMax_DaysToKeep_ImportScanFiles_FailedImport() {
		return max_DaysToKeep_ImportScanFiles_FailedImport;
	}

	public void setMax_DaysToKeep_ImportScanFiles_FailedImport(
			int max_DaysToKeep_ImportScanFiles_FailedImport) {
		this.max_DaysToKeep_ImportScanFiles_FailedImport = max_DaysToKeep_ImportScanFiles_FailedImport;
	}
		
}
