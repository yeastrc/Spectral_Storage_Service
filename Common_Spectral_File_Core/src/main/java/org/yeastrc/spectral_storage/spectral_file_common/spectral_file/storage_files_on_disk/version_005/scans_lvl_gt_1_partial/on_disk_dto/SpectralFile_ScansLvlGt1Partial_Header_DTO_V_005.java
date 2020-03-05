package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_lvl_gt_1_partial.on_disk_dto;

/**
 * Header in Scans Level > 1 Partial  File
 *
 */
public class SpectralFile_ScansLvlGt1Partial_Header_DTO_V_005 {

	private short version;
	
	private byte fileFullWrittenIndicator;  //  0 = no, 1 = yes
	

	/**
	 * 8 - No parentScanNumber_Offset_From_Prev_ParentScanNumber per scan  
	 *        since each parent scan number is scan number - 1 
	 *        (parentScanNumberOffset is always -1)
	 * 1 - byte
	 * 2 - short
	 * 3 - int
	 */
	private byte parentScanNumberOffsetType;	
}
