package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_GZIP_V_005;

/**
 * Create a SpectralFile_Writer__IF of the latest version
 *
 */
public class SpectralFile_Writer_CurrentFormat_Factory {

	/**
	 * private constructor
	 */
	private SpectralFile_Writer_CurrentFormat_Factory(){}
	public static SpectralFile_Writer_CurrentFormat_Factory getInstance( ) throws Exception {
		SpectralFile_Writer_CurrentFormat_Factory instance = new SpectralFile_Writer_CurrentFormat_Factory();
		return instance;
	}
	
	/**
	 * Create a SpectralFile_Writer__IF of the latest version
	 * 
	 * @return
	 * @throws Exception
	 */
	public SpectralFile_Writer__IF getSpectralFile_Writer_LatestVersion() throws Exception {
		return SpectralFile_Writer_GZIP_V_005.getInstance();
	}
}
