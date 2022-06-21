package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan;


/**
 * !!  More than just Scan Batch, Also Open and Close Scan File  !!
 * 
 * 
 * There are 2 instances of this class
 * 
 *    1)  The immediate response from the Parse Scan File Webservice call
 *    2)  After the response is parsed
 * 
 * 
 * Queue of items to process.
 * 
 * items:  
 *   Open Data File
 *   Process Scan Batch
 *   End of Scans
 *
 */
public class GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue {

	/**
	 * @return
	 */
	public static GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue getNewInstance() {
		
		return new GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue();
	}
	
	private GetScanBatch_ResponseBytes_AndOr_ResponseParsed_Queue() {
		
		processQueue = new ArrayBlockingQueue<>( 5 );  // Hold up to 5 scan batches
	}
	
	private ArrayBlockingQueue<GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry> processQueue;
	
	/**
	 * Waits for space to become available if the queue is full
	 * 
	 * @param entry
	 * @throws InterruptedException 
	 */
	void addToQueue_Blocking( GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry entry ) throws InterruptedException {
		
		processQueue.put(entry); //  Waits for space to become available if the queue is full
	}
	
	/**
	 * Waits if necessary until an element becomes available
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry getNextEntryFromQueue_Blocking() throws InterruptedException {
		return processQueue.take(); // Waits if necessary until an element becomes available
	}

	/**
	 * Package Private
	 *
	 */
	enum GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType {
		
		OPEN_DATA_FILE,
		SCAN_BATCH,
		END_OF_SCANS
	}
	
	/**
	 * Package Private
	 * 
	 * One entry in Queue
	 *
	 */
	static class GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry {
		
		GetScanBatch_ResponseBytes_AndOr_ResponseParsed_QueueEntry_RequestType requestType;
		
		List<ScanFileParser_ScanBatch_SingleScan> scanBatchList;
		
		Integer scan_batch_number;
		
		volatile byte[] webservice_ResponseBytes;  // Raw Bytes from Get Scan Batch Webservice Response when too large
	}
}
