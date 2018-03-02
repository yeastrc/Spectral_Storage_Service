package org.yeastrc.spectral_storage.get_data_webapp.config;

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
public class ConfigData_ScanDataLocation_InWorkDirectory {
	
	private static final ConfigData_ScanDataLocation_InWorkDirectory instance = new ConfigData_ScanDataLocation_InWorkDirectory();

	//  private constructor
	private ConfigData_ScanDataLocation_InWorkDirectory() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigData_ScanDataLocation_InWorkDirectory getSingletonInstance() { 
		return instance; 
	}
	
	/**
	 * The Base Directory that the scan data is written to for perm storage
	 */
	private File scanStorageBaseDirectory;

	/**
	 * The S3 bucket that the scan data is written to for perm storage
	 */
	private String s3Bucket;

	/**
	 * The S3 region that the scan data is written to for perm storage
	 */
	private String s3Region;
	
	//  Setters and "clear" are package private
	
	/**
	 * 
	 */
	void clear() {
		scanStorageBaseDirectory = null;
		s3Bucket = null;
		s3Region = null;
	}

	public File getScanStorageBaseDirectory() {
		return scanStorageBaseDirectory;
	}

	void setScanStorageBaseDirectory(File scanStorageBaseDirectory) {
		this.scanStorageBaseDirectory = scanStorageBaseDirectory;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	void setS3Bucket(String s3Bucket) {
		this.s3Bucket = s3Bucket;
	}

	public String getS3Region() {
		return s3Region;
	}

	void setS3Region(String s3Region) {
		this.s3Region = s3Region;
	} 
}
