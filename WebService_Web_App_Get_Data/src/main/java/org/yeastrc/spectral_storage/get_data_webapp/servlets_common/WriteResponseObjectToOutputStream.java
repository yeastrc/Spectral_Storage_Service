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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

				//  Jackson JSON Mapper object for JSON deserialization and serialization
				ObjectMapper jacksonJSON_Mapper = new ObjectMapper();
				//   serialize 
				try {
					jacksonJSON_Mapper.writeValue( outputStreamBufferOfServerResponse, webserviceResponseAsObject );
				} catch ( JsonParseException e ) {
					String msg = "Failed to serialize 'resultsObject', JsonParseException. class of param: " + webserviceResponseAsObject.getClass() ;
					log.error( msg, e );
					throw e;
				} catch ( JsonMappingException e ) {
					String msg = "Failed to serialize 'resultsObject', JsonMappingException. class of param: " + webserviceResponseAsObject.getClass() ;
					log.error( msg, e );
					throw e;
				} catch ( IOException e ) {
					String msg = "Failed to serialize 'resultsObject', IOException. class of param: " + webserviceResponseAsObject.getClass() ;
					log.error( msg, e );
					throw e;
				}
				
			} else {
				String msg = "Unknown value for servetResponseFormat: " + servetResponseFormat;
				log.error( msg );
				throw new SpectralFileWebappConfigException( msg );
			}
			
			int outputStreamBufferOfServerResponseSize = outputStreamBufferOfServerResponse.size();
			
			{
				byte[] outputBytes = outputStreamBufferOfServerResponse.toByteArray();
				String outputString = new String( outputBytes );
				int z = 0;
			}
			
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
