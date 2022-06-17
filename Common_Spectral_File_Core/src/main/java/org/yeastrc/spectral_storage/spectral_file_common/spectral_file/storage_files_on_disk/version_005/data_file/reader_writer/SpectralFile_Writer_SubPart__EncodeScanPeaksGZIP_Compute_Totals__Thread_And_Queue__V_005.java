package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__EncodeScanPeaksToByteArray_GZIP_V_005.SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__ProcessQueue__V_005.SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005;



/**
 * Package Private
 * 
 * Thread and Thread Queue 
 * 
 * Process SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 object to Encode Scan Peaks into GZIP byte[] and compute totals
 *
 */
class SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005 {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005.class);

	/**
	 * @param threadCountGzipScanPeaks - Used for Queue Size Computation
	 * @return
	 */
	static SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005 getNewInstance(
			
			int threadCountGzipScanPeaks,
			SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 queueProcessor_FinalWriteToFiles_GZIP__Thread,
			SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005 //  Pass any exceptions to this object
			) {
		
		if ( queueProcessor_FinalWriteToFiles_GZIP__Thread == null ) {
			throw new IllegalArgumentException( "param 'queueProcessor_FinalWriteToFiles_GZIP__Thread' is null" );
		}
		
		if ( spectralFile_Writer_GZIP_V_005 == null ) {
			throw new IllegalArgumentException( "param 'spectralFile_Writer_GZIP_V_005' is null" );
		}
		
		SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005 instance = 
				new SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005(threadCountGzipScanPeaks);
		
		instance.initialize( queueProcessor_FinalWriteToFiles_GZIP__Thread, spectralFile_Writer_GZIP_V_005 );
		
		return instance;
	}
	
	//  private constructor
	private SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread_And_Queue__V_005( 
			
			int threadCountGzipScanPeaks
			) {
		
		this.threadCountGzipScanPeaks = threadCountGzipScanPeaks;  // Set last from constructor params since 'volatile'
		
		this.threadQueue = new ArrayBlockingQueue<>(threadCountGzipScanPeaks + 5);  // Add 5 so ensure Add to queue never blocks
	}
	
	//  Instance properties from constructor
	
	private volatile int threadCountGzipScanPeaks;  //  Set last since Volatile

	//  Instance properties
	
	private ArrayBlockingQueue<SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005> threadQueue;
	
	/**
	 * 
	 */
	private void initialize(
			
			SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 queueProcessor_FinalWriteToFiles_GZIP__Thread,
			SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005 //  Pass any exceptions to this object
			) {
		
		//  Create the Processing threads and add to queue and start
		
		for ( int counter = 1; counter <= threadCountGzipScanPeaks; counter++ ) {
			
			SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005 thread = new SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005();
			
			thread.queueProcessor_FinalWriteToFiles_GZIP__Thread = queueProcessor_FinalWriteToFiles_GZIP__Thread;
			thread.spectralFile_Writer_GZIP_V_005 = spectralFile_Writer_GZIP_V_005;
			
			thread.threadQueue = threadQueue;
			
			thread.setName( "SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals-" + counter );
			thread.setDaemon(true); // Set Daemon so do NOT keep JVM from exit
			
			thread.start();
		}
		
	}
	
	/**
	 * Blocks for available Thread to do processing
	 * 
	 * Updates the object passed in
	 * 
	 * @throws Exception
	 */
	void process_EncodeScanPeaksGZIP_Compute_Totals__UpdateInputObject__Blocking(SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 queueEntry ) throws Exception {
		
		SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005 thread = threadQueue.take(); // Waits if necessary until an element becomes available
		
		queueEntry.setAssigned__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005(thread);
		
		thread.set_Entry_And__Awaken( queueEntry );
	}
	
	/**
	 * !!  Thread class !!
	 *
	 */
	static class SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005 extends Thread {

		private static final Logger log = LoggerFactory.getLogger(SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005.class);
		
		// Set when construct

		private volatile SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 queueProcessor_FinalWriteToFiles_GZIP__Thread; 

		/**
		 * Pass any exceptions to this object
		 */
		private volatile SpectralFile_Writer_GZIP_V_005 spectralFile_Writer_GZIP_V_005;

		private volatile ArrayBlockingQueue<SpectralFile_Writer_SubPart__EncodeScanPeaksGZIP_Compute_Totals__Thread__V_005> threadQueue; // From parent class object
		
		private volatile SpectralFile_Writer_SubPart__EncodeScanPeaksToByteArray_GZIP_V_005 spectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005 = new SpectralFile_Writer_SubPart__EncodeScanPeaksToByteArray_GZIP_V_005();

		// for each 'process' through the loop
		private volatile SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 queueEntry; //  Set before thread awaken to be processed
		
		/**
		 * 
		 */
		private void set_Entry_And__Awaken(SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 queueEntry) {
			
			synchronized (this) {

				this.queueEntry = queueEntry;
				
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
				while(true) {

					this.queueEntry = null;

					while ( this.queueEntry == null ) {

						synchronized (this) {

							threadQueue.add(this); // Add to threadQueue in same 'synchronized' to ensure get the notify
							
							try {
								wait( 1000 ); // wait for next request.  Wait max 1 second to recheck queueEntry in case miss 'notify()'.
							} catch (InterruptedException e) {

							}
						}
					}
					
					SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult encodePeaksAsCompressedBytes_Result =
							spectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005.encodePeaksAsCompressedBytes( this.queueEntry.getSpectralFile_SingleScan().getScanPeaksAsObjectArray() );

					byte[] encodedScanPeaks_ByteArray = encodePeaksAsCompressedBytes_Result.getEncodedScanPeaks_ByteArray();

					this.queueEntry.getSpectralFile_SingleScan().setScanPeaksAsByteArray(encodedScanPeaks_ByteArray);
					this.queueEntry.getSpectralFile_SingleScan().setNumberScanPeaks( encodePeaksAsCompressedBytes_Result.getScanPeaksTotalCount() );
					
					this.queueEntry.setScanPeaksEncoded_And_Totals_Set(true); //  Set flag that will be checked to determine if this processing is complete for this scan

					//  Done processing
					
					queueProcessor_FinalWriteToFiles_GZIP__Thread.awaken();
				}

			} catch (Throwable t) {
				
				log.error( "Exception Encountered encoding scan peaks to byte array GZIP", t );
				
				spectralFile_Writer_GZIP_V_005.setThrowable_Caught_InProcessing__call_notifyOnProcessingCompleteOrException(t);
			}
		}

		public SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 getQueueEntry() {
			return queueEntry;
		}
	}
	
}
