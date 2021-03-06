package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.to_data_file_reader_objects;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_003
 * 
 * Single Scan Level object
 * 
 * Each entry in SpectralFile_Index_TDFR_FileContents_Root_V_004.summaryDataPerScanLevelList
 *
 */
public class SpectralFile_Index_TDFR_SummaryDataPerScanLevel_V_004 {

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
