package org.yeastrc.spectral_storage.get_data_webapp.config;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileWebappConfigException;

/**
 * Update ConfigDataInWebApp with contents in config file
 *
 */
public class ConfigDataInWebApp_Reader {

	private static final Logger log = LoggerFactory.getLogger(ConfigDataInWebApp_Reader.class);
	

	private static String CONFIG_OVERRIDES_FILENAME = "spectral_storage_get_data_config.properties";

	private static String PROPERTY_NAME__WEBAPP_WORK_DIRECTORY = "webapp.work.directory";
	private static String ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY = "SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY";

	//  private constructor
	private ConfigDataInWebApp_Reader() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigDataInWebApp_Reader getInstance() { 
		return new ConfigDataInWebApp_Reader(); 
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void readConfigDataInWebApp() throws Exception {
		
		ConfigDataInWebApp configDataInWebApp = new ConfigDataInWebApp();

		String webappWorkDirectoryString = System.getenv( ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY );

		if ( webappWorkDirectoryString != null ) {
			webappWorkDirectoryString = webappWorkDirectoryString.trim();
		}
		if ( StringUtils.isNotEmpty( webappWorkDirectoryString ) ) {

			log.warn( "INFO: Webapp Work Directory - to use: Value found in Environment Variable: '" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY + "' with value: " + webappWorkDirectoryString );

			File workDirectory = new File( webappWorkDirectoryString );
			if ( ! ( workDirectory.exists() && workDirectory.isDirectory() && workDirectory.canRead() ) ) {
				String msg = "INFO: Webapp Work Directory - to use: Value found in Environment Variable: '" 
						+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY + "' with value: " + webappWorkDirectoryString
						+ "' does not exist, is not  a directory, or is not readable. ";
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}

			configDataInWebApp.setWebappWorkDirectory( workDirectory );

			log.warn( "INFO: '" + PROPERTY_NAME__WEBAPP_WORK_DIRECTORY 
					+ "' has value: " 
					+ configDataInWebApp.getWebappWorkDirectory().getCanonicalPath() );
		} else {

			//  Not in config file or Environment Variable so get from JVM -D Property

			Properties prop = System.getProperties();
			webappWorkDirectoryString = prop.getProperty(ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY);

			if ( webappWorkDirectoryString != null ) {

				webappWorkDirectoryString = webappWorkDirectoryString.trim();
			}

			if ( StringUtils.isNotEmpty( webappWorkDirectoryString ) ) {

				log.warn( "INFO: Webapp Work Directory - to use: Value found in JVM param: '-D" + ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY + "' with value: " + webappWorkDirectoryString );

				File workDirectory = new File( webappWorkDirectoryString );
				if ( ! ( workDirectory.exists() && workDirectory.isDirectory() && workDirectory.canRead() ) ) {
					String msg = "INFO: Webapp Work Directory - to use: Value found in JVM param: '-D" 
							+ ENVIRONMENT_VARIABLE__SPECTRAL_STORAGE_WEBAPP_WORK_DIRECTORY + "' with value: " + webappWorkDirectoryString
							+ "' does not exist, is not  a directory, or is not readable. ";
					log.error( msg );
					throw new SpectralFileWebappConfigException( msg );
				}

				configDataInWebApp.setWebappWorkDirectory( workDirectory );

				log.warn( "INFO: '" + PROPERTY_NAME__WEBAPP_WORK_DIRECTORY 
						+ "' has value: " 
						+ configDataInWebApp.getWebappWorkDirectory().getCanonicalPath() );
			} else {
				
				webappWorkDirectoryString = processPropertiesFilename( CONFIG_OVERRIDES_FILENAME );

				if ( StringUtils.isEmpty( webappWorkDirectoryString ) ) {
					log.warn( "Property '" + PROPERTY_NAME__WEBAPP_WORK_DIRECTORY 
							+ "' in config is empty or missing.  " );

				} else { 

					File workDirectory = new File( webappWorkDirectoryString );
					if ( ! ( workDirectory.exists() && workDirectory.isDirectory() && workDirectory.canRead() ) ) {
						String msg = "Property '" + PROPERTY_NAME__WEBAPP_WORK_DIRECTORY 
								+ "' in config does not exist, is not  a directory, or is not readable. Value: "
								+ webappWorkDirectoryString;
						log.error( msg );
						throw new SpectralFileWebappConfigException( msg );
					}

					configDataInWebApp.setWebappWorkDirectory( workDirectory );

					log.warn( "INFO: '" + PROPERTY_NAME__WEBAPP_WORK_DIRECTORY 
							+ "' has value: " 
							+ configDataInWebApp.getWebappWorkDirectory().getCanonicalPath() );
				}
			}
		}
		
		ConfigDataInWebApp.setInstance( configDataInWebApp );
	}

	/**
	 * @param propertiesFilename
	 * @param configDataInWebApp
	 * @throws IOException
	 * @throws SpectralFileWebappConfigException 
	 */
	private String processPropertiesFilename( 
			String propertiesFilename ) throws Exception {

		InputStream propertiesFileAsStream = null;
		try {
			//  Get config file from class path
			ClassLoader thisClassLoader = this.getClass().getClassLoader();
			URL configPropFile = thisClassLoader.getResource( propertiesFilename );
			if ( configPropFile == null ) {
				//  No properties file
				return null;  //  EARLY EXIT
			} else {
				log.info( "Properties file '" 
						+ propertiesFilename 
						+ "' found, load path = " 
						+ configPropFile.getFile() );
			}
			propertiesFileAsStream = thisClassLoader.getResourceAsStream( propertiesFilename );
			if ( propertiesFileAsStream == null ) {
				//  No properties file
				return null;  //  EARLY EXIT
			}
			Properties configProps = new Properties();
			configProps.load(propertiesFileAsStream);
			String propertyValue = null;
			propertyValue = configProps.getProperty( PROPERTY_NAME__WEBAPP_WORK_DIRECTORY );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				return propertyValue;
			}
			
			return null;

		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" 
					+ propertiesFilename 
					+ "', exception: " 
					+ e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
	}
	
}
