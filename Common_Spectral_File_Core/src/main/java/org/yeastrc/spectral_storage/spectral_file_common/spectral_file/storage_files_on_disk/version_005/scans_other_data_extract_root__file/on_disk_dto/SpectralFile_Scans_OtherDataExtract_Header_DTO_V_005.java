package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.on_disk_dto;

public class SpectralFile_Scans_OtherDataExtract_Header_DTO_V_005 {

	private short version;
	
	private byte fileFullWrittenIndicator;  //  0 = no, 1 = yes
	

	/**
	 * 0 -  No Ion Injection Time Data Written (Have to compare the value before can return the value)
	 * 1 - Yes Ion Injection Time Data Written (Have to compare the value before can return the value)
	 */
	private byte ionInjectionTime_DataField_Written;	
}
