package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.from_data_file_writer_objects;

/**
 * Data from Main Data File Writer - SpectralFile_Writer_GZIP_V_005
 * 
 * 
 * Single Scan object
 *
 */
public class SpectralFile_Index_FDFW_SingleScan_V_005 {
	
	private int scanNumber;
	private byte level;
	private float retentionTime;
	private byte isCentroid;

	/**
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 */
	private Float ionInjectionTime;

	/**
	 * Value per Scan, retrieved from file, not computed: mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 */
	private Float totalIonCurrent;

	//  Only applicable where level > 1
	
	private int parentScanNumber;
	private byte precursorCharge;
	private double precursor_M_Over_Z;
	
	
	private long scanIndex_InDataFile_InBytes;
	private int scanSize_InDataFile_InBytes;
	
	
	public int getScanNumber() {
		return scanNumber;
	}
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	public byte getLevel() {
		return level;
	}
	public void setLevel(byte level) {
		this.level = level;
	}
	public float getRetentionTime() {
		return retentionTime;
	}
	public void setRetentionTime(float retentionTime) {
		this.retentionTime = retentionTime;
	}
	public long getScanIndex_InDataFile_InBytes() {
		return scanIndex_InDataFile_InBytes;
	}
	public void setScanIndex_InDataFile_InBytes(long scanIndex_InDataFile_InBytes) {
		this.scanIndex_InDataFile_InBytes = scanIndex_InDataFile_InBytes;
	}
	public int getScanSize_InDataFile_InBytes() {
		return scanSize_InDataFile_InBytes;
	}
	public void setScanSize_InDataFile_InBytes(int scanSize_InDataFile_InBytes) {
		this.scanSize_InDataFile_InBytes = scanSize_InDataFile_InBytes;
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
	public Float getTotalIonCurrent() {
		return totalIonCurrent;
	}
	public void setTotalIonCurrent(Float totalIonCurrent) {
		this.totalIonCurrent = totalIonCurrent;
	}
}
