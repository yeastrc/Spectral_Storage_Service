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
	 * Is the IonInjectionTime Set values for this scan level
	 * 0 - false - IonInjectionTime == null
	 * 1 - true - IonInjectionTime != null
	 * 2 - both - both IonInjectionTime == null and IonInjectionTime != null
	 *            ScanHasIonInjectionTimeConstants.SCAN_HAS_ION_INJECTION_TIME_VALUES_IN_FILE_BOTH
	 */
	Byte isIonInjectionTime_Set_ScanLevel;
	
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
	/**
	 * Is the IonInjectionTime Set values for this scan level
	 * 0 - false - IonInjectionTime == null
	 * 1 - true - IonInjectionTime != null
	 * 2 - both - both IonInjectionTime == null and IonInjectionTime != null
	 *            ScanHasIonInjectionTimeConstants.SCAN_HAS_ION_INJECTION_TIME_VALUES_IN_FILE_BOTH
	 * @return
	 */
	public Byte getIsIonInjectionTime_Set_ScanLevel() {
		return isIonInjectionTime_Set_ScanLevel;
	}
	/**
	 * Is the IonInjectionTime Set values for this scan level
	 * 0 - false - IonInjectionTime == null
	 * 1 - true - IonInjectionTime != null
	 * 2 - both - both IonInjectionTime == null and IonInjectionTime != null
	 *            ScanHasIonInjectionTimeConstants.SCAN_HAS_ION_INJECTION_TIME_VALUES_IN_FILE_BOTH
	 * @param isIonInjectionTime_Set_ScanLevel
	 */
	public void setIsIonInjectionTime_Set_ScanLevel(Byte isIonInjectionTime_Set_ScanLevel) {
		this.isIonInjectionTime_Set_ScanLevel = isIonInjectionTime_Set_ScanLevel;
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
