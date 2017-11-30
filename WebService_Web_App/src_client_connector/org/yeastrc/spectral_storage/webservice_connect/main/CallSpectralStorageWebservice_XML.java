package org.yeastrc.spectral_storage.webservice_connect.main;

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
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.yeastrc.spectral_storage.shared_server_client.constants.WebserviceSpectralStoragePathConstants;
import org.yeastrc.spectral_storage.shared_server_client.constants.WebserviceSpectralStorageQueryParamsConstants;
import org.yeastrc.spectral_storage.shared_server_client.constants.WebserviceSpectralStorageScanFileAllowedSuffixesConstants;
import org.yeastrc.spectral_storage.shared_server_client.exceptions.YRCSpectralStorageWebserviceCallErrorException;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanDataFromScanNumbers_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanNumbersFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanPeakIntensityBinnedOn_RT_MZ_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScanRetentionTimes_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_ScansDataFromRetentionTimeRange_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.Get_UploadedScanFileInfo_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Delete_For_ScanProcessStatusKey_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Init_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Init_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_Submit_Response;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Request;
import org.yeastrc.spectral_storage.shared_server_client.webservice_request_response.main.UploadScanFile_UploadScanFile_Response;

/**
 * 
 *
 */
public class CallSpectralStorageWebservice_XML {

	private static final String XML_ENCODING_CHARACTER_SET = StandardCharsets.UTF_8.toString();
	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/xml";
	
	private String spectralStorageServerBaseURL;
	private JAXBContext jaxbContext;
	private boolean instanceInitialized;
	
	//  private constructor
	private CallSpectralStorageWebservice_XML() { }
	/**
	 * @return newly created instance
	 */
	public static CallSpectralStorageWebservice_XML getInstance() { 
		return new CallSpectralStorageWebservice_XML(); 
	}
	
	/**
	 * Must be called before any other methods are called
	 * 
	 * @param spectralStorageServerBaseURL - excludes "/services..."
	 * @param requestingWebappIdentifier - identifier of the requesting web app
	 * @param requestingWebappKey - key for the requesting web app - null if none
	 * @throws Throwable
	 */
	public synchronized void init( CallSpectralStorageWebserviceInitParameters initParameters ) throws Exception {
		
		if ( initParameters.getSpectralStorageServerBaseURL() == null || initParameters.getSpectralStorageServerBaseURL().length() == 0 ) {
			throw new IllegalArgumentException( "spectralStorageServerBaseURL cannot be empty");
		}
		this.spectralStorageServerBaseURL = initParameters.getSpectralStorageServerBaseURL();

		jaxbContext = 
				JAXBContext.newInstance( 
						UploadScanFile_Init_Request.class,
						UploadScanFile_Init_Response.class,
						UploadScanFile_UploadScanFile_Response.class,
						UploadScanFile_Submit_Request.class,
						UploadScanFile_Submit_Response.class,
						Get_UploadedScanFileInfo_Request.class, 
						Get_UploadedScanFileInfo_Response.class,
						UploadScanFile_Delete_For_ScanProcessStatusKey_Request.class,
						UploadScanFile_Delete_For_ScanProcessStatusKey_Response.class,
						
						Get_ScanDataFromScanNumbers_Request.class,
						Get_ScanDataFromScanNumbers_Response.class,
						Get_ScanRetentionTimes_Request.class,
						Get_ScanRetentionTimes_Response.class,
						Get_ScanNumbersFromRetentionTimeRange_Request.class,
						Get_ScanNumbersFromRetentionTimeRange_Response.class,
						Get_ScansDataFromRetentionTimeRange_Request.class,
						Get_ScansDataFromRetentionTimeRange_Response.class,
						
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
	public UploadScanFile_Init_Response call_UploadScanFile_Init_Webservice( UploadScanFile_Init_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_UploadedScanFileInfo_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.UPLOAD_SCAN_FILE_INIT_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof UploadScanFile_Init_Response ) ) {
			String msg = "Response unmarshaled to class other than UploadScanFile_Init_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		UploadScanFile_Init_Response webserviceResponse = null;
		try {
			webserviceResponse = (UploadScanFile_Init_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as UploadScanFile_Init_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}

	/**
	 * @param scanFile
	 * @return
	 * @throws Exception 
	 */
	public UploadScanFile_UploadScanFile_Response call_UploadScanFile_UploadScanFile_Service( UploadScanFile_UploadScanFile_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_UploadScanFile_UploadScanFile_Service(...)" );
		}
		
		File scanFile = webserviceRequest.getScanFile();
		if ( scanFile == null ) {
			throw new IllegalArgumentException( "scanFile property in webserviceRequest param must not be null in call to call_UploadScanFile_UploadScanFile_Service(...)" );
		}
		if ( ! scanFile.exists() ) {
			throw new IllegalArgumentException( "File in scanFile property in webserviceRequest param must exist in call to call_UploadScanFile_UploadScanFile_Service(...)" );
		}
		String uploadScanFileTempKey = webserviceRequest.getUploadScanFileTempKey();
		if ( uploadScanFileTempKey == null || uploadScanFileTempKey.length() == 0 ) {
			throw new IllegalArgumentException( "uploadScanFileTempKey property in webserviceRequest param must not be null or empty in call to call_UploadScanFile_UploadScanFile_Service(...)" );
		}
		
		String scanFilenameSuffix = null;
		
		String scanFilename = scanFile.getName();
		
	    if ( scanFilename.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML ) ) {
	    	
	    	scanFilenameSuffix = WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML;
	    
	    } else if ( scanFilename.endsWith( WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML ) ) {

	    	scanFilenameSuffix = WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML;
	    	
	    } else {
	    	String msg = "Scan Filename must end with '" 
	    			+ WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZML
	    			+ "' or '"
	    			+ WebserviceSpectralStorageScanFileAllowedSuffixesConstants.UPLOAD_SCAN_FILE_ALLOWED_SUFFIX_MZXML
	    			+ "'.";
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setScanFilenameError(true);
			exception.setScanFilenameErrorMessage( msg );
			throw exception;
	    }
		
		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.UPLOAD_SCAN_FILE_UPLOAD_SCAN_FILE_SERVLET_XML
				+ "?"
				+ WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_SERVLET_QUERY_PARAM_SCAN_FILENAME_SUFFIX
				+ "="
				+ scanFilenameSuffix
				+ "&"
				+ WebserviceSpectralStorageQueryParamsConstants.UPLOAD_SCAN_FILE_TEMP_KEY_QUERY_PARAM
				+ "="
				+ uploadScanFileTempKey;
		
		Object webserviceResponseAsObject = 
				callActualWebserviceOnServerSendByteArrayOrFileAsStreamReturnObject(
						null /* bytesToSend */, scanFile, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof UploadScanFile_UploadScanFile_Response ) ) {
			String msg = "Response unmarshaled to class other than UploadScanFile_UploadScanFile_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		UploadScanFile_UploadScanFile_Response webserviceResponse = null;
		try {
			webserviceResponse = (UploadScanFile_UploadScanFile_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as UploadScanFile_UploadScanFile_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
	public UploadScanFile_Submit_Response call_UploadScanFile_Submit_Webservice( UploadScanFile_Submit_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_UploadedScanFileInfo_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.UPLOAD_SCAN_FILE_SUBMIT_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof UploadScanFile_Submit_Response ) ) {
			String msg = "Response unmarshaled to class other than UploadScanFile_Submit_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		UploadScanFile_Submit_Response webserviceResponse = null;
		try {
			webserviceResponse = (UploadScanFile_Submit_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as UploadScanFile_Submit_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
	public Get_UploadedScanFileInfo_Response call_Get_UploadedScanFileInfo_Webservice( Get_UploadedScanFileInfo_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_UploadedScanFileInfo_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.UPLOADED_SCAN_FILE_STATUS_API_KEY_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_UploadedScanFileInfo_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_UploadedScanFileInfo_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_UploadedScanFileInfo_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_UploadedScanFileInfo_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_UploadedScanFileInfo_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
	public UploadScanFile_Delete_For_ScanProcessStatusKey_Response call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice( UploadScanFile_Delete_For_ScanProcessStatusKey_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_UploadScanFile_Delete_For_ScanProcessStatusKey_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.UPLOADED_SCAN_FILE_DELETE_FOR_SCAN_PROCESS_STATUS_KEY_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof UploadScanFile_Delete_For_ScanProcessStatusKey_Response ) ) {
			String msg = "Response unmarshaled to class other than UploadScanFile_Delete_For_ScanProcessStatusKey_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		UploadScanFile_Delete_For_ScanProcessStatusKey_Response webserviceResponse = null;
		try {
			webserviceResponse = (UploadScanFile_Delete_For_ScanProcessStatusKey_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as UploadScanFile_Delete_For_ScanProcessStatusKey_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		return webserviceResponse;
	}
	
	////////////

	/**
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
				+ WebserviceSpectralStoragePathConstants.GET_SCAN_DATA_FROM_SCAN_NUMBERS_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanDataFromScanNumbers_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanDataFromScanNumbers_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanDataFromScanNumbers_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanDataFromScanNumbers_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanDataFromScanNumbers_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
				+ WebserviceSpectralStoragePathConstants.GET_SCAN_RETENTION_TIMES_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanRetentionTimes_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanRetentionTimes_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanRetentionTimes_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanRetentionTimes_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanRetentionTimes_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
				+ WebserviceSpectralStoragePathConstants.GET_SCAN_NUMBERS_FROM_RETENTION_TIME_RANGE_SERVLET_XML;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScanNumbersFromRetentionTimeRange_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScanNumbersFromRetentionTimeRange_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScanNumbersFromRetentionTimeRange_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScanNumbersFromRetentionTimeRange_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScanNumbersFromRetentionTimeRange_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
				+ WebserviceSpectralStoragePathConstants.GET_SCAN_PEAK_INTENSITY_BINNED_RT_MZ_JSON_GZIPPED;
		Object webserviceResponseAsObject = callActualWebserviceOnServerSendObject( webserviceRequest, webserviceURL );
		if ( ! ( webserviceResponseAsObject instanceof Get_ScansDataFromRetentionTimeRange_Response ) ) {
			String msg = "Response unmarshaled to class other than Get_ScansDataFromRetentionTimeRange_Response.  "
					+ " Unmarshaled Class: " + webserviceResponseAsObject.getClass();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
			exception.setFailToDecodeDataReceivedFromServer(true);
			throw exception;
		}
		Get_ScansDataFromRetentionTimeRange_Response webserviceResponse = null;
		try {
			webserviceResponse = (Get_ScansDataFromRetentionTimeRange_Response) webserviceResponseAsObject;
		} catch ( Exception e ) {
			String msg = "Error. Fail to cast response as Get_ScansDataFromRetentionTimeRange_Response: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
	public byte[] call_Get_ScanPeakIntensityBinnedOn_RT_MZ_Webservice( Get_ScanPeakIntensityBinnedOn_RT_MZ_Request webserviceRequest ) throws Exception {
		if ( ! instanceInitialized ) {
			throw new IllegalStateException( "Not initialized" );
		}
		if ( webserviceRequest == null ) {
			throw new IllegalArgumentException( "webserviceRequest param must not be null in call to call_Get_ScansDataFromRetentionTimeRange_Webservice(...)" );
		}

		String webserviceURL = spectralStorageServerBaseURL
				+ WebserviceSpectralStoragePathConstants.GET_SCAN_PEAK_INTENSITY_BINNED_RT_MZ_JSON_GZIPPED;
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
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			@SuppressWarnings("unused")
//			Object unmarshalledObject = unmarshaller.unmarshal( bais );

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg, e );
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
//			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//			@SuppressWarnings("unused")
//			Object unmarshalledObject = unmarshaller.unmarshal( bais );

		} catch ( Exception e ) {
			String msg = "Error. Fail to encode request to send to server: "
					+ e.toString();
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg, e );
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
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			webserviceResponseAsObject = unmarshaller.unmarshal( inputStreamBufferOfServerResponse );
		} catch ( JAXBException e ) {
			YRCSpectralStorageWebserviceCallErrorException wcee = 
					new YRCSpectralStorageWebserviceCallErrorException( "JAXBException unmarshalling XML received from server at URL: " + webserviceURL, e );
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
	 * @throws YRCSpectralStorageWebserviceCallErrorException
	 */
	private byte[] sendToServerSendByteArrayOrFileAsStream_GetByteArrayResponseFromServer(
			ByteArrayOutputStream byteArrayOutputStream_ToSend,
			File fileToSendAsStream, 
			String webserviceURL) throws YRCSpectralStorageWebserviceCallErrorException {
		
		byte[] serverResponseByteArray = null;
		
		if ( ( ! ( byteArrayOutputStream_ToSend != null || fileToSendAsStream != null ) )
				|| (  byteArrayOutputStream_ToSend != null && fileToSendAsStream != null)) {
			String msg = "Exactly one of either byteArrayOutputStream_ToSend or fileToSendAsStream must be not null";
			YRCSpectralStorageWebserviceCallErrorException exception = new YRCSpectralStorageWebserviceCallErrorException( msg );
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
			YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Exception creating URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		//   Open connection to server
		URLConnection urlConnection;
		try {
			urlConnection = urlObject.openConnection();
		} catch (IOException e) {
			YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Exception calling openConnection() on URL object to connect to server.  URL: " + webserviceURL, e );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		// Downcast URLConnection to HttpURLConnection to allow setting of HTTP parameters 
		if ( ! ( urlConnection instanceof HttpURLConnection ) ) {
			YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
			wcee.setServerURLError(true);
			wcee.setWebserviceURL( webserviceURL );
			throw wcee;
		}
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = (HttpURLConnection) urlConnection;
		} catch (Exception e) {
			YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Processing Error: Cannot cast URLConnection to HttpURLConnection" );
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
				YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Exception connecting to server at URL: " + webserviceURL, e );
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
				YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "IOException sending XML to server at URL: " + webserviceURL, e );
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
						YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "IOException closing output Stream to server at URL: " + webserviceURL, e );
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
									YRCSpectralStorageWebserviceCallErrorException wcee = new YRCSpectralStorageWebserviceCallErrorException( "Exception closing output Stream to server at URL: " + webserviceURL, e );
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
					YRCSpectralStorageWebserviceCallErrorException wcee = 
							new YRCSpectralStorageWebserviceCallErrorException( "Unsuccessful HTTP response code of " + httpResponseCode
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
				YRCSpectralStorageWebserviceCallErrorException wcee = 
						new YRCSpectralStorageWebserviceCallErrorException( "IOException getting HTTP response code from server at URL: " + webserviceURL, e );
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
				YRCSpectralStorageWebserviceCallErrorException wcee = 
						new YRCSpectralStorageWebserviceCallErrorException( "IOException receiving XML from server at URL: " + webserviceURL, e );
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
						YRCSpectralStorageWebserviceCallErrorException wcee = 
								new YRCSpectralStorageWebserviceCallErrorException( "IOException closing input Stream from server at URL: " + webserviceURL, e );
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

}
