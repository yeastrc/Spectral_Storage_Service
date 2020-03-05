package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums;

/**
 * Main Data Storage Filname / S3 object name Suffixes 
 *
 */
public class SpectralStorage_Filename_Constants {

	public static final String DATA_FILENAME_SUFFIX = ".data";
	public static final String INDEX_FILENAME_SUFFIX = ".index";
	
	/**
	 * Appended to both Data and Index Filenames to indicate what the version is.  Version is written as file contents
	 */
	public static final String DATA_INDEX_FILENAME_FILE_FORMAT_VERSION_EXTRA_SUFFIX = "_v_";
	
	public static final String SCANS_LEVEL_GT_1_PARTIAL_FILENAME_SUFFIX = ".scnlvlgt1p";

	public static final String SCANS_OTHER_EXTRACT_DATA_FILENAME_SUFFIX = ".scn_other_extact_data";
	
	
	/**
	 * Prefix for Intensity Binned JSON GZIP file
	 */
	public static final String SCAN_BINNED_INTENSITY_ON_RT_MZ__JSON_GZIPPED_FILENAME_SUFFIX_START = ".intbin_";

	/**
	 * Prefix for Intensity Binned Binary file
	 */
	public static final String SCAN_BINNED_INTENSITY_ON_RT_MZ__BINARY_GZ__FILENAME_SUFFIX_START = ".intbin_binary_gz_";

	/**
	 * Prefix for Intensity Binned Binary file - But no Summed Intensities
	 */
	public static final String SCAN_BINNED_INTENSITY_ON_RT_MZ__BINARY_NO_INTENSITIES__FILENAME_SUFFIX_START = ".intbin_binary_no_intensities_";

	/**
	 * Prefix for Intensity Binned Binary file - But no Summed Intensities
	 */
	public static final String SCAN_BINNED_INTENSITY_ON_RT_MZ__JSON_GZIP_NO_INTENSITIES__FILENAME_SUFFIX_START = ".intbin_json_gz_no_intensities_";
	
	
	//  added after main suffix for while writing the file
	public static final String IN_PROGRESS_FILENAME_SUFFIX_SUFFIX = "_in_prog";

	public static final String DATA_INDEX_FILES_STARTED_FILENAME_SUFFIX = ".data_index_files_started";

	public static final String DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX = ".data_index_files_complete";
}
