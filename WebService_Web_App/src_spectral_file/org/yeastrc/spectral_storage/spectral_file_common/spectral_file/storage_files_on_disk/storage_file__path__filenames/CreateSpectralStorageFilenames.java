package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.SpectralStorage_Filename_Constants;

/**
 *  
 *
 */
public class CreateSpectralStorageFilenames {

	private static final Logger log = Logger.getLogger(CreateSpectralStorageFilenames.class);
	
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
	public String createSpectraStorage_Index_Filename( String hash ) throws Exception {
		
		return hash + SpectralStorage_Filename_Constants.INDEX_FILENAME_SUFFIX;
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
}
