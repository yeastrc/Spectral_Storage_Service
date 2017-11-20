package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import java.io.File;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;



/**
 * 
 *
 */
public interface SpectralFile_Writer__IF {

	/**
	 * @throws Exception
	 */
	public void close() throws Exception;

	public void open( String hash_String, File subDirForStorageFiles, SpectralFile_Header_Common spectralFile_Header_Common ) throws Exception;

	public void writeScan(SpectralFile_SingleScan_Common spectralFile_SingleScan) throws Exception;

	public long getScanPeaksTotalBytes();

	public long getScanPeaksCompressedTotalBytes();

	public long getScanPeaksTotalCount();


}