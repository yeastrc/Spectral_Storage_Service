package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

import java.util.List;


/**
 * Data for Single Scan
 * 
 * Contains properties in all versions
 *
 */
public class SpectralFile_SingleScan_Common {

	private int scanTotalBytesInDataFile = -1;
	
	private byte level;
	private int scanNumber;
	private float retentionTime;
	
	/**
	 *  in milliseconds
	 *  
	 *  Not populated if request other than peaks 
	 *  
	 *  Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 *  
	 *  In Data file Reader, if value read from disk is Float.NEGATIVE_INFINITY, then the Java object property is not set.
	 *  
	 *  New for Data File Version V005
	 */
	private Float ionInjectionTime; // in milliseconds

	/**
	 * Value per Scan, retrieved from file, From (if available): mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * 
	 * Otherwise computed from scan peaks
	 *  
	 *  New for Data File Version V005
	 */
	private Float totalIonCurrent;
	
	/**
	 * Not populated if request other than peaks and scan file contains more than one unique value for this scan level
	 */
	private Byte isCentroid;
	
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


	/**
	 * Not populated if request other than peaks and scan file contains more than one unique value for this scan level
	 * @return null if not populated
	 */
	public Byte getIsCentroid() {
		return isCentroid;
	}


	public void setIsCentroid(Byte isCentroid) {
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


	/**
	 * in milliseconds
	 * 
	 * Not populated if request other than peaks 
	 * 
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 * 
	 * In Data file Reader, if value read from disk is this value, then the Java object property is not set.
	 * 
	 * New for Data File Version V005
	 *  
	 * @return
	 */
	public Float getIonInjectionTime() {
		return ionInjectionTime;
	}


	/**
	 * in milliseconds
	 * 
	 * Not populated if request other than peaks 
	 *  
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 * 
	 * In Data file Reader, if value read from disk is Float.NEGATIVE_INFINITY, then the Java object property is not set.
	 * 
	 * New for Data File Version V005
	 * 
	 * @param ionInjectionTime
	 */
	public void setIonInjectionTime(Float ionInjectionTime) {
		this.ionInjectionTime = ionInjectionTime;
	}


	/**
	 * Value per Scan, retrieved from file, From (if available): mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * 
	 * Otherwise computed from scan peaks
	 * 
	 * New for Data File Version V005
	 * 
	 * @return
	 */
	public Float getTotalIonCurrent() {
		return totalIonCurrent;
	}


	/**
	 * Value per Scan, retrieved from file, From (if available): mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * 
	 * Otherwise computed from scan peaks
	 * 
	 * New for Data File Version V005
	 * 
	 * @param totalIonCurrent
	 */
	public void setTotalIonCurrent(Float totalIonCurrent) {
		this.totalIonCurrent = totalIonCurrent;
	}


}
