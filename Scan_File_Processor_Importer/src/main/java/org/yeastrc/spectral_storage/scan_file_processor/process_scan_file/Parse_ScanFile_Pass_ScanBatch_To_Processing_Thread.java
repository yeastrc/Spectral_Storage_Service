package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileRequest;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_Root;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue.GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;

/**
 * This Thread calls Scan Parser Webservices to initiate parsing of scan file and getting batches of scans.
 * 
 * This thread repeatedly calls Scan Parser Webservice to get next batch of scans and then puts the scan batch response bytes into a queue to be processed by another thread.
 * 
 * Calling the Scan Parser Webservice to close parsing of the scan file is done elsewhere.
 *
 */
public class Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread extends Thread {

	private static final Logger log = LoggerFactory.getLogger( Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread.class );
	
	/**
	 * @param pgmParams
	 * @return
	 */
	public static Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread getNewInstance( Scan_File_Processor_MainProgram_Params pgmParams, GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue ) {
		
		Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread instance = new Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread(pgmParams, parse_ScanFile_ScanBatch_Queue);
		return instance;
	}

	// Private Constructor
	private Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread(Scan_File_Processor_MainProgram_Params pgmParams, GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue ) {

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
	
	private GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue parse_ScanFile_ScanBatch_Queue;
	
	private volatile String converter_identifier_for_scan_file; // Can get call on alt thread using this
	
	private volatile boolean close_ScanFile_Parser__Called;
	
	@Override
	public void run() {
		
		try {
			ValidateInputScanFile validateInputScanFile = ValidateInputScanFile.getInstance();

			converter_identifier_for_scan_file = null;

			File scanFile = pgmParams.getInputScanFile();
					
			try {
				//  Call Scan File Parser Webservice to Initiate Parsing of scan file 
				{
					Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response initResponse =
							Call_ScanFileParser_HTTP_CommunicationManagement.getSingletonInstance().initialize_ParsingOf_ScanFile(pgmParams);

					converter_identifier_for_scan_file = initResponse.getConverter_identifier_for_scan_file();
				}
				
				{
					//  Add Queue entry for 'Open Data File'
					GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry entry = new GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry();
					entry.requestType = GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.OPEN_DATA_FILE;
					parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
				}
				
				//    Process all Scans in Scan File
				
				processAllScans( converter_identifier_for_scan_file, pgmParams, validateInputScanFile );
				
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
				String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath();
				log.error( msg, t );
				log.error( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
				
				throw t;
			}
			
		} catch ( SpectralStorage__Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread__EndThread_Exception t ) {
			
			//  This Exception is only in this Java file and only thrown from inside this Java file

			//  Eat Exception since have already received the main error message from Scan File Parser that will be processed elsewhere
			
		} catch ( Throwable t) {
			
			throwable_Caught_Main_run_method = t;
			
			ProcessUploadedScanFileRequest.getSingletonInstance().awaken();
		}
	}

	/**
	 * !!!   Called from External Code/Thread since this object has the 'converter_identifier_for_scan_file' value
	 * 
	 * 
	 * @throws Exception
	 */
	public void close_ScanFile_Parser( Integer last_scan_batch_number_received ) throws Exception {

		//  Close Input Scan File
		
		if ( close_ScanFile_Parser__Called ) {
			// already called
			return;
		}
		
		close_ScanFile_Parser__Called = true;
		
		if ( converter_identifier_for_scan_file != null ) {
			Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response response =
					Call_ScanFileParser_HTTP_CommunicationManagement.getSingletonInstance().close_ParsingOf_ScanFile(pgmParams, converter_identifier_for_scan_file);
			
			if ( last_scan_batch_number_received != null && response.last_scan_batch_number_sent != null ) {
				if ( last_scan_batch_number_received.intValue() != response.last_scan_batch_number_sent.intValue() ) {
					
					String msg = "last_scan_batch_number_received != Scan File Parser response.last_scan_batch_number_sent. last_scan_batch_number_received: " 
							+ last_scan_batch_number_received 
							+ ", Close Webservice response.last_scan_batch_number_sent: " + response.last_scan_batch_number_sent;
					log.error(msg);
					throw new SpectralStorageProcessingException(msg);
				}
			}
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
				
				ScanFileParser_ScanBatch_Root scanFileParser_ScanBatch_Root = get_NextScans_Response.getScanFileParser_ScanBatch_Root();
				
				if ( scanFileParser_ScanBatch_Root != null ) {
					
					//  Webservice Response has been parsed
					

					if ( scanFileParser_ScanBatch_Root.getIsError() != null && scanFileParser_ScanBatch_Root.getIsError() ) {
						
						if ( get_NextScans_Response.isErrorMessageCode_Is_ErrorMsgAlreadySent() ) {
							
							log.error( "Error Message Code of Error Message Already Sent has been received so exit processing this thread");
							
							throw new SpectralStorage__Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread__EndThread_Exception();
						}
							

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
					
					/////

					if ( get_NextScans_Response.getScanFileParser_ScanBatch_Root().isEndOfScans() ) {

						if ( scanFileParser_ScanBatch_Root.getScans() != null ) {
							
							String msg = "Contents returned from Scan File Parser cannot have both populated, scans and endOfScans == true";
							log.error(msg);
							throw new SpectralStorageProcessingException(msg);
						}
						
						//  Add Queue entry for 'END_OF_SCANS'
						GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry entry = new GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry();
						entry.requestType = GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.END_OF_SCANS;
						entry.scan_batch_number = scanFileParser_ScanBatch_Root.getScan_batch_number();
						parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
						
						break;  // EARLY BREAK LOOP
					} 
					
					//  Add Queue entry for 'SCAN_BATCH'
					GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry entry = new GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry();
					entry.requestType = GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.SCAN_BATCH;
					entry.scanBatchList = scanFileParser_ScanBatch_Root.getScans();
					entry.scan_batch_number = scanFileParser_ScanBatch_Root.getScan_batch_number();
					parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
					
				} else {
					//  Webservice Response has NOT been parsed

					{
						//  Add Queue entry for 'SCAN_BATCH'
						GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry entry = new GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry();
						entry.requestType = GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType.SCAN_BATCH;
						entry.webservice_ResponseBytes = get_NextScans_Response.getWebservice_ResponseBytes();
						parse_ScanFile_ScanBatch_Queue.addToQueue_Blocking(entry);
					}
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
		} catch ( Throwable e ) {

			File scanFile = pgmParams.getInputScanFile();
					
			String msg = "Error Exception processing Scan file: " + scanFile.getAbsolutePath();
			log.error( msg, e );
			throw e;
		}
	}
	
	/**
	 * 
	 *
	 */
	private static class SpectralStorage__Parse_ScanFile_Pass_ScanBatch_To_Processing_Thread__EndThread_Exception extends RuntimeException {
		
	}
}
