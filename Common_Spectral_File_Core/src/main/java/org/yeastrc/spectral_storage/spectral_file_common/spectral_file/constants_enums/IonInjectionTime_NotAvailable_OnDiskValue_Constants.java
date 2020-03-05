package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums;

public class IonInjectionTime_NotAvailable_OnDiskValue_Constants {
	
	/**
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 * 
	 * In Data file Reader, if value read from disk is this value, then the Java object property is not set.
	 */
	public static final float ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE = Float.NEGATIVE_INFINITY;
}
