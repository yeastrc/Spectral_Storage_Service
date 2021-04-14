package org.yeastrc.spectral_storage.get_data_webapp.config;

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
	
	private volatile static ConfigData_Allowed_Remotes_InWorkDirectory instance;

	//  package private  constructor
	ConfigData_Allowed_Remotes_InWorkDirectory() { }
	
	/**
	 * @return Singleton instance
	 */
	public static ConfigData_Allowed_Remotes_InWorkDirectory getSingletonInstance() { 
		return instance; 
	}
	
	
	/** Package Private Setter
	 * @param instance
	 */
	static void setInstance(ConfigData_Allowed_Remotes_InWorkDirectory instance) {
		ConfigData_Allowed_Remotes_InWorkDirectory.instance = instance;
	}


	/**
	 * When True, then connection for Query is allowed from any IP address
	 */
	private boolean accessAllowed_allRemoteIps_query;

	
	/**
	 * Overall.  Applied to all URLs
	 */
	private Set<String> allowedRemoteIPs_Overall = new HashSet<>();
	
	/**
	 * URLs that start with "/admin/" also have applied filter using this allowed IP list
	 */
	private Set<String> allowedRemoteIPs_Admin = new HashSet<>();

	/**
	 * URLs that start with "/query/" also have applied filter using this allowed IP list
	 */
	private Set<String> allowedRemoteIPs_Query = new HashSet<>();
	
	
	/**
	 * @param allowedRemoteIP
	 */
	public void addAllowedRemoteIP_Overall( String allowedRemoteIP) {
		this.allowedRemoteIPs_Overall.add( allowedRemoteIP );
	}
	public void clearAllowedRemoteIP_Overall_Collection() {
		this.allowedRemoteIPs_Overall.clear();
	}

	/**
	 * Adds to allowedRemoteIPs_Overall as well
	 * @param allowedRemoteIP
	 */
	public void addAllowedRemoteIP_Admin( String allowedRemoteIP) {
		this.allowedRemoteIPs_Admin.add( allowedRemoteIP );
		this.allowedRemoteIPs_Overall.add( allowedRemoteIP );
	}
	public void clearAllowedRemoteIP_Admin_Collection() {
		this.allowedRemoteIPs_Admin.clear();
	}

	/**
	 * Adds to allowedRemoteIPs_Overall as well
	 * @param allowedRemoteIP
	 */
	public void addAllowedRemoteIP_Query( String allowedRemoteIP) {
		this.allowedRemoteIPs_Overall.add( allowedRemoteIP );
		this.allowedRemoteIPs_Query.add( allowedRemoteIP );
	}
	public void clearAllowedRemoteIP_Query_Collection() {
		this.allowedRemoteIPs_Query.clear();
	}



	public Set<String> getAllowedRemoteIPs_Overall() {
		return allowedRemoteIPs_Overall;
	}

	public void setAllowedRemoteIPs_Overall(Set<String> allowedRemoteIPs_Overall) {
		this.allowedRemoteIPs_Overall = allowedRemoteIPs_Overall;
	}

	public Set<String> getAllowedRemoteIPs_Admin() {
		return allowedRemoteIPs_Admin;
	}

	public void setAllowedRemoteIPs_Admin(Set<String> allowedRemoteIPs_Admin) {
		this.allowedRemoteIPs_Admin = allowedRemoteIPs_Admin;
	}

	public Set<String> getAllowedRemoteIPs_Query() {
		return allowedRemoteIPs_Query;
	}

	public void setAllowedRemoteIPs_Query(Set<String> allowedRemoteIPs_Query) {
		this.allowedRemoteIPs_Query = allowedRemoteIPs_Query;
	}

	public boolean isAccessAllowed_allRemoteIps_query() {
		return accessAllowed_allRemoteIps_query;
	}

	public void setAccessAllowed_allRemoteIps_query(boolean accessAllowed_allRemoteIps_query) {
		this.accessAllowed_allRemoteIps_query = accessAllowed_allRemoteIps_query;
	}


}
