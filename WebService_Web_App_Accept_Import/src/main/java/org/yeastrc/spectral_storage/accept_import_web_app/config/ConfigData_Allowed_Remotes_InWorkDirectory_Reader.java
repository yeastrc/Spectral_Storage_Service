package org.yeastrc.spectral_storage.accept_import_web_app.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.accept_import_web_app.exceptions.SpectralFileWebappConfigException;

/**
 * Update ConfigData_Allowed_Remotes_InWorkDirectory with contents in config file
 *
 */
public class ConfigData_Allowed_Remotes_InWorkDirectory_Reader {

	private static final Logger log = LoggerFactory.getLogger(ConfigData_Allowed_Remotes_InWorkDirectory_Reader.class);
	

	private static String CONFIG_DEFAULTS_FILENAME = "spectral_server_accept_import_config_allowed_remotes_defaults.properties";
	private static String CONFIG_OVERRIDES_FILENAME = "spectral_server_accept_import_config_allowed_remotes.properties";
	
	private static String PROPERTY_NAME__ALLOWED_REMOTE_IPS_OVERALL = "allowed.remote.ips.overall";
	private static String PROPERTY_NAME__ALLOWED_REMOTE_IPS_ADMIN = "allowed.remote.ips.admin";
	private static String PROPERTY_NAME__ALLOWED_REMOTE_IPS_UPDATE = "allowed.remote.ips.update";
	private static String PROPERTY_NAME__ALLOWED_REMOTE_IPS_IMPORTER = "allowed.remote.ips.importer";
	private static String PROPERTY_NAME__ALLOWED_REMOTE_IPS_DELIMITER = ",";
	
	private static enum IsDefaultPropertiesFile { YES, NO }
	private static enum AllowNoPropertiesFile { YES, NO }
	
	//  private constructor
	private ConfigData_Allowed_Remotes_InWorkDirectory_Reader() { }
	
	/**
	 * @return newly created instance
	 */
	public static ConfigData_Allowed_Remotes_InWorkDirectory_Reader getInstance() { 
		return new ConfigData_Allowed_Remotes_InWorkDirectory_Reader(); 
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public void readConfigDataInWebApp() throws Exception {
		
		ConfigData_Allowed_Remotes_InWorkDirectory configData_Allowed_Remotes_InWorkDirectory = new ConfigData_Allowed_Remotes_InWorkDirectory();

		processPropertiesFilename( CONFIG_DEFAULTS_FILENAME, IsDefaultPropertiesFile.YES, AllowNoPropertiesFile.NO, configData_Allowed_Remotes_InWorkDirectory );
		processPropertiesFilename( CONFIG_OVERRIDES_FILENAME, IsDefaultPropertiesFile.NO, AllowNoPropertiesFile.YES, configData_Allowed_Remotes_InWorkDirectory );
		

//		if ( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Overall() == null 
//				|| configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Overall().isEmpty() ) {
//			String msg = "Property '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_OVERALL + "' in config is empty or missing";
//			log.error( msg );
//			throw new SpectralFileWebappConfigException( msg );
//		}
//
//		if ( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Admin() == null 
//				|| configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Admin().isEmpty() ) {
//			String msg = "Property '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_ADMIN + "' in config is empty or missing";
//			log.error( msg );
//			throw new SpectralFileWebappConfigException( msg );
//		}
		
		
		log.warn( "INFO: '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_OVERALL + "' has value: " 
				+ StringUtils.join( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Overall(), " , "  ) );
		log.warn( "INFO: '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_ADMIN + "' has value: " 
				+ StringUtils.join( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Admin(), " , "  ) );
		log.warn( "INFO: '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_UPDATE + "' has value: " 
				+ StringUtils.join( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Update(), " , " ) );
		log.warn( "INFO: '" + PROPERTY_NAME__ALLOWED_REMOTE_IPS_IMPORTER + "' has value: " 
				+ StringUtils.join( configData_Allowed_Remotes_InWorkDirectory.getAllowedRemoteIPs_Importer(), " , " ) );
		
		ConfigData_Allowed_Remotes_InWorkDirectory.setInstance( configData_Allowed_Remotes_InWorkDirectory );
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
			ConfigData_Allowed_Remotes_InWorkDirectory configData_Allowed_Remotes_InWorkDirectory ) throws Exception {

		InputStream propertiesFileAsStream = null;
		try {
			if ( isDefaultPropertiesFile == IsDefaultPropertiesFile.YES ) {

				//  Get config file from class path
				ClassLoader thisClassLoader = this.getClass().getClassLoader();
				URL configPropFile = thisClassLoader.getResource( propertiesFilename );
				if ( configPropFile == null ) {
					//  No properties file
					return;  //  EARLY EXIT
					//				String msg = "Properties file '" + DB_CONFIG_FILENAME + "' not found in class path.";
					//				log.error( msg );
					//				throw new Exception( msg );
				} else {
					log.info( "Properties file '" + propertiesFilename + "' found, load path = " + configPropFile.getFile() );
				}
				propertiesFileAsStream = thisClassLoader.getResourceAsStream( propertiesFilename );
				if ( propertiesFileAsStream == null ) {
					//  No properties file
					if ( allowNoPropertiesFile == AllowNoPropertiesFile.YES ) {
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
			configProps.load(propertiesFileAsStream);
			String propertyValue = null;
						
			propertyValue = configProps.getProperty( PROPERTY_NAME__ALLOWED_REMOTE_IPS_OVERALL );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				
				String[] allowedRemoteIPs = propertyValue.split( PROPERTY_NAME__ALLOWED_REMOTE_IPS_DELIMITER );

				for ( String allowedRemoteIP : allowedRemoteIPs ) {
					configData_Allowed_Remotes_InWorkDirectory.addAllowedRemoteIP_Overall( allowedRemoteIP );
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__ALLOWED_REMOTE_IPS_ADMIN );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				
				String[] allowedRemoteIPs = propertyValue.split( PROPERTY_NAME__ALLOWED_REMOTE_IPS_DELIMITER );

				for ( String allowedRemoteIP : allowedRemoteIPs ) {
					configData_Allowed_Remotes_InWorkDirectory.addAllowedRemoteIP_Admin( allowedRemoteIP );
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__ALLOWED_REMOTE_IPS_UPDATE );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				
				String[] allowedRemoteIPs = propertyValue.split( PROPERTY_NAME__ALLOWED_REMOTE_IPS_DELIMITER );

				for ( String allowedRemoteIP : allowedRemoteIPs ) {
					configData_Allowed_Remotes_InWorkDirectory.addAllowedRemoteIP_Update( allowedRemoteIP );
				}
			}

			propertyValue = configProps.getProperty( PROPERTY_NAME__ALLOWED_REMOTE_IPS_IMPORTER );
			if ( StringUtils.isNotEmpty( propertyValue ) ) {
				
				String[] allowedRemoteIPs = propertyValue.split( PROPERTY_NAME__ALLOWED_REMOTE_IPS_DELIMITER );

				for ( String allowedRemoteIP : allowedRemoteIPs ) {
					configData_Allowed_Remotes_InWorkDirectory.addAllowedRemoteIP_Importer( allowedRemoteIP );
				}
			}
			
			
		} catch ( RuntimeException e ) {
			log.error( "Error processing Properties file '" + propertiesFilename + "', exception: " + e.toString(), e );
			throw e;
		} finally {
			if ( propertiesFileAsStream != null ) {
				propertiesFileAsStream.close();
			}
		}
	}
	
}