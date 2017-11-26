package org.yeastrc.spectral_storage.scan_file_processor.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.scan_file_processor.check_if_spectral_file_exists.CheckIfSpectralFileAlreadyExists;
import org.yeastrc.spectral_storage.scan_file_processor.process_scan_file.Process_ScanFile_Create_SpectralFile;
import org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file.ValidateInputScanFile;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.ScanFileToProcessConstants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.Compute_File_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.Compute_File_Hashes.Compute_File_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing.ScanFileHashToFileReadWrite;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.storage_file__path__filenames.GetOrCreateSpectralStorageSubPath;

/**
 * 
 *
 */
public class ProcessUploadedScanFileDir {

	private static final Logger log = Logger.getLogger(ProcessUploadedScanFileDir.class);
	/**
	 * private constructor
	 */
	private ProcessUploadedScanFileDir(){}
	public static ProcessUploadedScanFileDir getInstance( ) throws Exception {
		ProcessUploadedScanFileDir instance = new ProcessUploadedScanFileDir();
		return instance;
	}
	
	/**
	 * @param outputBaseDir
	 * @throws Exception 
	 */
	public void processUploadedScanFileDir( File outputBaseDir, boolean deleteScanFileOnSuccess ) throws Exception {
		
		File inputScanFile = getInputScanFile();
		
		System.out.println( "Starting validate scan file.  Now: " + new Date() );
		
		//  Throws exception SpectralStorageDataException if any error
		ValidateInputScanFile.getInstance().validateScanFile( inputScanFile );
		
		System.out.println( "Finished validate scan file.  Now: " + new Date() );
		
		System.out.println( "Starting Compute hashes for scan file.  Now: " + new Date() );
		
		Compute_File_Hashes_Result compute_File_Hashes_Result =
				Compute_File_Hashes.getInstance().compute_File_Hashes( inputScanFile );
		
		System.out.println( "Finished Compute hashes for scan file.  Now: " + new Date() );
		
		processInputFileWithComputedHash(outputBaseDir, 
				deleteScanFileOnSuccess, 
				inputScanFile, 
				compute_File_Hashes_Result);
		

	}
	
	
	/**
	 * @param outputBaseDir
	 * @param deleteScanFileOnSuccess
	 * @param inputScanFile
	 * @param compute_File_Hashes_Result
	 * @throws Exception
	 * @throws IOException
	 */
	public  void processInputFileWithComputedHash(
			File outputBaseDir, 
			boolean deleteScanFileOnSuccess, 
			File inputScanFile,
			Compute_File_Hashes_Result compute_File_Hashes_Result ) throws Exception, IOException {
		byte[] hash_sha384_Bytes = compute_File_Hashes_Result.getSha_384_Hash();
		
		String hash_sha384_String = Compute_File_Hashes.getInstance().hashBytesToHexString( hash_sha384_Bytes );
		
		System.out.println( "Main hash string: " + hash_sha384_String );
		
		ScanFileHashToFileReadWrite.getInstance().writeScanFileHashToInProcessFileInCurrentDir( hash_sha384_String );
		
		File subDirForStorageFiles = 
				GetOrCreateSpectralStorageSubPath.getInstance()
				.createDirsForHashIfNotExists( hash_sha384_String, outputBaseDir );
		
		System.out.println( "storage dir to write data files to: " + subDirForStorageFiles.getCanonicalPath() );
		
		writeAssocStorageDirInCurrentDir( subDirForStorageFiles.getCanonicalPath() );
		
		if ( CheckIfSpectralFileAlreadyExists.getInstance()
				.doesSpectralFileAlreadyExist( inputScanFile, /* uploadedScanFilename, */ subDirForStorageFiles, hash_sha384_String, hash_sha384_Bytes ) ) {
			
			System.out.println( "Data File already exists so no processing needed");
			
			if ( deleteScanFileOnSuccess ) {
				cleanupInputScanFile( inputScanFile );
			}
			
			return;
		}
		
		System.out.println( "Data File does NOT already exists so STARTING processing the scan file.  Now: " + new Date() );
		
		try {
			Process_ScanFile_Create_SpectralFile.getInstance()
			.processScanFile( inputScanFile, subDirForStorageFiles, hash_sha384_String, compute_File_Hashes_Result );

		} catch ( Exception e) {
			log.error( "Failed to process scan file: " + inputScanFile.getAbsolutePath(), e );
			throw e;
		}

		System.out.println( "DONE Successfully processing the scan file.  Now: " + new Date() );
		
//		if ( deleteScanFileOnSuccess ) {
//			cleanupInputScanFile( inputScanFile );
//		}
	}
	
	/**
	 * @param inputScanFile
	 */
	private void cleanupInputScanFile( File inputScanFile ) {

		System.out.println( "Import was successful and delete scan file on successful import is set in config file so deleting uploaded scan file: "
				+ inputScanFile.getAbsolutePath() );
		
		if ( ! inputScanFile.delete() ) {
			System.err.println( "Failed to delete input scan file: " + inputScanFile.getAbsolutePath() );
		} else {
			System.out.println( "Deleted input scan file: " + inputScanFile.getAbsolutePath() );
		}
		
	}
	
	/**
	 * @param hashString
	 * @throws Exception 
	 */
	private void writeAssocStorageDirInCurrentDir( String storageDirPath ) throws Exception {

		File hashFile = new File( "x_Assoc_Storage_dir.txt" );

		try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( hashFile ), StandardCharsets.UTF_8 ) ) ) {
			writer.write( storageDirPath );
		} catch ( Exception e ) {
			String msg = "Failed to write hash to file: " + hashFile.getAbsolutePath();
			log.error( msg );
			throw new Exception(msg);
		}
		
	}

	/**
	 * @return
	 * @throws Exception 
	 */
	private File getInputScanFile() throws Exception {
		
		File currentDir = new File( "." );
		
		File[] dirContents = currentDir.listFiles();
		
		for ( File dirEntry : dirContents ) {
			String dirEntryFilename = dirEntry.getName();
			if ( dirEntryFilename.startsWith( ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX )
					&& ( dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) 
							|| dirEntryFilename.endsWith( ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) ) {
				return dirEntry; // EARLY EXIT
			}
		}
		
		String msg = "No file found that starts with '" + ScanFileToProcessConstants.SCAN_FILE_TO_PROCESS_FILENAME_PREFIX
				+ "' and ends with '" + ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
				+ "' or '" + ScanFileToProcessConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
				+ "' in dir: " + currentDir.getCanonicalPath();
		log.error( msg );
		throw new Exception(msg);
		
//		return null;
		
	}
	
}
