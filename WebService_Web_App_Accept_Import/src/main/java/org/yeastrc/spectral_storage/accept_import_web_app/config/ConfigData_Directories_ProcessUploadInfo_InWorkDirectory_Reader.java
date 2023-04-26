package org.yeastrc.spectral_storage.accept_import_web_app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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

	private static final String CONFIG_FILENAME = "spectral_server_accept_import_config_dirs_process_cmd.properties";


	//   S3_BUCKET OVERRIDES SCAN_STORAGE_BASE_DIRECTORY

	private static String PROPERTY_NAME__S3_BUCKET = "s3.bucket";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET = "SPECTRAL_STORAGE_S3_BUCKET";

	private static String PROPERTY_NAME__S3_REGION = "s3.region";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION = "SPECTRAL_STORAGE_S3_REGION";

	private static String PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY = "scan.storage.base.directory";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY = "SPECTRAL_STORAGE_BASE_DIRECTORY";
	
	
	private static final String PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY = "temp.upload.base.directory";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY = "SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY";
	
	/**
	 * The Base Directory that the importer writes the data files to 
	 * before either moving them to under scanStorageBaseDirectory or copying them to S3
	 * 
	 * If this is not configured, then a special directory is created under scanStorageBaseDirectory and that is used.
	 * 
	 * Either this or scanStorageBaseDirectory MUST be configured, even if using S3
	 */
	private static final String PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY = "importer.temp.output.base.directory";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY = "SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY";
	

//	#  Files moved here from 'scan.storage.base.directory' here when a new file is created for a newer File Format version
//	#     Need to be able to do simple move of files from 'scan.storage.base.directory' to this directory
//	#     Valid to not configure this. 
	private static final String PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY = "backup.old.base.directory";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY = "SPECTRAL_STORAGE_IMPORTER_BACKUP_OLD_BASE_DIRECTORY";
	

	private static final String PROPERTY_NAME__S3_BUCKET_INPUT_SCAN_FILE_STORAGE = "s3.bucket.input.scan.file.storage";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE = "SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE";
	private static final String PROPERTY_NAME__S3_REGION_INPUT_SCAN_FILE_STORAGE = "s3.region.input.scan.file.storage";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE = "SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE";
	
	private static final String PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS = "submitted.scan.file.path.restrictions";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS = "SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS";
	
	
	private static final String PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE = "process.scan.upload.jar.file";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE = "SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE";

	private static final String PROPERTY_NAME__JAVA_EXECUTABLE = "java.executable";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE = "SPECTRAL_STORAGE_JAVA_EXECUTABLE";
	
	private static final String PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS = "java.executable.parameters";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS = "SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS";
	
	private static final String PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT = 
			"delete.uploaded.scan.file.on.successful.import";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT =
			"SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT";
	
	private static final String PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT = 
			"max.import.scan.files.to.keep.for.successful.import";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT =
			"SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT";

	private static final String PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT = 
			"max.import.scan.files.to.keep.for.failed.import";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT =
			"SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT";

	private static final String PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT = 
			"max.days.to.keep.import.scan.files.for.successful.import";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT =
			"SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT";

	private static final String PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT = 
			"max.days.to.keep.import.scan.files.for.failed.import";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT =
			"SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT";
	
	
	//  Email on error config

	//  Probably used.  SMTP Server Host
	private static final String PROPERTY_NAME__EMAIL_SMTP_HOST = "email.smtp.host";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_SMTP_HOST = "SPECTRAL_STORAGE_EMAIL_SMTP_HOST";
	
	private static final String PROPERTY_NAME__EMAIL_FROM_ADDRESS = "email.from.address";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS = "SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS";
	
	private static final String PROPERTY_NAME__EMAIL_TO_ADDRESSES = "email.to.addresses";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESS = "SPECTRAL_STORAGE_EMAIL_TO_ADDRESS";
	
	private static final String PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY = "email.to.addresses.failed.only";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY = "SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY";
	
	private static final String PROPERTY_NAME__EMAIL_MACHINE_NAME = "email.machine.name";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_MACHINE_NAME = "SPECTRAL_STORAGE_EMAIL_MACHINE_NAME";
	
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

		try {
			Properties propertiesFile_Properties = null;

			{
				File workDirectory = ConfigDataInWebApp.getSingletonInstance().getWebappWorkDirectory();

				//  Already tested but test here to be extra safe
				if ( workDirectory == null ) {
					String msg = "work directory in config is empty or missing so NOT reading the config file " + CONFIG_FILENAME;
					log.warn( msg );

				} else {
	
					File configFile = new File( workDirectory, CONFIG_FILENAME );
	
					if ( ( configFile.exists() && configFile.isFile() && configFile.canRead() ) ) {
	
						log.warn( "INFO:: Processing Config file '" + CONFIG_FILENAME
								+ "'."
								+ "  Config file with path: " + configFile.getCanonicalPath() );
						//  Get config file from Work Directory
	
						try ( InputStream propertiesFileAsStream = new FileInputStream( configFile ); ){
	
							propertiesFile_Properties = new Properties();
							propertiesFile_Properties.load( propertiesFileAsStream );
	
						} catch ( Exception e ) {
							log.error( "Error processing Properties file '" + CONFIG_FILENAME + "', exception: " + e.toString(), e );
							throw e;
						}
	
					}
				}
			}			
			

			{  //   S3  Bucket
				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					log.warn( "INFO: S3 Bucket - Main Data Storage - to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET + "' with value: " + valueFoundInLabel_String );
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						log.warn( "INFO: S3 Bucket - Main Data Storage - to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET + "' with value: " + valueFoundInLabel_String );
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__S3_BUCKET );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								log.warn( "INFO: S3 Bucket to to use: Value found in Properties file with key: '" + PROPERTY_NAME__S3_BUCKET + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket( valueFoundInLabel_String );
				}
			}

			{  //  S3 Region - Main Data Storage
				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					log.warn( "INFO: S3 Region - Main Data Storage - to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION + "' with value: " + valueFoundInLabel_String );
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						log.warn( "INFO: S3 Region to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION + "' with value: " + valueFoundInLabel_String );
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__S3_REGION );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								log.warn( "INFO: S3 Region to to use: Value found in Properties file with key: '" + PROPERTY_NAME__S3_REGION + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region( valueFoundInLabel_String );
				}
			}
				
			{
				///   scanStorageBaseDirectory -- Store on Local Disk  in specified directory

				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					File scanStorageBaseDirectory = new File( valueFoundInLabel_String );

					if ( ! ( scanStorageBaseDirectory.exists() && scanStorageBaseDirectory.isDirectory() && scanStorageBaseDirectory.canRead() ) ) {
						String msg = "!!Storage Base Directory to use: Value INVALID in Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY 
								+ "' does not exist or is not a directory or is not readable.  Value:  " 
								+ valueFoundInLabel_String;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

					log.warn( "INFO: Storage Base Directory to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						File scanStorageBaseDirectory = new File( valueFoundInLabel_String );

						if ( ! ( scanStorageBaseDirectory.exists() && scanStorageBaseDirectory.isDirectory() && scanStorageBaseDirectory.canRead() ) ) {
							String msg = "!!Storage Base Directory to use: Value INVALID in JVM param: '-D '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY 
									+ "' does not exist or is not a directory or is not readable.  Value:  " 
									+ valueFoundInLabel_String;
							log.error( msg );
							throw new SpectralFileWebappConfigException( msg );
						}
						
						configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

						log.warn( "INFO: Storage Base Directory to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File
						
						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								File scanStorageBaseDirectory = new File( valueFoundInLabel_String );
	
								if ( ! ( scanStorageBaseDirectory.exists() && scanStorageBaseDirectory.isDirectory() && scanStorageBaseDirectory.canRead() ) ) {
									String msg = "!!Property '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
											+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
											+ valueFoundInLabel_String;
									log.error( msg );
									throw new SpectralFileWebappConfigException( msg );
								}
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );
	
								log.warn( "INFO: Storage Base Directory to to use: Value found in Properties file with key: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   tempScanUploadDirectory -- Store on Local Disk  in specified directory

				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					File tempScanUploadDirectory = new File( valueFoundInLabel_String );

					if ( ! ( tempScanUploadDirectory.exists() && tempScanUploadDirectory.isDirectory() && tempScanUploadDirectory.canRead() ) ) {
						String msg = "!!Temp Scan Upload Directory to use: Value INVALID in Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY 
								+ "' does not exist or is not a directory or is not readable.  Value:  " 
								+ valueFoundInLabel_String;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setTempScanUploadBaseDirectory(tempScanUploadDirectory);

					log.warn( "INFO: Temp Scan Upload Directory to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						File tempScanUploadDirectory = new File( valueFoundInLabel_String );

						if ( ! ( tempScanUploadDirectory.exists() && tempScanUploadDirectory.isDirectory() && tempScanUploadDirectory.canRead() ) ) {
							String msg = "!!Temp Scan Upload Directory to use: Value INVALID in JVM param: '-D '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY 
									+ "' does not exist or is not a directory or is not readable.  Value:  " 
									+ valueFoundInLabel_String;
							log.error( msg );
							throw new SpectralFileWebappConfigException( msg );
						}
						
						configData_Directories_ProcessUploadCommand_InWorkDirectory.setTempScanUploadBaseDirectory(tempScanUploadDirectory);

						log.warn( "INFO: Temp Scan Upload Directory to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								File tempScanUploadDirectory = new File( valueFoundInLabel_String );

								if ( ! ( tempScanUploadDirectory.exists() && tempScanUploadDirectory.isDirectory() && tempScanUploadDirectory.canRead() ) ) {
									String msg = "!!Property '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY 
											+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
											+ valueFoundInLabel_String;
									log.error( msg );
									throw new SpectralFileWebappConfigException( msg );
								}

								configData_Directories_ProcessUploadCommand_InWorkDirectory.setTempScanUploadBaseDirectory(tempScanUploadDirectory);

								log.warn( "INFO: Temp Scan Upload Directory to to use: Value found in Properties file with key: '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   importerTempOutputBaseDirectory -- Store on Local Disk  in specified directory

				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					File importerTempOutputBaseDirectory = new File( valueFoundInLabel_String );

					if ( ! ( importerTempOutputBaseDirectory.exists() && importerTempOutputBaseDirectory.isDirectory() && importerTempOutputBaseDirectory.canRead() ) ) {
						String msg = "!!Importer Temp Output Base Directory to use: Value INVALID in Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY 
								+ "' does not exist or is not a directory or is not readable.  Value:  " 
								+ valueFoundInLabel_String;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setImporterTempOutputBaseDirectory(importerTempOutputBaseDirectory);

					log.warn( "INFO: Importer Temp Output Base to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						File importerTempOutputBaseDirectory = new File( valueFoundInLabel_String );

						if ( ! ( importerTempOutputBaseDirectory.exists() && importerTempOutputBaseDirectory.isDirectory() && importerTempOutputBaseDirectory.canRead() ) ) {
							String msg = "!!Importer Temp Output Base Directory to use: Value INVALID in JVM param: '-D '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY 
									+ "' does not exist or is not a directory or is not readable.  Value:  " 
									+ valueFoundInLabel_String;
							log.error( msg );
							throw new SpectralFileWebappConfigException( msg );
						}
						
						configData_Directories_ProcessUploadCommand_InWorkDirectory.setImporterTempOutputBaseDirectory(importerTempOutputBaseDirectory);

						log.warn( "INFO: Importer Temp Output Base Directory to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								File importerTempOutputBaseDirectory = new File( valueFoundInLabel_String );
	
								if ( ! ( importerTempOutputBaseDirectory.exists() && importerTempOutputBaseDirectory.isDirectory() && importerTempOutputBaseDirectory.canRead() ) ) {
									String msg = "!!Property '" + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY 
											+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
											+ valueFoundInLabel_String;
									log.error( msg );
									throw new SpectralFileWebappConfigException( msg );
								}
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setImporterTempOutputBaseDirectory(importerTempOutputBaseDirectory);
	
								log.warn( "INFO: Importer Temp Output Base Directory to to use: Value found in Properties file with key: '" + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   backupOldBaseDirectory -- Store on Local Disk  in specified directory

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					File backupOldBaseDirectory = new File( valueFoundInLabel_String );

					if ( ! ( backupOldBaseDirectory.exists() && backupOldBaseDirectory.isDirectory() && backupOldBaseDirectory.canRead() ) ) {
						String msg = "!!Backup Old Base Directory to use: Value INVALID in Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY 
								+ "' does not exist or is not a directory or is not readable.  Value:  " 
								+ valueFoundInLabel_String;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setBackupOldBaseDirectory(backupOldBaseDirectory);

					log.warn( "INFO: Backup Old Base Directory to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						File backupOldBaseDirectory = new File( valueFoundInLabel_String );

						if ( ! ( backupOldBaseDirectory.exists() && backupOldBaseDirectory.isDirectory() && backupOldBaseDirectory.canRead() ) ) {
							String msg = "!!Backup Old Base Directory to use: Value INVALID in JVM param: '-D '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY 
									+ "' does not exist or is not a directory or is not readable.  Value:  " 
									+ valueFoundInLabel_String;
							log.error( msg );
							throw new SpectralFileWebappConfigException( msg );
						}
						
						configData_Directories_ProcessUploadCommand_InWorkDirectory.setBackupOldBaseDirectory(backupOldBaseDirectory);

						log.warn( "INFO: Backup Old Base Directory to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BACKUP_OLD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								File backupOldBaseDirectory = new File( valueFoundInLabel_String );
	
								if ( ! ( backupOldBaseDirectory.exists() && backupOldBaseDirectory.isDirectory() && backupOldBaseDirectory.canRead() ) ) {
									String msg = "!!Property '" + PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY 
											+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
											+ valueFoundInLabel_String;
									log.error( msg );
									throw new SpectralFileWebappConfigException( msg );
								}
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setBackupOldBaseDirectory(backupOldBaseDirectory);
	
								log.warn( "INFO: Backup Old Base Directory to to use: Value found in Properties file with key: '" + PROPERTY_NAME__BACKUP_OLD_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   s3Bucket_InputScanFileStorage -- Store on S3  in specified bucket

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket_InputScanFileStorage(valueFoundInLabel_String);

					log.warn( "INFO: Temp Scan Upload S3 Bucket to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket_InputScanFileStorage(valueFoundInLabel_String);

						log.warn( "INFO: Temp Scan Upload S3 Bucket to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__S3_BUCKET_INPUT_SCAN_FILE_STORAGE );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket_InputScanFileStorage(valueFoundInLabel_String);
	
								log.warn( "INFO: Temp Scan Upload S3 Bucket to to use: Value found in Properties file with key: '" + PROPERTY_NAME__S3_BUCKET_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   s3Region_InputScanFileStorage -- Store on S3  in specified bucket

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region_InputScanFileStorage(valueFoundInLabel_String);

					log.warn( "INFO: Temp Scan Upload S3 Region to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region_InputScanFileStorage(valueFoundInLabel_String);

						log.warn( "INFO: Temp Scan Upload S3 Region to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__S3_REGION_INPUT_SCAN_FILE_STORAGE );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region_InputScanFileStorage(valueFoundInLabel_String);
	
								log.warn( "INFO: Temp Scan Upload S3 Region to to use: Value found in Properties file with key: '" + PROPERTY_NAME__S3_REGION_INPUT_SCAN_FILE_STORAGE + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   submittedScanFilePathRestrictions -- 
				
				String finalValueToParse = null;

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValueToParse = valueFoundInLabel_String;

					log.warn( "INFO: Submitted Scan File Path Restrictions to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValueToParse = valueFoundInLabel_String;

						log.warn( "INFO: Submitted Scan File Path Restrictions to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								
								finalValueToParse = valueFoundInLabel_String;
	
								log.warn( "INFO: Submitted Scan File Path Restrictions to to use: Value found in Properties file with key: '" + PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				
				if ( finalValueToParse != null ) {
					
					String[] valuesArray = finalValueToParse.split( "," );
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
			}

			{
				///   processScanUploadJarFile -- Jar file to execute to import

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					configData_Directories_ProcessUploadCommand_InWorkDirectory.setProcessScanUploadJarFile(valueFoundInLabel_String);

					log.warn( "INFO: Importer Jar File to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setProcessScanUploadJarFile(valueFoundInLabel_String);

						log.warn( "INFO: Importer Jar File to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setProcessScanUploadJarFile(valueFoundInLabel_String);
	
								log.warn( "INFO: Importer Jar File to to use: Value found in Properties file with key: '" + PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}

				if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getProcessScanUploadJarFile() ) ) {

					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					log.error( "!!!" );
					log.error( "INFO:  NO Process Scan Upload Jar." ); 
					log.error( "!!!" );
					log.error( "!!!  Process Scan Upload Jar is configured using one of the following:  " );

					log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE );
					log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE );
					log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_PROCESS_SCAN_UPLOAD_JAR_FILE );

					log.error( "!!!" );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );


					throw new SpectralFileWebappConfigException( "FATAL Webapp START ERROR::  NO Process Scan Import Jar specified." );
				}
				
			}

			{
				///   JavaExecutable -- Command to run to execute Java for running the Importer

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable(valueFoundInLabel_String);

					log.warn( "INFO: Java Executable for running Importer Jar to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable(valueFoundInLabel_String);

						log.warn( "INFO: Java Executable for running Importer Jar to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								
								configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable(valueFoundInLabel_String);
	
								log.warn( "INFO: Java Executable for running Importer Jar to to use: Value found in Properties file with key: '" + PROPERTY_NAME__JAVA_EXECUTABLE + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				
				if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutable() ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setJavaExecutable( ProcessUploadedScanFilesConstants.JAVA_EXECUTABLE_DEFAULT );
					
					log.warn( "INFO:  Java Executable is not set so will be set to default value of '" 
							+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getJavaExecutable() //  Log after updating to default.
							+ "'.  It is configured using one of the following:  " );

					log.warn( "INFO:  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__JAVA_EXECUTABLE );
					log.warn( "INFO:  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE );
					log.warn( "INFO:  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE );
				}
			}

			{
				///   JavaExecutableParameters -- Parameters passed to Command to run to execute Java for running the Importer.  Java Max Heap, etc

				String finalValueToParse = null;
					
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValueToParse = valueFoundInLabel_String;

					log.warn( "INFO: Parameters to pass to Java Executable for running Importer Jar to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValueToParse = valueFoundInLabel_String;

						log.warn( "INFO: Parameters to pass to Java Executable for running Importer Jar to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_JAVA_EXECUTABLE_PARAMETERS + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								
								finalValueToParse = valueFoundInLabel_String;
	
								log.warn( "INFO: Parameters to pass to Java Executable for running Importer Jar to to use: Value found in Properties file with key: '" + PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS + "' with value: " + valueFoundInLabel_String );
							}

						}
					}
				}
				

				if ( finalValueToParse != null ) {
					String[] javaExecutableParametersArray = finalValueToParse.split( " " );
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
			}

			{
				///   EmailSmtpServerHost -- Send Email on Import end, success or fail

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_SMTP_HOST );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailSmtpServerHost(valueFoundInLabel_String);

					log.warn( "INFO: Email SMTP Host to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_SMTP_HOST + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_SMTP_HOST);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailSmtpServerHost(valueFoundInLabel_String);

						log.warn( "INFO: Email SMTP Host to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_SMTP_HOST + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__EMAIL_SMTP_HOST );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailSmtpServerHost(valueFoundInLabel_String);

								log.warn( "INFO: Email SMTP Host to to use: Value found in Properties file with key: '" + PROPERTY_NAME__EMAIL_SMTP_HOST + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   EmailFromEmailAddress -- Send Email on Import end, success or fail

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailFromEmailAddress(valueFoundInLabel_String);

					log.warn( "INFO: Email From Address to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailFromEmailAddress(valueFoundInLabel_String);

						log.warn( "INFO: Email From Address to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_FROM_ADDRESS + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__EMAIL_FROM_ADDRESS );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailFromEmailAddress(valueFoundInLabel_String);

								log.warn( "INFO: Email From Address to to use: Value found in Properties file with key: '" + PROPERTY_NAME__EMAIL_FROM_ADDRESS + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   EmailToEmailAddresses -- Send Email on Import end, success or fail

				String finalValueToParse = null;
					
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESS );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValueToParse = valueFoundInLabel_String;

					log.warn( "INFO: Email To Address(es) to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESS + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESS);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValueToParse = valueFoundInLabel_String;

						log.warn( "INFO: Email To Address(es) to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESS + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__EMAIL_TO_ADDRESSES );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								finalValueToParse = valueFoundInLabel_String;

								log.warn( "INFO: Email To Address(es) to to use: Value found in Properties file with key: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				

				if ( finalValueToParse != null ) {
					String[] propertyValueSplitArray = finalValueToParse.split( "," );
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
			}

			{
				///   EmailToEmailAddresses_FailedOnly -- Send Email on Import end, fail only

				String finalValueToParse = null;
					
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValueToParse = valueFoundInLabel_String;

					log.warn( "INFO: Email To Address(es) to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValueToParse = valueFoundInLabel_String;

						log.warn( "INFO: Email To Address(es) to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_TO_ADDRESSES_FAILED_ONLY + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								finalValueToParse = valueFoundInLabel_String;

								log.warn( "INFO: Email To Address(es) to to use: Value found in Properties file with key: '" + PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
				

				if ( finalValueToParse != null ) {
					String[] propertyValueSplitArray = finalValueToParse.split( "," );
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
			}


			{
				///   EmailMachineName -- Send Email on Import end, success or fail

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_MACHINE_NAME );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailMachineName(valueFoundInLabel_String);

					log.warn( "INFO: Email From Address to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_MACHINE_NAME + "' with value: " + valueFoundInLabel_String );

				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_MACHINE_NAME);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailMachineName(valueFoundInLabel_String);

						log.warn( "INFO: Email From Address to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_EMAIL_MACHINE_NAME + "' with value: " + valueFoundInLabel_String );

					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__EMAIL_MACHINE_NAME );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

								configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailMachineName(valueFoundInLabel_String);

								log.warn( "INFO: Email From Address to to use: Value found in Properties file with key: '" + PROPERTY_NAME__EMAIL_MACHINE_NAME + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{
				///   DeleteUploadedScanFileOnSuccessfulImport -- Set to 'true', anything else is false

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					if ( BOOLEAN_STRING_TRUE.equals( valueFoundInLabel_String ) ) {
						
						configData_Directories_ProcessUploadCommand_InWorkDirectory.setDeleteUploadedScanFileOnSuccessfulImport( true );

						log.warn( "INFO: Delete Uploaded Scan File On Successful Import: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT 
								+ "' with value '" + valueFoundInLabel_String 
								+ "' so will do Delete Uploaded Scan File On Successful Import." );
					}
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
						if ( BOOLEAN_STRING_TRUE.equals( valueFoundInLabel_String ) ) {
							
							configData_Directories_ProcessUploadCommand_InWorkDirectory.setDeleteUploadedScanFileOnSuccessfulImport( true );
	
							log.warn( "INFO: Delete Uploaded Scan File On Successful Import: Value found in JVM param: '-D" 
									+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT 
									+ "' with value: " + valueFoundInLabel_String
									+ "' so will do Delete Uploaded Scan File On Successful Import." );
						}
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT );

							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
								if ( BOOLEAN_STRING_TRUE.equals( valueFoundInLabel_String ) ) {

									configData_Directories_ProcessUploadCommand_InWorkDirectory.setDeleteUploadedScanFileOnSuccessfulImport( true );

									log.warn( "INFO: Delete Uploaded Scan File On Successful Import: Value found in Properties file with key: '" 
											+ PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
											+ "' with value: " + valueFoundInLabel_String
											+ "' so will do Delete Uploaded Scan File On Successful Import." );
								}
							}
						}
					}
				}
				
				if ( ! configData_Directories_ProcessUploadCommand_InWorkDirectory.isDeleteUploadedScanFileOnSuccessfulImport() ) {
				
					//  Display message since NOT set
					
					log.warn( "INFO: NOT Performing since NOT set: 'Delete Uploaded Scan File On Successful Import': Value '" + BOOLEAN_STRING_TRUE
							+ "' NOT found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
							+ "' or in  JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
							+ "' or in Properties file with key: '" 
							+ PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT
							+ "'." );
				}
			}

			{ // Max_ImportScanFilesToKeep_SuccessfulImport
				
				String finalValue_String = null;
				
				String message_Label = null;
				

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValue_String = valueFoundInLabel_String;
					
					message_Label = "Environment Variable: '" 
							+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT 
							+ "'";
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValue_String = valueFoundInLabel_String;
						
						message_Label = "JVM param: '-D" 
								+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT 
								+ "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								finalValue_String = valueFoundInLabel_String;
								
								message_Label = "Properties file key: '" 
										+ PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_SUCCESSFUL_IMPORT 
										+ "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( finalValue_String ) ) {
					String valueString = finalValue_String;
					try {
						int valueInt = Integer.parseInt( valueString );

						if ( valueInt < 0 ) {

							valueInt = 0;

							log.warn( "INFO: " + message_Label
									+ " has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
						}

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(valueInt);

						log.warn( "INFO: " + message_Label
								+ " has value so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 

					} catch ( Exception e ) {

						log.error( "FAILED TO PARSE Config value as Integer for " + message_Label
								+ ". Value '" + valueString 
								+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
								+ " uploaded scan file processing directories on successful import" ); 

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
					}
				} else {

					log.warn( "INFO: NO Value for " + message_Label
							+ " so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on successful import" );

					configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
				}
			}

			{ // Max_ImportScanFilesToKeep_FailedImport
				
				String finalValue_String = null;
				
				String message_Label = null;
				

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValue_String = valueFoundInLabel_String;
					
					message_Label = "Environment Variable: '" 
							+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT 
							+ "'";
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValue_String = valueFoundInLabel_String;
						
						message_Label = "JVM param: '-D" 
								+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT 
								+ "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								finalValue_String = valueFoundInLabel_String;
								
								message_Label = "Properties file key: '" 
										+ PROPERTY_NAME__MAX_IMPORT_SCAN_FILES_TO_KEEP_FOR_FAILED_IMPORT 
										+ "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( finalValue_String ) ) {
					String valueString = finalValue_String;
					try {
						int valueInt = Integer.parseInt( valueString );

						if ( valueInt < 0 ) {

							valueInt = 0;

							log.warn( "INFO: " + message_Label
									+ " has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on failed import" ); 
						}

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(valueInt);

						log.warn( "INFO: " + message_Label
								+ " has value so will only keep last " + valueInt + " uploaded scan file processing directories on failed import" ); 

					} catch ( Exception e ) {

						log.error( "FAILED TO PARSE Config value as Integer for " + message_Label
								+ ". Value '" + valueString 
								+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
								+ " uploaded scan file processing directories on successful import" ); 

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
					}
				} else {

					log.warn( "INFO: NO Value for " + message_Label
							+ " so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on failed import" );

					configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_ImportScanFilesToKeep_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
				}
			}

			{ // Max_DaysToKeep_ImportScanFiles_SuccessfulImport
				
				String finalValue_String = null;
				
				String message_Label = null;
				

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValue_String = valueFoundInLabel_String;
					
					message_Label = "Environment Variable: '" 
							+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT 
							+ "'";
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValue_String = valueFoundInLabel_String;
						
						message_Label = "JVM param: '-D" 
								+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT 
								+ "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								finalValue_String = valueFoundInLabel_String;
								
								message_Label = "Properties file key: '" 
										+ PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_SUCCESSFUL_IMPORT 
										+ "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( finalValue_String ) ) {
					String valueString = finalValue_String;
					try {
						int valueInt = Integer.parseInt( valueString );

						if ( valueInt < 0 ) {

							valueInt = 0;

							log.warn( "INFO: " + message_Label
									+ " has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 
						}

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(valueInt);

						log.warn( "INFO: " + message_Label
								+ " has value so will only keep last " + valueInt + " uploaded scan file processing directories on successful import" ); 

					} catch ( Exception e ) {

						log.error( "FAILED TO PARSE Config value as Integer for " + message_Label
								+ ". Value '" + valueString 
								+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
								+ " uploaded scan file processing directories on successful import" ); 

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
					}
				} else {

					log.warn( "INFO: NO Value for " + message_Label
							+ " so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on successful import" );

					configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_SuccessfulImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
				}
			}

			{ // Max_DaysToKeep_ImportScanFiles_FailedImport
				
				String finalValue_String = null;
				
				String message_Label = null;
				

				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
					
					finalValue_String = valueFoundInLabel_String;
					
					message_Label = "Environment Variable: '" 
							+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT 
							+ "'";
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						finalValue_String = valueFoundInLabel_String;
						
						message_Label = "JVM param: '-D" 
								+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT 
								+ "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							valueFoundInLabel_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT );
	
							if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {
	
								finalValue_String = valueFoundInLabel_String;
								
								message_Label = "Properties file key: '" 
										+ PROPERTY_NAME__MAX_DAYS_TO_KEEP_IMPORT_SCAN_FILES_FOR_FAILED_IMPORT 
										+ "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( finalValue_String ) ) {
					String valueString = finalValue_String;
					try {
						int valueInt = Integer.parseInt( valueString );

						if ( valueInt < 0 ) {

							valueInt = 0;

							log.warn( "INFO: " + message_Label
									+ " has value that is negative so so will only keep last " + valueInt + " uploaded scan file processing directories on failed import" ); 
						}

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(valueInt);

						log.warn( "INFO: " + message_Label
								+ " has value so will only keep last " + valueInt + " uploaded scan file processing directories on failed import" ); 

					} catch ( Exception e ) {

						log.error( "FAILED TO PARSE Config value as Integer for " + message_Label
								+ ". Value '" + valueString 
								+ "'.  Using default for only keeping last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES 
								+ " uploaded scan file processing directories on successful import" ); 

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
					}
				} else {

					log.warn( "INFO: NO Value for " + message_Label
							+ " so using DEFAULT for only keep last " + MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES + " uploaded scan file processing directories on failed import" );

					configData_Directories_ProcessUploadCommand_InWorkDirectory.setMax_DaysToKeep_ImportScanFiles_FailedImport(MAX_DAYS_AND_COUNT_OF_IMPORT_DIRECTORIES);
				}
			}
			
			///////////////
			
			//   Start Required Fields and Cross Validation
			
			
			{  // Validation:: Main Storage Location and Location of Temporary Output Data from Importer

				if ( StringUtils.isNotEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getS3Bucket() ) ) {

					if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getImporterTempOutputBaseDirectory() == null ) {

						if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getScanStorageBaseDirectory() == null ) {

							log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
							log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
							log.error( "!!!" );
							log.error( "!!!  FATAL Webapp START ERROR::  S3 Bucket for main storage IS Specified, BUT Still need a value for either Importer Temp Output Base Directory OR Scan Storage Base Directory." ); 
							log.error( "!!!" );
							log.error( "!!!  Importer Temp Output Base Directory is configured using one of the following:  " );

							log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );
							log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );
							log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_IMPORTER_TEMP_OUTPUT_BASE_DIRECTORY );

							log.error( "!!!" );						
							log.error( "!!!  Scan Storage Base Directory is configured using one of the following:  " );

							log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY );
							log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );
							log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );

							log.error( "!!!" );

							log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
							log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

							throw new SpectralFileWebappConfigException( "FATAL Webapp START ERROR::  S3 Bucket for main storage IS Specified, BUT Still need a value for either Importer Temp Output Base Directory OR Scan Storage Base Directory." );

						}
					} else {

						//  Clear since not needed or used

						configData_Directories_ProcessUploadCommand_InWorkDirectory.setScanStorageBaseDirectory(null);
					}
				
				} else {

					if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getScanStorageBaseDirectory() == null ) {

						log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
						log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
						log.error( "!!!" );
						log.error( "!!!  FATAL Webapp START ERROR::  NO Scan Storage Base Directory AND NO S3 Bucket for main storage." ); 
						log.error( "!!!" );
						log.error( "!!!  Scan Storage Base Directory is configured using one of the following:  " );

						log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY );
						log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );
						log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );

						log.error( "!!!" );
						log.error( "!!!  S3 Bucket for main storage is configured using one of the following:  " );

						log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__S3_BUCKET );
						log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET );
						log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET );

						log.error( "!!!" );
						log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
						log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

						throw new SpectralFileWebappConfigException( "FATAL Webapp START ERROR::  NO Scan Storage Base Directory AND NO S3 Bucket for main storage." );
					}
				}
			}

			if ( configData_Directories_ProcessUploadCommand_InWorkDirectory.getTempScanUploadBaseDirectory() == null ) {

				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!" );
				log.error( "!!!  FATAL Webapp START ERROR::  NO Temp Upload Base Directory.  This is required even if uploading to S3" ); 
				log.error( "!!!" );
				log.error( "!!!  Temp Upload Base Directory is configured using one of the following:  " );

				log.error( "!!!  Configuration file: "  + CONFIG_FILENAME + " and property: " + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY );
				log.error( "!!!  Environment Variable: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_TEMP_UPLOAD_BASE_DIRECTORY );
				log.error( "!!!  Passed to java command as '-D' parameter: " + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY );

				log.error( "!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

				throw new SpectralFileWebappConfigException( "FATAL Webapp START ERROR::  NO Temp Scan Upload Base Directory." );
			}

		} catch ( Exception e ) {
			log.error( "Error processing Properties file '" + CONFIG_FILENAME + "' and associated Environment Variables and JVM '-D' parameters.  Exception: " + e.toString(), e );
			throw e;
		}
		
		log.warn( "Finished processing Confiration in config file '" 
				+ CONFIG_FILENAME
				+ "' and associated Environment Variables and JVM '-D' parameters." );
		
		
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory.setInstance( configData_Directories_ProcessUploadCommand_InWorkDirectory );
	}

}
