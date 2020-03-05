/**
 * MzXmlScan.java
 * @author Vagisha Sharma
 * Jun 23, 2009
 * @version 1.0
 */
package org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto;

import java.util.List;

import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.constants_enums.DataConversionType;

//import org.yeastrc.xlink.base.spectrum.common.dto.Peak;

//import org.yeastrc.ms.domain.run.DataConversionType;
//import org.yeastrc.ms.domain.run.MsScanIn;
//import org.yeastrc.ms.domain.run.Peak;

/**
 * 
 */
public class MzML_MzXmlScan { // implements MsScanIn {

    private byte msLevel = 1;
    private int scanNumber = -1;
    private int precursorScanNum = -1;

    private double precursorMz;
    private byte precursorCharge;
    private float retentionTime;

    /**
     * 2020: YRC: Spectral Storage Service change to add support for ion injection time
     */
    private Float ionInjectionTime;  // In Milliseconds

	/**
	 * 2020: YRC: Spectral Storage Service change to add support for total ion current per scan
	 * 
	 * Value per Scan, retrieved from file, not computed: mzML: <cvParam cvRef="MS" accession="MS:1000285" name="total ion current" value="5.0278541e05"/>
	 * Set to Float.NEGATIVE_INFINITY in disk file if not available.  Set to null in Java objects.
	 * 
	 * In Data file Reader, if value read from disk is Float.NEGATIVE_INFINITY, then the Java object property is not set.
	 *  
	 *  New for Data File Version V005
	 */
	private Float totalIonCurrent;
    
    private String activationType; //  setter/getter match property name fragmentationType which is in the MSDaPl msScan table

    
    
    /**
     * Added for Crosslinks Project
     */
    private byte isCentroided = 0;



	private DataConversionType dataConversionType = DataConversionType.UNKNOWN;
    
    private int peakCount;
    private List<ScanPeak> scanPeakList;
    
    public List<ScanPeak> getScanPeakList() {
		return scanPeakList;
	}


	public void setScanPeakList(List<ScanPeak> scanPeakList) {
		this.scanPeakList = scanPeakList;
	}


    
    public byte getIsCentroided() {
		return isCentroided;
	}

	public void setIsCentroided(byte isCentroided) {
		this.isCentroided = isCentroided;
	}
	
    

    public int getPeakCount() {
        return peakCount;
    }
    
    public void setPeakCount(int peakCount) {
        this.peakCount = peakCount;
    }


    public byte getMsLevel() {
        return msLevel;
    }
    
    public void setMsLevel(byte msLevel) {
        this.msLevel = msLevel;
    }

    public int getPrecursorScanNum() {
        return precursorScanNum;
    }
    
    public void setPrecursorScanNum(int scanNum) {
        this.precursorScanNum = scanNum;
    }
    

    public DataConversionType getDataConversionType() {
        return this.dataConversionType;
    }
    
    public void setDataConversionType(DataConversionType type) {
        this.dataConversionType = type;
    }


    public String getFragmentationType() {
        return this.activationType;
    }
    
    public void setFragmentationType(String fragmentationType) {
        this.activationType = fragmentationType;
    }


	public int getScanNumber() {
		return scanNumber;
	}


	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}


	public double getPrecursorMz() {
		return precursorMz;
	}


	public void setPrecursorMz(double precursorMz) {
		this.precursorMz = precursorMz;
	}


	public float getRetentionTime() {
		return retentionTime;
	}


	public void setRetentionTime(float retentionTime) {
		this.retentionTime = retentionTime;
	}


	public String getActivationType() {
		return activationType;
	}


	public void setActivationType(String activationType) {
		this.activationType = activationType;
	}


	public byte getPrecursorCharge() {
		return precursorCharge;
	}


	public void setPrecursorCharge(byte precursorCharge) {
		this.precursorCharge = precursorCharge;
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
