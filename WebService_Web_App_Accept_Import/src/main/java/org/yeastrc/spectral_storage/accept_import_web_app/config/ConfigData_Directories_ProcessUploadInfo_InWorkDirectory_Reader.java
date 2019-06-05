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
	
	private static String BOOLEAN_STRING_TRUE = "true";

	//  No Default file
//	private static String CONFIG_DEFAULTS_FILENAME = "spectral_server_accept_import_config_dirs_process_cmd_defaults.properties";
	
	private static String CONFIG_OVERRIDES_FILENAME = "spectral_server_accept_import_config_dirs_process_cmd.properties";

	private static String PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY = "scan.storage.base.directory";
	private static String PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY = "temp.upload.base.directory";
	
	private static String PROPERTY_NAME__S3_BUCKET = "s3.bucket";
	private static String PROPERTY_NAME__S3_REGION = "s3.region";
	
	private static String PROPERTY_NAME__SUBMITTED_SCAN_FILE_PATH_RESTRICTIONS = "submitted.scan.file.path.restrictions";
	
	
	private static String PROPERTY_NAME__PROCESS_SCAN_UPLOAD_JAR_FILE = "process.scan.upload.jar.file";
	private static String PROPERTY_NAME__JAVA_EXECUTABLE = "java.executable";
	private static String PROPERTY_NAME__JAVA_EXECUTABLE_PARAMETERS = "java.executable.parameters";
	
	private static String PROPERTY_NAME__DELETE_UPLOADED_SCAN_FILE_ON_SUCCESSFUL_IMPORT = 
			"delete.uploaded.scan.file.on.successful.import";
	
	//  Email on error config

	//  Probably used.  SMTP Server Host
	private static String PROPERTY_NAME__EMAIL_SMTP_HOST = "email.smtp.host";

	//  Probably not used
	private static String PROPERTY_NAME__EMAIL_WEBSERVICE_URL = "email.webservice.url";

	private static String PROPERTY_NAME__EMAIL_FROM_ADDRESS = "email.from.address";
	
	private static String PROPERTY_NAME__EMAIL_TO_ADDRESSES = "email.to.addresses";

	private static String PROPERTY_NAME__EMAIL_TO_ADDRESSES_FAILED_ONLY = "email.to.addresses.failed.only";

	private static String PROPERTY_NAME__EMAIL_MACHINE_NAME = "email.machine.name";

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

//		processPropertiesFilename( CONFIG_DEFAULTS_FILENAME, IsDefaultPropertiesFile.YES, AllowNoPropertiesFile.NO, configData_Directories_ProcessUploadCommand_InWorkDirectory );
		
		processPropertiesFilename( 
				CONFIG_OVERRIDES_FILENAME, 
				IsDefaultPropertiesFile.NO, 
				AllowNoPropertiesFile.NO, 
				configData_Directories_ProcessUploadCommand_InWorkDirectory,
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
		}
		
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

		File tempScanUploadBaseDirectory = new File( internalConfigDirectoryStrings.tempScanUploadBaseDirectory );
		
		if ( ! ( tempScanUploadBaseDirectory.exists() && tempScanUploadBaseDirectory.isDirectory() && tempScanUploadBaseDirectory.canRead() ) ) {
			String msg = "!!!Property '" + PROPERTY_NAME__TEMP_UPLOAD_BASE_DIRECTORY 
					+ "' in config does not exist or is not a directory or is not readable.  Value:  " 
					+ internalConfigDirectoryStrings.tempScanUploadBaseDirectory;
			log.error( msg );
			throw new SpectralFileWebappConfigException( msg );
		}

		configData_Directories_ProcessUploadCommand_InWorkDirectory.setTempScanUploadBaseDirectory( tempScanUploadBaseDirectory );


		if ( StringUtils.isEmpty( internalConfigDirectoryStrings.scanStorageBaseDirectory ) ) {

			log.warn( "INFO: '" + PROPERTY_NAME__SCAN_STORAGE_BASE_DIRECTORY + "' has value: " 
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

		if ( StringUtils.isEmpty( configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailSmtpServerHost() ) ) {
		} else {
			log.warn( "INFO: '" + PROPERTY_NAME__EMAIL_WEBSERVICE_URL + "' has value: " 
					+ configData_Directories_ProcessUploadCommand_InWorkDirectory.getEmailWebserviceURL() );
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

			propertyValue = configProps.getProperty( PROPERTY_NAME__S3_BUCKET );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Bucket( propertyValue );
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__S3_REGION );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setS3Region( propertyValue );
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

			propertyValue = configProps.getProperty( PROPERTY_NAME__EMAIL_WEBSERVICE_URL );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				configData_Directories_ProcessUploadCommand_InWorkDirectory.setEmailWebserviceURL( propertyValue );
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

		/**
		 * The 'temp' Base directory that scan files are uploaded into
		 */
		private String tempScanUploadBaseDirectory;

	}
	
}
