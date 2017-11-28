package org.yeastrc.spectral_storage.index_file_rebuild.program;

import java.io.File;

import org.yeastrc.spectral_storage.index_file_rebuild.constants.DataFile_Version_BeingProcessed_Constants;
import org.yeastrc.spectral_storage.index_file_rebuild.main.ProcessFileWithDataFileList;
import org.yeastrc.spectral_storage.index_file_rebuild.run_control.RunControlFile_Create_Read;


/**
 * 
 *
 */
public class Rebuild_Indexes_MainProgram {
	
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

		if ( ! ( args.length >= 2 && args.length <= 2 ) ) {
			System.err.println( "<pgm> <output base dir> <list_of_data_files__file>");
			System.exit( 1 );
		}
		
		System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
		System.out.println();
		System.out.println( "Processing for data file version: " + DataFile_Version_BeingProcessed_Constants.dataFile_Version_BeingProcessed );
		System.out.println();
		System.out.println( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );

		String outputBaseDirString = args[ 0 ];
		String fileContainingListOfDataFilesString = args[ 1 ];
				
		System.out.println( "Output Base Dir command line: " + outputBaseDirString );
		System.out.println( "File Containing List Of Files command line: " + fileContainingListOfDataFilesString );
		
		File outputBaseDir = new File( outputBaseDirString );

		System.out.println( "Output Base Dir canonical: " + outputBaseDir.getCanonicalPath() );
		
		if ( ! outputBaseDir.exists() ) {
			System.err.println( "Output Base Dir command line Not Exist: " + outputBaseDirString );
			System.exit( 1 );
		}

		File fileContainingListOfDataFiles = new File( fileContainingListOfDataFilesString );

		System.out.println( "File Containing List Of Files canonical: " + fileContainingListOfDataFiles.getCanonicalPath() );
		
		if ( ! fileContainingListOfDataFiles.exists() ) {
			System.err.println( "File Containing List Of Files command line Not Exist: " + fileContainingListOfDataFilesString );
			System.exit( 1 );
		}
		
		RunControlFile_Create_Read.getInstance().runControlFile_Create();
		
		ProcessFileWithDataFileList.getInstance().processFileWithDataFileList( outputBaseDir, fileContainingListOfDataFiles );
	}

}
