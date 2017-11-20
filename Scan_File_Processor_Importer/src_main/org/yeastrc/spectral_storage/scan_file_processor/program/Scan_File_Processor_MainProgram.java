package org.yeastrc.spectral_storage.scan_file_processor.program;

import java.io.File;

import org.yeastrc.spectral_storage.scan_file_processor.main.ProcessUploadedScanFileDir;

/**
 * 
 *
 */
public class Scan_File_Processor_MainProgram {
	
	public static final String CMD_LINE_PARAM_DELETE_ON_SUCCESS = "--delete-scan-file-on-successful-processing";

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		System.out.println( "Command Line args START");
		for ( int index = 0; index < args.length; index++) {
			System.out.println( "arg: " + args[ index ] );
		}
		System.out.println( "Command Line args END");

		if ( args.length >= 1 && args.length <= 2 ) {
		} else {
			System.err.println( "<pgm> <output base dir> [" + CMD_LINE_PARAM_DELETE_ON_SUCCESS + "]");
			System.exit( 1 );
		}

		String outputBaseDirString = args[ 0 ];
		
		boolean deleteScanFileOnSuccess = false;
		
		if ( args.length >= 2 ) {
			if ( ! CMD_LINE_PARAM_DELETE_ON_SUCCESS.equals( args[ 1 ] ) ) {
				System.err.println( "Second command line param is not '" + CMD_LINE_PARAM_DELETE_ON_SUCCESS + "'."
						+ "  No other value is allowed.");
				System.exit( 1 );
			}
			
			deleteScanFileOnSuccess = true;
		}
		
		System.out.println( "Output Base Dir command line: " + outputBaseDirString );
		
		File outputBaseDir = new File( outputBaseDirString );

		System.out.println( "Output Base Dir canonical: " + outputBaseDir.getCanonicalPath() );
		
		if ( ! outputBaseDir.exists() ) {
			System.err.println( "Output Base Dir command line Not Exist: " + outputBaseDirString );
			System.exit( 1 );
		}
		
		if ( deleteScanFileOnSuccess ) {
			System.out.println( "Deleting uploaded scan file on successful import" );
		}
		
		ProcessUploadedScanFileDir.getInstance().processUploadedScanFileDir( outputBaseDir, deleteScanFileOnSuccess );
	}

}
