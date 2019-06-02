package org.yeastrc.spectral_storage.get_data_webapp.servlets_common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileDeserializeRequestException;

/**
 * Read the Request Object off the input stream and de-serialize it and return it
 *
 */
public class GetRequestObjectFromInputStream {

	private static final Logger log = Logger.getLogger( GetRequestObjectFromInputStream.class );

	private static GetRequestObjectFromInputStream instance = null;

	//  private constructor
	private GetRequestObjectFromInputStream() { }
	
	/**
	 * @return Singleton instance
	 */
	public synchronized static GetRequestObjectFromInputStream getSingletonInstance() throws Exception {
		if ( instance == null ) {
			instance = new GetRequestObjectFromInputStream();
		}
		return instance; 
	}
	
	/**
	 * @param webserviceRequest
	 * @param webserviceURL
	 * @return
	 * @throws Exception
	 */
	public Object getRequestObjectFromStream( 
			HttpServletRequest request ) throws Exception {
		Object webserviceResponseAsObject = null;
		try {

			//  Get request XML from client
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
				String msg = "Failed to read request object from input stream";
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
			
			JAXBContext jaxbContext = Z_JAXBContext_ForRequestResponse.getSingletonInstance().getJAXBContext();
			
			byte[] serverResponseByteArrayFromServer = outputStreamBufferOfClientRequest.toByteArray();
			byte[] serverResponseByteArray = serverResponseByteArrayFromServer;
			ByteArrayInputStream inputStreamBufferOfServerResponse = 
					new ByteArrayInputStream( serverResponseByteArray );
			// Unmarshal received XML into Java objects
			try {
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				webserviceResponseAsObject = unmarshaller.unmarshal( inputStreamBufferOfServerResponse );
			} catch ( JAXBException e ) {
				String msg = "Failed to deserialize request object";
				log.error( msg, e );
				throw new SpectralFileDeserializeRequestException( msg, e );
			}
			return webserviceResponseAsObject; 
		} finally {

		}
	}
	
}
