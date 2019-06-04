package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.shared_server_importer.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;

/**
 * Take the scan file API Key (Based on Hash) and write it to a file or read it from a file
 *
 */
public class ScanFileAPIKey_ToFileReadWrite {

	private static final Logger log = Logger.getLogger(ScanFileAPIKey_ToFileReadWrite.class);

	/**
	 * private constructor
	 */
	private ScanFileAPIKey_ToFileReadWrite(){}
	public static ScanFileAPIKey_ToFileReadWrite getInstance( ) throws Exception {
		ScanFileAPIKey_ToFileReadWrite instance = new ScanFileAPIKey_ToFileReadWrite();
		return instance;
	}

	/**
	 * @param hashString
	 * @throws Exception 
	 */
	public void writeScanFileHashToInProcessFileInCurrentDir( String scanFilehash_String ) throws Exception {

		File hashFile = new File( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( scanFilehash_String );
		} catch ( Exception e ) {
			String msg = "Failed to write hash to file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
	}

	/**
	 * @param hashString
	 * @throws Exception 
	 */
	public void writeScanFileHashToInProcessFileInDir( String scanFilehash_String, File dir ) throws Exception {
		
		if ( ! dir.exists() ) {
			String msg = "dir passed to writeScanFileHashToInProcessFileInDir(...) does not exist: " + dir.getAbsolutePath();
			log.error( msg );
			throw new SpectralStorageProcessingException( msg );
		}

		File hashFile = new File( dir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME );

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

		File hashFile = new File( subDir, ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_HASH_STRING_API_KEY_FILENAME );

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
