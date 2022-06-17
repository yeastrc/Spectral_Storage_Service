package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Call_ScanFileParser_HTTP_CommunicationManagement.ScanFileParser_ScanBatch_SingleScan;


/**
 * Queue of items to process.
 * 
 * items:  
 *   Open Data File
 *   Process Scan Batch
 *   End of Scans
 *
 */
public class Parse_ScanFile_ScanBatch_Queue {

	/**
	 * @return
	 */
	public static Parse_ScanFile_ScanBatch_Queue getNewInstance() {
		
		return new Parse_ScanFile_ScanBatch_Queue();
	}
	
	private Parse_ScanFile_ScanBatch_Queue() {
		
		processQueue = new ArrayBlockingQueue<>( 5 );  // Hold up to 5 scan batches
	}
	
	private ArrayBlockingQueue<Parse_ScanFile_ScanBatch_QueueEntry> processQueue;
	
	/**
	 * Waits for space to become available if the queue is full
	 * 
	 * @param entry
	 * @throws InterruptedException 
	 */
	void addToQueue_Blocking( Parse_ScanFile_ScanBatch_QueueEntry entry ) throws InterruptedException {
		
		processQueue.put(entry); //  Waits for space to become available if the queue is full
	}
	
	/**
	 * Waits if necessary until an element becomes available
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	Parse_ScanFile_ScanBatch_QueueEntry getNextEntryFromQueue_Blocking() throws InterruptedException {
		return processQueue.take(); // Waits if necessary until an element becomes available
	}

	/**
	 * Package Private
	 *
	 */
	enum Parse_ScanFile_ScanBatch_QueueEntry_RequestType {
		
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
	static class Parse_ScanFile_ScanBatch_QueueEntry {
		
		Parse_ScanFile_ScanBatch_QueueEntry_RequestType requestType;
		
		List<ScanFileParser_ScanBatch_SingleScan> scanBatchList;
	}
}
