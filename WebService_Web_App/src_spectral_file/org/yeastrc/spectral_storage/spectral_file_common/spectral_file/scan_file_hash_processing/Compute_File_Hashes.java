package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_file_hash_processing;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class Compute_File_Hashes {

	private static final Logger log = Logger.getLogger(Compute_File_Hashes.class);

	private static final String SHA_384_ALGORITHM = "SHA-384";
	private static final String SHA_512_ALGORITHM = "SHA-512";
	private static final String SHA_1_ALGORITHM = "SHA1";
	
	/**
	 * Result from call to compute_File_Hashes
	 *
	 */
	public static class Compute_File_Hashes_Result {
		
		private byte[] sha_384_Hash;
		private byte[] sha_512_Hash;
		private byte[] sha_1_Hash;
		
		public byte[] getSha_384_Hash() {
			return sha_384_Hash;
		}
		public void setSha_384_Hash(byte[] sha_384_Hash) {
			this.sha_384_Hash = sha_384_Hash;
		}
		public byte[] getSha_512_Hash() {
			return sha_512_Hash;
		}
		public void setSha_512_Hash(byte[] sha_512_Hash) {
			this.sha_512_Hash = sha_512_Hash;
		}
		public byte[] getSha_1_Hash() {
			return sha_1_Hash;
		}
		public void setSha_1_Hash(byte[] sha_1_Hash) {
			this.sha_1_Hash = sha_1_Hash;
		}
	}
	
	/**
	 * private constructor
	 */
	private Compute_File_Hashes(){}
	public static Compute_File_Hashes getInstance( ) throws Exception {
		Compute_File_Hashes instance = new Compute_File_Hashes();
		return instance;
	}

	/**
	 * @param inputFile
	 * @return
	 * @throws Exception 
	 */
	public Compute_File_Hashes_Result compute_File_Hashes( File inputFile ) throws Exception {

		if ( log.isInfoEnabled() ) {
			log.info( "Computing File Hashes for file: " + inputFile.getAbsolutePath() );
		}
		
		Compute_File_Hashes_Result result = new Compute_File_Hashes_Result();
				
		FileInputStream fis = null;
		
		try {

			MessageDigest md_SHA_384 = MessageDigest.getInstance( SHA_384_ALGORITHM );

			MessageDigest md_SHA_512 = MessageDigest.getInstance( SHA_512_ALGORITHM );

			MessageDigest md_SHA_1 = MessageDigest.getInstance( SHA_1_ALGORITHM );

			fis = new FileInputStream( inputFile );
			
			byte[] dataBytes = new byte[1024];

			int nread = 0; 

			while ((nread = fis.read(dataBytes)) != -1) {
				md_SHA_384.update(dataBytes, 0, nread);
				md_SHA_512.update(dataBytes, 0, nread);
				md_SHA_1.update(dataBytes, 0, nread);
			}

			result.setSha_384_Hash( md_SHA_384.digest() );
			result.setSha_512_Hash( md_SHA_512.digest() );
			result.setSha_1_Hash( md_SHA_1.digest() );
	
		} finally {
			if ( fis != null ) {
				fis.close();
			}
		}
		
//		if ( log.isInfoEnabled() ) {
//			log.info( "SHA384 Sum for file: " + inputFile.getAbsolutePath() + " is: " + hashBytesToHexString( result.sha_384_Hash ) );
//		}
		
		log.warn( "INFO:  SHA384 Sum for file: " + inputFile.getAbsolutePath() + " is: " + hashBytesToHexString( result.sha_384_Hash ) );
		
	    return result;
	}
	
	/**
	 * @param hashBytes
	 * @return
	 */
	public String hashBytesToHexString( byte[] hashBytes ) {

		StringBuilder hashBytesAsHexSB = new StringBuilder( hashBytes.length * 2 + 2 );

		for ( int i = 0; i < hashBytes.length; i++ ) {
			String byteAsHex = Integer.toHexString( Byte.toUnsignedInt( hashBytes[ i ] ) );
			if ( byteAsHex.length() == 1 ) {
				hashBytesAsHexSB.append( "0" ); //  Leading zero dropped by 'toHexString' so add here
			}
			hashBytesAsHexSB.append( byteAsHex );
		}

		String result = hashBytesAsHexSB.toString();

		return result;
		
		//  WAS - which is equivalent, except for the added "0" when a hex pair starts with "0"
		
		//convert the byte to hex format
//		StringBuffer sb = new StringBuffer("");
//		for (int i = 0; i < hashBytes.length; i++) {
//			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
//		}
//		
//		String result = sb.toString();
//		
//		return result;
	}
	

//	/**
//	 * @param hashBytes
//	 * @return
//	 */
//	public String hashBytesToBase64String( byte[] hashBytes ) {
//		
//		//   Special Base64 encoding
//		
//		//  https://google.github.io/guava/releases/16.0/api/docs/com/google/common/io/BaseEncoding.html
//		
//		//  base64Url()	A-Z a-z 0-9 - _          Safe to use as filenames, or to pass in URLs without escaping
//		
//		//  Thoughts on using base64Url()
//		
//		// Positive, it creates a more compact string than hex.  
//		// Negative, it is non-standard so any bytes converted to string using standard base 64 encoding won't match.
//
//		//  String encoded = BaseEncoding.base64Url().encode( hashBytes );
//		
//		/////////////////////////////////
//		
//		//  Standard Base64 encoding
//		
//		//      base64()	A-Z a-z 0-9 + /
//		
//		//             Not filename legal
//		
//		String encoded = BaseEncoding.base64().encode( hashBytes );
//		
//		return encoded;
//	}
	
	
}
