package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.scans_lvl_gt_1_partial.constants;

public class SpectralFile_ScansLvlGt1Partial_Header_DTO_V_003__Constants {

//	public static final int FIRST_SINGLE_SCAN_SCAN_NUMBER_NOT_SET = -1;

//	public static final byte SCAN_NUMBER_OFFSET_TYPE_BYTE = 1;
//	public static final byte SCAN_NUMBER_OFFSET_TYPE_SHORT = 2;
//	public static final byte SCAN_NUMBER_OFFSET_TYPE_INT = 3;
//	public static final byte SCAN_NUMBER_OFFSET_TYPE_NONE__SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_1 = 8;
	

	public static final int FIRST_SINGLE_SCAN_PARENT_SCAN_NUMBER_NOT_SET = -1;

	public static final byte PARENT_SCAN_NUMBER_OFFSET_TYPE_BYTE = 1;
	public static final byte PARENT_SCAN_NUMBER_OFFSET_TYPE_SHORT = 2;
	public static final byte PARENT_SCAN_NUMBER_OFFSET_TYPE_INT = 3;
	public static final byte PARENT_SCAN_NUMBER_OFFSET_TYPE_NONE__PARENT_SCAN_NUMBER_OFFSET_ALWAYS_DEFAULT_NEGATIVE_1 = 8;
	
	public static final byte PARENT_SCAN_NUMBER_OFFSET_DEFAULT_NEGATIVE_1 = -1;
}
