package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.on_disk_dto;

/**
 * Single Scan in Index File
 * 
 * TODO  Consider scan number and scan index as offsets from previous values, as smaller numbers (int,short,byte)
 *
 */
public class SpectralFile_Index_SingleScan_DTO_V_005 {

	//  May be byte, short, or int, depending on value in header.	
	private int scanSize_InDataFile_InBytes;

	//  Not in file if header scanNumberOffsetType is set to SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_1
	//  May be byte, short, or int, depending on value in header.
	private int scanNumber_Offset_From_Prev_ScanNumber;


	private byte level;
	private float retentionTime;

}
