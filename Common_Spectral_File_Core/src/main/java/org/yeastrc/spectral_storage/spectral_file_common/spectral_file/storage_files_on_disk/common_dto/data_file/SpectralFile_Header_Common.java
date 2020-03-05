package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

/**
 * Data at beginning of Spectral file
 *
 */
public class SpectralFile_Header_Common {
	
	private int headerTotalBytesInDataFile;

	//  Ignored when writing.  Writer will write it's version number
	private short version;

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
	
	
	private long scanFileLength_InBytes;
	
	private byte[] mainHash;

	private byte[] altHashSHA512;
	private byte[] altHashSHA1;
	
	
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

	/**
	 * Ignored when writing.  Total number of bytes in header
	 * @return
	 */
	public int getHeaderTotalBytesInDataFile() {
		return headerTotalBytesInDataFile;
	}

	public void setHeaderTotalBytesInDataFile(int headerTotalBytes) {
		this.headerTotalBytesInDataFile = headerTotalBytes;
	}
	
	/**
	 * Ignored when writing.  Writer will write it's version number
	 * @return
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * Ignored when writing.  Writer will write it's version number
	 * @param version
	 */
	public void setVersion(short version) {
		this.version = version;
	}


	public long getScanFileLength_InBytes() {
		return scanFileLength_InBytes;
	}

	public void setScanFileLength_InBytes(long scanFileLength_InBytes) {
		this.scanFileLength_InBytes = scanFileLength_InBytes;
	}

	public byte[] getMainHash() {
		return mainHash;
	}

	public void setMainHash(byte[] mainHash) {
		this.mainHash = mainHash;
	}

	public byte[] getAltHashSHA512() {
		return altHashSHA512;
	}

	public void setAltHashSHA512(byte[] altHashSHA512) {
		this.altHashSHA512 = altHashSHA512;
	}

	public byte[] getAltHashSHA1() {
		return altHashSHA1;
	}

	public void setAltHashSHA1(byte[] altHashSHA1) {
		this.altHashSHA1 = altHashSHA1;
	}


}
