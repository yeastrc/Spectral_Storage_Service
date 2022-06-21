package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader.MzMl_MzXml_FileReader;

/**
 * 
 *
 */
public class ScanFile_Parsing_InProgress_Item {

	private static final Logger log = LoggerFactory.getLogger(ScanFile_Parsing_InProgress_Item.class);
	
	/**
	 * @param scanFilenameWithPath
	 * @param scanBatchSizeMaximum
	 * @return
	 */
	public static ScanFile_Parsing_InProgress_Item getNewInstance( String scanFilenameWithPath, int scanBatchSizeMaximum ) {
		return new ScanFile_Parsing_InProgress_Item(scanFilenameWithPath, scanBatchSizeMaximum);
	}
	
	/**
	 * Private Contructor
	 */
	private ScanFile_Parsing_InProgress_Item(String scanFilenameWithPath, int scanBatchSizeMaximum) {
		this.scanFilenameWithPath = scanFilenameWithPath;
		this.scanBatchSizeMaximum = scanBatchSizeMaximum;
	}
	
	///  passed in when constructed
	
	private String scanFilenameWithPath;
	private int scanBatchSizeMaximum;
	
	///  
	
	private MzMl_MzXml_FileReader scanFileReader__MzMl_MzXml_FileReader;   // From ISB mzML/mzXML parsing code
	
	private AtomicInteger scanBatchNumber_Current = new AtomicInteger( 0 );
	
	private boolean endOf_ScanFile_Reached;

	public boolean isEndOf_ScanFile_Reached() {
		return endOf_ScanFile_Reached;
	}

	/**
	 * @return Next Batch Number
	 */
	public int getNext_scanBatchNumber() {
		
		return scanBatchNumber_Current.incrementAndGet();
	}
	
	/**
	 * @return Current Batch Number
	 */
	public int getCurrent_scanBatchNumber() {
		
		return scanBatchNumber_Current.get();
	}
	
	/**
	 * 
	 */
	public void initializeParsing() {

		{
			File scan_file = new File( scanFilenameWithPath );
			if ( ! scan_file.exists() ) {

				String msg = "scan file not exist: " + scanFilenameWithPath;
				log.warn( msg );
				throw new RuntimeException( msg );
			}
			if ( ! scan_file.canRead() ) {

				String msg = "scan file cannot read: " + scanFilenameWithPath;
				log.warn( msg );
				throw new RuntimeException( msg );
			}
		}
		
	    this.scanFileReader__MzMl_MzXml_FileReader = new MzMl_MzXml_FileReader();  //  MzMl_MzXml_FileReader from ISB code
        try {
        	this.scanFileReader__MzMl_MzXml_FileReader.open(scanFilenameWithPath);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find file: " + scanFilenameWithPath, e);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Error reading file: " + scanFilenameWithPath, e);
        
        } catch ( Exception e ) {
        	
        	throw new RuntimeException("Error Opening or reading file: " + scanFilenameWithPath, e);
        }
        
	}

    /**
     * 
     */
    public void close() {
        if(this.scanFileReader__MzMl_MzXml_FileReader != null)
        	this.scanFileReader__MzMl_MzXml_FileReader.close();
    }
    
    public MzML_MzXmlScan getNextScan() throws Exception {
    	
    	if ( endOf_ScanFile_Reached ) {
    		return null;
    	}
    	
    	MzML_MzXmlScan scan = this.scanFileReader__MzMl_MzXml_FileReader.getNextScan();
    	
    	if ( scan == null ) {
    		
    		endOf_ScanFile_Reached = true;
    	}
    	
    	return scan;
    }

	public int getScanBatchSizeMaximum() {
		return scanBatchSizeMaximum;
	}
}
