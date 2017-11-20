package org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file;

import java.io.File;
import java.text.NumberFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlHeader;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader.MzMl_MzXml_FileReader;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;

/**
 * Validate Scan file before processing it.
 * 
 * For now, just read the whole file using the scan file reader
 *
 */
public class ValidateInputScanFile {

	private static final Logger log = Logger.getLogger(ValidateInputScanFile.class);
	/**
	 * private constructor
	 */
	private ValidateInputScanFile(){}
	public static ValidateInputScanFile getInstance( ) throws Exception {
		ValidateInputScanFile instance = new ValidateInputScanFile();
		return instance;
	}
	
	/**
	 * Validate Scan file before processing it.
	 * 
	 * For now, just read the whole file using the scan file reader
	 * @param scanFile
	 * @throws Exception
	 */
	public void validateScanFile( File scanFile ) throws Exception, SpectralStorageDataException {

		if ( ! scanFile.exists() ) {
			String msg = "Input scan file does not exist: " + scanFile.getAbsolutePath();
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		System.out.println( "Starting Validating input file: "  + scanFile.getAbsolutePath() 
			+ ", Now: " + new Date() );
		
		String scanFilename = scanFile.getName(); //  Name of scan file stored on disk
		String scanFilePath = scanFile.getCanonicalFile().getParentFile().getCanonicalPath();
		long scanFileLength_InBytes = scanFile.length();
		
		MzMl_MzXml_FileReader scanFileReader = null;
		
		try {
			scanFileReader = getMzMLFileReader( scanFile );
			
			MzML_MzXmlHeader mzXmlHeader = scanFileReader.getRunHeader();
			
			validateAllScans( scanFileReader, scanFile );

		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFile.getAbsolutePath()
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new Exception( msgForException );
		} finally {
			if ( scanFileReader != null ) {
				scanFileReader.close();
			}
		}
		

		System.out.println( "Ended Validating input file: "  + scanFile.getAbsolutePath() 
			+ ", Now: " + new Date() );
	}
	
	/**
	 * @param scanFileWithPath
	 * @return
	 * @throws Exception
	 */
	private MzMl_MzXml_FileReader getMzMLFileReader( File scanFileWithPath /* , String sha1Sum */ ) throws Exception {
		if ( ! scanFileWithPath.exists() ) {
			throw new Exception( "Input mzMl or MzXml file not found: '" + scanFileWithPath.getAbsolutePath() + "'");
		}
		MzMl_MzXml_FileReader scanFileReader = new MzMl_MzXml_FileReader();
		scanFileReader.open( scanFileWithPath.getAbsolutePath() /* , sha1Sum */ );
		return scanFileReader;
	}
	
	/**
	 * @param scanFileReader
	 * @param scanFile
	 * @throws Exception
	 */
	private void validateAllScans( 
			MzMl_MzXml_FileReader scanFileReader, 
			File scanFile ) throws Exception {
		
		NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
		
		int scansReadBlockCounter = 0; // track number since last reported on number read.
		
		long scanCounter = 0;
		long ms1_ScanCounter = 0;
		long ms_gt_1_ScanCounter = 0;
		
		try {
			MzML_MzXmlScan scanIn = null;
			while ( ( scanIn = scanFileReader.getNextScan() ) != null ) {
				scanCounter++;
				scansReadBlockCounter++;
    			if ( scansReadBlockCounter > 10000 ) {
    				System.out.println( "Number of scans (ms1, ms2, ?) validated so far: " 
    						+ numberFormatInsertedScansCounter.format( scanCounter )
    						+ ", Now: " + new Date() );
    				scansReadBlockCounter = 0;
    			}
    			
    			if ( scanIn.getMsLevel() == 1 ) {
    				ms1_ScanCounter++;
    			} else {
    				ms_gt_1_ScanCounter++;
    			}

			}
//		} catch (IOException e) {
//			String msg = "Error IOException processing mzML or MzXml Scan file: " + scanFileWithPath.getAbsolutePath();
//			log.error( msg, e );
//			throw new Exception( msg, e );
		} catch ( Exception e ) {
			String msg = "Error Exception Validating mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFile.getAbsolutePath()
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new SpectralStorageDataException( msgForException );
		}
		
		
		System.out.println( "Done Validating the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
		System.out.println( "Number of scans (ms1, ms2, ?) read: " 
				+ numberFormatInsertedScansCounter.format( scanCounter ) );
		System.out.println( "Number of scans level 1 read: "  
				+ numberFormatInsertedScansCounter.format( ms1_ScanCounter ) );
		System.out.println( "Number of scans level > 1  read: "  
				+ numberFormatInsertedScansCounter.format( ms_gt_1_ScanCounter ) );

		if ( log.isInfoEnabled() ) {
			log.info( "Done Validating the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
			log.info( "Number of scans (ms1, ms2, ?) read: " 
					+ numberFormatInsertedScansCounter.format( scanCounter ) );
			log.info( "Number of scans level 1 read: "  
					+ numberFormatInsertedScansCounter.format( ms1_ScanCounter ) );
			log.info( "Number of scans level > 1 read: "  
					+ numberFormatInsertedScansCounter.format( ms_gt_1_ScanCounter ) );
		}
	}
}
