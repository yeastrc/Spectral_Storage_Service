package org.yeastrc.spectral_storage.web_app.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Config data for config file in web app Work Directory
 * 
 * The Secondary configuration for the web app
 * 
 * Config for which remote computers can access
 * 
 * 
 * Singleton Instance
 *
 */
public class ConfigData_Allowed_Remotes_InWorkDirectory {
	
	private static final ConfigData_Allowed_Remotes_InWorkDirectory instance = new ConfigData_Allowed_Remotes_InWorkDirectory();

	//  private constructor
	private ConfigData_Allowed_Remotes_InWorkDirectory() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigData_Allowed_Remotes_InWorkDirectory getSingletonInstance() { 
		return instance; 
	}
	
	/**
	 * Main
	 */
	private Set<String> allowedRemoteIPs = new HashSet<>();
	

	/**
	 * URLs that start with "/admin_" also have applied filter AccessControl_Admin_ServletFilter
	 */
	private Set<String> allowedAdminRemoteIPs = new HashSet<>();

	public void addAllowedRemoteIP( String allowedRemoteIP) {
		this.allowedRemoteIPs.add( allowedRemoteIP );
	}
	public void clearAllowedRemoteIP_Collection() {
		this.allowedRemoteIPs.clear();
	}

	public void addAllowedAdminRemoteIP( String allowedRemoteIP) {
		this.allowedAdminRemoteIPs.add( allowedRemoteIP );
	}
	public void clearAllowedAdminRemoteIP_Collection() {
		this.allowedAdminRemoteIPs.clear();
	}
	


	public Set<String> getAllowedRemoteIPs() {
		return allowedRemoteIPs;
	}

	public void setAllowedRemoteIPs(Set<String> allowedRemoteIPs) {
		this.allowedRemoteIPs = allowedRemoteIPs;
	}

	public Set<String> getAllowedAdminRemoteIPs() {
		return allowedAdminRemoteIPs;
	}

	public void setAllowedAdminRemoteIPs(Set<String> allowedAdminRemoteIPs) {
		this.allowedAdminRemoteIPs = allowedAdminRemoteIPs;
	}
	

}
