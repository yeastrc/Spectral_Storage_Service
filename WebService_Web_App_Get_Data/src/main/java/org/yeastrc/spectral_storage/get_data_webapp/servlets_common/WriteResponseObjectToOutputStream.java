package org.yeastrc.spectral_storage.get_data_webapp.servlets_common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.constants_enums.ServetResponseFormatEnum;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileSerializeRequestException;
import org.yeastrc.spectral_storage.get_data_webapp.exceptions.SpectralFileWebappConfigException;

/**
 * Write the Response Object to the output stream
 * 
 * 
 * JSON requests currently throw new IllegalArgumentException since no Jackson jars in web app
 *
 */
public class WriteResponseObjectToOutputStream {

	private static final Logger log = LoggerFactory.getLogger( WriteResponseObjectToOutputStream.class );

	private static WriteResponseObjectToOutputStream instance = null;

	//  private constructor
	private WriteResponseObjectToOutputStream() { }
	
	/**
	 * @return Singleton instance
	 */
	public synchronized static WriteResponseObjectToOutputStream getSingletonInstance() throws Exception {
		if ( instance == null ) {
			instance = new WriteResponseObjectToOutputStream();
		}
		return instance; 
	}
	
	/**
	 * @param webserviceResponseAsObject
	 * @param servetResponseFormat
	 * @param response
	 * @throws Exception
	 */
	public void writeResponseObjectToOutputStream( 
			Object webserviceResponseAsObject,
			ServetResponseFormatEnum servetResponseFormat,
			HttpServletResponse response ) throws Exception {
		
		this.writeResponseObjectToOutputStream( webserviceResponseAsObject, null, servetResponseFormat, response );
	}
	

	/**
	 * @param webserviceResponseAsObject
	 * @param servetResponseFormat
	 * @param response
	 * @throws Exception
	 */
	public void writeResponseObjectToOutputStream( 
			Object webserviceResponseAsObject,
			Class callingClass,
			ServetResponseFormatEnum servetResponseFormat,
			HttpServletResponse response ) throws Exception {
		try {

			ByteArrayOutputStream outputStreamBufferOfServerResponse = 
					new ByteArrayOutputStream( 1000000 );
			
			if ( servetResponseFormat == ServetResponseFormatEnum.XML ) {

				JAXBContext jaxbContext = Z_JAXBContext_ForRequestResponse.getSingletonInstance().getJAXBContext();

				// Marshal Java object into XML
				try {
					Marshaller marshaller = jaxbContext.createMarshaller();

					marshaller.marshal( webserviceResponseAsObject, outputStreamBufferOfServerResponse );
				} catch ( JAXBException e ) {
					String msg = "Failed to serialize response object";
					log.error( msg, e );
					throw new SpectralFileSerializeRequestException( msg, e );
				}
			} else if ( servetResponseFormat == ServetResponseFormatEnum.JSON ) {

				String msg = "JSON not currently supported.  Add in Jackson jars for support";
				log.error( msg );
				throw new IllegalArgumentException( msg );
				
//				// send the JSON response 
//				ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//				mapper.writeValue( outputStreamBufferOfServerResponse, webserviceResponseAsObject ); // where first param can be File, OutputStream or Writer
				
			} else {
				String msg = "Unknown value for servetResponseFormat: " + servetResponseFormat;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}
			
			int outputStreamBufferOfServerResponseSize = outputStreamBufferOfServerResponse.size();
			
//			{
//				String msgcallingClass = "";
//				if ( callingClass != null ) {
//					 msgcallingClass =",  Calling class" + callingClass.getCanonicalName();
//				}
//				
//				log.warn( "outputStreamBufferOfServerResponseSize: " + outputStreamBufferOfServerResponseSize + msgcallingClass );
//			}
			
			response.setContentLength( outputStreamBufferOfServerResponseSize );
			
			try ( OutputStream outputStream = response.getOutputStream() ) {
				outputStreamBufferOfServerResponse.writeTo( outputStream );
			} catch ( IOException e ) {
				String msgcallingClass = "";
				if ( callingClass != null ) {
					 msgcallingClass ="  Calling class" + callingClass.getCanonicalName();
				}
				String msg = "Failed to write response object to output stream." + msgcallingClass;
				log.error( msg, e );
				throw e;
			} finally {
			}
			
		} finally {

		}
	}
}
