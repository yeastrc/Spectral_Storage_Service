package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_other_data_extract_root__file.to_data_file_reader_objects;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_005
 * 
 *  Single Scan object
 */
public class SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 implements Comparable<SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005> {

	private int scanNumber;
	
	private byte isCentroid;
	private Float ionInjectionTime;
	private float totalIonCurrent;
	
	@Override
	public int compareTo(SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 o) {
		if ( scanNumber < o.scanNumber ) {
			return -1;
		} 
		if ( scanNumber > o.scanNumber ) {
			return 1;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + scanNumber;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005 other = (SpectralFile_Scans_OtherDataExtract_TDFR_SingleScan_V_005) obj;
		if (scanNumber != other.scanNumber)
			return false;
		return true;
	}

	public int getScanNumber() {
		return scanNumber;
	}

	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}

	public byte getIsCentroid() {
		return isCentroid;
	}

	public void setIsCentroid(byte isCentroid) {
		this.isCentroid = isCentroid;
	}

	public Float getIonInjectionTime() {
		return ionInjectionTime;
	}

	public void setIonInjectionTime(Float ionInjectionTime) {
		this.ionInjectionTime = ionInjectionTime;
	}

	public float getTotalIonCurrent() {
		return totalIonCurrent;
	}

	public void setTotalIonCurrent(float totalIonCurrent) {
		this.totalIonCurrent = totalIonCurrent;
	}
	
}
