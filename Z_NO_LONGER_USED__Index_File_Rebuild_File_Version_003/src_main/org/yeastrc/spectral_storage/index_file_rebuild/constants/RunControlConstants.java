package org.yeastrc.spectral_storage.index_file_rebuild.constants;

public class RunControlConstants {

	public static final String RUN_CONTROL_FILENAME = "SpectralStorage_RebuildIndexFiles_RunControl.txt";
	
	public static final String RUN_CONTROL_STOP_RUN_TEXT = "stop run";
			
	public static final String RUN_CONTROL_TEXT_SECOND_LINE = 
			"Enter only '" + RUN_CONTROL_STOP_RUN_TEXT + "' on first line to stop this program after processing the current data file.";
}
