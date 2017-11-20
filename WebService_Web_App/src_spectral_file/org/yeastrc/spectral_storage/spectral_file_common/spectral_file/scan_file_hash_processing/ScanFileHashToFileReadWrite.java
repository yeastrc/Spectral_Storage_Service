package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;

/**
 * Take the scan file hash and write it to a file or read it from a file
 *
 */
public class ScanFileHashToFileReadWrite {

	private static final Logger log = Logger.getLogger(ScanFileHashToFileReadWrite.class);

	/**
	 * private constructor
	 */
	private ScanFileHashToFileReadWrite(){}
	public static ScanFileHashToFileReadWrite getInstance( ) throws Exception {
		ScanFileHashToFileReadWrite instance = new ScanFileHashToFileReadWrite();
		return instance;
	}

	/**
	 * @param hashString
	 * @throws Exception 
	 */
	public void writeScanFileHashToInProcessFileInCurrentDir( String scanFilehash_String ) throws Exception {

		File hashFile = new File( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( scanFilehash_String );
		} catch ( Exception e ) {
			String msg = "Failed to write hash to file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
	}

	/**
	 * @throws Exception 
	 */
	public String readScanFileHashFromInProcessFile( File subDir ) throws Exception {

		File hashFile = new File( subDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING );

		String hash = null;

		try ( BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			hash = reader.readLine();
		} catch ( Exception e ) {
			String msg = "Failed to read hash from file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
		return hash;
	}
	
	/**
	 * @param hashString
	 * @throws Exception 
	 */
	public void writeScanFileHashToFinalHashKeyFile( String scanFilehash_String, File subDir ) throws Exception {

		File hashFile = new File( subDir, ScanFileToProcessConstants.Z_FINAL_HASH_KEY );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( scanFilehash_String );
		} catch ( Exception e ) {
			String msg = "Failed to write hash to file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
	}
	

	/**
	 * @throws Exception 
	 */
	public String readScanFileHashFromFinalHashKeyFile( File subDir ) throws Exception {

		File hashFile = new File( subDir, ScanFileToProcessConstants.Z_FINAL_HASH_KEY );

		String hash = null;

		try ( BufferedReader reader = new BufferedReader( new InputStreamReader(new FileInputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			hash = reader.readLine();
		} catch ( Exception e ) {
			String msg = "Failed to read hash from file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
		return hash;
	}
	
}
