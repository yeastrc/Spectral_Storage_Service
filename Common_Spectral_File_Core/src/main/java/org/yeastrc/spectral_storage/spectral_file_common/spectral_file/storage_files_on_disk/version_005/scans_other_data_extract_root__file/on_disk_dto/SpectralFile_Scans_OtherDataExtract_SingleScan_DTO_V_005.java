package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.on_disk_dto;

/**
 * Single Scan  File
 *
 */
public class SpectralFile_Scans_OtherDataExtract_SingleScan_DTO_V_005 {

	private byte isCentroid; // 0 or 1, false or true

	/**
	 * 
	 */
	private float totalIonCurrent;	
	
	/**
	 * Only in file if header ionInjectionTime_DataField_Written is == 1
	 */
	private float ionInjectionTime;	
	

}
