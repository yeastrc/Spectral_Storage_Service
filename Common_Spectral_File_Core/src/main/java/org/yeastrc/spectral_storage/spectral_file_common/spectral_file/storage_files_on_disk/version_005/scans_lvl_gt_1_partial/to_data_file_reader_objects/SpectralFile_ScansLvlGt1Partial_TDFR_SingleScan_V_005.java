package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.scans_lvl_gt_1_partial.to_data_file_reader_objects;

/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_005
 * 
 *  Single Scan object
 */
public class SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_005 implements Comparable<SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_005> {

	private int scanNumber;
	
	private int parentScanNumber;
	
	private byte precursorCharge;
	private double precursor_M_Over_Z;

	@Override
	public int compareTo(SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_005 o) {
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
		SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_005 other = (SpectralFile_ScansLvlGt1Partial_TDFR_SingleScan_V_005) obj;
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
	public int getParentScanNumber() {
		return parentScanNumber;
	}
	public void setParentScanNumber(int parentScanNumber) {
		this.parentScanNumber = parentScanNumber;
	}
	public byte getPrecursorCharge() {
		return precursorCharge;
	}
	public void setPrecursorCharge(byte precursorCharge) {
		this.precursorCharge = precursorCharge;
	}
	public double getPrecursor_M_Over_Z() {
		return precursor_M_Over_Z;
	}
	public void setPrecursor_M_Over_Z(double precursor_M_Over_Z) {
		this.precursor_M_Over_Z = precursor_M_Over_Z;
	}

}
