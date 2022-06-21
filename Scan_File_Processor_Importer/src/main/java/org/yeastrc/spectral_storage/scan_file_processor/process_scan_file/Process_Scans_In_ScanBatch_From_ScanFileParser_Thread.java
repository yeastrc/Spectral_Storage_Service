package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.mutable.MutableLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileRequest;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan_SinglePeak;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile.ValidateInputScanFile_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ByteValuesFor_Boolean_TrueFalse_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;

/**
 * Processes Batches of Scans from Scan Parser Webservice. Submits individual scans to Spectral Storage File Writer.
 * 
 * Takes in Data to Put into Spectral File and Calls methods on Writer code for latest Spectral File Format (open, writeScan, close)
 * 
 * This validates the Scan Batch Numbers start at 1 and are sequential.
 * 
 * This calls the Scan Parser Webservice to Close the Scan File.
 *
 */
public class Process_Scans_In_ScanBatch_From_ScanFileParser_Thread extends Thread {

	private static final Logger log = LoggerFactory.getLogger( Process_Scans_In_ScanBatch_From_ScanFileParser_Thread.class );
	
	/**
	 * @param pgmParams
	 * @return
	 */
	public static Process_Scans_In_ScanBatch_From_ScanFileParser_Thread getNewInstance( 
			
			Scan_File_Processor_MainProgram_Params pgmParams, 
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue,
			SpectralFile_Writer__IF spectralFile_Writer,
			Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread parse_ScanFile_Pass_ScanBatch_To_Processing_Thread // For 'Close' call
			) {
		
		Process_Scans_In_ScanBatch_From_ScanFileParser_Thread instance = 
				new Process_Scans_In_ScanBatch_From_ScanFileParser_Thread(pgmParams, parse_ScanFile_ScanBatch_Queue, spectralFile_Writer, parse_ScanFile_Pass_ScanBatch_To_Processing_Thread);
		return instance;
	}

	// Private Constructor
	private Process_Scans_In_ScanBatch_From_ScanFileParser_Thread(
			
			Scan_File_Processor_MainProgram_Params pgmParams, 
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue,
			SpectralFile_Writer__IF spectralFile_Writer,
			Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread parse_ScanFile_Pass_ScanBatch_To_Processing_Thread // For 'Close' call
			) {

		this.setName( "Thread-Process_Scans_In_ScanBatch_From_ScanFileParser_Thread" );
		this.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

		this.parse_ScanFile_ScanBatch_Queue = parse_ScanFile_ScanBatch_Queue;
		this.spectralFile_Writer = spectralFile_Writer;
		
		this.parse_ScanFile_Pass_ScanBatch_To_Processing_Thread = parse_ScanFile_Pass_ScanBatch_To_Processing_Thread;
		
		this.pgmParams = pgmParams;  // Always set last since 'volatile'
	}
	
	private volatile Throwable throwable_Caught_Main_run_method;

	public Throwable getThrowable_Caught_Main_run_method() {
		return throwable_Caught_Main_run_method;
	}
	
	/**
	 * Set to true when get to end of the 'run()' with no exceptions
	 */
	private volatile boolean writeSpectralFiles_Complete_And_Successful = false;
	
	public boolean isWriteSpectralFiles_Complete_And_Successful() {
		return writeSpectralFiles_Complete_And_Successful;
	}
	
	/**
	 * Main Program Params from command line
	 */
	private volatile Scan_File_Processor_MainProgram_Params pgmParams;
	
	private SpectralFile_Writer__IF spectralFile_Writer;
	
	private Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread parse_ScanFile_Pass_ScanBatch_To_Processing_Thread;  // For 'Close' call

	private GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue;
	
	//  Tracking prev_ScanBatchNumber
	
	/**
	 * 
	 */
	private Integer prev_ScanBatchNumber = null;
	
	//  Properties used for reporting while processing scans

	private NumberFormat numberFormatInsertedScansCounter = NumberFormat.getInstance();
	
	private int scansReadBlockCounter = 0; // track number since last reported on number read.
	
	private long scanCounter = 0;

	private Map<Integer, MutableLong> scanCountsPerScanLevel = new HashMap<>();
	
	@Override
	public void run() {
		
		try {
//			Thread thisThread = Thread.currentThread();
//			thisThread.setName( "Thread-Parse_ScanFile_PassTo_Processing_Thread" );
//			thisThread.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

			ValidateInputScanFile validateInputScanFile = ValidateInputScanFile.getInstance();

			//  Used by the 'close' of spectralFile_Writer
			SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common = new SpectralFile_CloseWriter_Data_Common();

			File scanFile = pgmParams.getInputScanFile();
				
			
			try {
				
				try {
					//  Wait for "Open" first entry in Queue
					GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry parse_ScanFile_ScanBatch_QueueEntry = parse_ScanFile_ScanBatch_Queue.getNextEntryFromQueue_Blocking();
				
					if ( parse_ScanFile_ScanBatch_QueueEntry.requestType != GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.OPEN_DATA_FILE ) {
						
						String msg = "First Entry from Queue is NOT requestType OPEN_DATA_FILE";
						log.error(msg);
						throw new SpectralStorageProcessingException(msg);
					}
					
					try {
						spectralFile_Writer.open();
					} catch ( Exception e ) {
						log.error( "spectralFile_Writer.open() threw Exception: ", e );
						throw e;
					}
	
					//    Process all Scans in Scan File
					
					processAllScans( pgmParams, spectralFile_Writer, validateInputScanFile );
	
				} catch ( SpectralStorageDataException e ) {

					throwable_Caught_Main_run_method = e;
					
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					String msg = "Error Exception processing Scan Batch from Scan file: " + scanFile.getAbsolutePath();
					log.error( msg, e );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					
					throw e;
					
				} catch ( Throwable t) {
	
					throwable_Caught_Main_run_method = t;
					
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					String msg = "Error Exception processing Scan Batch from Scan file: " + scanFile.getAbsolutePath();
					log.error( msg, t );
					log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
					
					throw t;
					
				} finally {
					
					try {
						this.parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.close_ScanFile_Parser(this.prev_ScanBatchNumber);
						
					} catch ( Throwable t ) {

						if ( throwable_Caught_Main_run_method != null ) {
							
							//  No existing saved Exception so save this and throw it
							
							throwable_Caught_Main_run_method = t;
						
							throw t;
						}
					}
				}
					
			} finally {

				if ( spectralFile_Writer != null ) {
					
					if ( throwable_Caught_Main_run_method != null ) {
						
						spectralFile_CloseWriter_Data_Common.setExceptionEncounteredProcessingScanFile(true);
					
					} else {

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

					//  Close Output Data File and write other files (index, etc)

					//  spectralFile_Writer is the Writer for the Latest Version of the Spectral File Format

					try {

						spectralFile_Writer.close(spectralFile_CloseWriter_Data_Common);

						if ( throwable_Caught_Main_run_method == null ) {
							this.writeSpectralFiles_Complete_And_Successful = true;
						}

					} catch ( Throwable t) {

						if ( throwable_Caught_Main_run_method != null ) {
							
							//  No existing saved Exception so save this and throw it
							
							throwable_Caught_Main_run_method = t;
						
							throw t;
						}
					}
				}
			}

		} catch ( Throwable t) {
			
			throwable_Caught_Main_run_method = t;
			
			ProcessUploadedScanFileRequest.getSingletonInstance().awaken();
		}
	}
	
	/**
	 * @param scanFileReader
	 * @param spectralFile_Writer
	 * @param scanFile
	 * @throws Exception
	 */
	private void processAllScans(
			
			Scan_File_Processor_MainProgram_Params pgmParams,
			
			SpectralFile_Writer__IF spectralFile_Writer,
			ValidateInputScanFile validateInputScanFile
			 ) throws Exception {
		
		
		try {
			while (true) {  //  Exit using 'break' when get 'END_OF_SCANS'
			
				GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry parse_ScanFile_ScanBatch_QueueEntry = parse_ScanFile_ScanBatch_Queue.getNextEntryFromQueue_Blocking();
				
				
				if ( parse_ScanFile_ScanBatch_QueueEntry.requestType == GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.END_OF_SCANS ) {
					
					//  End of Scans
					
					if ( parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number != null ) {
						this.prev_ScanBatchNumber = parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number;
					}
					
					break;  //  EARLY BREAK from Loop
				}
				
				{
					//  FAKE FORCE Exception for Exception testing
					
//					if (parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number > 3) {
//						String msg = "FAKE FORCE Exception for Exception testing";
//						log.error(msg);
//						throw new SpectralStorageProcessingException(msg);
//					}
				}
				
				//  Validate scan_batch_number
				
				{
					if ( parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number == null ) {
						String msg = "For Scan Entry, the parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number == null or unassigned";
						log.error(msg);
						throw new SpectralStorageProcessingException(msg);
					}
					
//					log.warn( "INFO::  parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number: " + parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number );
					
					if ( this.prev_ScanBatchNumber == null && parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number.intValue() != 1 ) {
						String msg = "For Scan Entry, the parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number does NOT start at 1";
						log.error(msg);
						throw new SpectralStorageProcessingException(msg);
					}
					if ( this.prev_ScanBatchNumber != null ) {
						
						//  Not first one so validate they are sequential
						
						if ( ( this.prev_ScanBatchNumber.intValue() + 1 ) != parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number.intValue() ) {
							String msg = "For Scan Entry, the parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number is not sequential.  Prev Value: "
									+ this.prev_ScanBatchNumber.intValue()
									+ ", current value: "
									+ parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number.intValue();
							log.error(msg);
							throw new SpectralStorageProcessingException(msg);
						}
					}

					this.prev_ScanBatchNumber = parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number;
				}
				
				List<ScanFileParser_ScanBatch_SingleScan> scanBatchList = parse_ScanFile_ScanBatch_QueueEntry.scanBatchList;
				
				if ( scanBatchList == null || scanBatchList.isEmpty() ) {
					
					continue;  // EARLY CONTINUE
				}
			
				for ( ScanFileParser_ScanBatch_SingleScan scanIn : scanBatchList ) {

					scanCounter++;
					scansReadBlockCounter++;
					if ( scansReadBlockCounter > 10000 ) {
						System.out.println( "Number of scans (ms1, ms2, ?) processed so far: " 
								+ numberFormatInsertedScansCounter.format( scanCounter )
								+ ", Now: " + new Date() );
						scansReadBlockCounter = 0;
					}
					{
						int msLevel = scanIn.getScanLevel();
						MutableLong scanCountForScanLevel = scanCountsPerScanLevel.get( msLevel );
						if ( scanCountForScanLevel == null ) {
							scanCountForScanLevel = new MutableLong( 1 );
							scanCountsPerScanLevel.put( msLevel, scanCountForScanLevel );
						} else {
							scanCountForScanLevel.increment();
						}
					}


					//						private byte level;
					//						private int scanNumber;
					//						private float retentionTime;
					//						private Float ionInjectionTime; // in milliseconds
					//						private float totalIonCurrent;
					//						private byte isCentroid;
					//
					//						//  Only applicable where level > 1
					//
					//						private int parentScanNumber;
					//						private byte precursorCharge;
					//						private float precursor_M_Over_Z;

					SpectralFile_SingleScan_Common spectralFile_SingleScan = new SpectralFile_SingleScan_Common();

					spectralFile_SingleScan.setLevel( (byte) scanIn.getScanLevel() );
					spectralFile_SingleScan.setScanNumber( scanIn.getScanNumber() );
					spectralFile_SingleScan.setRetentionTime( scanIn.getRetentionTime() );
					if ( scanIn.isCentroid ) {
						spectralFile_SingleScan.setIsCentroid( ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_TRUE );
					} else {
						spectralFile_SingleScan.setIsCentroid( ByteValuesFor_Boolean_TrueFalse_Constants.BYTE_VALUE_FOR_BOOLEAN_FALSE );
					}
					spectralFile_SingleScan.setParentScanNumber( scanIn.getParentScanNumber() );
					if ( scanIn.getPrecursorCharge() != null ) {
						spectralFile_SingleScan.setPrecursorCharge( (byte) scanIn.getPrecursorCharge().intValue() );
					}
					spectralFile_SingleScan.setPrecursor_M_Over_Z( scanIn.getPrecursor_M_Over_Z() );
					spectralFile_SingleScan.setIonInjectionTime( scanIn.getIonInjectionTime() );

					//  Process Scan Peaks and if needed compute totalIonCurrent from individual scan peaks

					List<ScanFileParser_ScanBatch_SingleScan_SinglePeak> scanPeakList = scanIn.getScanPeaks();
					List<SpectralFile_SingleScanPeak_Common> scanPeaksList = new ArrayList<>( scanPeakList.size() );
					spectralFile_SingleScan.setScanPeaksAsObjectArray( scanPeaksList );

					double scanIntensitiesSummedForScan = 0;

					for ( ScanFileParser_ScanBatch_SingleScan_SinglePeak scanPeak : scanPeakList ) {

						SpectralFile_SingleScanPeak_Common spectralFile_SingleScanPeak = new SpectralFile_SingleScanPeak_Common();
						spectralFile_SingleScanPeak.setM_over_Z( scanPeak.getM_over_Z() );
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

					//  Call to spectralFile_Writer (For latest File Format) to Write Scan

					spectralFile_Writer.writeScan( spectralFile_SingleScan );
				}
			}
			
		} catch (SpectralStorageDataException e) {

			File scanFile = pgmParams.getInputScanFile();
					
			String msg = "Error SpectralStorageDataException processing Scan file: " + scanFile.getAbsolutePath();
			log.error( msg, e );
			throw e;
			
//		} catch (IOException e) {
//			String msg = "Error IOException processing mzML or MzXml Scan file: " + scanFileWithPath.getAbsolutePath();
//			log.error( msg, e );
//			throw new Exception( msg, e );
		} catch ( Exception e ) {

			File scanFile = pgmParams.getInputScanFile();
					
			String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}
		

		File scanFile = pgmParams.getInputScanFile();
				
		System.out.println( "Done processing the scan file: " + scanFile.getAbsolutePath() );
		System.out.println( "Number of scans (ms1, ms2, ?) read: " 
				+ numberFormatInsertedScansCounter.format( scanCounter ) );

		{
			List<Integer> scanLevels = new ArrayList<>( scanCountsPerScanLevel.keySet() );
			Collections.sort( scanLevels );
			for ( Integer scanLevel : scanLevels ) {
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
			log.info( "Done processing the scan file: " + scanFile.getAbsolutePath() );
			log.info( "Number of scans (ms1, ms2, ?) read: " 
					+ numberFormatInsertedScansCounter.format( scanCounter ) );
			{
				List<Integer> scanLevels = new ArrayList<>( scanCountsPerScanLevel.keySet() );
				Collections.sort( scanLevels );
				for ( Integer scanLevel : scanLevels ) {
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
