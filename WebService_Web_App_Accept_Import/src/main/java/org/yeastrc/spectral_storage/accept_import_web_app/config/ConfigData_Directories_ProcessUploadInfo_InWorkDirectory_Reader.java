package org.yeastrc.spectral_storage.accept_import_web_app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappConfigException;
import org.yeastrc.spectral_storage.accept_import_web_app.process_uploaded_scan_file.constants.ProcessUploadedScanFilesConstants;

/**
 * Update ConfigData_Directories_ProcessUploadCommand_InWorkDirectory with contents in config file
 *
 */
public class ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader {

	private static final Logger log = LoggerFactory.getLogger(ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader.class);
	
	private static final int MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES = 2;
	
	private static final String BOOLEAN_STRING_TRUE = "true";

	//  No Default file
//	private static String CONFIG_DEFAULTS_FILENAME = "spectral_server_accept_import_config_dirs_process_cmd_defaults.properties";
	
	private static final String CONFIG_OVERRIDES_FILENAME = "spectral_server_accept_import_config_dirs_process_cmd.properties";

	private static final String PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY = "scan.storage.base.directory";
	private static final String PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY = "temp.upload.base.directory";

	/**
	 * The Base Directory that the importer writes the data files to 
	 * before either moving them to under scanStorageBaseDirectory or copying them to S3
	 * 
	 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
	 */
	private static final String PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY = "importer.temp.output.base.directory";
	

//	#  Files moved here from 'scan.storage.base.directory' here when a new file is created for a newer File Format version
//	#     Need to be able to do simple move of files from 'scan.storage.base.directory' to this directory
//	#     Valid to not configure this. 
	private static final String PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY = "backup.old.base.directory";

//  AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

	private static final String PROPERTY_NAME__S3_BUCKET = "s3.bucket";
	private static final String PROPERTY_NAME__S3_REGION = "s3.region";
	
	private static final String PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS = "submitted.scan.file.path.restrictions";
	
	
	private static final String PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE = "process.scan.upload.jar.file";
	private static final String PROPERTY_NAME__JAVA_EXECUTABLE = "java.executable";
	private static final String PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS = "java.executable.parameters";
	
	private static final String PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT = 
			"delete.uploaded.scan.file.on.successful.import";

	private static final String PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT = 
			"max.import.scan.files.to.keep.for.successful.import";

	private static final String PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT = 
			"max.import.scan.files.to.keep.for.failed.import";

	private static final String PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT = 
			"max.days.to.keep.import.scan.files.for.successful.import";

	private static final String PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT = 
			"max.days.to.keep.import.scan.files.for.failed.import";
	

//	 * Max Number of Import execution directories to keep for Successful Import
//	 * 
//	 * Has a default.  Maybe 2.
//	 */
//	private int max_ImportScanFilesToKeep_SuccessfulImport;
//	
//	/**
//	 * Max Number of Import execution directories to keep for Failed Import
//	 * 
//	 * Has a default.  Maybe 2.
//	 */
//	private int max_ImportScanFilesToKeep_FailedImport;
//
//	/**
//	 * Max Number of Days to keep Import execution directories for Successful Import
//	 * 
//	 * Has a default.  Maybe 2.
//	 */
//	private int max_DaysToKeep_ImportScanFiles_SuccessfulImport;
//
//	/**
//	 * Max Number of Days to keep Import execution directories for Failed Import
//	 * 
//	 * Has a default.  Maybe 2.
//	 */
//	private int max_DaysToKeep_ImportScanFiles_FailedImport;
//	
//	
	
	//  Email on error config

	//  Probably used.  SMTP Server Host
	private static final String PROPERTY_NAME__EMAIL_SMTP_HOST = "email.smtp.host";

	private static final String PROPERTY_NAME__EMAIL_FROM_ADDRESS = "email.from.address";
	
	private static final String PROPERTY_NAME__EMAIL_TO_ADDRESSES = "email.to.addresses";

	private static final String PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY = "email.to.addresses.failed.only";

	private static final String PROPERTY_NAME__EMAIL_MACHINE_NAME = "email.machine.name";

	private static enum IsDefaultPropertiesFile { YES, NO }
	private static enum AllowNoPropertiesFile { YES, NO }
	
	//  private constructor
	private ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader getInstance() { 
		return new ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader(); 
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void readConfigDataInWebApp() throws Exception {
		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadCommand_InWorkDirectory = new ConfigData_Directories_ProcessUploadInfo_InWorkDirectory();
		
		//  Local Internal class
		
		InternalConfigDirectoryStrings internalConfigDirectoryStrings = new InternalConfigDirectoryStrings();
		
		InternalConfig_OptionalParams_Temp internalConfig_OptionalParams_Temp = new InternalConfig_OptionalParams_Temp();

//		processPropertiesFilename( CONFIG_DEFAULTS_FILENAME, IsDefaultPropertiesFile.YES, AllowNoPropertiesFile.NO, configData_Directories_ProcessUploadCommand_InWorkDirectory );
		
		processPropertiesFilename( 
				CONFIG_OVERRIDES_FILENAME, 
				IsDefaultPropertiesFile.NO, 
				AllowNoPropertiesFile.NO, 
				configData_Directories_ProcessUploadCommand_InWorkDirectory,
				internalConfig_OptionalParams_Temp,
				internalConfigDirectoryStrings );
		
		log.warn( "Finished processing config file '" 
				+ CONFIG_OVERRIDES_FILENAME
				+ "'." );

//		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) 
//				&& StringUtils.isNotEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Bucket() ) ) {
//			String msg = "Cannot set both properties '"
//				+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
//				+ "' and '"
//				+ PROPERTY_NAME__S3_BUCKET
//				+ "' to a value in config.";
//			log.error( msg );
//			throw new SpectralFileWebappConfigException( msg );
//		}
		
		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) ) {

			File scanStorageBaseDirectory = new File( internalConfigDirectoryStrings.scanStorageBaseDirectory );

			if ( ! ( scanStorageBaseDirectory.exists() && scanStorageBaseDirectory.isDirectory() && scanStorageBaseDirectory.canRead() ) ) {
				String msg = "!!Property '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
						+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
						+ internalConfigDirectoryStrings.scanStorageBaseDirectory;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}

			configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

			log.warn( "INFO: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' has value: " 
					+ internalConfigDirectoryStrings.scanStorageBaseDirectory );
		
		} else {

		//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

			if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Bucket() ) ) {
				String msg = "Must set One of properties '"
					+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
					+ "' and '"
					+ PROPERTY_NAME__S3_BUCKET
					+ "' to a value in config.";
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}

			log.warn( "INFO: '" + PROPERTY_NAME__S3_BUCKET + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Bucket() );
			
			//  NO S3 so if PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY not set throw Error
			
			String msg = "Must set property '"
					+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
					+ "' to a value in config.";
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
		}

		//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

		if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Region() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__S3_REGION + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Region() );
		}
		

		if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getSubmittedScanFilePathRestrictions() != null 
				&& ( !  configData_Directories_ProcessUploadCommand_InWorkDirectory.getSubmittedScanFilePathRestrictions().isEmpty() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS + "' has value(s) (comma delim): " 
					+ StringUtils.join(  configData_Directories_ProcessUploadCommand_InWorkDirectory.getSubmittedScanFilePathRestrictions(), "," ) );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS 
					+ "' has NO values or is missing.  All requests with scan filename and path will be rejected with the appropriate flag in the response.  " );
		}
		
		
		if ( StringUtils.isEmpty( internalConfigDirectoryStrings.tempScanUploadBaseDirectory ) ) {
			String msg = "Property '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY 
					+ "' in config is empty or missing";
			log.error( msg );
			throw new SpectralFileWebappConfigException( msg );
		}

		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getProcessScanUploadJarFile() ) ) {
			String msg = "Property '" + PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE 
					+ "' in config is empty or missing";
			log.error( msg );
			throw new SpectralFileWebappConfigException( msg );
		}
		
		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) ) {

			File scanStorageBaseDirectory = new File( internalConfigDirectoryStrings.scanStorageBaseDirectory );

			if ( ! ( scanStorageBaseDirectory.exists() && scanStorageBaseDirectory.isDirectory() && scanStorageBaseDirectory.canRead() ) ) {
				String msg = "!!Property '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
						+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
						+ internalConfigDirectoryStrings.scanStorageBaseDirectory;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}
			
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );
		}

		{
			File tempScanUploadBaseDirectory = new File( internalConfigDirectoryStrings.tempScanUploadBaseDirectory );
			
			if ( ! ( tempScanUploadBaseDirectory.exists() && tempScanUploadBaseDirectory.isDirectory() && tempScanUploadBaseDirectory.canRead() ) ) {
				String msg = "!!!Property '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY 
						+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
						+ internalConfigDirectoryStrings.tempScanUploadBaseDirectory;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}
	
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setTempScanUploadBaseDirectory( tempScanUploadBaseDirectory );
		}
		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.importerTempOutputBaseDirectory ) ) {

			// Optional

			File importerTempOutputBaseDirectory = new File( internalConfigDirectoryStrings.importerTempOutputBaseDirectory );
			
			if ( ! ( importerTempOutputBaseDirectory.exists() && importerTempOutputBaseDirectory.isDirectory() && importerTempOutputBaseDirectory.canRead() ) ) {
				String msg = "!!!Property '" + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY 
						+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
						+ internalConfigDirectoryStrings.importerTempOutputBaseDirectory;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}
	
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setImporterTempOutputBaseDirectory( importerTempOutputBaseDirectory );
		}

		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.backupOldBaseDirectory ) ) {

			File backupOldBaseDirectory = new File( internalConfigDirectoryStrings.backupOldBaseDirectory );

			if ( ! ( backupOldBaseDirectory.exists() && backupOldBaseDirectory.isDirectory() && backupOldBaseDirectory.canRead() ) ) {
				String msg = "!!Property '" + PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY 
						+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
						+ internalConfigDirectoryStrings.backupOldBaseDirectory;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}

			configData_Directories_ProcessUploadCommand_InWorkDirectory.setBackupOldBaseDirectory( backupOldBaseDirectory );

			log.warn( "INFO: '" + PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY + "' has value: " 
					+ internalConfigDirectoryStrings.backupOldBaseDirectory );
		}


		if ( StringUtils.isEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) ) {

			log.warn( "INFO: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' has value: " 
					+ internalConfigDirectoryStrings.scanStorageBaseDirectory );
		}
		
		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.importerTempOutputBaseDirectory ) ) {

			log.warn( "INFO: '" + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY + "' has value: " 
					+ internalConfigDirectoryStrings.importerTempOutputBaseDirectory );
		} else {
			
			log.warn( "INFO: '" + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY 
					+ "' has NO value but is optional.  A directory will be created (and used) under value for property '" 
					+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY
					+ "' which is value: "
					+ internalConfigDirectoryStrings.scanStorageBaseDirectory );
		}
		
		log.warn( "INFO: '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY + "' has value: " 
				+ internalConfigDirectoryStrings.tempScanUploadBaseDirectory );
		
		log.warn( "INFO: '" + PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE + "' has value: " 
				+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getProcessScanUploadJarFile() );

		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutable() ) ) {
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable( ProcessUploadedScanFilesConstants.JAVA_EXECUTABLE_DEFAULT );
			log.warn( "INFO: '" + PROPERTY_NAME__JAVA_EXECUTABLE + "' does NOT have a value and so will use the default of: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutable() );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__JAVA_EXECUTABLE + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutable() );
		}
		
		if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutableParameters() != null ) {
			log.warn( "INFO: '" + PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS + "' has value(s) [Space delimited]: " 
					+ StringUtils.join( configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutableParameters(), ' ' ) );
		}
		
		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailSmtpServerHost() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_SMTP_HOST + "' does NOT have a value." );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_SMTP_HOST + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailSmtpServerHost() );
		}

		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailFromEmailAddress() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_FROM_ADDRESS + "' does NOT have a value." );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_FROM_ADDRESS + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailFromEmailAddress() );
		}

		if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailToEmailAddresses() == null ) {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES + "' does NOT have a value." );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES + "' has value(s) [Comma delimited]: " 
					+ StringUtils.join( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailToEmailAddresses(), ' ' ) );
		}

		if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailToEmailAddresses_FailedOnly()== null ) {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY + "' does NOT have a value." );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY + "' has value(s) [Comma delimited]: " 
					+ StringUtils.join( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailToEmailAddresses_FailedOnly(), ' ' ) );
		}
		

		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailMachineName() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_MACHINE_NAME + "' does NOT have a value." );
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_MACHINE_NAME + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailMachineName() );
		}
		
		
		if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.isDeleteUploadedScanFileOnSuccessfulImport() ) {
			log.warn( "INFO: '" + PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
					+ "' has value: '" + BOOLEAN_STRING_TRUE
					+ "' so will delete uploaded scan file on successful import" ); 
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
					+ "' does NOT have value: '" + BOOLEAN_STRING_TRUE
					+ "' so will NOT delete uploaded scan file on successful import" ); 
		}
		
		//  MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT
		
		if ( StringUtils.isNotEmpty( internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_SuccessfulImport ) ) {
			String valueString = internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_SuccessfulImport;
			try {
				int valueInt = Integer.parseInt( valueString );
				
				if ( valueInt < 0 ) {

					valueInt = 0;
					
					log.warn( "INFO: '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT
							+ "' has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
				}
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(valueInt);
				
				log.warn( "INFO: '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT
						+ "' has value so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
				
			} catch ( Exception e ) {

				log.error( "FAILED TO PARSE Config value as Integer for property '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT
						+ "'. Value '" + valueString 
						+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
						+ " uploaded scan file processing directories on successful import" ); 
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
			}
		} else {

			log.warn( "INFO: NO Value for config property '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT
					+ "' so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on successful import" );
			
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
		}

		//  PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT
		
		if ( StringUtils.isNotEmpty( internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_FailedImport ) ) {
			String valueString = internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_FailedImport;
			try {
				int valueInt = Integer.parseInt( valueString );
				
				if ( valueInt < 0 ) {

					valueInt = 0;
					
					log.warn( "INFO: '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT
							+ "' has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
				}
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(valueInt);
				
				log.warn( "INFO: '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT
						+ "' has value so will only keep last " + valueInt + " uploaded scan file processing directories on failed import" ); 
				
			} catch ( Exception e ) {

				log.error( "FAILED TO PARSE Config value as Integer for property '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT
						+ "'. Value '" + valueString 
						+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
						+ " uploaded scan file processing directories on failed import" ); 
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
			}
		} else {

			log.warn( "INFO: NO Value for config property '" + PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT
					+ "' so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on failed import" );
			
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
		}

		//  PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT
		
		if ( StringUtils.isNotEmpty( internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_SuccessfulImport ) ) {
			String valueString = internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_SuccessfulImport;
			try {
				int valueInt = Integer.parseInt( valueString );
				
				if ( valueInt < 0 ) {

					valueInt = 0;
					
					log.warn( "INFO: '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT
							+ "' has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
				}
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(valueInt);
				
				log.warn( "INFO: '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT
						+ "' has value so will only keep last " + valueInt + " days of uploaded scan file processing directories on successful import" ); 
				
			} catch ( Exception e ) {

				log.error( "FAILED TO PARSE Config value as Integer for property '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT
						+ "'. Value '" + valueString 
						+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
						+ " days of uploaded scan file processing directories on successful import" ); 
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
			}
		} else {

			log.warn( "INFO: NO Value for config property '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT
					+ "' so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " days of uploaded scan file processing directories on successful import" );
			
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
		}

		//  PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT
		
		if ( StringUtils.isNotEmpty( internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_FailedImport ) ) {
			String valueString = internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_FailedImport;
			try {
				int valueInt = Integer.parseInt( valueString );
				
				if ( valueInt < 0 ) {

					valueInt = 0;
					
					log.warn( "INFO: '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT
							+ "' has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
				}
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(valueInt);
				
				log.warn( "INFO: '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT
						+ "' has value so will only keep last " + valueInt + " days of uploaded scan file processing directories on failed import" ); 
				
			} catch ( Exception e ) {

				log.error( "FAILED TO PARSE Config value as Integer for property '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT
						+ "'. Value '" + valueString 
						+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
						+ " days of uploaded scan file processing directories on failed import" ); 
				
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
			}
		} else {

			log.warn( "INFO: NO Value for config property '" + PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT
					+ "' so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " days of uploaded scan file processing directories on failed import" );
			
			configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
		}

		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.setInstance( configData_Directories_ProcessUploadCommand_InWorkDirectory );
	}

	/**
	 * @param propertiesFilename
	 * @param configDataInWebApp
	 * @throws IOException
	 * @throws SpectralFileWebappConfigException 
	 */
	private void processPropertiesFilename( 
			String propertiesFilename, 
			IsDefaultPropertiesFile isDefaultPropertiesFile,
			AllowNoPropertiesFile allowNoPropertiesFile,
			ConfigData_Directories_ProcessUploadInfo_InWorkDirectory configData_Directories_ProcessUploadCommand_InWorkDirectory,
			InternalConfig_OptionalParams_Temp internalConfig_OptionalParams_Temp,
			InternalConfigDirectoryStrings internalConfigDirectoryStrings
			) throws Exception {

		InputStream propertiesFileAsStream = null;
		try {
			if ( isDefaultPropertiesFile == IsDefaultPropertiesFile.YES ) {

				//  Get config file from class path
				ClassLoader thisClassLoader = this.getClass().getClassLoader();
				URL configPropFile = thisClassLoader.getResource( propertiesFilename );
				if ( configPropFile == null ) {
					//  No properties file
					if ( allowNoPropertiesFile == AllowNoPropertiesFile.NO ) {
						return;  //  EARLY EXIT
					}
					//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
					//				log.error( msg );
					//				throw new Exception( msg );
				} else {
					log.info( "Properties file '" + propertiesFilename + "' found, load path = " + configPropFile.getFile() );
				}
				propertiesFileAsStream = thisClassLoader.getResourceAsStream( propertiesFilename );
				if ( propertiesFileAsStream == null ) {
					//  No properties file
					if ( allowNoPropertiesFile == AllowNoPropertiesFile.NO ) {
						return;  //  EARLY EXIT
					}
					String msg = "Properties file '" + propertiesFilename + "' not found in class path.";
					log.error( msg );
					throw new SpectralFileWebappConfigException( msg );
				}
				
			} else {

				//  Get config file from Work Directory

				File workDirectory = ConfigDataInWebApp.getSingletonInstance().getWebappWorkDirectory();

				//  Already tested but test here to be extra safe
				if ( workDirectory == null ) {
					String msg = "work directory in config is empty or missing";
					log.error( msg );
					throw new SpectralFileWebappConfigException( msg );
				}

				File configFile = new File( workDirectory, propertiesFilename );
				if ( ! ( configFile.exists() && configFile.isFile() && configFile.canRead() ) ) {
					
					if ( allowNoPropertiesFile == AllowNoPropertiesFile.YES ) {
						return;  // EARLY EXIT
					}
					
					String msg = "Config file '" + propertiesFilename
							+ "' does not exist, is not  a file, or is not readable."
							+ "  Config file with path: " + configFile.getCanonicalPath();
					log.error( msg );
					throw new SpectralFileWebappConfigException( msg );
				}
				
				propertiesFileAsStream = new FileInputStream( configFile );

			}
			
			Properties configProps = new Properties();
			configProps.load( propertiesFileAsStream );
			String propertyValue = null;
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				internalConfigDirectoryStrings.scanStorageBaseDirectory = propertyValue;
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				internalConfigDirectoryStrings.tempScanUploadBaseDirectory = propertyValue;
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				internalConfigDirectoryStrings.importerTempOutputBaseDirectory = propertyValue;
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				internalConfigDirectoryStrings.backupOldBaseDirectory = propertyValue;
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__S3_BUCKET );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {

				//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

				configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket( propertyValue );
				
//				//  NO S3 so if PROPERTY_NAME__S3_BUCKET is set throw Error
//				
//				String msg = "No S3 support so property '"
//					+ PROPERTY_NAME__S3_BUCKET
//					+ "' cannot have a value.";
//				log.error( msg );
//				throw new SpectralFileWebappConfigException( msg );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__S3_REGION );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {

				//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

				configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region( propertyValue );
				
//				//  NO S3 so if PROPERTY_NAME__S3_BUCKET is set throw Error
//				
//				String msg = "No S3 support so property '"
//					+ PROPERTY_NAME__S3_REGION
//					+ "' cannot have a value.";
//				log.error( msg );
//				throw new SpectralFileWebappConfigException( msg );
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] valuesArray = propertyValue.split( "," );
				List<String> values = new ArrayList<>( valuesArray.length );
				for ( String value : valuesArray ) {
					if ( StringUtils.isNotEmpty(value) ) {
						values.add( value.trim() );
					}
				}
				if ( ! values.isEmpty() ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setSubmittedScanFilePathRestrictions( values );
				}
			}
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setProcessScanUploadJarFile( propertyValue );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable( propertyValue );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] javaExecutableParametersArray = propertyValue.split( " " );
				List<String> javaExecutableParameters = new ArrayList<>( javaExecutableParametersArray.length );
				for ( String javaExecutableParameter : javaExecutableParametersArray ) {
					if ( StringUtils.isNotEmpty(javaExecutableParameter) ) {
						javaExecutableParameters.add( javaExecutableParameter );
					}
				}
				if ( ! javaExecutableParameters.isEmpty() ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutableParameters( javaExecutableParameters );
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_SMTP_HOST );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailSmtpServerHost( propertyValue );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_FROM_ADDRESS );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailFromEmailAddress( propertyValue );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_TO_ADDRESSES );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] propertyValueSplitArray = propertyValue.split( "," );
				List<String> propertyValues = new ArrayList<>( propertyValueSplitArray.length );
				for ( String propertyValueSingle : propertyValueSplitArray ) {
					if ( StringUtils.isNotEmpty(propertyValueSingle) ) {
						propertyValues.add( propertyValueSingle );
					}
				}
				if ( ! propertyValues.isEmpty() ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailToEmailAddresses( propertyValues );
				}
			}


			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				String[] propertyValueSplitArray = propertyValue.split( "," );
				List<String> propertyValues = new ArrayList<>( propertyValueSplitArray.length );
				for ( String propertyValueSingle : propertyValueSplitArray ) {
					if ( StringUtils.isNotEmpty(propertyValueSingle) ) {
						propertyValues.add( propertyValueSingle );
					}
				}
				if ( ! propertyValues.isEmpty() ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailToEmailAddresses_FailedOnly( propertyValues );
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_MACHINE_NAME );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailMachineName( propertyValue );
			}
			
			
			propertyValue = configProps.getProperty( PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT );
			if ( BOOLEAN_STRING_TRUE.equals( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setDeleteUploadedScanFileOnSuccessfulImport( true );
			}
			
			internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_SuccessfulImport = 
					configProps.getProperty( PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT );
			
			internalConfig_OptionalParams_Temp.max_ImportScanFilesToKeep_FailedImport = 
					configProps.getProperty( PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT );

			internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_SuccessfulImport = 
					configProps.getProperty( PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT );

			internalConfig_OptionalParams_Temp.max_DaysToKeep_ImportScanFiles_FailedImport = 
					configProps.getProperty( PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT );

		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + propertiesFilename + "', exception: " + e.toString(), e );
			throw e;

		} catch ( Exception e ) {
			log.error( "Error processing Properties file '" + propertiesFilename + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	private static class InternalConfigDirectoryStrings {
		

		/**
		 * The 'temp' Base directory that scan files are uploaded into
		 */
		private String tempScanUploadBaseDirectory;
		
		/**
		 * The Base Directory that the importer writes the data files to 
		 * before either moving them to under scanStorageBaseDirectory or copying them to S3
		 * 
		 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
		 */
		private String importerTempOutputBaseDirectory;
		
		/**
		 * The Base Directory that the scans are written to for perm storage
		 */
		private String scanStorageBaseDirectory;


		/**
		 * The Base Directory that the Old scan Files are written When there is a new Version of the File Format
		 */
		private String backupOldBaseDirectory;
	}


	/**
	 * 
	 *
	 */
	private static class InternalConfig_OptionalParams_Temp {


		/**
		 * Max Number of Import execution directories to keep for Successful Import
		 */
		private String max_ImportScanFilesToKeep_SuccessfulImport;

		/**
		 * Max Number of Import execution directories to keep for Failed Import
		 */
		private String max_ImportScanFilesToKeep_FailedImport;

		/**
		 * Max Number of Days to keep Import execution directories for Successful Import
		 */
		private String max_DaysToKeep_ImportScanFiles_SuccessfulImport;

		/**
		 * Max Number of Days to keep Import execution directories for Failed Import
		 */
		private String max_DaysToKeep_ImportScanFiles_FailedImport;

	}
	
}
