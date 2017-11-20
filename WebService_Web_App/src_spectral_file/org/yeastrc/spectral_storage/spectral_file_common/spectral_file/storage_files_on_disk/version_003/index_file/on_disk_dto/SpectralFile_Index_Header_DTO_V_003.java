package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.on_disk_dto;

/**
 * For writing, this is populated in SpectralFile_Writer_GZIP_V_003
 *
 */
public class SpectralFile_Index_Header_DTO_V_003 {

	private byte fileFullWrittenIndicator;  //  0 = no, 1 = yes
	
	private short version;

	/**
	 * Is centroid values for the whole file
	 * 0 - false
	 * 1 - true
	 * 2 - both - both false and true found in the file
	 */
	private byte isCentroidWholeFile;
	
	private byte scansAreInScanNumberOrder;
	private byte scansAreInRetentionTimeOrder;
	
	private int numberOfScans;
	
	private long totalBytesForAllSingleScans;
	
	private int first_SingleScan_ScanNumber;
	
	private long first_SingleScan_FileIndex;

	//  Values for scanSizeType, scanNumberOffsetType in class SpectralFile_Index_Header_DTO_V_003__Constants
	
	/**
	 * 1 - byte
	 * 2 - short
	 * 3 - int
	 */
	private byte scanSizeType;	
	
	/**
	 * 8 - No scanNumber_Offset_From_Prev_ScanNumber per scan  
	 *        since each scan number is the next increment from the previous scan number 
	 *        (scanNumberOffset is always 1)
	 * 1 - byte
	 * 2 - short
	 * 3 - int
	 */
	private byte scanNumberOffsetType;	

	
}
