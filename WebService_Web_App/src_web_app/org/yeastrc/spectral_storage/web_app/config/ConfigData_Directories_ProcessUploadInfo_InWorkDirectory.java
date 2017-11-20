package org.yeastrc.spectral_storage.web_app.config;

import java.io.File;

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
	
	private static final ConfigData_Directories_ProcessUploadInfo_InWorkDirectory instance = new ConfigData_Directories_ProcessUploadInfo_InWorkDirectory();

	//  private constructor
	private ConfigData_Directories_ProcessUploadInfo_InWorkDirectory() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigData_Directories_ProcessUploadInfo_InWorkDirectory getSingletonInstance() { 
		return instance; 
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
	 * Java Jar File to run in the directory the scan file has been uploaded into
	 */
	private String processScanUploadJarFile;
	
	/**
	 * command (filename and path to Java executable)
	 */
	private String javaExecutable;
	
	/**
	 * Delete the uploaded scan file on successful import
	 */
	private boolean deleteUploadedScanFileOnSuccessfulImport;
		

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

		
}
