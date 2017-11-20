package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums;

/**
 * Has the Data or Index File been fully written.
 * 
 * Initially written as NO, Updated to YES after the writing of the file is done.
 * 
 * Stored in the first byte of the data file
 *
 */
public class DataOrIndexFileFullyWrittenConstants {

	public static final byte FILE_FULLY_WRITTEN_NO = 0;
	public static final byte FILE_FULLY_WRITTEN_YES = 1;
}
