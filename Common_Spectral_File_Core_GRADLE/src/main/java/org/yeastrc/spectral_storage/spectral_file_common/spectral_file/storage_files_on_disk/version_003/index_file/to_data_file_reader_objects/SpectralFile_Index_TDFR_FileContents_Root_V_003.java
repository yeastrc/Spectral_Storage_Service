package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_003
 * 
 * Index File Root object
 *
 */
public class SpectralFile_Index_TDFR_FileContents_Root_V_003 implements SpectralFile_Index_FileContents_Root_IF {

	private short version;

	/**
	 * Is centroid values for the whole file
	 * 0 - false
	 * 1 - true
	 * 2 - both - both false and true found in the file
	 */
	private byte isCentroidWholeFile;

	/**
	 * summary per distinct scan level
	 */
	private List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003> summaryDataPerScanLevelList;

	private boolean scansAreInScanNumberOrder;
	private boolean scansAreInRetentionTimeOrder;
	
	private int numberOfScans;

	private long totalBytesForAllSingleScans;

	/**
	 * List of scan entries in main data file in order of scan number
	 */
	private List<SpectralFile_Index_TDFR_SingleScan_V_003> indexScanEntries;
	
//	private boolean scansAreInRetentionTimeOrder;
	
	
	/**
	 * @param scanNumber
	 * @return
	 */
	public SpectralFile_Index_TDFR_SingleScan_V_003 get_SingleScan_ForScanNumber( int scanNumber ) {

		SpectralFile_Index_TDFR_SingleScan_V_003 searchKey = new SpectralFile_Index_TDFR_SingleScan_V_003();
		searchKey.setScanNumber( scanNumber );

		int searchKeyIndex = Collections.binarySearch( indexScanEntries, searchKey );
		if ( searchKeyIndex < 0 ) {
			//  Scan number not found in index
			return null;  //  EARLY RETURN
		}

		SpectralFile_Index_TDFR_SingleScan_V_003 indexEntry = indexScanEntries.get( searchKeyIndex );
		
		return indexEntry;
	}
	
	

	/**
	 * @param retentionTimeStart
	 * @param retentionTimeEnd
	 * @param scanLevel - Optional
	 * @return
	 */
	public List<Integer> getScanNumbersForRetentionTimeRange( 
			float retentionTimeStart, 
			float retentionTimeEnd,
			Byte scanLevel // Optional
			) {
		
		//  Simple linear search to retrieve all within range
		
		List<Integer> scansInRange = new ArrayList<>();
		
		for ( SpectralFile_Index_TDFR_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
			
			float scanRetentionTime = indexScanEntry.getRetentionTime();
			if ( scanRetentionTime >= retentionTimeStart && scanRetentionTime <= retentionTimeEnd ) {
				if ( scanLevel != null ) {
					if ( scanLevel == indexScanEntry.getLevel() ) {
						scansInRange.add( indexScanEntry.getScanNumber() );
					}
				} else {
					scansInRange.add( indexScanEntry.getScanNumber() );
				}
			}
		}
		
		return scansInRange;
		
		
		//  Next code block is INCOMPLETE
		
		//  Only use next code if scans are in retention time order
		
		//  Using binary search find starting scan with retentionTimeStart
		
//		SpectralFile_Index_SingleScan_DTO searchKey = new SpectralFile_Index_SingleScan_DTO();
//		searchKey.setRetentionTime( retentionTimeStart );
//		
//		int searchKeyIndex = 
//				Collections.binarySearch( indexScanEntries, searchKey, new Comparator<SpectralFile_Index_SingleScan_DTO>() {
//
//					@Override
//					public int compare(SpectralFile_Index_SingleScan_DTO o1, SpectralFile_Index_SingleScan_DTO o2) {
//						return 0; // o1.getRetentionTime() - o2.getRetentionTime();
//					}
//				});
//		
//		//   Need to do more coding here
//
	}
	

	/**
	 * @param retentionTimeStart
	 * @param retentionTimeEnd
	 * @param scanLevel - Optional
	 * @return
	 */
	public List<SpectralFile_Index_TDFR_SingleScan_V_003> getScansIndexRecordsForRetentionTimeRange( 
			float retentionTimeStart, 
			float retentionTimeEnd,
			Byte scanLevel // Optional
			) {
		
		//  Simple linear search to retrieve all within range
		
		List<SpectralFile_Index_TDFR_SingleScan_V_003> scansInRange = new ArrayList<>();
		
		for ( SpectralFile_Index_TDFR_SingleScan_V_003 indexScanEntry : indexScanEntries ) {
			
			float scanRetentionTime = indexScanEntry.getRetentionTime();
			if ( scanRetentionTime >= retentionTimeStart && scanRetentionTime <= retentionTimeEnd ) {
				if ( scanLevel != null ) {
					if ( scanLevel == indexScanEntry.getLevel() ) {
						scansInRange.add( indexScanEntry );
					}
				} else {
					scansInRange.add( indexScanEntry );
				}
			}
		}
		
		return scansInRange;
		
		
		//  Next code block is INCOMPLETE
		
		//  Only use next code if scans are in retention time order
		
		//  Using binary search find starting scan with retentionTimeStart
		
//		SpectralFile_Index_SingleScan_DTO searchKey = new SpectralFile_Index_SingleScan_DTO();
//		searchKey.setRetentionTime( retentionTimeStart );
//		
//		int searchKeyIndex = 
//				Collections.binarySearch( indexScanEntries, searchKey, new Comparator<SpectralFile_Index_SingleScan_DTO>() {
//
//					@Override
//					public int compare(SpectralFile_Index_SingleScan_DTO o1, SpectralFile_Index_SingleScan_DTO o2) {
//						return 0; // o1.getRetentionTime() - o2.getRetentionTime();
//					}
//				});
//		
//		//   Need to do more coding here
//
	}
	

	/**
	 * List of scan entries in main data file in order of scan number
	 * @return
	 */
	public List<SpectralFile_Index_TDFR_SingleScan_V_003> getIndexScanEntries() {
		return indexScanEntries;
	}

	/**
	 * List of scan entries in main data file in order of scan number
	 * @param indexScanEntries
	 */
	public void setIndexScanEntries(List<SpectralFile_Index_TDFR_SingleScan_V_003> indexScanEntries) {
		this.indexScanEntries = Collections.unmodifiableList( indexScanEntries );
	}


	public int getNumberOfScans() {
		return numberOfScans;
	}


	public void setNumberOfScans(int numberOfScans) {
		this.numberOfScans = numberOfScans;
	}


	public short getVersion() {
		return version;
	}


	public void setVersion(short version) {
		this.version = version;
	}


	public byte getIsCentroidWholeFile() {
		return isCentroidWholeFile;
	}


	public void setIsCentroidWholeFile(byte isCentroidWholeFile) {
		this.isCentroidWholeFile = isCentroidWholeFile;
	}


	public boolean getScansAreInScanNumberOrder() {
		return scansAreInScanNumberOrder;
	}


	public void setScansAreInScanNumberOrder(boolean scansAreInScanNumberOrder) {
		this.scansAreInScanNumberOrder = scansAreInScanNumberOrder;
	}


	public boolean getScansAreInRetentionTimeOrder() {
		return scansAreInRetentionTimeOrder;
	}


	public void setScansAreInRetentionTimeOrder(boolean scansAreInRetentionTimeOrder) {
		this.scansAreInRetentionTimeOrder = scansAreInRetentionTimeOrder;
	}


	public long getTotalBytesForAllSingleScans() {
		return totalBytesForAllSingleScans;
	}


	public void setTotalBytesForAllSingleScans(long totalBytesForAllSingleScans) {
		this.totalBytesForAllSingleScans = totalBytesForAllSingleScans;
	}



	/**
	 * summary per distinct scan level
	 * @return
	 */
	public List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003> getSummaryDataPerScanLevelList() {
		return summaryDataPerScanLevelList;
	}



	public void setSummaryDataPerScanLevelList(
			List<SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_003> summaryDataPerScanLevelList) {
		this.summaryDataPerScanLevelList = summaryDataPerScanLevelList;
	}
	

}
