package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlet_utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream {

	private static final Logger log = LoggerFactory.getLogger(ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream.class);
	
	/**
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static ByteArrayOutputStream  read_ServletRequest_Into_ByteArrayOutputStream (HttpServletRequest request) throws IOException {
		

		ByteArrayOutputStream outputStreamBufferOfClientRequest = new ByteArrayOutputStream( 1000000 );
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			int nRead;
			byte[] data = new byte[ 16384 ];
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				outputStreamBufferOfClientRequest.write(data, 0, nRead);
			}
		} catch ( IOException e ) {
			String msg = "Failed to read Servlet input stream";
			log.error( msg, e );
			throw e;
		} finally {
			if ( inputStream != null ) {
				try {
					inputStream.close();
				} catch ( IOException e ) {
					throw e;
				}
			}
		}
		
		return outputStreamBufferOfClientRequest;
		
	}
	
}
