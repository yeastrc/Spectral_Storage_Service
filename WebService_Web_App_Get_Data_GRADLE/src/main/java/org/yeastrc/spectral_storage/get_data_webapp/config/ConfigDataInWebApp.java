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
	
	private volatile static ConfigDataInWebApp instance;

	//  package private constructor
	ConfigDataInWebApp() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigDataInWebApp getSingletonInstance() { 
		return instance; 
	}

	/**
	 * Package private 
	 * @param instance
	 */
	static void setInstance(ConfigDataInWebApp instance) {
		ConfigDataInWebApp.instance = instance;
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
