package org.yeastrc.spectral_storage.get_data_webapp.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
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

	//  No Default file
//	private static String CONFIG_DEFAULTS_FILENAME = "spectral_storage_get_data_scan_data_location_defaults.properties";
	
	private static String CONFIG_OVERRIDES_FILENAME = "spectral_storage_get_data_scan_data_location.properties";

	private static String PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY = "scan.storage.base.directory";

//  AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

	private static String PROPERTY_NAME__S3_BUCKET = "s3.bucket";
	private static String PROPERTY_NAME__S3_REGION = "s3.region";
	
	//  This value comes from Environment variable, jvm -D parameter, or property file, in that order.

	private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE = "SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE";

	private static String PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS = "max.number.scans.to.return.for.requests.that.include.scan.peaks";

	private static enum IsDefaultPropertiesFile { YES, NO }
	private static enum AllowNoPropertiesFile { YES, NO }
	
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
		
		//  Local Internal class
		
		InternalConfigDirectoryStrings internalConfigDirectoryStrings = new InternalConfigDirectoryStrings();

//		processPropertiesFilename( CONFIG_DEFAULTS_FILENAME, IsDefaultPropertiesFile.YES, AllowNoPropertiesFile.NO, configData_ScanDataLocation_InWorkDirectory );
		
		processPropertiesFilename( 
				CONFIG_OVERRIDES_FILENAME, 
				IsDefaultPropertiesFile.NO, 
				AllowNoPropertiesFile.NO, 
				configData_ScanDataLocation_InWorkDirectory,
				internalConfigDirectoryStrings );
		
		log.warn( "Finished processing config file '" 
				+ CONFIG_OVERRIDES_FILENAME
				+ "'." );
		

	//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

		if ( StringUtils.isNotEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) 
				&& StringUtils.isNotEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() ) ) {
			String msg = "Cannot set both properties '"
				+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
				+ "' and '"
				+ PROPERTY_NAME__S3_BUCKET
				+ "' to a value in config.";
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

			configData_ScanDataLocation_InWorkDirectory.setScanStorageBaseDirectory( scanStorageBaseDirectory );

			log.warn( "INFO: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' has value: " 
					+ internalConfigDirectoryStrings.scanStorageBaseDirectory );
		
		} else {

			//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

				if ( StringUtils.isEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Bucket() ) ) {
					String msg = "Must set One of properties '"
						+ PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY 
						+ "' and '"
						+ PROPERTY_NAME__S3_BUCKET
						+ "' to a value in config.";
					log.error( msg );
					throw new SpectralFileWebappConfigException( msg );
				}
	
				log.warn( "INFO: '" + PROPERTY_NAME__S3_BUCKET + "' has value: " 
						+ configData_ScanDataLocation_InWorkDirectory.getS3Bucket() );
		}

		//   AWS S3 Support commented out.  See file ZZ__AWS_S3_Support_CommentedOut.txt in GIT repo root.

		if ( StringUtils.isNotEmpty( configData_ScanDataLocation_InWorkDirectory.getS3Region() ) ) {
			log.warn( "INFO: '" + PROPERTY_NAME__S3_REGION + "' has value: " 
					+ configData_ScanDataLocation_InWorkDirectory.getS3Region() );
		}
		


//		if ( configData_ScanDataLocation_InWorkDirectory.getMaxNumberScansReturn() != null ) {
//			log.warn( "INFO: '" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "' has value: " 
//					+ configData_ScanDataLocation_InWorkDirectory.getMaxNumberScansReturn() );
//			
//
//			//  This value comes from Environment variable, jvm -D parameter, or property file, in that order.
//
//			private static final String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE = "SPECTRAL_STORAGE_MAX_SCAN_BATCH_SIZE";
//
//			private static String PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS = "max.number.scans.to.return.for.requests.that.include.scan.peaks";
//
//		}
		
		
		
		ConfigData_ScanDataLocation_InWorkDirectory.setSingletonInstance( configData_ScanDataLocation_InWorkDirectory );
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
			ConfigData_ScanDataLocation_InWorkDirectory configData_ScanDataLocation_InWorkDirectory,
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

			{
				String propertyValue = configProps.getProperty( PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY );
				if ( StringUtils.isNotEmpty( propertyValue ) ) {
					internalConfigDirectoryStrings.scanStorageBaseDirectory = propertyValue.trim();
				}
			}

			{
				String propertyValue = configProps.getProperty( PROPERTY_NAME__S3_BUCKET );
				if ( StringUtils.isNotEmpty( propertyValue ) ) {
					configData_ScanDataLocation_InWorkDirectory.setS3Bucket( propertyValue.trim() );
				}
			}

			{
				String propertyValue = configProps.getProperty( PROPERTY_NAME__S3_REGION );
				if ( StringUtils.isNotEmpty( propertyValue ) ) {
					configData_ScanDataLocation_InWorkDirectory.setS3Region( propertyValue.trim() );
				}
			}
			
			{
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

						maxNumberScansReturn_String = configProps.getProperty( PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS );

						if ( maxNumberScansReturn_String != null ) {

							maxNumberScansReturn_String = maxNumberScansReturn_String.trim();
						}

						if ( StringUtils.isNotEmpty( maxNumberScansReturn_String ) ) {

							log.warn( "INFO: maxNumberScansReturn to use: Value found in Properties file with key: '" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "' with value: " + maxNumberScansReturn_String );

							valueFoundInLabel = "maxNumberScansReturn to use: Value found in Properties file with key:" + PROPERTY_NAME__MAX_NUMBER_SCANS_TO_RETURN_FOR_REQUESTS_THAT_INCLUDE_SCAN_PEAKS + "'";
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
		 * The Base Directory that the scans are written to for perm storage
		 */
		private String scanStorageBaseDirectory;

	}
	
}
