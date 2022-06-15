package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

import java.io.File;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.accum_scan_summary_data.AccumulateSummaryDataPerScanLevel;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.Accumulate_RT_MZ_Binned_ScanLevel_1;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_CloseWriter_Data_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__ProcessQueue__V_005.SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer.SpectralFile_Writer_SubPart__ProcessQueue__V_005.SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005;

/**
 * V 005
 * 
 * Uses matching SpectralFile_Index_File_Writer_V_005
 * 
 * Uses GZIPOutputStream for compression
 *
 */
public class SpectralFile_Writer_GZIP_V_005 implements SpectralFile_Writer__IF  {

	private static final Logger log = LoggerFactory.getLogger(SpectralFile_Writer_GZIP_V_005.class);
	
	/**
	 * Set to True on successful close and spectralFile_CloseWriter_Data_Common.isExceptionEncounteredProcessingScanFile() returns false
	 */
	private boolean processingIs_Successfull_And_Complete; 
	
	@Override
	public boolean isProcessingIs_Successfull_And_Complete() {
		return processingIs_Successfull_And_Complete;
	}

	private volatile Throwable throwable_Caught_InProcessing;
	
	
	/**
	 * set this.throwable_Caught_InProcessing
	 * 
	 * call notifyOnProcessingCompleteOrException.notifyOnProcessingCompleteOrException();
	 * 
	 * Package Private Setter
	 * @param throwable_Caught_InProcessing
	 */
	void setThrowable_Caught_InProcessing__call_notifyOnProcessingCompleteOrException(Throwable throwable_Caught_InProcessing) {
		
		this.throwable_Caught_InProcessing = throwable_Caught_InProcessing;
		
		notifyOnProcessingCompleteOrException.notifyOnProcessingCompleteOrException();
	}

	/* 
	 * Public Getter
	 */
	@Override
	public Throwable getException() {
		return throwable_Caught_InProcessing;
	}
	
	private SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF notifyOnProcessingCompleteOrException;
	
	/**
	 * private constructor
	 */
	private SpectralFile_Writer_GZIP_V_005(){}
	public static SpectralFile_Writer__IF getInstance( ) throws Exception {
		SpectralFile_Writer__IF instance = new SpectralFile_Writer_GZIP_V_005();
		return instance;
	}
	
	private boolean initializeCalled;
	private boolean closeCalled;
	private boolean openCalled;
	private boolean writeHeaderCalled;
	private boolean writeScanCalled;
	
	private SpectralFile_Writer_SubPart__ProcessQueue__V_005 processQueue;
	
	/**
	 * Accumulate statistics
	 */
	AccumulateSummaryDataPerScanLevel accumulateSummaryDataPerScanLevel;
	
	/**
	 * Accumulate scan level 1 data binned for RT and MZ
	 */
	Accumulate_RT_MZ_Binned_ScanLevel_1 accumulate_RT_MZ_Binned_ScanLevel_1;
	

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF#getVersion()
	 */
	@Override
	public int getVersion() {

		return StorageFile_Version_005_Constants.FILE_VERSION;
	}

	
	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF#initialize(java.lang.String, java.io.File, org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_Header_Common, org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF)
	 */
	@Override
	public void initialize( 
			String hash_String, 
			File subDirForStorageFiles, 
			SpectralFile_Header_Common spectralFile_Header_Common,
			SpectralFile_Writer__NotifyOnProcessingCompleteOrException__IF notifyOnProcessingCompleteOrException ) throws Exception {
		
		if ( initializeCalled ) {
			String msg = "In Writer, initialize(...) cannot be called more than once";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}

		initializeCalled = true;

		if ( spectralFile_Header_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks() != null ) {
			String msg = "In Writer, cannot be not null: spectralFile_Header_Common.getTotalIonCurrent_ForEachScan_ComputedFromScanPeaks()";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		if ( spectralFile_Header_Common.getIonInjectionTime_NotPopulated() != null ) {
			String msg = "In Writer, cannot be not null: spectralFile_Header_Common.getIonInjectionTime_NotPopulated()";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}

		this.notifyOnProcessingCompleteOrException = notifyOnProcessingCompleteOrException;
		
		int processingThreadsCount = 2;
		
		SpectralFile_Writer_SubPart__ActualWriteToFiles_GZIP_V_005 spectralFile_Writer_SubPart__ActualWriteToFiles = 
				SpectralFile_Writer_SubPart__ActualWriteToFiles_GZIP_V_005.getNewInstance();
		
		spectralFile_Writer_SubPart__ActualWriteToFiles.initialize(hash_String, subDirForStorageFiles, spectralFile_Header_Common, notifyOnProcessingCompleteOrException);
				
		processQueue = SpectralFile_Writer_SubPart__ProcessQueue__V_005.getNewInstance(processingThreadsCount);
		
		SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 queueProcessor_FinalWriteToFiles = 
				SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005.getNewInstance(processQueue, spectralFile_Writer_SubPart__ActualWriteToFiles, this);
		
		queueProcessor_FinalWriteToFiles.start();
	}
	
	/**
	 * Package Private
	 * 
	 * Called by SpectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005 object when the "CLOSE..." is received and processed successfully
	 */
	void spectralFile_Writer_SubPart__QueueProcessor_FinalWriteToFiles_GZIP__Thread__V_005__ProcessingComplete() {
		
		notifyOnProcessingCompleteOrException.notifyOnProcessingCompleteOrException();
	}
	
	
	/* 
	 * Close Main Data File
	 * 
	 * Write Index file and other files
	 * 
	 * 
	 * (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#close()
	 */
	@Override
	public void close(SpectralFile_CloseWriter_Data_Common spectralFile_CloseWriter_Data_Common) throws Exception {

		if ( closeCalled ) {
			String msg = "In Writer, close(...) cannot be called more than once";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		closeCalled = true;
		
		if ( ! initializeCalled ) {
			String msg = "In Writer, close(...) cannot be called before initialize() is called";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
//		if ( ! openCalled ) {
//			String msg = "In Writer, close(...) cannot be called before open() is called";
//			log.error(msg);
//			throw new SpectralStorageProcessingException(msg);
//		}
		
		SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 processQueue_Entry = new SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005();
		
		processQueue_Entry.setRequestType(SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.CLOSE_DATA_FILE_WRITE_OTHER_FILES);
		processQueue_Entry.setSpectralFile_CloseWriter_Data_Common(spectralFile_CloseWriter_Data_Common);
		
		processQueue.addToQueue_Blocking( processQueue_Entry );
		
	}
	
	/* 
	 * Open 
	 * 
	 * (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.reader_writer_if_factories.SpectralFile_Writer__IF#open()
	 */
	@Override
	public void open() throws Exception {

		if ( openCalled ) {
			String msg = "In Writer, open(...) cannot be called more than once";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}

		openCalled = true;

		if ( ! initializeCalled ) {
			String msg = "In Writer, open(...) cannot be called before initialize() is called";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 processQueue_Entry = new SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005();
		
		processQueue_Entry.setRequestType(SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.OPEN_DATA_FILE);
		
		processQueue.addToQueue_Blocking( processQueue_Entry );
	}
	

	/* (non-Javadoc)
	 * @see org.yeastrc.spectral_storage.spectral_file_common.spectral_file.writer.SpectralFile_Writer__IF#writeScan(org.yeastrc.spectral_storage.spectral_file_common.spectral_file.dto.SpectralFile_SingleScan)
	 */
	@Override
	public void writeScan( SpectralFile_SingleScan_Common spectralFile_SingleScan ) throws Exception {

		if ( ! openCalled ) {
			String msg = "In Writer, writeScan(...) cannot be called before open() is called";
			log.error(msg);
			throw new SpectralStorageProcessingException(msg);
		}
		
		writeScanCalled = true;

		SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005 processQueue_Entry = new SpectralFile_Writer_SubPart__ProcessQueueEntry__V_005();
		
		processQueue_Entry.setRequestType(SpectralFile_Writer_SubPart__ProcessQueueEntry_RequestType__V_005.WRITE_SCAN );
		processQueue_Entry.setSpectralFile_SingleScan(spectralFile_SingleScan);
		
		processQueue.addToQueue_Blocking( processQueue_Entry );
	}

	
}
