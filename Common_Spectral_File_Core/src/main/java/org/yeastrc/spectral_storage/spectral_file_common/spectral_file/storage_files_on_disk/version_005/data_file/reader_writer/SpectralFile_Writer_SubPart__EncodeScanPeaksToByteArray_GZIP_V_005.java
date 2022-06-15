package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.data_file.reader_writer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;

/**
 * Package Private class
 * 
 * V 005
 * 
 * Writer Sub Part:  Encode Scan Peaks To Byte Array
 * 
 * Uses matching SpectralFile_Index_File_Writer_V_005
 * 
 * Uses GZIPOutputStream for compression
 *
 */
class SpectralFile_Writer_SubPart__EncodeScanPeaksToByteArray_GZIP_V_005 {

	private static final Logger log = LoggerFactory.getLogger( SpectralFile_Writer_SubPart__EncodeScanPeaksToByteArray_GZIP_V_005.class );
	
	private static final int TEMP_OUTPUT_STREAM_INITIAL_SIZE = 1024 * 1024;
	
	/**
	 * Package Private
	 *
	 */
	static class SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult {
		
		private byte[] encodedScanPeaks_ByteArray;
		
		private long scanPeaksTotalBytes;
		private long scanPeaksTotalCount;
		
		public long getScanPeaksTotalBytes() {
			return scanPeaksTotalBytes;
		}
		public long getScanPeaksTotalCount() {
			return scanPeaksTotalCount;
		}
		public byte[] getEncodedScanPeaks_ByteArray() {
			return encodedScanPeaks_ByteArray;
		}
	}

	/**
	 * Package Private
	 * 
	 * @param scanPeaksAsObjectArray
	 * @return
	 * @throws Exception
	 */
	SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult encodePeaksAsCompressedBytes( List<SpectralFile_SingleScanPeak_Common> scanPeaksAsObjectArray ) throws Exception {

		long scanPeaksTotalBytes = 0;
		long scanPeaksTotalCount = 0;
		
		ByteArrayOutputStream tempScansCompressedOutputStream = new ByteArrayOutputStream( TEMP_OUTPUT_STREAM_INITIAL_SIZE );
		
		GZIPOutputStream gZIPOutputStream = 
				new GZIPOutputStream( tempScansCompressedOutputStream, true /* syncFlush */ );
		// syncFlush - if true invocation of the inherited flush() method of this instance flushes the compressor with flush mode Deflater.SYNC_FLUSH before flushing the output stream, otherwise only flushes the output strea

		ByteArrayOutputStream singlePeakOutputStream = new ByteArrayOutputStream( 8 ); 

		DataOutputStream dataOutputStream = new DataOutputStream( singlePeakOutputStream );
			
		for ( SpectralFile_SingleScanPeak_Common peak : scanPeaksAsObjectArray ) {
			
			singlePeakOutputStream.reset();
			
			dataOutputStream.writeDouble( peak.getM_over_Z() );
			dataOutputStream.writeFloat( peak.getIntensity() );
			dataOutputStream.flush();
			
			scanPeaksTotalBytes += singlePeakOutputStream.size();
			
			scanPeaksTotalCount++;
			
			singlePeakOutputStream.writeTo( gZIPOutputStream );
		}
		
//		gZIPOutputStream.finish();  //  Forces gZIPOutputStream to compress cached data and flush
//		
		//  This works to forces gZIPOutputStream to compress cached data and flush
		//    ONLY IF  syncFlush on constructor is set to true.
//		gZIPOutputStream.flush(); 
		
		
		//  Needed to force gZIPOutputStream to compress cached data and flush, 
		//    if gZIPOutputStream.finish();  is not called.
		gZIPOutputStream.close();  //  This will have no effect on ByteArrayOutputStream tempScansCompressedOutputStream
		
		tempScansCompressedOutputStream.flush();
		
		SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult result = new SpectralFile_Writer__EncodeScanPeaksToByteArray_GZIP_V_005__MethodResult();
		
		result.encodedScanPeaks_ByteArray = tempScansCompressedOutputStream.toByteArray();
		result.scanPeaksTotalBytes = scanPeaksTotalBytes;
		result.scanPeaksTotalCount = scanPeaksTotalCount;
		
		return result;

	}
	
}
