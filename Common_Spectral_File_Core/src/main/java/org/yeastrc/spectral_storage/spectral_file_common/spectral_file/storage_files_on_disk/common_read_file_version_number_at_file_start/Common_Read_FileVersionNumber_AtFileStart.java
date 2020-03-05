package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_read_file_version_number_at_file_start;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3;

/**
 * 
 *
 */
public class Common_Read_FileVersionNumber_AtFileStart {

	private static final Logger log = LoggerFactory.getLogger(Common_Read_FileVersionNumber_AtFileStart.class);

	/**
	 * private constructor
	 */
	private Common_Read_FileVersionNumber_AtFileStart(){}
	public static Common_Read_FileVersionNumber_AtFileStart getInstance( ) throws Exception {
		Common_Read_FileVersionNumber_AtFileStart instance = new Common_Read_FileVersionNumber_AtFileStart();
		return instance;
	}
	
	/**
	 * @param spectralDataFilename
	 * @param hashKey
	 * @param commonReader_File_And_S3
	 * @return
	 * @throws Exception 
	 * @throws SpectralStorageDataNotFoundException 
	 */
	public short common_Read_FileVersionNumber_AtFileStart( String spectralDataFilename, String hashKey, CommonReader_File_And_S3 commonReader_File_And_S3 ) throws SpectralStorageDataNotFoundException, Exception {

		//  Read version number of file.  First 2 bytes as a Short

		short fileVersionInFile = 0;
		{	
			final int startOffset = 0; // read from start of file
			final int numBytes = Short.BYTES;
	
			//  SpectralStorageDataNotFoundException thrown if spectralDataFilename not found
			byte[] spectralDataFile_VersionBytes =
					commonReader_File_And_S3.getBytesFromScanStorageItem( spectralDataFilename, hashKey, startOffset, numBytes );
			
			if ( spectralDataFile_VersionBytes == null ) {
				
				String msg = "spectralDataFile_VersionBytes == null";
				log.error(msg);
				throw new SpectralStorageProcessingException( msg );
			}
			
			ByteArrayInputStream spectralDataFile_VersionInputStream = new ByteArrayInputStream( spectralDataFile_VersionBytes ); 
			
			try ( DataInputStream dataInputStream = new DataInputStream( spectralDataFile_VersionInputStream ) ) {
				
				fileVersionInFile = dataInputStream.readShort();
			}
		}
		
		return fileVersionInFile;
	}
}
