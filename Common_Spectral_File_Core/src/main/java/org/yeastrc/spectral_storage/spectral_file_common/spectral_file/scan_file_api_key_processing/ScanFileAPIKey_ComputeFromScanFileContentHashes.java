package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_api_key_processing;

//import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Compute_Hashes.Compute_Hashes_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing.Get_HashBytes_As_HexString;

/**
 * Compute the Scan File API from the Scan File Content Hashes
 *
 */
public class ScanFileAPIKey_ComputeFromScanFileContentHashes {

//	private static final Logger log = LoggerFactory.getLogger(ScanFileAPIKey_ComputeFromScanFileContentHashes.class);
	
	/**
	 * private constructor
	 */
	private ScanFileAPIKey_ComputeFromScanFileContentHashes(){}
	public static ScanFileAPIKey_ComputeFromScanFileContentHashes getInstance( ) throws Exception {
		ScanFileAPIKey_ComputeFromScanFileContentHashes instance = new ScanFileAPIKey_ComputeFromScanFileContentHashes();
		return instance;
	}
	
	/**
	 * Compute the Scan File API from the Scan File Content Hashes
	 * 
	 * @return API Key as String
	 * @throws Exception 
	 */
	public String scanFileAPIKey_ComputeFromScanFileContentHashes( Compute_Hashes compute_Hashes ) throws Exception {

		Compute_Hashes_Result compute_Hashes_result = compute_Hashes.compute_Hashes();

		byte[] hash_sha384_Bytes = compute_Hashes_result.getSha_384_Hash();
		
		String hash_sha384_String = Get_HashBytes_As_HexString.getInstance().get_HashBytes_As_HexString( hash_sha384_Bytes );
		
		return hash_sha384_String;
	}
}
