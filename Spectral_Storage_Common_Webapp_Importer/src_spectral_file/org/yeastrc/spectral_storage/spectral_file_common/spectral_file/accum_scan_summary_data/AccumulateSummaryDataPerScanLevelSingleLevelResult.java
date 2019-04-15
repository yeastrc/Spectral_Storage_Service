package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data;

/**
 * Data from AccumulateSummaryDataPerScanLevel
 * 
 * Single Scan Level object
 *
 */
public class AccumulateSummaryDataPerScanLevelSingleLevelResult {

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
	 * 0 - false - ScanCentroidedConstants.SCAN_CENTROIDED_FALSE
	 * 1 - true - ScanCentroidedConstants.SCAN_CENTROIDED_TRUE
	 * 2 - both - both false and true found for this scan level the file
	 *            ScanCentroidedConstants.SCAN_CENTROIDED_VALUES_IN_FILE_BOTH
	 */
	Byte isCentroidScanLevel;
	
	/**
	 * Sum of intensity of all peaks for all scans with this scan level
	 */
	double totalIonCurrent;

	/**
	 * Is centroid values for this scan level
	 * 0 - false
	 * 1 - true
	 * 2 - both - both false and true found for this scan level the file
	 */
	public Byte getIsCentroidScanLevel() {
		return isCentroidScanLevel;
	}
	/**
	 * Is centroid values for this scan level
	 * 0 - false
	 * 1 - true
	 * 2 - both - both false and true found for this scan level the file
	 */
	public void setIsCentroidScanLevel(Byte isCentroidScanLevel) {
		this.isCentroidScanLevel = isCentroidScanLevel;
	}
	
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
}
