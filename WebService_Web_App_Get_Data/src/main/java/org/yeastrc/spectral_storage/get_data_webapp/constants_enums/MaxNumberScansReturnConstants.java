package org.yeastrc.spectral_storage.get_data_webapp.constants_enums;

public class MaxNumberScansReturnConstants {

	/**
	 * For webservices that serialize the whole response at once.  DEFAULT.
	 * 
	 * If create streaming response later, this doesn't apply to them.
	 * 
	 * This is put in place to keep from consuming all the RAM in the server and the client.
	 */
	public static final int MAX_NUMBER_SCANS_RETURN_FOR_IMMEDIATE_WEBSERVICES__DEFAULT = 20;
}
