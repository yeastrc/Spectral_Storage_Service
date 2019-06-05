package org.yeastrc.spectral_storage.index_file_rebuild.run_control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.index_file_rebuild.constants.RunControlConstants;

/**
 * 
 *
 */
public class RunControlFile_Create_Read {

	private static final Logger log = LoggerFactory.getLogger(RunControlFile_Create_Read.class);
	/**
	 * private constructor
	 */
	private RunControlFile_Create_Read(){}
	public static RunControlFile_Create_Read getInstance( ) throws Exception {
		RunControlFile_Create_Read instance = new RunControlFile_Create_Read();
		return instance;
	}
	
	/**
	 * @throws IOException
	 */
	public void runControlFile_Create() throws IOException {
		
		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( RunControlConstants.RUN_CONTROL_FILENAME ) ) ) {
			writer.newLine();
			writer.write( RunControlConstants.RUN_CONTROL_TEXT_SECOND_LINE );
			writer.newLine();
		}
	}
	
	/**
	 * @return
	 * @throws IOException
	 */
	public boolean runControlFile_Contains_StopRun() throws IOException {
		try ( BufferedReader reader = new BufferedReader( new FileReader( RunControlConstants.RUN_CONTROL_FILENAME ) ) ) {
			String firstLine = reader.readLine();
			if ( RunControlConstants.RUN_CONTROL_STOP_RUN_TEXT.equals( firstLine ) ) {
				System.out.println( "Run control file '"
						+ RunControlConstants.RUN_CONTROL_FILENAME 
						+ "' contains ' "
						+ "+ RunControlConstants.RUN_CONTROL_STOP_RUN_TEXT "
						+ "' so stopping processing." );
				return true;
			}
			return false;
		}
	}
}
