package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

/**
 * Data passed when "Close Spectral file", used by Data File Writer to update the header section and provide other information
 * 
 * This is NOT used by any Data File Reader
 *
 */
public class SpectralFile_CloseWriter_Data_Common {
	
	private boolean exceptionEncounteredProcessingScanFile;
	
	/**
	 * Original Scan File did NOT have Total Ion Current Per Scan so it is computed in Spectral Storage Service Importer from Scan Peaks.
	 * Not populated for Data File Version < 5. 
	 */
	private Boolean totalIonCurrent_ForEachScan_ComputedFromScanPeaks;
	/**
	 * Original Scan File did NOT have Ion Injection Time Per Scan.
	 * Not populated for Data File Version < 5.
	 */
	private Boolean ionInjectionTime_NotPopulated;
	
	
	//////////////////////////////

	/**
	 * Original Scan File did NOT have Total Ion Current Per Scan so it is computed in Spectral Storage Service Importer from Scan Peaks.
	 * Not populated for Data File Version < 5.
	 * @return - null if not stored in data file (Old Version of data file) 
	 */
	public Boolean getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() {
		return totalIonCurrent_ForEachScan_ComputedFromScanPeaks;
	}
	/**
	 * Original Scan File did NOT have Total Ion Current Per Scan so it is computed in Spectral Storage Service Importer from Scan Peaks.
	 * Not populated for Data File Version < 5.
	 * @param totalIonCurrent_ForEachScan_ComputedFromScanPeaks
	 */
	public void setTotalIonCurrent_ForEachScan_ComputedFromScanPeaks(Boolean totalIonCurrent_ForEachScan_ComputedFromScanPeaks) {
		this.totalIonCurrent_ForEachScan_ComputedFromScanPeaks = totalIonCurrent_ForEachScan_ComputedFromScanPeaks;
	}

	/**
	 * Original Scan File did NOT have Ion Injection Time Per Scan.
	 * Not populated for Data File Version < 5.
	 * @return - null if not stored in data file (Old Version of data file)
	 */
	public Boolean getIonInjectionTime_NotPopulated() {
		return ionInjectionTime_NotPopulated;
	}
	/**
	 * Original Scan File did NOT have Ion Injection Time Per Scan.
	 * Not populated for Data File Version < 5.
	 * @param ionInjectionTime_NotPopulated
	 */
	public void setIonInjectionTime_NotPopulated(Boolean ionInjectionTime_NotPopulated) {
		this.ionInjectionTime_NotPopulated = ionInjectionTime_NotPopulated;
	}
	public boolean isExceptionEncounteredProcessingScanFile() {
		return exceptionEncounteredProcessingScanFile;
	}
	public void setExceptionEncounteredProcessingScanFile(boolean exceptionEncounteredProcessingScanFile) {
		this.exceptionEncounteredProcessingScanFile = exceptionEncounteredProcessingScanFile;
	}

}
