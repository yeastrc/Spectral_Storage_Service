package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.on_disk_dto;

/**
 * Data at beginning of Spectral file
 * 
 * !!  Do NOT Make any changes above Line with text: 
 * 		
 * 		Do NOT Make any changes above this point
 * 
 * That will allow reading all values above that point for all versions
 *
 */
public class SpectralFile_Header_V_005 {

	private short version;

	private byte fileFullWrittenIndicator;  //  0 = no, 1 = yes
	
	private long spectralStorageDataFileLength_InBytes;  // Length of this file
	
	private short mainHeaderLength; // Excludes version and this number
	
	private long scanFileLength_InBytes;
	
	private short scanFileMainHashBytesLength;
	
	private byte[] scanFileMainHashBytes;  //  Main Hash used for filename, currently SHA 384
	

	private short scanFileSHA512HashBytesLength;
	
	private byte[] scanFileSHA512HashBytes;  //  Alt hash SHA 512
	

	private short scanFileSHA1HashBytesLength;
	
	private byte[] scanFileSHA1HashBytes;  //  Alt hash SHA 1
	
	//  ===========================
			
	//   Do NOT Make any changes above this point.	

	//       Encoded Boolean values as bytes

	private byte totalIonCurrent_ForEachScan_ComputedFromScanPeaks;  // 0 or 1
	
	private byte ionInjectionTime_NotPopulated;  // 0 or 1
}
