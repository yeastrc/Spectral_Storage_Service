package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlHeader;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.ScanPeak;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader.MzMl_MzXml_FileReader;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile.ValidateInputScanFile_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes.Compute_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer_CurrentFormat_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_999_latest.StorageFile_Version_999_LATEST_Constants;

/**
 * 
 *
 */
public class Process_ScanFile_Create_SpectralFile {
	
	private static final Logger log = LoggerFactory.getLogger(Process_ScanFile_Create_SpectralFile.class);
	/**
	 * private constructor
	 */
	private Process_ScanFile_Create_SpectralFile(){}
	public static Process_ScanFile_Create_SpectralFile getInstance( ) throws Exception {
		Process_ScanFile_Create_SpectralFile instance = new Process_ScanFile_Create_SpectralFile();
		return instance;
	}
	
	/**
	 * @param scanFile
	 * @param subDirForOutputFiles
	 * @param hash_String
	 * @param compute_Hashes
	 * @param validateInputScanFile_Result
	 * @throws Exception
	 */
	public void processScanFile( 
			Scan_File_Processor_MainProgram_Params pgmParams, 
			File subDirForOutputFiles,
			String hash_String, 
			Compute_Hashes compute_Hashes ) throws Exception {

		String converterBaseUrlString = pgmParams.getConverterBaseUrlString();
		
		File scanFile = pgmParams.getInputScanFile();
				
		Compute_Hashes_Result compute_Hashes_Result = compute_Hashes.compute_Hashes();
		
		if ( ! scanFile.exists() ) {
			String msg = "Input scan file does not exist: " + scanFile.getAbsolutePath();
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		System.out.println( "Starting Processing input file: "  + scanFile.getAbsolutePath() 
			+ ", Now: " + new Date() );
		
		long scanFileLength_InBytes = scanFile.length();
		
		MzMl_MzXml_FileReader scanFileReader = null;
		SpectralFile_Writer__IF spectralFile_Writer = null;
		
		ValidateInputScanFile validateInputScanFile = ValidateInputScanFile.getInstance();

		//  Used by the 'close' of spectralFile_Writer
		SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common = new SpectralFile_CloseWriter_Data_Common();
		
		try {
			scanFileReader = getMzMLFileReader( scanFile );
			
			MzML_MzXmlHeader mzXmlHeader = scanFileReader.getRunHeader();
			
			SpectralFile_Header_Common spectralFile_Header_Common = new SpectralFile_Header_Common();
			
			spectralFile_Header_Common.setScanFileLength_InBytes( scanFileLength_InBytes );
			spectralFile_Header_Common.setMainHash( compute_Hashes_Result.getSha_384_Hash() );
			spectralFile_Header_Common.setAltHashSHA512( compute_Hashes_Result.getSha_512_Hash() );
			spectralFile_Header_Common.setAltHashSHA1( compute_Hashes_Result.getSha_1_Hash() );
			
			spectralFile_Writer = SpectralFile_Writer_CurrentFormat_Factory.getInstance().getSpectralFile_Writer_LatestVersion();
			
			if ( StorageFile_Version_999_LATEST_Constants.FILE_VERSION != spectralFile_Writer.getVersion() ) {
				
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msg = "ERROR: Call to SpectralFile_Writer_CurrentFormat_Factory.getInstance().getSpectralFile_Writer_LatestVersion() did not return latest version. StorageFile_Version_999_LATEST_Constants.FILE_VERSION != spectralFile_Writer.getVersion(). "
							+ " StorageFile_Version_999_LATEST_Constants.FILE_VERSION: " + StorageFile_Version_999_LATEST_Constants.FILE_VERSION
							+ "spectralFile_Writer.getVersion(): " + spectralFile_Writer.getVersion();
				log.error( msg );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				System.err.println( msg );
				System.err.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			}
			
			spectralFile_Writer.open( hash_String, subDirForOutputFiles, spectralFile_Header_Common );
			
			processAllScans( scanFileReader, spectralFile_Writer, validateInputScanFile, scanFile );

			System.out.println( "************************************" );
			System.out.println( "***  Statistics" );
			System.out.println();
			System.out.println( "Scan Peak count: " + spectralFile_Writer.getScanPeaksTotalCount() );
			System.out.println( "Output Scan Peak data bytes, not commpressed: " + spectralFile_Writer.getScanPeaksTotalBytes() );
			System.out.println( "Output Scan Peak data bytes, commpressed: " + spectralFile_Writer.getScanPeaksCompressedTotalBytes() );

			
		} catch ( SpectralStorageDataException e ) {
			
			spectralFile_CloseWriter_Data_Common.setExceptionEncounteredProcessingScanFile(true);
			
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			
			throw e;
			
		} catch ( Throwable e ) {
			
			spectralFile_CloseWriter_Data_Common.setExceptionEncounteredProcessingScanFile(true);
			
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
			String msgForException = "Error processing Scan file. " 
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			
			throw new SpectralStorageDataException( msgForException );
			
		} finally {
			if ( scanFileReader != null ) {
				scanFileReader.close();
			}
			if ( spectralFile_Writer != null ) {

				if ( ! spectralFile_CloseWriter_Data_Common.isExceptionEncounteredProcessingScanFile() ) {
					
					ValidateInputScanFile_Result validateInputScanFile_Result = validateInputScanFile.get_ValidateInputScanFile_Result();
					
					if ( validateInputScanFile_Result.isNoScans_InScanFile_Have_TotalIonCurrent_Populated() ) {
						spectralFile_CloseWriter_Data_Common.setTotalIonCurrent_ForEachScan_ComputedFromScanPeaks( true );  //  Boolean so must set to true or false
					} else {
						spectralFile_CloseWriter_Data_Common.setTotalIonCurrent_ForEachScan_ComputedFromScanPeaks( false );  //  Boolean so must set to true or false
					}
					if ( validateInputScanFile_Result.isNoScans_InScanFile_Have_IonInjectionTime_Populated() ) {
						spectralFile_CloseWriter_Data_Common.setIonInjectionTime_NotPopulated( true );  //  Boolean so must set to true or false
					} else {
						spectralFile_CloseWriter_Data_Common.setIonInjectionTime_NotPopulated( false );  //  Boolean so must set to true or false
					}
				}
				
				spectralFile_Writer.close(spectralFile_CloseWriter_Data_Common);
			}
		}
		
		System.out.println( "Ended Processing input file: "  + scanFile.getAbsolutePath() 
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
	 * @param spectralFile_Writer
	 * @param scanFile
	 * @throws Exception
	 */
	private void processAllScans( 
			MzMl_MzXml_FileReader scanFileReader, 
			SpectralFile_Writer__IF spectralFile_Writer,
			ValidateInputScanFile validateInputScanFile,
			File scanFile ) throws Exception {
		
//		int insertedScansCounter = 0;
		NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
		
//		int insertedScansBlockCounter = 0; // track number since last reported on number inserted.
		int scansReadBlockCounter = 0; // track number since last reported on number read.
//		int scansForSysoutLineCounter = 0;
		
		long scanCounter = 0;

		Map<Byte, MutableLong> scanCountsPerScanLevel = new HashMap<>();
		
		
		try {
			MzML_MzXmlScan scanIn = null;
			while ( ( scanIn = scanFileReader.getNextScan() ) != null ) {
				scanCounter++;
				scansReadBlockCounter++;
    			if ( scansReadBlockCounter > 10000 ) {
    				System.out.println( "Number of scans (ms1, ms2, ?) processed so far: " 
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


//    			private byte level;
//    			private int scanNumber;
//    			private float retentionTime;
//    			private Float ionInjectionTime; // in milliseconds
//    			private float totalIonCurrent;
//    			private byte isCentroid;
//    			
//    			//  Only applicable where level > 1
//    			
//    			private int parentScanNumber;
//    			private byte precursorCharge;
//    			private float precursor_M_Over_Z;
    			
    			SpectralFile_SingleScan_Common spectralFile_SingleScan = new SpectralFile_SingleScan_Common();
    			
    			spectralFile_SingleScan.setLevel( scanIn.getMsLevel() );
    			spectralFile_SingleScan.setScanNumber( scanIn.getScanNumber() );
    			spectralFile_SingleScan.setRetentionTime( scanIn.getRetentionTime() );
    			spectralFile_SingleScan.setIsCentroid( scanIn.getIsCentroided() );
    			spectralFile_SingleScan.setParentScanNumber( scanIn.getPrecursorScanNum() );
    			spectralFile_SingleScan.setPrecursorCharge( scanIn.getPrecursorCharge() );
    			spectralFile_SingleScan.setPrecursor_M_Over_Z( scanIn.getPrecursorMz()  );
    			spectralFile_SingleScan.setIonInjectionTime( scanIn.getIonInjectionTime() );

				//  Process Scan Peaks and if needed compute totalIonCurrent from individual scan peaks
		
    			List<ScanPeak> scanPeakList = scanIn.getScanPeakList();
    			List<SpectralFile_SingleScanPeak_Common> scanPeaksList = new ArrayList<>( scanPeakList.size() );
    			spectralFile_SingleScan.setScanPeaksAsObjectArray( scanPeaksList );
    			
		        double scanIntensitiesSummedForScan = 0;
		        
    			for ( ScanPeak scanPeak : scanPeakList ) {
    				
    				SpectralFile_SingleScanPeak_Common spectralFile_SingleScanPeak = new SpectralFile_SingleScanPeak_Common();
    				spectralFile_SingleScanPeak.setM_over_Z( scanPeak.getMz() );
    				spectralFile_SingleScanPeak.setIntensity( scanPeak.getIntensity() );
    				
    				scanPeaksList.add( spectralFile_SingleScanPeak );
    				
    				scanIntensitiesSummedForScan += scanPeak.getIntensity();
    			}

    			if ( scanIn.getTotalIonCurrent() != null ) {
    				spectralFile_SingleScan.setTotalIonCurrent( scanIn.getTotalIonCurrent() );
    			} else {

    				//  TotalIonCurrent not in header so used Summed value from Scan Peaks

    				float totalIonCurrent = (float) scanIntensitiesSummedForScan;
    				spectralFile_SingleScan.setTotalIonCurrent( totalIonCurrent );
    			}
    			
    			validateInputScanFile.validate_SingleScan( spectralFile_SingleScan ); //  Throws an exception if error
    			
    			spectralFile_Writer.writeScan( spectralFile_SingleScan );

			}
			
		} catch (SpectralStorageDataException e) {
			
			String msg = "Error SpectralStorageDataException processing mzML or MzXml Scan file: " + scanFile.getAbsolutePath();
			log.error( msg, e );
			throw e;
			
//		} catch (IOException e) {
//			String msg = "Error IOException processing mzML or MzXml Scan file: " + scanFileWithPath.getAbsolutePath();
//			log.error( msg, e );
//			throw new Exception( msg, e );
		} catch ( Exception e ) {
			String msg = "Error Exception processing mzML or mzXml Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFile.getAbsolutePath()
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new Exception( msgForException );
		}
		
		
		System.out.println( "Done processing the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
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
			log.info( "Done processing the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
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
	}
}
