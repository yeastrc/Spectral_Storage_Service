package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__ProcessQueue__V_005.SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__ProcessQueue__V_005.SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005;

/**
 * Package Private class - Thread
 * 
 * V 005
 * 
 * Writer Sub Part:  Actual Write To Files - Queue Processor
 * 
 * Uses GZIPOutputStream for compression
 *
 */
class SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 extends Thread {

	private static final Logger log = LoggerFactory.getLogger( SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005.class );
	
	/**
	 * Package Private
	 * 
	 * @param processQueue
	 * @param spectralFile_Writer_SubPart__ActualWriteToFiles
	 * @param spectralFile_Writer_GZIP_V_005 - Pass any exceptions to this object
	 * @return
	 */
	static SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 getNewInstance( 
			
			SpectralFile_Writer_SubPart__ProcessQueue__V_005 processQueue,
			SpectralFile_Writer_SubPart__ActualWriteToFiles_GZIP_V_005 spectralFile_Writer_SubPart__ActualWriteToFiles,
			
			SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005 ) {
		
		SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 instance = 
				new SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005(
						processQueue, spectralFile_Writer_SubPart__ActualWriteToFiles, spectralFile_Writer_GZIP_V_005 );
		
		return instance;
	}
	
	/**
	 * Private Constructor
	 * @param processQueue
	 */
	private SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005( 
			
			SpectralFile_Writer_SubPart__ProcessQueue__V_005 processQueue,
			SpectralFile_Writer_SubPart__ActualWriteToFiles_GZIP_V_005 spectralFile_Writer_SubPart__ActualWriteToFiles,
			
			SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005 ) {

		this.setName( "Thread-QueueProcessor_FinalWriteToFiles_Thread" );
		this.setDaemon(true);  // Make daemon thread so program can exit without this thread exit

		this.processQueue = processQueue;
		this.spectralFile_Writer_SubPart__ActualWriteToFiles = spectralFile_Writer_SubPart__ActualWriteToFiles;
		this.spectralFile_Writer_GZIP_V_005 = spectralFile_Writer_GZIP_V_005;
	}
	
	private volatile SpectralFile_Writer_SubPart__ActualWriteToFiles_GZIP_V_005 spectralFile_Writer_SubPart__ActualWriteToFiles;
	private volatile SpectralFile_Writer_SubPart__ProcessQueue__V_005 processQueue;
	
	/**
	 * Pass any exceptions to this object
	 */
	private volatile SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005;

	/**
	 * 
	 */
	void awaken() {
		
		synchronized (this) {
			notify();
		}
	}

	/* 
	 * Thread 'run()'
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		
		try {
			while (true) {

				SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 entry = processQueue.getNextEntryFromQueue_Blocking();
				
				if ( entry.requestType == SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.OPEN_DATA_FILE ) {
					
					spectralFile_Writer_SubPart__ActualWriteToFiles.open();
					
				} else if ( entry.requestType == SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.WRITE_SCAN ) {
					
					while ( true ) {
						
						//  Loop until Scan Peaks encoded and Totals Set.  'awaken() [ notify() ] will be called each time scan peaks are encoded.

						synchronized (this) {
							
							if ( ( ! entry.isScanPeaksEncoded_And_Totals_Set() )
									&& entry.getAssigned__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005().getQueueEntry() != entry ) {
								
								String msg = "( ! entry.isScanPeaksEncoded_And_Totals_Set() ) AND Assigned EncodeScanPeaksGZIP_Compute_Totals__Thread NO LONGER Holds Reference to Entry";
								log.error(msg);
								throw new SpectralStorageProcessingException(msg);
							}
								
							if ( entry.isScanPeaksEncoded_And_Totals_Set() ) {
								//  Data is Set so exit loop
								break;  // EARLY BREAK LOOP
							}
							
							try {
								wait( 1000 ); // wait for scan peaks are encoded.  Wait max 1 second to recheck flag in case miss 'notify()'.
							} catch (InterruptedException e) {
								
								throw e;
							}

							if ( entry.isScanPeaksEncoded_And_Totals_Set() ) {
								//  Data is Set so exit loop
								break;  // EARLY BREAK LOOP
							}
						}
						
					}
					
					spectralFile_Writer_SubPart__ActualWriteToFiles.writeScan( entry.getSpectralFile_SingleScan() );
					
				} else if ( entry.requestType == SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.CLOSE_DATA_FILE_WRITE_OTHER_FILES ) {
					
					spectralFile_Writer_SubPart__ActualWriteToFiles.close( entry.getSpectralFile_CloseWriter_Data_Common() );
					
					break;
					
				} else {
					String msg = "Unknown value in SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 entry.requestType: " + entry.requestType;
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}
			}

		} catch (Throwable t) {
			
			spectralFile_Writer_GZIP_V_005.setThrowable_Caught_InProcessing__call_notifyOnProcessingCompleteOrException(t);
		}
		
		spectralFile_Writer_GZIP_V_005.spectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005__ProcessingComplete();
	}

}
