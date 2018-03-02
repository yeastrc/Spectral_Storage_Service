package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.to_data_file_reader_objects;


/**
 * Data for Main Data File Reader - SpectralFile_Reader_GZIP_V_003
 * 
 *  Single Scan object
 */
public class SpectralFile_Index_TDFR_SingleScan_V_003 implements Comparable<SpectralFile_Index_TDFR_SingleScan_V_003> {
	
	private int scanNumber;
	private byte level;
	private float retentionTime;
	
	/**
	 * The position this was read from the index file in
	 */
	private int indexFile_IndexPosition;

	private long scanIndex_InDataFile_InBytes;
	private int scanSize_InDataFile_InBytes;
	

	@Override
	public int compareTo(SpectralFile_Index_TDFR_SingleScan_V_003 o) {
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
		SpectralFile_Index_TDFR_SingleScan_V_003 other = (SpectralFile_Index_TDFR_SingleScan_V_003) obj;
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

	public int getIndexFile_IndexPosition() {
		return indexFile_IndexPosition;
	}

	public void setIndexFile_IndexPosition(int indexFile_IndexPosition) {
		this.indexFile_IndexPosition = indexFile_IndexPosition;
	}
}
