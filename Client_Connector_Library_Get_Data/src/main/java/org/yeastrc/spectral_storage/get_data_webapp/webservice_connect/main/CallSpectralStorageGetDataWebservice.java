package org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;

import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.constants.WebserviceSpectralStorageGetDataPathConstants;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.exceptions.YRCSpectralStorageGetDataWebserviceCallErrorException;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_MaxScanCountToReturn_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_MaxScanCountToReturn_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanData_AllScans_ExcludePeaks_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbers_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanNumbers_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanPeakIntensityBinnedOn_RT_MZ_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_SummaryDataPerScanLevel_Request;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.main.Get_SummaryDataPerScanLevel_Response;

/**
 * 
 *
 */
public class CallSpectralStorageGetDataWebservice {

	private static final String XML_ENCODING_CHARACTER_SET = StandardCharsets.UTF_8.toString();
	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";
	
	private String spectralStorageServerBaseURL;
	private JAXBContext jaxbContext;
	private boolean instanceInitialized;
	
	//  private constructor
	private CallSpectralStorageGetDataWebservice() { }
	/**
	 * @return newly created instance
	 */
	public static CallSpectralStorageGetDataWebservice getInstance() { 
		return new CallSpectralStorageGetDataWebservice(); 
	}
	
	/**
	 * Must be called before any other methods are called
	 * 
	 * @param spectralStorageServerBaseURL - excludes "/services..."
	 * @param requestingWebappIdentifier - identifier of the requesting web app
	 * @param requestingWebappKey - key for the requesting web app - null if none
	 * @throws Throwable
	 */
	public synchronized void init( CallSpectralStorageGetDataWebserviceInitParameters initParameters ) throws Exception {
		
		if ( initParameters.getSpectralStorageServerBaseURL() == null || initParameters.getSpectralStorageServerBaseURL().length() == 0 ) {
			throw new IllegalArgumentException( "spectralStorageServerBaseURL cannot be empty");
		}
		this.spectralStorageServerBaseURL = initParameters.getSpectralStorageServerBaseURL();

		jaxbContext = 
				JAXBContext.newInstance( 
						Get_MaxScanCountToReturn_Request.class,
						Get_MaxScanCountToReturn_Response.class,
						Get_ScanNumbers_Request.class,
						Get_ScanNumbers_Response.class,
						Get_ScanData_AllScans_ExcludePeaks_Request.class,
						Get_ScanData_AllScans_ExcludePeaks_Response.class,
						Get_ScanDataFromScanNumbers_Request.class,
						Get_ScanDataFromScanNumbers_Response.class,
						Get_ScanRetentionTimes_Request.class,
						Get_ScanRetentionTimes_Response.class,
						Get_ScanNumbersFromRetentionTimeRange_Request.class,
						Get_ScanNumbersFromRetentionTimeRange_Response.class,
						Get_ScansDataFromRetentionTimeRange_Request.class,
						Get_ScansDataFromRetentionTimeRange_Response.class,
						Get_SummaryDataPerScanLevel_Request.class,
						Get_SummaryDataPerScanLevel_Response.class,
						
						//  No XML response
						Get_ScanPeakIntensityBinnedOn_RT_MZ_Request.class
						);
		instanceInitialized = true;
	}
	
	/////////////////////////////
	
	

	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_MaxScanCountToReturn_Response call_Get_MaxScanCountToReturn_Webservice( Get_MaxScanCountToReturn_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanNumbers_Request_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_MAX_SCAN_COUNT_TO_RETURN_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_MaxScanCountToReturn_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_MaxScanCountToReturn_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_MaxScanCountToReturn_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_MaxScanCountToReturn_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_MaxScanCountToReturn_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	
	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScanNumbers_Response call_Get_ScanNumbers_Webservice( Get_ScanNumbers_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanNumbers_Request_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_NUMBERS_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanNumbers_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanNumbers_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanNumbers_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanNumbers_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanNumbers_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}

	/**
	 * If specify exclude scan peaks, the "IsCentroid" may not be populated if there is more than 1 value for the file.
	 * If "IsCentroid" is not populated, it will be null.
	 * 
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScanData_AllScans_ExcludePeaks_Response call_Get_ScanData_AllScans_ExcludePeaks_Webservice( Get_ScanData_AllScans_ExcludePeaks_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanDataFromScanNumbers_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_DATA_ALL_SCANS_EXCLUDE_PEAKS_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanData_AllScans_ExcludePeaks_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanData_AllScans_ExcludePeaks_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanData_AllScans_ExcludePeaks_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanData_AllScans_ExcludePeaks_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanData_AllScans_ExcludePeaks_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	
	/**
	 * If specify exclude scan peaks, the "IsCentroid" may not be populated if there is more than 1 value for the file.
	 * If "IsCentroid" is not populated, it will be null.
	 * 
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScanDataFromScanNumbers_Response call_Get_ScanDataFromScanNumbers_Webservice( Get_ScanDataFromScanNumbers_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanDataFromScanNumbers_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_DATA_FROM_SCAN_NUMBERS_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanDataFromScanNumbers_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanDataFromScanNumbers_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanDataFromScanNumbers_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanDataFromScanNumbers_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanDataFromScanNumbers_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}

	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScanRetentionTimes_Response call_Get_ScanRetentionTimes_Webservice( Get_ScanRetentionTimes_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanRetentionTimes_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_RETENTION_TIMES_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanRetentionTimes_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanRetentionTimes_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanRetentionTimes_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanRetentionTimes_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanRetentionTimes_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	
	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScanNumbersFromRetentionTimeRange_Response call_Get_ScanNumbersFromRetentionTimeRange_Webservice( Get_ScanNumbersFromRetentionTimeRange_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScanNumbersFromRetentionTimeRange_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_NUMBERS_FROM_RETENTION_TIME_RANGE_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanNumbersFromRetentionTimeRange_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanNumbersFromRetentionTimeRange_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanNumbersFromRetentionTimeRange_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanNumbersFromRetentionTimeRange_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanNumbersFromRetentionTimeRange_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}

	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_ScansDataFromRetentionTimeRange_Response call_Get_ScansDataFromRetentionTimeRange_Webservice( Get_ScansDataFromRetentionTimeRange_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScansDataFromRetentionTimeRange_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCANS_DATA_FROM_RETENTION_TIME_RANGE_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScansDataFromRetentionTimeRange_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScansDataFromRetentionTimeRange_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScansDataFromRetentionTimeRange_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScansDataFromRetentionTimeRange_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScansDataFromRetentionTimeRange_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	

	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public Get_SummaryDataPerScanLevel_Response call_GetSummaryDataPerScanLevel_Webservice( Get_SummaryDataPerScanLevel_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScansDataFromRetentionTimeRange_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SUMMARY_DATA_PER_SCAN_LEVEL_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_SummaryDataPerScanLevel_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_SummaryDataPerScanLevel_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_SummaryDataPerScanLevel_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_SummaryDataPerScanLevel_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_SummaryDataPerScanLevel_Response: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	
	
	////////////////////////////////////////////

	/////////   These return a byte[] that is directly returned from the server
	
	/**
	 * @param webserviceRequest
	 * @return
	 * @throws Exception 
	 */
	public byte[] call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice( Get_ScanPeakIntensityBinnedOn_RT_MZ_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScansDataFromRetentionTimeRange_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStorageGetDataPathConstants.GET_SCAN_PEAK_INTENSITY_BINNED_RT_MZ_JSON_GZIPPED;
		byte[] serverResponse = 
				callActualWebserviceOnServerSendObject_ReturnServerResponseByteArray( webserviceRequest,	webserviceURL );

		return serverResponse;
	}
	
	
	
	
	
	//////////////////////////////////////////////////////////////////
	//    Internal Methods
	
	/**
	 * @param webserviceRequest
	 * @param webserviceURL
	 * @return
	 * @throws Exception
	 */
	private Object callActualWebserviceOnServerSendObject( 
			Object webserviceRequest,
			String webserviceURL ) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream_ToSend = new ByteArrayOutputStream(100000);
		try {
			//  Jackson JSON code for JSON testing
			//  JSON using Jackson
//			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//			requestXMLToSend = mapper.writeValueAsBytes( webserviceRequest );
			
			//  Marshal (write) the object to the byte array as XML
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			marshaller.setProperty( Marshaller.JAXB_ENCODING, XML_ENCODING_CHARACTER_SET );
			try {
				marshaller.marshal( webserviceRequest, byteArrayOutputStream_ToSend );
			} catch ( Exception e ) {
				throw e;
			} finally {
				if ( byteArrayOutputStream_ToSend != null ) {
					byteArrayOutputStream_ToSend.close();
				}
			}
			//  Confirm that the generated XML can be parsed.
//			ByteArrayInputStream bais = new ByteArrayInputStream( byteArrayOutputStream_ToSend.toByteArray() );
//			XMLInputFactory xmlInputFactory = create_XMLInputFactory_XXE_Safe();
//			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( bais ) );
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			@SuppressWarnings("unused")
//			Object unmarshalledObject = unmarshaller.unmarshal( xmlStreamReader );

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg, e );
			exception.setFailToEncodeDataToSendToServer(true);
			throw exception;
		}
		
		return callActualWebserviceOnServerSendByteArrayOrFileAsStreamReturnObject( 
				byteArrayOutputStream_ToSend, null /* fileToSendAsStream */, webserviceURL );
	}
	

	/**
	 * @param webserviceRequest
	 * @param webserviceURL
	 * @return
	 * @throws Exception
	 */
	private byte[] callActualWebserviceOnServerSendObject_ReturnServerResponseByteArray( 
			Object webserviceRequest,
			String webserviceURL ) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream_ToSend = new ByteArrayOutputStream(100000);
		try {
			//  Jackson JSON code for JSON testing
			//  JSON using Jackson
//			ObjectMapper mapper = new ObjectMapper();  //  Jackson JSON library object
//			requestXMLToSend = mapper.writeValueAsBytes( webserviceRequest );
			
			//  Marshal (write) the object to the byte array as XML
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			marshaller.setProperty( Marshaller.JAXB_ENCODING, XML_ENCODING_CHARACTER_SET );
			try {
				marshaller.marshal( webserviceRequest, byteArrayOutputStream_ToSend );
			} catch ( Exception e ) {
				throw e;
			} finally {
				if ( byteArrayOutputStream_ToSend != null ) {
					byteArrayOutputStream_ToSend.close();
				}
			}
			//  Confirm that the generated XML can be parsed.
//			ByteArrayInputStream bais = new ByteArrayInputStream( byteArrayOutputStream_ToSend.toByteArray() );
//			XMLInputFactory xmlInputFactory = create_XMLInputFactory_XXE_Safe();
//			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( bais ) );
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			@SuppressWarnings("unused")
//			Object unmarshalledObject = unmarshaller.unmarshal( xmlStreamReader );

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: "
					+ e.toString();
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg, e );
			exception.setFailToEncodeDataToSendToServer(true);
			throw exception;
		}
		
		return sendToServerSendByteArrayOrFileAsStream_GetByteArrayResponseFromServer( 
				byteArrayOutputStream_ToSend, null /* fileToSendAsStream */, webserviceURL );
	}
	
	/**
	 * Send byte array or File to server as stream
	 * 
	 * bytesToSend or fileToSendAsStream must not be null and both cannot be not null
	 * 
	 * @param bytesToSend
	 * @param fileToSendAsStream
	 * @param webserviceURL
	 * @return
	 * @throws Exception
	 */
	private Object callActualWebserviceOnServerSendByteArrayOrFileAsStreamReturnObject( 
			ByteArrayOutputStream byteArrayOutputStream_ToSend,
			File fileToSendAsStream,
			String webserviceURL ) throws Exception {

		Object webserviceResponseAsObject = null;
		
		byte[] serverResponseByteArray = 
				sendToServerSendByteArrayOrFileAsStream_GetByteArrayResponseFromServer(
						byteArrayOutputStream_ToSend,
						fileToSendAsStream, 
						webserviceURL );

		ByteArrayInputStream inputStreamBufferOfServerResponse = 
				new ByteArrayInputStream( serverResponseByteArray );
		// Unmarshal received XML into Java objects
		try {
			XMLInputFactory xmlInputFactory = create_XMLInputFactory_XXE_Safe();
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader( new StreamSource( inputStreamBufferOfServerResponse ) );
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			webserviceResponseAsObject = unmarshaller.unmarshal( xmlStreamReader );
		} catch ( Exception e ) {
			YRCSpectralStorageGetDataWebserviceCallErrorException wcee = 
					new YRCSpectralStorageGetDataWebserviceCallErrorException( "JAXBException unmarshalling XML received from server at URL: " + webserviceURL, e );
			wcee.setFailToDecodeDataReceivedFromServer(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		return webserviceResponseAsObject; 
	}
	
	
	/**
	 * @param byteArrayOutputStream_ToSend
	 * @param fileToSendAsStream
	 * @param webserviceURL
	 * @return
	 * @throws YRCSpectralStorageGetDataWebserviceCallErrorException
	 */
	private byte[] sendToServerSendByteArrayOrFileAsStream_GetByteArrayResponseFromServer(
			ByteArrayOutputStream byteArrayOutputStream_ToSend,
			File fileToSendAsStream, 
			String webserviceURL) throws YRCSpectralStorageGetDataWebserviceCallErrorException {
		
		byte[] serverResponseByteArray = null;
		
		if ( ( ! ( byteArrayOutputStream_ToSend != null || fileToSendAsStream != null ) )
				|| (  byteArrayOutputStream_ToSend != null && fileToSendAsStream != null)) {
			String msg = "Exactly one of either byteArrayOutputStream_ToSend or fileToSendAsStream must be not null";
			YRCSpectralStorageGetDataWebserviceCallErrorException exception = new YRCSpectralStorageGetDataWebserviceCallErrorException( msg );
			exception.setCallInterfaceInternalError(true);
			exception.setCallInterfaceInternalErrorMessage(msg);
			throw exception;
		}
		
		//  Get number of bytes to send to specify in httpURLConnection.setFixedLengthStreamingMode(...)
		//  (This causes httpURLConnection to not buffer the sent data to get the length,
		//   allowing > 2GB to be sent and also no memory is needed for the buffering)
		long numberOfBytesToSend = -1;
		
		if ( byteArrayOutputStream_ToSend != null ) {
			numberOfBytesToSend = byteArrayOutputStream_ToSend.size();
		} else {
			numberOfBytesToSend = fileToSendAsStream.length();
		}
		
		//   Create object for connecting to server
		URL urlObject;
		try {
			urlObject = new URL( webserviceURL );
		} catch (MalformedURLException e) {
			YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Exception creating URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();
		} catch (IOException e) {
			YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Exception calling openConnection() on URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		//  Set HttpURLConnection properties

		//   Set Number of bytes to send, can be int or long
		//     ( Calling setFixedLengthStreamingMode(...) allows > 2GB to be sent 
		//       and HttpURLConnection does NOT buffer the sent bytes using ByteArrayOutputStream )
		httpURLConnection.setFixedLengthStreamingMode( numberOfBytesToSend );
		
		httpURLConnection.setRequestProperty( "Accept", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setRequestProperty( "Content-Type", CONTENT_TYPE_SEND_RECEIVE );
		httpURLConnection.setDoOutput(true);
		// Send post request to server
		try {  //  Overall try/catch block to put "httpURLConnection.disconnect();" in the finally block

			try {
				httpURLConnection.connect();
			} catch ( IOException e ) {
				YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Exception connecting to server at URL: " + webserviceURL, e );
				wcee.setServerURLError(true);
				wcee.setWebserviceURL( webserviceURL );
				throw wcee;
			}
			//  Send bytes to server
			OutputStream outputStream = null;
			FileInputStream fileInputStream = null; // for when send file
			try {
				outputStream = httpURLConnection.getOutputStream();
				if ( byteArrayOutputStream_ToSend != null ) {
					//  Send bytes to server
					byteArrayOutputStream_ToSend.writeTo( outputStream );
				} else {
					//  Send file contents to server
					fileInputStream = new FileInputStream( fileToSendAsStream );
					int byteArraySize = 5000;
					byte[] data = new byte[ byteArraySize ];
					while (true) {
						int bytesRead = fileInputStream.read( data );
						if ( bytesRead == -1 ) {  // end of input
							break;
						}
						if ( bytesRead > 0 ) {
							outputStream.write( data, 0, bytesRead );
						}
					}
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "IOException sending XML to server at URL: " + webserviceURL, e );
				wcee.setServerURLError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			} finally {
				if ( outputStream != null ) {
					boolean closeOutputStreamFail = false;
					try {
						outputStream.close();
					} catch ( IOException e ) {
						closeOutputStreamFail = true;
						byte[] errorStreamContents = null;
						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection );
						} catch ( Exception ex ) {
						}
						YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "IOException closing output Stream to server at URL: " + webserviceURL, e );
						wcee.setServerURLError(true);
						wcee.setWebserviceURL( webserviceURL );
						wcee.setErrorStreamContents( errorStreamContents );
						throw wcee;
					} finally {
						if ( fileInputStream != null ) {
							try {
								fileInputStream.close();
							} catch ( Exception e ) {
								if ( ! closeOutputStreamFail ) {
									// Only throw exception if close of output stream successful
									byte[] errorStreamContents = null;
									try {
										errorStreamContents= getErrorStreamContents( httpURLConnection );
									} catch ( Exception ex ) {
									}
									YRCSpectralStorageGetDataWebserviceCallErrorException wcee = new YRCSpectralStorageGetDataWebserviceCallErrorException( "Exception closing output Stream to server at URL: " + webserviceURL, e );
									wcee.setServerURLError(true);
									wcee.setWebserviceURL( webserviceURL );
									wcee.setErrorStreamContents( errorStreamContents );
									throw wcee;
								}
							}
						}
					}
				}
			}
			try {
				int httpResponseCode = httpURLConnection.getResponseCode();
				if ( httpResponseCode != SUCCESS_HTTP_RETURN_CODE ) {
					byte[] errorStreamContents = null;
					try {
						errorStreamContents= getErrorStreamContents( httpURLConnection );
					} catch ( Exception ex ) {
					}
					YRCSpectralStorageGetDataWebserviceCallErrorException wcee = 
							new YRCSpectralStorageGetDataWebserviceCallErrorException( "Unsuccessful HTTP response code of " + httpResponseCode
									+ " connecting to server at URL: " + webserviceURL );
					wcee.setBadHTTPStatusCode(true);
					wcee.setHttpStatusCode( httpResponseCode );
					wcee.setWebserviceURL( webserviceURL );
					wcee.setErrorStreamContents( errorStreamContents );
					throw wcee;
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				YRCSpectralStorageGetDataWebserviceCallErrorException wcee = 
						new YRCSpectralStorageGetDataWebserviceCallErrorException( "IOException getting HTTP response code from server at URL: " + webserviceURL, e );
				wcee.setServerSendReceiveDataError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			}
			//  Get response XML from server
			ByteArrayOutputStream outputStreamBufferOfServerResponse = new ByteArrayOutputStream( 1000000 );
			InputStream inputStream = null;
			try {
				inputStream = httpURLConnection.getInputStream();
				int nRead;
				byte[] data = new byte[ 16384 ];
				while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
					outputStreamBufferOfServerResponse.write(data, 0, nRead);
				}
			} catch ( IOException e ) {
				byte[] errorStreamContents = null;
				try {
					errorStreamContents= getErrorStreamContents( httpURLConnection );
				} catch ( Exception ex ) {
				}
				YRCSpectralStorageGetDataWebserviceCallErrorException wcee = 
						new YRCSpectralStorageGetDataWebserviceCallErrorException( "IOException receiving XML from server at URL: " + webserviceURL, e );
				wcee.setServerSendReceiveDataError(true);
				wcee.setWebserviceURL( webserviceURL );
				wcee.setErrorStreamContents( errorStreamContents );
				throw wcee;
			} finally {
				if ( inputStream != null ) {
					try {
						inputStream.close();
					} catch ( IOException e ) {
						byte[] errorStreamContents = null;
						try {
							errorStreamContents= getErrorStreamContents( httpURLConnection );
						} catch ( Exception ex ) {
						}
						YRCSpectralStorageGetDataWebserviceCallErrorException wcee = 
								new YRCSpectralStorageGetDataWebserviceCallErrorException( "IOException closing input Stream from server at URL: " + webserviceURL, e );
						wcee.setServerSendReceiveDataError(true);
						wcee.setWebserviceURL( webserviceURL );
						wcee.setErrorStreamContents( errorStreamContents );
						throw wcee;
					}
				}
			}
			serverResponseByteArray = outputStreamBufferOfServerResponse.toByteArray();

			
		} finally {
//			httpURLConnection.disconnect();
		}
		return serverResponseByteArray;
	}
	
	/**
	 * @param httpURLConnection
	 * @return
	 * @throws IOException
	 */
	private byte[] getErrorStreamContents(HttpURLConnection httpURLConnection) throws IOException {
		
		InputStream inputStream = httpURLConnection.getErrorStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int byteArraySize = 5000;
		byte[] data = new byte[ byteArraySize ];
		while (true) {
			int bytesRead = inputStream.read( data );
			if ( bytesRead == -1 ) {  // end of input
				break;
			}
			if ( bytesRead > 0 ) {
				baos.write( data, 0, bytesRead );
			}
		}
		return baos.toByteArray();
	}

	/**
	 * Create XMLInputFactory that has the settings that make it safe from XXE
	 * 
	 * @return
	 */
	private XMLInputFactory create_XMLInputFactory_XXE_Safe() {

	    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

	    //  XXE  Mitigation
	    //  prevents using external resources when parsing xml
	    xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);

	    //  prevents using external document type definition when parsing xml
	    xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
	  
		return xmlInputFactory;
	}
}
