package org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;
import org.systemsbiology.jrap.stax.DataProcessingInfo;
import org.systemsbiology.jrap.stax.MSInstrumentInfo;
import org.systemsbiology.jrap.stax.MSXMLSequentialParser;
import org.systemsbiology.jrap.stax.MZXMLFileInfo;
import org.systemsbiology.jrap.stax.Scan;
import org.systemsbiology.jrap.stax.ScanHeader;
import org.systemsbiology.jrap.stax.SoftwareInfo;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.constants_enums.DataConversionType;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlHeader;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.ScanPeak;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanCentroidedConstants;

/**
 * Copied from MSDaPl, was MzXmlFileReader
 * 
 * Used to process MzML and MzXml files
 * 
 * For Retention Time, return zero if no value
 *
 */
public class MzMl_MzXml_FileReader { // implements MzXmlDataProvider {
	
	private static final Logger log = Logger.getLogger(MzMl_MzXml_FileReader.class);

//    private String sha1Sum;
    private String filename;
    private MSXMLSequentialParser parser;  //  MSXMLSequentialParser from ISB code
    private int numScans = 0;
    private int numScansRead = 0;
    private int lastMs1ScanNumber = -1;
//    private boolean isCentroided = false;
    private DataConversionType dataConvType = DataConversionType.UNKNOWN;
    
    private static final Pattern rtPattern = Pattern.compile("^PT(\\d+\\.?\\d*)S$"); 
    
    
    /**
     * @param filePath
     * @param sha1Sum
     * @throws Exception
     */
    public void open(String filePath
//    		, 
//    		String sha1Sum
    		)
            throws Exception {
    	
    	
//        this.sha1Sum = sha1Sum;
        this.filename = new File(filePath).getName();
        parser = new MSXMLSequentialParser();  //  MSXMLSequentialParser from ISB code
        try {
            parser.open(filePath);
        }
        catch (FileNotFoundException e) {
            throw new Exception("Could not find file: "+filePath, e);
        }
        catch (XMLStreamException e) {
            throw new Exception("Error reading file: "+filePath, e);
        
        } catch ( Exception e ) {
        	
        	throw new Exception("Error Opening or reading file: "+filePath, e);
        }
        
        numScans = parser.getScanCount();
    }
    
    public void close() {
        if(parser != null)
            parser.close();
    }

    public String getFileName() {
        return filename;
    }

    /**
     * Get Next Scan
     * 
     * @return
     * @throws Exception 
     */
    public MzML_MzXmlScan /* MsScanIn */ getNextScan() throws Exception {
    	
    	
        if(numScansRead >= numScans)
            return null;
        Scan scan = null;
        if(parser.hasNextScan()) {
            try {
                scan = parser.getNextScan();
            }
            catch (XMLStreamException e) {
                throw new Exception("Error reading scan.", e);
            }
        }
        if(scan == null)
            return null;
        
        ScanHeader header = scan.getHeader();
        
        
        MzML_MzXmlScan mScan = new MzML_MzXmlScan();
        
        int scanNumber = header.getNum();
        int msLevel = header.getMsLevel();
        if ( msLevel > Byte.MAX_VALUE ) {
        	throw new Exception( "scan msLevel > Byte.MAX_VALUE.  scan number: " + scanNumber );
        }
        
        mScan.setMsLevel( (byte) msLevel );
        mScan.setScanNumber( scanNumber );

        mScan.setMsLevel( (byte) msLevel );

        if( msLevel == 1) {
            this.lastMs1ScanNumber = mScan.getScanNumber();
        }
        else if(header.getMsLevel() > 1) {
            
            if( header.getPrecursorScanNum() > 0) {
                if( header.getPrecursorScanNum() != this.lastMs1ScanNumber) {
                    throw new Exception("last MS1 scan: " + this.lastMs1ScanNumber +
                            " is not the same as precursor scan number: " + header.getPrecursorScanNum()+
                            " for scan: "+header.getNum() );
                }
                mScan.setPrecursorScanNum(header.getPrecursorScanNum());
            }
            else {
                mScan.setPrecursorScanNum(this.lastMs1ScanNumber);
            }

            int precursorCharge = header.getPrecursorCharge();
            if ( precursorCharge > Byte.MAX_VALUE ) {
            	throw new Exception( "scan precursorCharge > Byte.MAX_VALUE.  scan number: " + scanNumber );
            }
            mScan.setPrecursorCharge( (byte) precursorCharge ); 
            
            mScan.setPrecursorMz( header.getPrecursorMz() );
        }
        mScan.setRetentionTime(getRetentionTime(header));
        
        
//        if(header.getCentroided() != -1)
//            mScan.setDataConversionType(getDataConversionType(header.getCentroided()));
//        else
//            mScan.setDataConversionType(this.dataConvType);
        
        int mzMLCentroided = header.getCentroided();
        
        byte scanCentroided = ScanCentroidedConstants.SCAN_CENTROIDED_TRUE; // 1
        
        if ( mzMLCentroided != 1 ) {
        	
        	scanCentroided = ScanCentroidedConstants.SCAN_CENTROIDED_FALSE; // 0
        }
        
        mScan.setIsCentroided( scanCentroided );
        
        
        
        

        mScan.setPeakCount(header.getPeaksCount());
        

        double[][] mzInt = scan.getMassIntensityList();
        
        
        int peaksCount = header.getPeaksCount();

        
        int mzIntLength = mzInt.length;
        
        
        if ( mzIntLength != 2 ) {
        	
        	String msg = "Data ERROR: mzIntLength != 2, scan number: " + header.getNum() + ", filename: " + filename;
        	log.error( msg );
        	throw new Exception(msg);
        }
        
        int mzIntIndexZeroLength = mzInt[0].length;
        int mzIntIndexOneLength = mzInt[1].length;
        
//        if ( mzIntIndexZeroLength != peaksCount ) {
//        	
//        	String msg = "Data ERROR: mzIntIndexZeroLength != peaksCount, mzIntIndexOneLength: "
//        			+ mzIntIndexZeroLength + ", peaksCount: " + peaksCount 
//        			+ ", scan number: " + header.getNum() + ", filename: " + filename;
//        	log.error( msg );
//        	throw new Exception(msg);
//        }
//        
//        if ( mzIntIndexOneLength != peaksCount ) {
//        	
//        	String msg = "Data ERROR: mzIntIndexOneLength != peaksCount, mzIntIndexOneLength: "
//        			+ mzIntIndexOneLength + ", peaksCount: " + peaksCount 
//        			+ ", scan number: " + header.getNum() + ", filename: " + filename;
//        	log.error( msg );
//        	throw new Exception(msg);
//        }
        
        
        ////  Validate all values at index > header.getPeaksCount() is zero.
        
        for ( int mzIntFirstIndex =0; mzIntFirstIndex < mzInt.length; mzIntFirstIndex++ ) {
        	
        	for ( int index = header.getPeaksCount(); index < mzInt[ mzIntFirstIndex ].length; index++ ) {

        		double value = mzInt[ mzIntFirstIndex ][ index ];
        		
        		if ( value != 0 ) {
        			
        			String msg = "Data ERROR: mzInt value for index >= peaksCount is not zero, is: " + value
        					+ ", mzIntIndexOneLength: " + mzIntIndexOneLength + ", peaksCount: " + peaksCount 
        					+ ", scan number: " + header.getNum() + ", filename: " + filename;
        			log.error( msg );
        			throw new Exception(msg);
        		}
        	}
        }        
        
		if ( peaksCount > mzInt[0].length || peaksCount > mzInt[1].length ) {
			
			String msg = "Data ERROR: peaksCount is greater than provided peaks arrays."
					+ ", mzIntIndexZeroLength: " + mzIntIndexZeroLength 
					+ ", mzIntIndexOneLength: " + mzIntIndexOneLength 
					+ ", peaksCount: " + peaksCount 
					+ ", scan number: " + header.getNum() + ", filename: " + filename;
			log.error( msg );
			throw new Exception(msg);
		}
		
		final double Float_MAX_VALUE = Float.MAX_VALUE;
		
		int headerPeaksCount = header.getPeaksCount();
		
		List<ScanPeak> scanPeakList = new ArrayList<ScanPeak>( headerPeaksCount );
        
        // Peak 0 mass = list[0][0], peak 0 intensity = list[1][0]
        // Peak 1 mass = list[0][1], peak 1 intensity = list[1][1]
        for(int index = 0; index < headerPeaksCount ; index++) {
            double mzDouble = mzInt[0][index];
            double intensityDouble = mzInt[1][index];
            
            if ( intensityDouble > Float_MAX_VALUE ) {
            	throw new Exception( "intensityDouble > Float_MAX_VALUE. intensityDouble: " + intensityDouble 
            			+ ", scanNumber: " + header.getNum() );
            }

            float intensityFloat = (float)intensityDouble;

            
            ScanPeak scanPeak = new ScanPeak();
            
            scanPeak.setMz( mzDouble );
            scanPeak.setIntensity( intensityFloat );
            
            scanPeakList.add( scanPeak );
        }
        
        mScan.setScanPeakList( scanPeakList );
        
        return mScan;
    }

    
    /**
     * @param header
     * @return zero if no value
     */
    private float getRetentionTime(ScanHeader header) {
        // In the schema, retentionTime is "xs:duration" 
        // http://www.w3schools.com/Schema/schema_dtypes_date.asp
        String rt = header.getRetentionTime();  // zero if no value
        if(rt == null)  return 0;
        rt = rt.trim();
        
        Matcher m = rtPattern.matcher(rt);
        if(m.matches()) {
            String time = m.group(1);
            if(time != null) {
                return Float.parseFloat(time);
            }
        }
        return 0;  // zero if no value
    }

    /**
     * @return
     * @throws Exception
     */
    public MzML_MzXmlHeader /* MsRunIn */ getRunHeader() throws Exception {
    	
        MZXMLFileInfo info = parser.getFileHeader();
        MzML_MzXmlHeader run = new MzML_MzXmlHeader();
        
        DataProcessingInfo dpInfo = info.getDataProcessing();
        
        dataConvType  = getDataConversionType(dpInfo.getCentroided());
        run.setDataConversionType(dataConvType);
        
        List<SoftwareInfo> swList = dpInfo.getSoftwareUsed();
        // TODO handle multiple software info.
        if(swList.size() > 0) {
            for(SoftwareInfo si: swList) {
                if(si.type.equalsIgnoreCase("conversion")) {
                    run.setConversionSW(swList.get(0).name);
                    run.setConversionSWVersion(swList.get(0).version);
                }
            }
        }
        
        MSInstrumentInfo msiInfo = info.getInstrumentInfo();
        run.setInstrumentModel(msiInfo.getModel());
        run.setInstrumentVendor(msiInfo.getManufacturer());
      
        run.setFileName(this.filename);
//        run.setSha1Sum(sha1Sum);
        
        
        return run;
    }

    /**
     * @param centroided
     * @return
     */
    private DataConversionType getDataConversionType(int centroided) {
        
        if (centroided == DataProcessingInfo.NO)
            return DataConversionType.NON_CENTROID;
        else if (centroided == DataProcessingInfo.YES)
            return DataConversionType.CENTROID;
        else {
        	return DataConversionType.NON_CENTROID;  // Hard code to Non_Centroid per Mike Riffle since for mzML files only centroid Yes is indicated. 
//            return DataConversionType.UNKNOWN;
        }
        
    }
    
}
