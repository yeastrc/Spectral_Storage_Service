package org.yeastrc.spectral_storage.get_data_webapp.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileWebappConfigException;

/**
 * Update ConfigData_Directories_ProcessUploadCommand_InWorkDirectory with contents in config file
 *
 */
public class ConfigData_ScanDataLocation_InWorkDirectory_Reader {

	private static final Logger log = LoggerFactory.getLogger(ConfigData_ScanDataLocation_InWorkDirectory_Reader.class);

	private static String CONFIG_FILENAME = "spectral_storage_get_data_scan_data_location.properties";

	//   S3_BUCKET OVERRIDES SCAN_STORAGE_BASE_DIRECTORY

	private static String PROPERTY_NAME__S3_BUCKET = "s3.bucket";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET = "SPECTRAL_STORAGE_S3_BUCKET";

	private static String PROPERTY_NAME__S3_REGION = "s3.region";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION = "SPECTRAL_STORAGE_S3_REGION";

	private static String PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY = "scan.storage.base.directory";
	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_BASE_DIRECTORY = "SPECTRAL_STORAGE_BASE_DIRECTORY";
	
	//  This value comes from Environment variable, jvm -D parameter, or property file, in that order.

	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE = "SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE";

	private static String PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS = "max.number.scans.to.return.for.requests.that.include.scan.peaks";
	
	//  This value comes from Environment variable, jvm -D parameter, or property file, in that order.

	private static final String PROPERTY_FILE_KEY__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE = "parallelstream.default.thread.pool.java.processing.enable";

	private static final String ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE = "SPECTRAL_STORAGE_PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE";

	private static final String VALUE_TRUE = "true";

	//  private constructor
	private ConfigData_ScanDataLocation_InWorkDirectory_Reader() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigData_ScanDataLocation_InWorkDirectory_Reader getInstance() { 
		return new ConfigData_ScanDataLocation_InWorkDirectory_Reader(); 
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void readConfigDataInWebApp() throws Exception {
		
		ConfigData_ScanDataLocation_InWorkDirectory configData_ScanDataLocation_InWorkDirectory = new ConfigData_ScanDataLocation_InWorkDirectory();

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

					log.warn( "INFO: S3 Bucket to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET + "' with value: " + valueFoundInLabel_String );
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					valueFoundInLabel_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET);

					if ( valueFoundInLabel_String != null ) {

						valueFoundInLabel_String = valueFoundInLabel_String.trim();
					}

					if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

						log.warn( "INFO: S3 Bucket to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET + "' with value: " + valueFoundInLabel_String );
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
					configData_ScanDataLocation_InWorkDirectory.setS3Bucket( valueFoundInLabel_String );
				}
			}

			{  //  S3 Region
				
				String valueFoundInLabel_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION );

				if ( valueFoundInLabel_String != null ) {
					valueFoundInLabel_String = valueFoundInLabel_String.trim();
				}
				if ( StringUtils.isNotEmpty( valueFoundInLabel_String ) ) {

					log.warn( "INFO: S3 Region to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_REGION + "' with value: " + valueFoundInLabel_String );
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
					configData_ScanDataLocation_InWorkDirectory.setS3Region( valueFoundInLabel_String );
				}
			}

			if ( StringUtils.isNotEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() ) ) {
				
				log.warn( "INFO::  Since S3 bucket IS populated, the Scan Storage Base Directory will NOT be Used" );
				
			} else {
				
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
					
					configData_ScanDataLocation_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

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
						
						configData_ScanDataLocation_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

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

								configData_ScanDataLocation_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

								log.warn( "INFO: Storage Base Directory to to use: Value found in Properties file with key: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' with value: " + valueFoundInLabel_String );
							}
						}
					}
				}
			}

			{ //  Max Scan Batch Size
				
				String valueFoundInLabel = "";
				

				String maxNumberScansReturn_String = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE );

				if ( maxNumberScansReturn_String != null ) {
				
					maxNumberScansReturn_String = maxNumberScansReturn_String.trim();
				}


				if ( StringUtils.isNotEmpty( maxNumberScansReturn_String ) ) {

					log.warn( "INFO: maxNumberScansReturn to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE + "' with value: " + maxNumberScansReturn_String );

					valueFoundInLabel = "maxNumberScansReturn to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE + "'";
					
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					maxNumberScansReturn_String = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE);

					if ( maxNumberScansReturn_String != null ) {

						maxNumberScansReturn_String = maxNumberScansReturn_String.trim();
					}

					if ( StringUtils.isNotEmpty( maxNumberScansReturn_String ) ) {

						log.warn( "INFO: maxNumberScansReturn to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE + "' with value: " + maxNumberScansReturn_String );

						valueFoundInLabel = "maxNumberScansReturn to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE + "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							maxNumberScansReturn_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS );

							if ( maxNumberScansReturn_String != null ) {

								maxNumberScansReturn_String = maxNumberScansReturn_String.trim();
							}

							if ( StringUtils.isNotEmpty( maxNumberScansReturn_String ) ) {

								log.warn( "INFO: maxNumberScansReturn to use: Value found in Properties file with key: '" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "' with value: " + maxNumberScansReturn_String );

								valueFoundInLabel = "maxNumberScansReturn to use: Value found in Properties file with key:" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( maxNumberScansReturn_String ) ) {

					try {
						String propertyValue_Trimmed = maxNumberScansReturn_String.trim();
						int maxNumberScansReturn = Integer.parseInt(propertyValue_Trimmed);

						configData_ScanDataLocation_InWorkDirectory.setMaxNumberScansReturn(maxNumberScansReturn);

					} catch ( Exception e ) {

						String msg = valueFoundInLabel
								+ "' is populated and is not parsable to an integer.  value: " + maxNumberScansReturn_String;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}
				}
			}

			{ //  PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE
				
				String valueFoundInLabel = "";
				

				String parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = System.getenv( ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE );

				if ( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String != null ) {
				
					parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String.trim();
				}


				if ( StringUtils.isNotEmpty( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String ) ) {

					log.warn( "INFO: parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE + "' with value: " + parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String );

					valueFoundInLabel = "parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE + "'";
					
				} else {

					//  Not in config file or Environment Variable so get from JVM -D Property

					Properties prop = System.getProperties();
					parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = prop.getProperty(ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE);

					if ( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String != null ) {

						parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String.trim();
					}

					if ( StringUtils.isNotEmpty( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String ) ) {

						log.warn( "INFO: parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE + "' with value: " + parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String );

						valueFoundInLabel = "parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__PARALLELSTREAM_DEFAULT_THREAD_POOL_JAVA_PROCESSING_ENABLE + "'";
					} else {

						//  Last place to check is Properties File

						if ( propertiesFile_Properties != null ) {
							parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = propertiesFile_Properties.getProperty( PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS );

							if ( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String != null ) {

								parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String = parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String.trim();
							}

							if ( StringUtils.isNotEmpty( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String ) ) {

								log.warn( "INFO: parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in Properties file with key: '" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "' with value: " + parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String );

								valueFoundInLabel = "parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String to use: Value found in Properties file with key:" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "'";
							}
						}
					}
				}
				
				if ( StringUtils.isNotEmpty( parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String ) ) {

					String propertyValue_Trimmed = parallelStream_DefaultThreadPool_Java_Processing_Enabled_True_String.trim();
					if ( VALUE_TRUE.equals(propertyValue_Trimmed) ) {

						configData_ScanDataLocation_InWorkDirectory.setParallelStream_DefaultThreadPool_Java_Processing_Enabled_True(true);
					}
				}
			}
			
			
		} catch ( Exception e ) {
			log.error( "Error processing Properties file '" + CONFIG_FILENAME + "' And Environment Variables And java '-D' parameters, exception: " + e.toString(), e );
			throw e;
		}
		
		log.warn( "Finished processing config file '" 
				+ CONFIG_FILENAME
				+ "'." );
		

		if ( configData_ScanDataLocation_InWorkDirectory.getScanStorageBaseDirectory() == null  
				&& StringUtils.isEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() ) ) {

			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			log.error( "!!!!!!" );

			String msg = "Must set S3 Bucket or Scan Storage Base Directory to a value.";
			log.error( msg );

			log.error( "S3 Bucket set by one of following: Environment Variable '" 
					+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET
					+ "', JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_S3_BUCKET
					+ "' or config file property '"
					+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY
					+ "' in property filename: " + CONFIG_FILENAME
					);
			
			log.error( "!!!!!!" );
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			
			throw new SpectralFileWebappConfigException( msg );
		}
		
		ConfigData_ScanDataLocation_InWorkDirectory.setSingletonInstance( configData_ScanDataLocation_InWorkDirectory );
	}

}
