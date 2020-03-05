package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames;

import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;

/**
 *  
 *
 */
public class CreateSpectralStorageFilenames {

	private static final Logger log = LoggerFactory.getLogger(CreateSpectralStorageFilenames.class);
	
	/**
	 * private constructor
	 */
	private CreateSpectralStorageFilenames(){}
	public static CreateSpectralStorageFilenames getInstance( ) throws Exception {
		CreateSpectralStorageFilenames instance = new CreateSpectralStorageFilenames();
		return instance;
	}
	
	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Data_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.DATA_FILENAME_SUFFIX;
	}
	
	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Data_Filename_FileFormatVersion_Suffix( String hash, int fileFormatVersion ) throws Exception {
		
		String fileFormatVersionString = String.format("%03d", fileFormatVersion ); // Zero pad to 3 decimals
		
		 String filename = 
				 hash 
				 + SpectralStorage_Filename_Constants.DATA_FILENAME_SUFFIX 
				 + SpectralStorage_Filename_Constants.DATA_INDEX_FILENAME_FILE_FORMAT_VERSION_EXTRA_SUFFIX
				 + fileFormatVersionString;
		 
		 return filename;
	}

	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Index_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.INDEX_FILENAME_SUFFIX;
	}

	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Index_Filename_FileFormatVersion_Suffix( String hash, int fileFormatVersion ) throws Exception {
		
		String fileFormatVersionString = String.format("%03d", fileFormatVersion ); // Zero pad to 3 decimals
		
		 String filename = 
				 hash 
				 + SpectralStorage_Filename_Constants.INDEX_FILENAME_SUFFIX 
				 + SpectralStorage_Filename_Constants.DATA_INDEX_FILENAME_FILE_FORMAT_VERSION_EXTRA_SUFFIX
				 + fileFormatVersionString;
		 
		 return filename;
	}

	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Scans_Level_Gt_1_Partial_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.SCANS_LEVEL_GT_1_PARTIAL_FILENAME_SUFFIX;
	}
	
	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Scans_OtherExtractData_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.SCANS_OTHER_EXTRACT_DATA_FILENAME_SUFFIX;
	}
	
	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Data_Index_Files_Started_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.DATA_INDEX_FILES_STARTED_FILENAME_SUFFIX;
	}
	
	/**
	 * @param hash
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_Data_Index_Files_Complete_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.DATA_INDEX_FILES_COMPLETE_FILENAME_SUFFIX;
	}
	
	//////////////////////////////////////////
	
	////   Special filename code that takes more parameters

	/**
	 * Get Filename for SCAN_BINNED_INTENSITY_ON_RT_MZ_FILENAME_SUFFIX_PART_1
	 * 
	 * @param hash
	 * @param rtBinSizeInSeconds
	 * @param mzBinSizeInMZ
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__JSON_GZIP_Filename( String hash, long rtBinSizeInSeconds, long mzBinSizeInMZ  ) throws Exception {
		
		return hash 
				+ SpectralStorage_Filename_Constants.SCAN_BINNED_INTENSITY_ON_RT_MZ__JSON_GZIPPED_FILENAME_SUFFIX_START
				+ Long.toString( rtBinSizeInSeconds )
				+ "_"
				+ Long.toString( mzBinSizeInMZ );
	}

	/**
	 * Get Filename for SCAN_BINNED_INTENSITY_ON_RT_MZ_FILENAME_SUFFIX_PART_1
	 * 
	 * @param hash
	 * @param rtBinSizeInSeconds
	 * @param mzBinSizeInMZ
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__Binary_GZ_Filename( String hash, long rtBinSizeInSeconds, long mzBinSizeInMZ  ) throws Exception {
		
		return hash 
				+ SpectralStorage_Filename_Constants.SCAN_BINNED_INTENSITY_ON_RT_MZ__BINARY_GZ__FILENAME_SUFFIX_START
				+ Long.toString( rtBinSizeInSeconds )
				+ "_"
				+ Long.toString( mzBinSizeInMZ );
	}

	/**
	 * Get Filename for SCAN_BINNED_INTENSITY_ON_RT_MZ__BINARY_NO_INTENSITIES__FILENAME_SUFFIX_START
	 * 
	 * @param hash
	 * @param rtBinSizeInSeconds
	 * @param mzBinSizeInMZ
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__Binary_NoIntensities_Filename( String hash, long rtBinSizeInSeconds, long mzBinSizeInMZ  ) throws Exception {
		
		return hash 
				+ SpectralStorage_Filename_Constants.SCAN_BINNED_INTENSITY_ON_RT_MZ__BINARY_NO_INTENSITIES__FILENAME_SUFFIX_START
				+ Long.toString( rtBinSizeInSeconds )
				+ "_"
				+ Long.toString( mzBinSizeInMZ );
	}

	/**
	 * Get Filename for SCAN_BINNED_INTENSITY_ON_RT_MZ__JSON_GZIP_NO_INTENSITIES__FILENAME_SUFFIX_START
	 * 
	 * @param hash
	 * @param rtBinSizeInSeconds
	 * @param mzBinSizeInMZ
	 * @return
	 * @throws Exception
	 */
	public String createSpectraStorage_ScanBinnedIntensityOn_RT_MZ__JSON_GZIP_NoIntensities_Filename( String hash, long rtBinSizeInSeconds, long mzBinSizeInMZ  ) throws Exception {
		
		return hash 
				+ SpectralStorage_Filename_Constants.SCAN_BINNED_INTENSITY_ON_RT_MZ__JSON_GZIP_NO_INTENSITIES__FILENAME_SUFFIX_START
				+ Long.toString( rtBinSizeInSeconds )
				+ "_"
				+ Long.toString( mzBinSizeInMZ );
	}
	
	
}
