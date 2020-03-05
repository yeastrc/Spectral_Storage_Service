package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects;

/**
 * Data from Main Data File Writer - SpectralFile_Writer_GZIP_V_005
 * 
 * Single Scan Level object
 * 
 * Each entry in SpectralFile_Index_FDFW_FileContents_Root_V_005.summaryDataPerScanLevelList
 *
 */
public class SpectralFile_Index_FDFW_SummaryDataPerScanLevel_V_005 {

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
	 * Sum of intensity of all peaks for all scans with this scan level
	 */
	double totalIonCurrent;
	
	
	public byte getScanLevel() {
		return scanLevel;
	}
	public void setScanLevel(byte scanLevel) {
		this.scanLevel = scanLevel;
	}
	public int getNumberOfScans() {
		return numberOfScans;
	}
	public void setNumberOfScans(int numberOfScans) {
		this.numberOfScans = numberOfScans;
	}
	public double getTotalIonCurrent() {
		return totalIonCurrent;
	}
	public void setTotalIonCurrent(double totalIonCurrent) {
		this.totalIonCurrent = totalIonCurrent;
	}
	public byte getIsCentroidScanLevel() {
		return isCentroidScanLevel;
	}
	public void setIsCentroidScanLevel(byte isCentroidScanLevel) {
		this.isCentroidScanLevel = isCentroidScanLevel;
	}
	
}
