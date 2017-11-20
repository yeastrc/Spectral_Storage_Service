package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlHeader;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.ScanPeak;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.reader.MzMl_MzXml_FileReader;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.Compute_File_Hashes.Compute_File_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer_CurrentFormat_Factory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;

/**
 * 
 *
 */
public class Process_ScanFile_Create_SpectralFile {
	
	private static final Logger log = Logger.getLogger(Process_ScanFile_Create_SpectralFile.class);
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
	 * @param sha384_String
	 * @throws Exception
	 */
	public void processScanFile( 
			File scanFile, 
			File subDirForOutputFiles, 
			String hash_String, 
			Compute_File_Hashes_Result compute_File_Hashes_Result ) throws Exception {

		if ( ! scanFile.exists() ) {
			String msg = "Input scan file does not exist: " + scanFile.getAbsolutePath();
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		System.out.println( "Starting Processing input file: "  + scanFile.getAbsolutePath() 
			+ ", Now: " + new Date() );
		
		String scanFilename = scanFile.getName(); //  Name of scan file stored on disk
		String scanFilePath = scanFile.getCanonicalFile().getParentFile().getCanonicalPath();
		long scanFileLength_InBytes = scanFile.length();
		
		MzMl_MzXml_FileReader scanFileReader = null;
		SpectralFile_Writer__IF spectralFile_Writer = null;
		
		try {
			scanFileReader = getMzMLFileReader( scanFile );
			
			MzML_MzXmlHeader mzXmlHeader = scanFileReader.getRunHeader();
			
			SpectralFile_Header_Common spectralFile_Header_Common = new SpectralFile_Header_Common();
			spectralFile_Header_Common.setScanFileLength_InBytes( scanFileLength_InBytes );
			spectralFile_Header_Common.setMainHash( compute_File_Hashes_Result.getSha_384_Hash() );
			spectralFile_Header_Common.setAltHashSHA512( compute_File_Hashes_Result.getSha_512_Hash() );
			spectralFile_Header_Common.setAltHashSHA1( compute_File_Hashes_Result.getSha_1_Hash() );

			spectralFile_Writer = SpectralFile_Writer_CurrentFormat_Factory.getInstance().getSpectralFile_Writer_LatestVersion();
			
			spectralFile_Writer.open( hash_String, subDirForOutputFiles, spectralFile_Header_Common );
			
			processAllScans( scanFileReader, spectralFile_Writer, scanFile );

			System.out.println( "************************************" );
			System.out.println( "***  Statistics" );
			System.out.println();
			System.out.println( "Scan Peak count: " + spectralFile_Writer.getScanPeaksTotalCount() );
			System.out.println( "Output Scan Peak data bytes, not commpressed: " + spectralFile_Writer.getScanPeaksTotalBytes() );
			System.out.println( "Output Scan Peak data bytes, commpressed: " + spectralFile_Writer.getScanPeaksCompressedTotalBytes() );

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
			if ( spectralFile_Writer != null ) {
				spectralFile_Writer.close();
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
			File scanFile ) throws Exception {
		
		int insertedScansCounter = 0;
		NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
		
		int insertedScansBlockCounter = 0; // track number since last reported on number inserted.
		int scansReadBlockCounter = 0; // track number since last reported on number read.
		int scansForSysoutLineCounter = 0;
		
		long scanCounter = 0;
		long ms1_ScanCounter = 0;
		long ms_gt_1_ScanCounter = 0;
		
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
    			
    			if ( scanIn.getMsLevel() == 1 ) {
    				ms1_ScanCounter++;
    			} else {
    				ms_gt_1_ScanCounter++;
    			}

//    			private byte level;
//    			private int scanNumber;
//    			private float retentionTime;
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
    			
    			
    			List<ScanPeak> scanPeakList = scanIn.getScanPeakList();
    			List<SpectralFile_SingleScanPeak_Common> scanPeaksList = new ArrayList<>( scanPeakList.size() );
    			spectralFile_SingleScan.setScanPeaksAsObjectArray( scanPeaksList );
    			
    			for ( ScanPeak scanPeak : scanPeakList ) {
    				
    				SpectralFile_SingleScanPeak_Common spectralFile_SingleScanPeak = new SpectralFile_SingleScanPeak_Common();
    				spectralFile_SingleScanPeak.setM_over_Z( scanPeak.getMz() );
    				spectralFile_SingleScanPeak.setIntensity( scanPeak.getIntensity() );
    				
    				scanPeaksList.add( spectralFile_SingleScanPeak );
    			}
    			
    			
    			spectralFile_Writer.writeScan( spectralFile_SingleScan );

			}
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
		System.out.println( "Number of scans level 1 read: "  
				+ numberFormatInsertedScansCounter.format( ms1_ScanCounter ) );
		System.out.println( "Number of scans level > 1  read: "  
				+ numberFormatInsertedScansCounter.format( ms_gt_1_ScanCounter ) );

		if ( log.isInfoEnabled() ) {
			log.info( "Done processing the MzML or MzXml scan file: " + scanFile.getAbsolutePath() );
			log.info( "Number of scans (ms1, ms2, ?) read: " 
					+ numberFormatInsertedScansCounter.format( scanCounter ) );
			log.info( "Number of scans level 1 read: "  
					+ numberFormatInsertedScansCounter.format( ms1_ScanCounter ) );
			log.info( "Number of scans level > 1 read: "  
					+ numberFormatInsertedScansCounter.format( ms_gt_1_ScanCounter ) );
		}
	}
}
