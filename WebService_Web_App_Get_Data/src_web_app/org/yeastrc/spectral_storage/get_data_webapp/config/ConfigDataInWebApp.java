package org.yeastrc.spectral_storage.get_data_webapp.config;

import java.io.File;

/**
 * Config data for config file in web app
 * 
 * The Primary configuration for the web app
 * 
 * 
 * Singleton Instance
 *
 */
public class ConfigDataInWebApp {
	
	private static final ConfigDataInWebApp instance = new ConfigDataInWebApp();

	//  private constructor
	private ConfigDataInWebApp() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigDataInWebApp getSingletonInstance() { 
		return instance; 
	}

	/**
	 * The 'work' directory for the webapp
	 */
	private File webappWorkDirectory;

	/**
	 * The 'work' directory for the webapp
	 * @return
	 */
	public File getWebappWorkDirectory() {
		return webappWorkDirectory;
	}

	/**
	 * The 'work' directory for the webapp
	 * 
	 * @param webappWorkDirectory
	 */
	public void setWebappWorkDirectory(File webappWorkDirectory) {
		this.webappWorkDirectory = webappWorkDirectory;
	}

}
