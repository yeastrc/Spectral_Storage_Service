package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file;

/**
 * Common interface to SpectralFile_Index_FileContents_DTO_... to support caching
 * 
 * Each data file reader will downcast the object to their index version
 *
 */
public interface SpectralFile_Index_FileContents_Root_IF {

	short getVersion();

}
