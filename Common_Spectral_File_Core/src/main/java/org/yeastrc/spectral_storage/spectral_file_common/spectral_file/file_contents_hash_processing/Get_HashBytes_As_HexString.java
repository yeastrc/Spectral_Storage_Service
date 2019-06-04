package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.file_contents_hash_processing;

import org.apache.log4j.Logger;

public class Get_HashBytes_As_HexString {

	private static final Logger log = Logger.getLogger(Get_HashBytes_As_HexString.class);

	/**
	 * private constructor
	 */
	private Get_HashBytes_As_HexString(){}
	public static Get_HashBytes_As_HexString getInstance( ) throws Exception {
		Get_HashBytes_As_HexString instance = new Get_HashBytes_As_HexString();
		return instance;
	}

	/**
	 * @param hashBytes
	 * @return
	 */
	public String get_HashBytes_As_HexString( byte[] hashBytes ) {

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
