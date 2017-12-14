package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

import java.util.List;


/**
 * Data for Single Scan
 * 
 * Contains properties in all versions
 *
 */
public class SpectralFile_SingleScan_Common {

	private int scanTotalBytesInDataFile;
	
	private byte level;
	private int scanNumber;
	private float retentionTime;
	private byte isCentroid;
	
	//  Only applicable where level > 1
	
	private Integer parentScanNumber;
	private Byte precursorCharge;
	private Double precursor_M_Over_Z;
	
	/**
	 * Number of scan peaks
	 */
	private int numberScanPeaks;
	
	/**
	 * Length of scan Peaks which is written immediately after the data in this class.
	 */
	private int scanPeaksDataLength;
	
	/**
	 * Scan Peaks as byte array to write to or read from disk.
	 * 
	 * Not always populated when this is read from disk
	 */
	private byte[] scanPeaksAsByteArray;
	
	/**
	 * Scan Peaks as object list
	 * 
	 * Not always populated when this is read from disk
	 */
	private List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray;

	
	/////////////////////

	/**
	 * Length of scan Peaks which is written immediately after the data in this class.
	 * 
	 * @return
	 */
	public int getScanPeaksDataLength() {
		return scanPeaksDataLength;
	}


	/**
	 * Length of scan Peaks which is written immediately after the data in this class.
	 * 
	 * @param scanPeaksDataLength
	 */
	public void setScanPeaksDataLength(int scanPeaksDataLength) {
		this.scanPeaksDataLength = scanPeaksDataLength;
	}
	
	/**
	 * Scan Peaks as byte array to write to or read from disk.
	 * 
	 * Not always populated when this is read from disk
	 * 
	 * @return
	 */
	public byte[] getScanPeaksAsByteArray() {
		return scanPeaksAsByteArray;
	}


	/**
	 * Scan Peaks as byte array to write to or read from disk.
	 * 
	 * Not always populated when this is read from disk
	 * 
	 * @param scanPeaksAsByteArray
	 */
	public void setScanPeaksAsByteArray(byte[] scanPeaksAsByteArray) {
		this.scanPeaksAsByteArray = scanPeaksAsByteArray;
	}


	/**
	 * Scan Peaks as object list
	 * 
	 * Not always populated when this is read from disk
	 * 
	 * @return
	 */
	public List<SpectralFile_SingleScanPeak_Common> getScanPeaksAsObjectArray() {
		return scanPeaksAsObjectArray;
	}


	/**
	 * Scan Peaks as object list
	 * 
	 * Not always populated when this is read from disk
	 * 
	 * @param scanPeaksAsObjectArray
	 */
	public void setScanPeaksAsObjectArray(List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray) {
		this.scanPeaksAsObjectArray = scanPeaksAsObjectArray;
	}


	///////////////////////
	
	
	

	public byte getLevel() {
		return level;
	}


	public void setLevel(byte level) {
		this.level = level;
	}


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


	public byte getIsCentroid() {
		return isCentroid;
	}


	public void setIsCentroid(byte isCentroid) {
		this.isCentroid = isCentroid;
	}


	public Integer getParentScanNumber() {
		return parentScanNumber;
	}


	public void setParentScanNumber(Integer parentScanNumber) {
		this.parentScanNumber = parentScanNumber;
	}


	public Byte getPrecursorCharge() {
		return precursorCharge;
	}


	public void setPrecursorCharge(Byte precursorCharge) {
		this.precursorCharge = precursorCharge;
	}


	public Double getPrecursor_M_Over_Z() {
		return precursor_M_Over_Z;
	}


	public void setPrecursor_M_Over_Z(Double precursor_M_Over_Z) {
		this.precursor_M_Over_Z = precursor_M_Over_Z;
	}


	public int getNumberScanPeaks() {
		return numberScanPeaks;
	}


	public void setNumberScanPeaks(int numberScanPeaks) {
		this.numberScanPeaks = numberScanPeaks;
	}


	public int getScanTotalBytesInDataFile() {
		return scanTotalBytesInDataFile;
	}


	public void setScanTotalBytesInDataFile(int scanTotalBytesInDataFile) {
		this.scanTotalBytesInDataFile = scanTotalBytesInDataFile;
	}


}
