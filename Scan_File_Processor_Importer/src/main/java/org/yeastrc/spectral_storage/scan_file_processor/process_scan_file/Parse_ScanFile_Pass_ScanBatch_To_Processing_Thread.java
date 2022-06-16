package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileRequest;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Parse_ScanFile_ScanBatch_Queue.Parse_ScanFile_ScanBatch_QueueEntry;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Parse_ScanFile_ScanBatch_Queue.Parse_ScanFile_ScanBatch_QueueEntry_RequestType;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;

/**
 * Takes in Data to Put into Spectral File and Calls methods on Writer code for latest Spectral File Format (open, writeScan, close)
 *
 */
public class Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread extends Thread {

	private static final Logger log = LoggerFactory.getLogger( Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.class );
	
	/**
	 * @param pgmParams
	 * @return
	 */
	public static Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread getNewInstance( Scan_File_Processor_MainProgram_Params pgmParams, Parse_ScanFile_ScanBatch_Queue parse_ScanFile_ScanBatch_Queue ) {
		
		Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread instance = new Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread(pgmParams, parse_ScanFile_ScanBatch_Queue);
		return instance;
	}

	// Private Constructor
	private Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread(Scan_File_Processor_MainProgram_Params pgmParams, Parse_ScanFile_ScanBatch_Queue parse_ScanFile_ScanBatch_Queue ) {

		this.setName( "Thread-Parse_ScanFile_PassTo_Processing_Thread" );
		this.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

		this.parse_ScanFile_ScanBatch_Queue = parse_ScanFile_ScanBatch_Queue;

		this.pgmParams = pgmParams;  // Assign last since 'volatile'
	}
	
	private Throwable throwable_Caught_Main_run_method;

	public Throwable getThrowable_Caught_Main_run_method() {
		return throwable_Caught_Main_run_method;
	}
	
	/**
	 * Main Program Params from command line
	 */
	private volatile Scan_File_Processor_MainProgram_Params pgmParams;
	
	private Parse_ScanFile_ScanBatch_Queue parse_ScanFile_ScanBatch_Queue;
	
	private volatile String converter_identifier_for_scan_file; // Can get call on alt thread using this
	
	private volatile boolean close_ScanFile_Parser__Called;
	
	@Override
	public void run() {
		
		try {
//			Thread thisThread = Thread.currentThread();
//			thisThread.setName( "Thread-Parse_ScanFile_PassTo_Processing_Thread" );
//			thisThread.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

			ValidateInputScanFile validateInputScanFile = ValidateInputScanFile.getInstance();

			converter_identifier_for_scan_file = null;

			File scanFile = pgmParams.getInputScanFile();
					
			try {
				{
					Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response initResponse =
							Call_ScanFileParser_HTTP_CommunicationManagement.getSingletonInstance().initialize_ParsingOf_ScanFile(pgmParams);

					converter_identifier_for_scan_file = initResponse.getConverter_identifier_for_scan_file();
				}
				
				{
					//  Add Queue entry for 'Open Data File'
					Parse_ScanFile_ScanBatch_QueueEntry entry = new Parse_ScanFile_ScanBatch_QueueEntry();
					entry.requestType = Parse_ScanFile_ScanBatch_QueueEntry_RequestType.OPEN_DATA_FILE;
					parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
				}
				
				//    Process all Scans in Scan File
				
				processAllScans( converter_identifier_for_scan_file, pgmParams, validateInputScanFile );

				{
					//  Add Queue entry for 'END_OF_SCANS'
					Parse_ScanFile_ScanBatch_QueueEntry entry = new Parse_ScanFile_ScanBatch_QueueEntry();
					entry.requestType = Parse_ScanFile_ScanBatch_QueueEntry_RequestType.END_OF_SCANS;
					parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
				}
				
			} catch ( SpectralStorageDataException e ) {
				
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath()
						+ ",  Throwing Data error since probably error in file format.";
				log.error( msg, e );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				
				throw e;
				
			} catch ( Throwable t) {

				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath()
						+ ",  Throwing Data error since probably error in file format.";
				log.error( msg, t );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msgForException = "Error processing Scan file. " 
						+ ".  Please check the file to ensure it contains the correct contents for "
						+ "a scan file based on the suffix of the file";
				
				throw new SpectralStorageDataException( msgForException );
				
			} finally {
				if ( converter_identifier_for_scan_file != null ) {
					
					//  Close Input Scan File
					
					try {
						close_ScanFile_Parser();
					} catch ( Throwable t ) {
						
						log.warn( "EXCEPTION IGNORED:: Exception closing Scan File Parser. ", t );
						// eat exception
					}
				}
			}

		} catch ( Throwable t) {
			
			throwable_Caught_Main_run_method = t;
			
			ProcessUploadedScanFileRequest.getSingletonInstance().awaken();
		}
	}
	
	/**
	 * @throws Exception
	 */
	public void close_ScanFile_Parser() throws Exception {

		//  Close Input Scan File
		
		if ( close_ScanFile_Parser__Called ) {
			// already called
			return;
		}
		
		close_ScanFile_Parser__Called = true;
		
		if ( converter_identifier_for_scan_file != null ) {
			Call_ScanFileParser_HTTP_CommunicationManagement.getSingletonInstance().close_ParsingOf_ScanFile(pgmParams, converter_identifier_for_scan_file);
		}
	}

	/**
	 * @param converter_identifier_for_scan_file
	 * @param pgmParams
	 * @param validateInputScanFile
	 * @throws Exception
	 */
	private void processAllScans(
			
			String converter_identifier_for_scan_file,
			Scan_File_Processor_MainProgram_Params pgmParams,
			
			ValidateInputScanFile validateInputScanFile
			 ) throws Exception {
		
		
		try {
			while (true) {
			
				Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response get_NextScans_Response =
						Call_ScanFileParser_HTTP_CommunicationManagement.getSingletonInstance().get_NextScans_ParsingOf_ScanFile(pgmParams, converter_identifier_for_scan_file);
				
				List<ScanFileParser_ScanBatch_SingleScan> scanBatchList =  get_NextScans_Response.getScanFileParser_ScanBatch_Root().getScans();
				if ( scanBatchList == null || scanBatchList.isEmpty() ) {
					if ( get_NextScans_Response.getScanFileParser_ScanBatch_Root().isEndOfScans() ) {
						
						break;  // EARLY BREAK LOOP
					}
				}
				
				if ( scanBatchList != null && ( ! scanBatchList.isEmpty() ) ) {

					{
						//  Add Queue entry for 'SCAN_BATCH'
						Parse_ScanFile_ScanBatch_QueueEntry entry = new Parse_ScanFile_ScanBatch_QueueEntry();
						entry.requestType = Parse_ScanFile_ScanBatch_QueueEntry_RequestType.SCAN_BATCH;
						entry.scanBatchList = scanBatchList;
						parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
					}
					
				}
				
				if ( get_NextScans_Response.getScanFileParser_ScanBatch_Root().isEndOfScans() ) {
					
					break;  // EARLY BREAK LOOP
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
					
			String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath()
					+ ",  Throwing Data error since probably error in file format.";
			log.error( msg, e );
			String msgForException = "Error processing Scan file: " + scanFile.getAbsolutePath()
					+ ".  Please check the file to ensure it contains the correct contents for "
					+ "a scan file based on the suffix of the file ('mzML' or 'mzXML')";
			throw new Exception( msgForException );
		}
	}
}
