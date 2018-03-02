package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.SpectralStorage_DataFiles_S3_Prefix_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageConfigException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 * Common reader code for retrieving bytes from Files or AWS S3
 * 
 * For reading a whole file, it will return an object that returns an input stream
 * 
 * For reading specific bytes, it will return a byte array.
 *
 */
public class CommonReader_File_And_S3 {

	private static final Logger log = Logger.getLogger( CommonReader_File_And_S3.class );

	private static final String FILE_MODE_READ = "r"; // Used in RandomAccessFile constructor below
	
	// package private constructor
	CommonReader_File_And_S3() {}
	
	//  Storing in local file system
	private File subDirForStorageFiles;
	
	//  Storing in S3
	private String s3_Bucket;

	//  Storing in S3 - Optional Region, otherwise SDK uses environment variables 
	private String s3_Region;
	
	private AmazonS3 s3_Client;

	@Override
	public String toString() {
		return "CommonReader_File_And_S3 [subDirForStorageFiles=" + subDirForStorageFiles + ", s3_Bucket=" + s3_Bucket
				+ ", s3_Region=" + s3_Region + "]";
	}
	
	/**
	 * @param mainFilename - main filename with hash ('main' since in S3 it will be prefaced)
	 * @param hash_String - needed for when on filesystem
	 * @return
	 * @throws Exception 
	 */
	public InputStream getInputStreamForScanStorageItem( String mainFilename, String hash_String ) throws Exception {
		
		if ( subDirForStorageFiles != null ) {
	
			//  getDirsForHash returns null when no data at path
			File subDirForHashForStorageFiles = 
					GetOrCreateSpectralStorageSubPath.getInstance().getDirsForHash( hash_String, subDirForStorageFiles );
			
			if ( subDirForHashForStorageFiles == null ) {
				String msg = "No subdir found for hash: " + hash_String
						+ ", subDirForStorageFiles: " + subDirForStorageFiles.getAbsolutePath();
				log.warn( msg );
				throw new SpectralStorageDataNotFoundException( msg );
			}
			
			File mainFile = new File( subDirForHashForStorageFiles, mainFilename );
			if ( ! mainFile.exists() ) {
				String msg = "File not found: " + mainFile.getAbsolutePath();
				log.error( msg );
				throw new SpectralStorageDataNotFoundException( msg );
			}
			return new FileInputStream( mainFile );
		}
		
		//  S3 object
		
		String s3_objectName = 
				SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES 
				+ mainFilename;
		
		//  throws exception when object not found for bucket and object name
		
		S3Object s3Object = null;
		
		try {
			s3Object = s3_Client.getObject( s3_Bucket, s3_objectName );

		} catch ( AmazonServiceException e ) {

			if ( e.getStatusCode() == 404 ) {
					String msg = "Hash Key not found: " + s3_objectName;
//					log.warn( msg );
					throw new SpectralStorageDataNotFoundException( msg );
			}
			throw e;
		} catch ( SdkClientException e ) {
			throw e;
		}
        
		
		InputStream isS3Object = s3Object.getObjectContent();
		return isS3Object;
	}
	
	/**
	 * @param mainFilename - main filename with hash ('main' since in S3 it will be prefaced)
	 * @param hash_String - needed for when on filesystem
	 * @param startOffset
	 * @param length
	 * @return
	 * @throws Exception 
	 */
	public byte[] getBytesFromScanStorageItem( 
			String mainFilename, 
			String hash_String, 
			long startOffset,
			int length ) throws Exception {

		byte[] bytesReadArray = new byte[ length ];

		if ( subDirForStorageFiles != null ) {
			
			//  getDirsForHash returns null when no data at path
			File subDirForHashForStorageFiles = 
					GetOrCreateSpectralStorageSubPath.getInstance().getDirsForHash( hash_String, subDirForStorageFiles );
			
			if ( subDirForHashForStorageFiles == null ) {
				String msg = "No subdir found for hash: " + hash_String
						+ ", subDirForStorageFiles: " + subDirForStorageFiles.getAbsolutePath();
				log.warn( msg );
				throw new SpectralStorageDataNotFoundException( msg );
			}
			
			File mainFile = new File( subDirForHashForStorageFiles, mainFilename );

			try ( RandomAccessFile spectalFile = new RandomAccessFile( mainFile, FILE_MODE_READ ) ) {

				//			spectalFile.getFilePointer();
				//			spectalFile.length();

				spectalFile.seek( startOffset );

				int totalBytesRead = 0;
				int numBytesRead = -1;

				do {
					numBytesRead = spectalFile.read( bytesReadArray, totalBytesRead, bytesReadArray.length - totalBytesRead );
					totalBytesRead += numBytesRead;

				} while ( numBytesRead != -1 && totalBytesRead < bytesReadArray.length );

				if ( totalBytesRead != bytesReadArray.length ) {
					//  Not at end of file and bytes read not number of bytes requested:
					String msg = "Number of bytes read is not number of bytes needed. totalBytesRead: " + totalBytesRead
							+ ", scanBytes.length: " + bytesReadArray.length
							+ ", startOffset: " + startOffset
							+ ", mainFile: " + mainFile.getAbsolutePath();
					log.error( msg );
					throw new SpectralStorageProcessingException(msg);
				}
			} catch ( Exception e ) {
				String msg = "Error reading range of bytes from file. byte count to read: " + bytesReadArray.length
						+ ", startOffset: " + startOffset
						+ ", mainFile: " + mainFile.getAbsolutePath();
				log.error( msg, e );
				throw e;
			}
		
			return bytesReadArray;  // EARLY EXIT
		} 

		//  S3 object
		
		String s3_objectName = 
				SpectralStorage_DataFiles_S3_Prefix_Constants.S3_PREFIX_DATA_FILES 
				+ mainFilename;
		
		long endIndex = startOffset + length - 1;

		GetObjectRequest rangeObjectRequest = new GetObjectRequest( s3_Bucket, s3_objectName );
		
		rangeObjectRequest.setRange( startOffset, endIndex ); // retrieve byte range, zero based (start,end).

		S3Object objectPortion_S3 = null;
		
		try {
			objectPortion_S3 = s3_Client.getObject( rangeObjectRequest );

		} catch ( AmazonServiceException e ) {

			if ( e.getStatusCode() == 404 ) {
					String msg = "Hash Key not found: " + s3_objectName;
//					log.warn( msg );
					throw new SpectralStorageDataNotFoundException( msg );
			}
			throw e;
		} catch ( SdkClientException e ) {
			throw e;
		}
        
		
		try ( InputStream objectDataIS = objectPortion_S3.getObjectContent() ) {

			int totalBytesRead = 0;
			int numBytesRead = -1;

			do {
				numBytesRead = objectDataIS.read( bytesReadArray, totalBytesRead, bytesReadArray.length - totalBytesRead );
				totalBytesRead += numBytesRead;

			} while ( numBytesRead != -1 && totalBytesRead < bytesReadArray.length );

			if ( totalBytesRead != bytesReadArray.length ) {
				//  Not at end of file and bytes read not number of bytes requested:
				String msg = "Number of bytes read is not number of bytes needed. totalBytesRead: " + totalBytesRead
						+ ", scanBytes.length: " + bytesReadArray.length
						+ ", startOffset: " + startOffset
						+ ", mainFilename: " + mainFilename;
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}
		} catch ( Exception e ) {
			String msg = "Error reading range of bytes from file. byte count to read: " + bytesReadArray.length
					+ ", startOffset: " + startOffset
					+ ", s3_objectName: " + s3_objectName;
			log.error( msg, e );
			throw e;
		}
		
		return bytesReadArray;
	}
	
	//  Package Private methods
	
	/**
	 * Validate called from CommonReader_File_And_S3_Builder build
	 * @throws SpectralStorageConfigException
	 */
	void init() throws SpectralStorageConfigException {
		if ( subDirForStorageFiles == null && StringUtils.isEmpty( s3_Bucket ) ) {
			String msg = "Must specify subDirForStorageFiles or s3 bucket but not both";
			log.error( msg );
			throw new SpectralStorageConfigException(msg);
		}
		if ( subDirForStorageFiles != null && StringUtils.isNotEmpty( s3_Bucket ) ) {
			String msg = "Not valid to specify both subDirForStorageFiles and s3 bucket";
			log.error( msg );
			throw new SpectralStorageConfigException(msg);
		}
		
		if ( StringUtils.isNotEmpty( s3_Bucket ) ) {
			AmazonS3ClientBuilder amazonS3ClientBuilder = AmazonS3ClientBuilder.standard();
			if ( StringUtils.isNotEmpty( s3_Region ) ) {
				amazonS3ClientBuilder = amazonS3ClientBuilder.withRegion( s3_Region );  // "us-west-2"
			}
			s3_Client = amazonS3ClientBuilder.build();
		}
	}

	//  Package Private Setters

	void setSubDirForStorageFiles(File subDirForStorageFiles) {
		this.subDirForStorageFiles = subDirForStorageFiles;
	}

	void setS3_Bucket(String s3_Bucket) {
		this.s3_Bucket = s3_Bucket;
	}

	void setS3_Region(String s3_Region) {
		this.s3_Region = s3_Region;
	}

	public AmazonS3 getS3_Client() {
		return s3_Client;
	}

	
}
