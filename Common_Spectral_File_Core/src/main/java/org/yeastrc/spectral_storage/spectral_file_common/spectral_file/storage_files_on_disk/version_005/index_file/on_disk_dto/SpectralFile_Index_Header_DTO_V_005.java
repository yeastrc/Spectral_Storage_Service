package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.on_disk_dto;

import java.util.List;

/**
 * For writing, this is populated in SpectralFile_Writer_GZIP_V_005
 *
 */
public class SpectralFile_Index_Header_DTO_V_005 {
	
	private short version;

	private byte fileFullWrittenIndicator;  //  0 = no, 1 = yes

	/**
	 * totalIonCurrent value for each scan:
	 */
	private byte totalIonCurrent_ForEachScan_ComputedFromScanPeaks;  // 0 or 1
	
	private byte ionInjectionTime_NotPopulated;  // 0 or 1


	/**
	 * Number of distinct scan level values, also length of summaryDataPerScanLevelList
	 */
	private byte distinctScanLevelValuesCount;
	
	/**
	 * Length is stored in distinctScanLevelValuesCount
	 * 
	 * Each entry 
	 * 
	 * See SummaryDataPerScanLevel class.
	 */
	private List<SummaryDataPerScanLevel_DTO_V_005> summaryDataPerScanLevelList;
	
	private class SummaryDataPerScanLevel_DTO_V_005 {
		/**
		 * Scan level for this summary data entry
		 */
		byte scanLevel;
		/**
		 * number of scans with this scan level
		 */
		int numberOfScans;

		/**
		 * Is centroid values for this scan level
		 * 0 - false
		 * 1 - true
		 * 2 - both - both false and true found for this scan level the file
		 */
		private byte isCentroidScanLevel;

		/**
		 * Is the IonInjectionTime Set values for this scan level
		 * 0 - false - IonInjectionTime == null
		 * 1 - true - IonInjectionTime != null
		 * 2 - both - both IonInjectionTime == null and IonInjectionTime != null
		 *            ScanHasIonInjectionTimeConstants.SCAN_HAS_ION_INJECTION_TIME_VALUES_IN_FILE_BOTH
		 */
		Byte isIonInjectionTime_Set_ScanLevel;

		/**
		 * This is either ( Based On Value of totalIonCurrent_ForEachScan_ComputedFromScanPeaks ):
		 * A) Sum of TotalIonCurrent for all scans with this scan level ( totalIonCurrent_ForEachScan_ComputedFromScanPeaks == 0 )
		 * B) Same as totalIonCurrent_SumOf_IntensityOfPeaks ( totalIonCurrent_ForEachScan_ComputedFromScanPeaks == 1 )
		 */
		double totalIonCurrent_SumOf_TotalIonCurrent_OfScans;
		
		/**
		 * Sum of intensity of all peaks for all scans with this scan level
		 */
		double totalIonCurrent_SumOf_IntensityOfPeaks;
	}
	
	private byte scansAreInScanNumberOrder;
	private byte scansAreInRetentionTimeOrder;
	
	private int numberOfScans;
	
	private long totalBytesForAllSingleScans;
	
	private int first_SingleScan_ScanNumber;
	
	private long first_SingleScan_FileIndex;

	//  Values for scanSizeType, scanNumberOffsetType in class SpectralFile_Index_Header_DTO_V_005__Constants
	
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
