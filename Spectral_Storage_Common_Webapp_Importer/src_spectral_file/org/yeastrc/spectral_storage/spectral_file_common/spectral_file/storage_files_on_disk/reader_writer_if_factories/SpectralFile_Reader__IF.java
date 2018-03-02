package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories;

import java.util.List;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SpectralFile_Result_RetentionTime_ScanNumber;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results.SummaryDataPerScanLevel;


/**
 * 
 *
 */
public interface SpectralFile_Reader__IF {
	
	/**
	 * Is the version number in parameter supported by this reader.
	 * A double check that the correct reader is created for the version
	 * @param version
	 * @return
	 */
	public boolean isVersionSupported( short version );
	
	/**
	 * @throws Exception
	 */
	public void close() throws Exception;

	public void init( String hash_String, CommonReader_File_And_S3 commonReader_File_And_S3 ) throws Exception;

	public SpectralFile_Header_Common getHeader()  throws Exception;

	public SpectralFile_SingleScan_Common getScanForScanNumber( int scanNumber) throws Exception;

	/**
	 * No Scan Peaks will be returned
	 * @param scanNumber
	 * @return
	 * @throws Exception
	 */
	public SpectralFile_SingleScan_Common getScanDataNoScanPeaksForScanNumber( int scanNumber) throws Exception;
	
	public Byte getScanLevelForScanNumber( int scanNumber) throws Exception;
	
	public List<Integer> getScanNumbersForScanLevelsToIncludeScanLevelsToExclude( List<Integer> scanLevelsToInclude, List<Integer> scanLevelsToExclude ) throws Exception;

	public SpectralFile_Result_RetentionTime_ScanNumber getScanRetentionTimeForScanNumber( int scanNumber ) throws Exception;
	
	public List<SpectralFile_Result_RetentionTime_ScanNumber> getScanRetentionTimes_All() throws Exception;

	public List<Integer> getScanNumbersForRetentionTimeRange(float retentionTimeStart, float retentionTimeEnd ) throws Exception;

	public List<Integer> getScanNumbersForRetentionTimeRangeScanLevel(float retentionTimeStart, float retentionTimeEnd, byte scanLevel ) throws Exception;
	
	public List<SummaryDataPerScanLevel> getSummaryDataPerScanLevel_All() throws Exception;
}