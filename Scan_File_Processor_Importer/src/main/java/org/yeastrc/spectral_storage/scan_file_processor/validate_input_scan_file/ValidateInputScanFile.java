package org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlHeader;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader.MzMl_MzXml_FileReader;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.IonInjectionTime_NotAvailable_OnDiskValue_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;

/**
 * Validate Scan file before processing it.
 * 
 * For now, just read the whole file using the scan file reader
 *
 */
public class ValidateInputScanFile {

	private static final Logger log = LoggerFactory.getLogger(ValidateInputScanFile.class);
	/**
	 * private constructor
	 */
	private ValidateInputScanFile(){}
	public static ValidateInputScanFile getInstance( ) throws Exception {
		ValidateInputScanFile instance = new ValidateInputScanFile();
		return instance;
	}
	
	public static class ValidateInputScanFile_Result {
		
		boolean noScans_InScanFile_Have_TotalIonCurrent_Populated;
		boolean noScans_InScanFile_Have_IonInjectionTime_Populated;
		
		public boolean isNoScans_InScanFile_Have_TotalIonCurrent_Populated() {
			return noScans_InScanFile_Have_TotalIonCurrent_Populated;
		}
		public void setNoScans_InScanFile_Have_TotalIonCurrent_Populated(
				boolean noScans_InScanFile_Have_TotalIonCurrent_Populated) {
			this.noScans_InScanFile_Have_TotalIonCurrent_Populated = noScans_InScanFile_Have_TotalIonCurrent_Populated;
		}
		public boolean isNoScans_InScanFile_Have_IonInjectionTime_Populated() {
			return noScans_InScanFile_Have_IonInjectionTime_Populated;
		}
		public void setNoScans_InScanFile_Have_IonInjectionTime_Populated(
				boolean noScans_InScanFile_Have_IonInjectionTime_Populated) {
			this.noScans_InScanFile_Have_IonInjectionTime_Populated = noScans_InScanFile_Have_IonInjectionTime_Populated;
		}
		
	}
	
	/**
	 * Validate Scan file before processing it.
	 * 
	 * For now, just read the whole file using the scan file reader
	 * @param scanFile
	 * @throws Exception
	 */
	public ValidateInputScanFile_Result validateScanFile( File scanFile ) throws Exception, SpectralStorageDataException {
		
		ValidateInputScanFile_Result validateResult = new ValidateInputScanFile_Result();

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
			
			validateAllScans( scanFileReader, scanFile, validateResult );
			
		} catch ( SpectralStorageDataException e ) {
			
			throw e;

		} catch ( Exception e ) {
			String msg = "AAAAA  Error Exception processing mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format. Next: throw new SpectralStorageDataException( msgForException )";
			log.error( msg, e );
			String msgForException = "Error processing Scan file.  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new SpectralStorageDataException( msgForException );
		} finally {
			if ( scanFileReader != null ) {
				scanFileReader.close();
			}
		}
		

		System.out.println( "Ended Validating input file: "  + scanFile.getAbsolutePath() 
			+ ", Now: " + new Date() );
		
		return validateResult;
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
			File scanFile,
			ValidateInputScanFile_Result validateResult ) throws Exception {

		//  Ensure no scan number is duplicated
		
		Set<Integer> scanNumbersInFile = new HashSet<>();
		
		NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
		
		int scansReadBlockCounter = 0; // track number since last reported on number read.
		
		long scanCounter = 0;
		
		Map<Byte, MutableLong> scanCountsPerScanLevel = new HashMap<>();
		
		boolean foundScanWith_IonInjectionTime_YES = false;
		boolean foundScanWith_IonInjectionTime_NO = false;

		boolean foundScanWith_TotalIonCurrent_YES = false;
		boolean foundScanWith_TotalIonCurrent_NO = false;
		
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
    			
    			{
    				byte msLevel = scanIn.getMsLevel();
    				MutableLong scanCountForScanLevel = scanCountsPerScanLevel.get( msLevel );
    				if ( scanCountForScanLevel == null ) {
    					scanCountForScanLevel = new MutableLong( 1 );
    					scanCountsPerScanLevel.put( msLevel, scanCountForScanLevel );
    				} else {
    					scanCountForScanLevel.increment();
    				}
    			}

    			//  Ensure no scan number is duplicated
    			
    			int scanNumber = scanIn.getScanNumber();
    			
    			if ( ( ! scanNumbersInFile.add( scanNumber ) ) ) {
    				String msg = "Error in Scan File: Duplicate scan number in file: " + scanNumber;
    				log.error( msg );
    				throw new SpectralStorageDataException( msg );
    			}
    			
    			if ( scanIn.getIonInjectionTime() != null ) {
    				
    				foundScanWith_IonInjectionTime_YES = true;
    				
    				if ( foundScanWith_IonInjectionTime_NO ) {
    					String msg = "Error in Scan File: At least 1 scan found with IonInjectionTime populated and at least 1 scan found with IonInjectionTime NOT populated ";
        				log.error( msg );
        				throw new SpectralStorageDataException( msg );
    				}
    				
    				if ( scanIn.getIonInjectionTime() == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE ) {
    					String msg = "Error in Scan File: IonInjectionTime == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE == Float.NEGATIVE_INFINITY. scanNumber: " + scanNumber;
        				log.error( msg );
        				throw new SpectralStorageDataException( msg );
    				}
    				
    			} else {
    				
    				foundScanWith_IonInjectionTime_NO = true;

    				if ( foundScanWith_IonInjectionTime_YES ) {
    					String msg = "Error in Scan File: At least 1 scan found with IonInjectionTime populated and at least 1 scan found with IonInjectionTime NOT populated ";
        				log.error( msg );
        				throw new SpectralStorageDataException( msg );
    				}
    			}

    			if ( scanIn.getTotalIonCurrent() != null ) {
    				
    				foundScanWith_TotalIonCurrent_YES = true;
    				
    				if ( foundScanWith_TotalIonCurrent_NO ) {
    					String msg = "Error in Scan File: At least 1 scan found with TotalIonCurrent populated and at least 1 scan found with TotalIonCurrent NOT populated ";
        				log.error( msg );
        				throw new SpectralStorageDataException( msg );
    				}
    				    				
    			} else {
    				
    				foundScanWith_TotalIonCurrent_NO = true;

    				if ( foundScanWith_TotalIonCurrent_YES ) {
    					String msg = "Error in Scan File: At least 1 scan found with TotalIonCurrent populated and at least 1 scan found with TotalIonCurrent NOT populated ";
        				log.error( msg );
        				throw new SpectralStorageDataException( msg );
    				}
    			}

			}
		} catch ( SpectralStorageDataException e ) {
			
			throw e;
			
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
		
		{
			List<Byte> scanLevels = new ArrayList<>( scanCountsPerScanLevel.keySet() );
			Collections.sort( scanLevels );
			for ( Byte scanLevel : scanLevels ) {
				MutableLong scanCountForScanLevel = scanCountsPerScanLevel.get( scanLevel );
				if ( scanCountForScanLevel == null ) {
					String msg = "scanCountForScanLevel == null for get for scanLevel from Map.keySet().  scanLevel: " + scanLevel;
					log.error( msg );
					throw new SpectralStorageProcessingException( msg );
				}

				System.out.println( "Number of scans level " + scanLevel + " read: "  
						+ numberFormatInsertedScansCounter.format( scanCountForScanLevel.longValue() ) );
			}
		}

		if ( log.isInfoEnabled() ) {
			log.info( "Done Validating the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
			log.info( "Number of scans (ms1, ms2, ?) read: " 
					+ numberFormatInsertedScansCounter.format( scanCounter ) );
			{
				List<Byte> scanLevels = new ArrayList<>( scanCountsPerScanLevel.keySet() );
				Collections.sort( scanLevels );
				for ( Byte scanLevel : scanLevels ) {
					MutableLong scanCountForScanLevel = scanCountsPerScanLevel.get( scanLevel );
					if ( scanCountForScanLevel == null ) {
						String msg = "scanCountForScanLevel == null for get for scanLevel from Map.keySet().  scanLevel: " + scanLevel;
						log.error( msg );
						throw new SpectralStorageProcessingException( msg );
					}

					log.info( "Number of scans level " + scanLevel + " read: "  
							+ numberFormatInsertedScansCounter.format( scanCountForScanLevel.longValue() ) );
				}
			}
		}
		
		

		validateResult.noScans_InScanFile_Have_TotalIonCurrent_Populated = foundScanWith_TotalIonCurrent_NO;
		validateResult.noScans_InScanFile_Have_IonInjectionTime_Populated = foundScanWith_IonInjectionTime_NO;
		
	}
}
