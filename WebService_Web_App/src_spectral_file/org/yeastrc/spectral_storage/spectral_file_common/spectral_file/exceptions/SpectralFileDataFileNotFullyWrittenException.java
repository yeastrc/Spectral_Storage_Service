package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions;

/**
 * Trying to read from a Data file that was not fully written
 * 
 * The first byte of the file is not 1
 *
 */
public class SpectralFileDataFileNotFullyWrittenException extends Exception {

	private static final long serialVersionUID = 1L;

	public SpectralFileDataFileNotFullyWrittenException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SpectralFileDataFileNotFullyWrittenException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public SpectralFileDataFileNotFullyWrittenException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SpectralFileDataFileNotFullyWrittenException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public SpectralFileDataFileNotFullyWrittenException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
