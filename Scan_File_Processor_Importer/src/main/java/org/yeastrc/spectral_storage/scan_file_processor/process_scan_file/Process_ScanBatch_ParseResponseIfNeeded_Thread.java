package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileRequest;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_Root;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Processes Batches of Scans from Scan Parser Webservice. If the response is not parsed, it is parsed and stored in the same object.
 * 
 * The result is placed in the queue for thread Process_Scans_In_ScanBatch_From_ScanFileParser_Thread
 *
 */
public class Process_ScanBatch_ParseResponseIfNeeded_Thread extends Thread {

	private static final Logger log = LoggerFactory.getLogger( Process_ScanBatch_ParseResponseIfNeeded_Thread.class );
	
	/**
	 * @param pgmParams
	 * @return
	 */
	public static Process_ScanBatch_ParseResponseIfNeeded_Thread getNewInstance( 
			
			Scan_File_Processor_MainProgram_Params pgmParams, 
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_INPUT,
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_OUTPUT
			
			) {
		
		Process_ScanBatch_ParseResponseIfNeeded_Thread instance = new Process_ScanBatch_ParseResponseIfNeeded_Thread(pgmParams, parse_ScanFile_ScanBatch_Queue_INPUT, parse_ScanFile_ScanBatch_Queue_OUTPUT);
		return instance;
	}

	// Private Constructor
	private Process_ScanBatch_ParseResponseIfNeeded_Thread(
			
			Scan_File_Processor_MainProgram_Params pgmParams, 
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_INPUT,
			GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_OUTPUT
			) {

		this.setName( "Thread-Process_Scans_In_ScanBatch_From_ScanFileParser_Thread" );
		this.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

		this.parse_ScanFile_ScanBatch_Queue_INPUT = parse_ScanFile_ScanBatch_Queue_INPUT;
		this.parse_ScanFile_ScanBatch_Queue_OUTPUT = parse_ScanFile_ScanBatch_Queue_OUTPUT;
		this.pgmParams = pgmParams;  // Always set last since 'volatile'
	}
	
	private Throwable throwable_Caught_Main_run_method;

	public Throwable getThrowable_Caught_Main_run_method() {
		return throwable_Caught_Main_run_method;
	}
	
	/**
	 * Main Program Params from command line
	 */
	private volatile Scan_File_Processor_MainProgram_Params pgmParams;
	
	private GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_INPUT;
	private GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue_OUTPUT;
	
	
	@Override
	public void run() {
		
		try {

			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			
			//  Wait for "Open" first entry in Queue
			{
				GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry parse_ScanFile_ScanBatch_QueueEntry = parse_ScanFile_ScanBatch_Queue_INPUT.getNextEntryFromQueue_Blocking();

				if ( parse_ScanFile_ScanBatch_QueueEntry.requestType != GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.OPEN_DATA_FILE ) {

					String msg = "First Entry from Queue is NOT requestType OPEN_DATA_FILE";
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}
				
				parse_ScanFile_ScanBatch_Queue_OUTPUT.addToQueue_Blocking(parse_ScanFile_ScanBatch_QueueEntry);
			}

			//  Pass along OPEN_DATA_FILE

			//    Process all Scans in Scan File


			try {
				while (true) {  //  Exit using 'break' when get 'END_OF_SCANS'

					GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry parse_ScanFile_ScanBatch_QueueEntry = parse_ScanFile_ScanBatch_Queue_INPUT.getNextEntryFromQueue_Blocking();

					if ( parse_ScanFile_ScanBatch_QueueEntry.requestType == GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.END_OF_SCANS ) {

						//  End of Scans

						parse_ScanFile_ScanBatch_Queue_OUTPUT.addToQueue_Blocking(parse_ScanFile_ScanBatch_QueueEntry);
						
						break;  //  EARLY BREAK from Loop

					}

					List<ScanFileParser_ScanBatch_SingleScan> scanBatchList = parse_ScanFile_ScanBatch_QueueEntry.scanBatchList;

					if ( scanBatchList == null ) {

						//  scanBatchList not populated in Entry so parse from Webservice Response Bytes

						if ( parse_ScanFile_ScanBatch_QueueEntry.webservice_ResponseBytes == null ) {
							String msg = "parse_ScanFile_ScanBatch_QueueEntry.scanBatchList AND parse_ScanFile_ScanBatch_QueueEntry.webservice_ResponseBytes are BOTH NULL";
							log.error( msg );
							throw new SpectralStorageProcessingException(msg);
						}

						ScanFileParser_ScanBatch_Root scanFileParser_ScanBatch_Root = null;
						try {
							scanFileParser_ScanBatch_Root = jacksonJSON_Mapper.readValue( parse_ScanFile_ScanBatch_QueueEntry.webservice_ResponseBytes, ScanFileParser_ScanBatch_Root.class );
						} catch ( Exception e ) {
							log.error( "Failed to parse Get Scan Batch webservice response. ", e );
							throw e;
						}

						if ( scanFileParser_ScanBatch_Root.getIsError() != null && scanFileParser_ScanBatch_Root.getIsError() ) {

							if ( StringUtils.isNotEmpty( scanFileParser_ScanBatch_Root.getErrorMessage_ScanFileContentsError_ForEndUser() ) ) {
							

								String msg = "get_NextScans_ParsingOf_ScanFile: webserviceResponse: isError is true. errorMessageToLog: " + scanFileParser_ScanBatch_Root.getErrorMessageToLog()
										+ "\n scanFileParser_ScanBatch_Root.errorMessage_ScanFileContentsError_ForEndUser: " + scanFileParser_ScanBatch_Root.getErrorMessage_ScanFileContentsError_ForEndUser();
								log.error( msg );
								
								throw new SpectralStorageDataException( scanFileParser_ScanBatch_Root.getErrorMessage_ScanFileContentsError_ForEndUser() );
							}
							
							String msg = "get_NextScans_ParsingOf_ScanFile: webserviceResponse: isError is true. errorMessageToLog: " + scanFileParser_ScanBatch_Root.getErrorMessageToLog();
							log.error( msg );
							throw new SpectralStorageProcessingException(msg);
						}
						
						
						parse_ScanFile_ScanBatch_QueueEntry.scanBatchList = scanFileParser_ScanBatch_Root.getScans();
						
						parse_ScanFile_ScanBatch_QueueEntry.scan_batch_number = scanFileParser_ScanBatch_Root.getScan_batch_number();
					}
					
					parse_ScanFile_ScanBatch_Queue_OUTPUT.addToQueue_Blocking(parse_ScanFile_ScanBatch_QueueEntry);
				}

			} catch ( Throwable t) {

				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				String msg = "Error Exception Parsing Scan Batch: ";
				log.error( msg, t );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				throw t;
				
			} finally {

			}

		} catch ( Throwable t) {
			
			throwable_Caught_Main_run_method = t;
			
			ProcessUploadedScanFileRequest.getSingletonInstance().awaken();
		}
	}
}
