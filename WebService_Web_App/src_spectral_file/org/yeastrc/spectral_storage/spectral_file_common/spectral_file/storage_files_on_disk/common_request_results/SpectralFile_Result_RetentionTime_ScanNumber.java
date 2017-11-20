package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_request_results;

/**
 * Single Scan Retention Time Data
 *
 * Internal
 */
public class SpectralFile_Result_RetentionTime_ScanNumber {

	private int scanNumber;
	private int level;
	private float retentionTime;
	
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public float getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(float retentionTime) {
		this.retentionTime = retentionTime;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
