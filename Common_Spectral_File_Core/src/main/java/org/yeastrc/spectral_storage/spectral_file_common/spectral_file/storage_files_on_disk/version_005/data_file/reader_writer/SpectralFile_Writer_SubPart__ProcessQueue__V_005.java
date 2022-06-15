package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

import java.util.concurrent.ArrayBlockingQueue;

import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Queue of items to process.
 * 
 * items:  
 *   Open Data File
 *   Write Scan
 *   Close Data File and Write other files
 *
 */
public class SpectralFile_Writer_SubPart__ProcessQueue__V_005 {
	
	/**
	 * @param processingThreadsCount
	 * @return
	 */
	public static SpectralFile_Writer_SubPart__ProcessQueue__V_005 getNewInstance(int processingThreadsCount) {
		
		return new SpectralFile_Writer_SubPart__ProcessQueue__V_005(processingThreadsCount);
	}
	
	private SpectralFile_Writer_SubPart__ProcessQueue__V_005( int processingThreadsCount ) {
		
		processQueue = new ArrayBlockingQueue<>(processingThreadsCount + 2);
	}
	
	private ArrayBlockingQueue<SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005> processQueue;
	
	/**
	 * Waits for space to become available if the queue is full
	 * 
	 * @param entry
	 * @throws InterruptedException 
	 */
	void addToQueue_Blocking( SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 entry ) throws InterruptedException {
		
		processQueue.put(entry); //  Waits for space to become available if the queue is full
	}
	
	/**
	 * Waits if necessary until an element becomes available
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 getNextEntryFromQueue_Blocking() throws InterruptedException {
		return processQueue.take(); // Waits if necessary until an element becomes available
	}

	enum SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005 {
		
		OPEN_DATA_FILE,
		WRITE_SCAN,
		CLOSE_DATA_FILE_WRITE_OTHER_FILES
	}
	
	/**
	 * Package Private
	 * 
	 * One entry in Queue
	 *
	 */
	static class SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 {
		
		SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005 requestType;
		
		/**
		 * Root Writer which is interface to common code
		 */
		private volatile SpectralFile_Writer_GZIP_V_005 spectralFile_Writer;
		
		private volatile SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common;
		
		private volatile SpectralFile_SingleScan_Common spectralFile_SingleScan;
		
		//  Computed by Encode Scan Peaks code
		
		private volatile byte[] encodedScanPeaks_ByteArray;
		
		private volatile long scanPeaksTotalBytes;
		private volatile long scanPeaksTotalCount;
		
		
		
		public long getScanPeaksTotalBytes() {
			return scanPeaksTotalBytes;
		}
		public long getScanPeaksTotalCount() {
			return scanPeaksTotalCount;
		}
		public byte[] getEncodedScanPeaks_ByteArray() {
			return encodedScanPeaks_ByteArray;
		}
		public SpectralFile_Writer_GZIP_V_005 getSpectralFile_Writer() {
			return spectralFile_Writer;
		}
		public void setSpectralFile_Writer(SpectralFile_Writer_GZIP_V_005 spectralFile_Writer) {
			this.spectralFile_Writer = spectralFile_Writer;
		}
		public SpectralFile_CloseWriter_Data_Common getSpectralFile_CloseWriter_Data_Common() {
			return spectralFile_CloseWriter_Data_Common;
		}
		public void setSpectralFile_CloseWriter_Data_Common(
				SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common) {
			this.spectralFile_CloseWriter_Data_Common = spectralFile_CloseWriter_Data_Common;
		}
		public SpectralFile_SingleScan_Common getSpectralFile_SingleScan() {
			return spectralFile_SingleScan;
		}
		public void setSpectralFile_SingleScan(SpectralFile_SingleScan_Common spectralFile_SingleScan) {
			this.spectralFile_SingleScan = spectralFile_SingleScan;
		}
		public void setEncodedScanPeaks_ByteArray(byte[] encodedScanPeaks_ByteArray) {
			this.encodedScanPeaks_ByteArray = encodedScanPeaks_ByteArray;
		}
		public void setScanPeaksTotalBytes(long scanPeaksTotalBytes) {
			this.scanPeaksTotalBytes = scanPeaksTotalBytes;
		}
		public void setScanPeaksTotalCount(long scanPeaksTotalCount) {
			this.scanPeaksTotalCount = scanPeaksTotalCount;
		}
		public SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005 getRequestType() {
			return requestType;
		}
		public void setRequestType(SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005 requestType) {
			this.requestType = requestType;
		}
	}
	
}
