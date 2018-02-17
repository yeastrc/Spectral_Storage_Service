package org.yeastrc.spectral_storage.accept_import_web_app.config;

import org.apache.log4j.Logger;

public class A_Load_Config {

	private static final Logger log = Logger.getLogger(A_Load_Config.class);

	//  private constructor
	private A_Load_Config() { }
	
	/**
	 * @return newly created instance
	 */
	public static A_Load_Config getInstance() { 
		return new A_Load_Config(); 
	}
	
	/**
	 * @throws Exception
	 */
	public void load_Config() throws Exception {
		
		ConfigDataInWebApp_Reader.getInstance().readConfigDataInWebApp();
		ConfigData_Directories_ProcessUploadInfo_InWorkDirectory_Reader.getInstance().readConfigDataInWebApp();
		ConfigData_Allowed_Remotes_InWorkDirectory_Reader.getInstance().readConfigDataInWebApp();

	}
}
