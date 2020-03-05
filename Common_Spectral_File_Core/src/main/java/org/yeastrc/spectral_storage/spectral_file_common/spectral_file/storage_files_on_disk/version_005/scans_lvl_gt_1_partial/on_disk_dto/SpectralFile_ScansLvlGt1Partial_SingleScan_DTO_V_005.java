package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_lvl_gt_1_partial.on_disk_dto;

/**
 * Single Scan in Scans Level > 1 Partial  File
 *
 */
public class SpectralFile_ScansLvlGt1Partial_SingleScan_DTO_V_005 {

	//  Only applicable where level > 1
	
	//  Not in file if header prarentScanNumberOffsetType is set to SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_1
	//  May be byte, short, or int, depending on value in header.	
	private int parentScanNumberOffsetFromScanNumber;
	
	private byte precursorCharge;
	private double precursor_M_Over_Z;
	
}
