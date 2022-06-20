package org.yeastrc.spectral_storage.scan_file_processor.process_scan_file;

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
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.exceptions.YRCSpectralStorageGetDataWebserviceCallErrorException;
import org.yeastrc.spectral_storage.scan_file_processor.program.Scan_File_Processor_MainProgram_Params;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class Call_ScanFileParser_HTTP_CommunicationManagement {

	private static final Logger log = LoggerFactory.getLogger( Call_ScanFileParser_HTTP_CommunicationManagement.class );
	
	private static final int SUCCESS_HTTP_RETURN_CODE = 200;
	private static final String CONTENT_TYPE_SEND_RECEIVE = "application/json";
	
	private static final String SCAN_FILE_PARSE_INIT_URI_PATH = "/scan-file-start";

	private static final String SCAN_FILE_PARSE_CLOSE_URI_PATH = "/scan-file-stop-close";

	private static final String SCAN_FILE_PARSE_GET_NEXT_SCANS_URI_PATH = "/scan-file-get-next-scans";
	
	
	private static final Call_ScanFileParser_HTTP_CommunicationManagement instance = new Call_ScanFileParser_HTTP_CommunicationManagement();
	
	/**
	 * private constructor
	 */
	private Call_ScanFileParser_HTTP_CommunicationManagement(){}
	
	
	/**
	 * @return
	 * @throws Exception
	 */
	public static Call_ScanFileParser_HTTP_CommunicationManagement getSingletonInstance( ) throws Exception {
		return instance;
	}
	
	/**
	 * 
	 *
	 */
	public static class Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response {
		
		private String converter_identifier_for_scan_file;
		private String errorMessage_ScanFileContentsError_ForEndUser; // Limelight (Proxl, etc) End User Display text - Shown to end User

		public String getConverter_identifier_for_scan_file() {
			return converter_identifier_for_scan_file;
		}
	}
	
	/**
	 * @param pgmParams
	 * @return
	 * @throws Exception 
	 */
	public Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response initialize_ParsingOf_ScanFile( Scan_File_Processor_MainProgram_Params pgmParams ) throws Exception {

		File scanFile = pgmParams.getInputScanFile();
		
		if ( ! scanFile.exists() ) {
			String msg = "Input scan file does not exist: " + scanFile.getAbsolutePath();
			log.error( msg );
			throw new IllegalArgumentException( msg );
		}
		
		String webserviceURL = pgmParams.getConverterBaseUrlString() + SCAN_FILE_PARSE_INIT_URI_PATH;
		
		WebserviceCall_Request__InitParsing webserviceRequest = new WebserviceCall_Request__InitParsing();
//		webserviceRequest.spectr_core_version = ;
		webserviceRequest.scan_filename_with_path = pgmParams.getInputScanFile().getAbsolutePath();
		webserviceRequest.scan_batch_size_maximum = pgmParams.getScanReadMaxBatchSize();
		
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		byte[] requestBytesToSend = jacksonJSON_Mapper.writeValueAsBytes( webserviceRequest );

		byte[] responseBytes = 
				sendToServerSendByteArray_GetByteArrayResponseFromServer(requestBytesToSend, webserviceURL);
		
		{
			
			
			log.warn(" About to parse response into WebserviceCall_Response__InitParsing:  response Bytes: " + new String(responseBytes, "UTF-8"));
		}
		
		WebserviceCall_Response__InitParsing webserviceResponse = null;
		try {
			webserviceResponse = jacksonJSON_Mapper.readValue( responseBytes, WebserviceCall_Response__InitParsing.class );
		} catch ( Exception e ) {
			log.error( "Failed to parse webservice response. ", e );
			throw e;
		}
		
		if ( webserviceResponse.isError != null && webserviceResponse.isError ) {
			
			if ( StringUtils.isNotEmpty( webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser ) ) {
			

				String msg = "webserviceResponse.isError is true. errorMessageToLog: " + webserviceResponse.errorMessageToLog
						+ "\n webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser: " + webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser;
				log.error( msg );
				
				throw new SpectralStorageDataException( webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser );
			}
			
			String msg = "webserviceResponse.isError is true. errorMessageToLog: " + webserviceResponse.errorMessageToLog;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response response = new Call_ScanFileParser_HTTP_CommunicationManagement__InitializeParsing_Response();
		response.converter_identifier_for_scan_file = webserviceResponse.converter_identifier_for_scan_file;
		
		return response;
	}
	

	private static class WebserviceCall_Request__InitParsing {
		
		private Integer spectr_core_version;
	    private String scan_filename_with_path;
	    private Integer scan_batch_size_maximum;
	    
		@SuppressWarnings("unused")
		public Integer getSpectr_core_version() {
			return spectr_core_version;
		}
		@SuppressWarnings("unused")
		public String getScan_filename_with_path() {
			return scan_filename_with_path;
		}
		@SuppressWarnings("unused")
		public Integer getScan_batch_size_maximum() {
			return scan_batch_size_maximum;
		}
	}
	    
	/**
	 * Webservice response parsed into this class
	 *
	 */
	private static class WebserviceCall_Response__InitParsing {
		

		private String converter_identifier_for_scan_file;
		private Integer spectr_minimum_version_supported; // : <number>,
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser; // Limelight (Proxl, etc) End User Display text - Shown to end User
		
		@SuppressWarnings("unused")
		public void setConverter_identifier_for_scan_file(String converter_identifier_for_scan_file) {
			this.converter_identifier_for_scan_file = converter_identifier_for_scan_file;
		}
		@SuppressWarnings("unused")
		public void setSpectr_minimum_version_supported(Integer spectr_minimum_version_supported) {
			this.spectr_minimum_version_supported = spectr_minimum_version_supported;
		}
		@SuppressWarnings("unused")
		public void setIsError(Boolean isError) {
			this.isError = isError;
		}
		@SuppressWarnings("unused")
		public void setErrorMessageCode(String errorMessageCode) {
			this.errorMessageCode = errorMessageCode;
		}
		@SuppressWarnings("unused")
		public void setErrorMessageToLog(String errorMessageToLog) {
			this.errorMessageToLog = errorMessageToLog;
		}
		@SuppressWarnings("unused")
		public void setErrorMessage_ScanFileContentsError_ForEndUser(String errorMessage_ScanFileContentsError_ForEndUser) {
			this.errorMessage_ScanFileContentsError_ForEndUser = errorMessage_ScanFileContentsError_ForEndUser;
		}
		
	}
	
	//////////////////////////////
	

	/**
	 * 
	 *
	 */
	public static class Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response {
		
	}
	
	/**
	 * @param pgmParams
	 * @return
	 * @throws Exception 
	 */
	public Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response close_ParsingOf_ScanFile( Scan_File_Processor_MainProgram_Params pgmParams, String converter_identifier_for_scan_file ) throws Exception {

		String webserviceURL = pgmParams.getConverterBaseUrlString() + SCAN_FILE_PARSE_CLOSE_URI_PATH;
		
		WebserviceCall_Request__CloseParsing webserviceRequest = new WebserviceCall_Request__CloseParsing();
//		webserviceRequest.spectr_core_version = ;
		webserviceRequest.scan_filename_with_path = pgmParams.getInputScanFile().getAbsolutePath();
		webserviceRequest.converter_identifier_for_scan_file = converter_identifier_for_scan_file;
		
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		byte[] requestBytesToSend = jacksonJSON_Mapper.writeValueAsBytes( webserviceRequest );

		byte[] responseBytes = 
				sendToServerSendByteArray_GetByteArrayResponseFromServer(requestBytesToSend, webserviceURL);
		
		WebserviceCall_Response__CloseParsing webserviceResponse = null;
		try {
			webserviceResponse = jacksonJSON_Mapper.readValue( responseBytes, WebserviceCall_Response__CloseParsing.class );
		} catch ( Exception e ) {
			log.error( "Failed to parse webservice response. ", e );
			throw e;
		}
		
		if ( webserviceResponse.isError != null && webserviceResponse.isError ) {

			if ( StringUtils.isNotEmpty( webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser ) ) {
			

				String msg = "webserviceResponse.isError is true. errorMessageToLog: " + webserviceResponse.errorMessageToLog
						+ "\n webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser: " + webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser;
				log.error( msg );
				
				throw new SpectralStorageDataException( webserviceResponse.errorMessage_ScanFileContentsError_ForEndUser );
			}
			
			String msg = "webserviceResponse.isError is true. errorMessageToLog: " + webserviceResponse.errorMessageToLog;
			log.error( msg );
			throw new SpectralStorageProcessingException(msg);
		}
		
		Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response response = new Call_ScanFileParser_HTTP_CommunicationManagement__CloseParsing_Response();
		
		return response;
	}
	

	private static class WebserviceCall_Request__CloseParsing {

		private Integer spectr_core_version;
	    private String scan_filename_with_path;
		private String converter_identifier_for_scan_file;
		private Integer previous_scan_batch_number;
		
		@SuppressWarnings("unused")
		public Integer getSpectr_core_version() {
			return spectr_core_version;
		}
		@SuppressWarnings("unused")
		public String getScan_filename_with_path() {
			return scan_filename_with_path;
		}
		@SuppressWarnings("unused")
		public String getConverter_identifier_for_scan_file() {
			return converter_identifier_for_scan_file;
		}
		@SuppressWarnings("unused")
		public Integer getPrevious_scan_batch_number() {
			return previous_scan_batch_number;
		}
	}
	    
	/**
	 * Webservice response parsed into this class
	 *
	 */
	private static class WebserviceCall_Response__CloseParsing {

		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser; // Limelight (Proxl, etc) End User Display text - Shown to end User

		@SuppressWarnings("unused")
		public void setIsError(Boolean isError) {
			this.isError = isError;
		}
		@SuppressWarnings("unused")
		public void setErrorMessageCode(String errorMessageCode) {
			this.errorMessageCode = errorMessageCode;
		}
		@SuppressWarnings("unused")
		public void setErrorMessageToLog(String errorMessageToLog) {
			this.errorMessageToLog = errorMessageToLog;
		}
		public void setErrorMessage_ScanFileContentsError_ForEndUser(String errorMessage_ScanFileContentsError_ForEndUser) {
			this.errorMessage_ScanFileContentsError_ForEndUser = errorMessage_ScanFileContentsError_ForEndUser;
		}
		
	}
	

	//////////////////////////////
	

	/**
	 * 
	 *
	 */
	public static class Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response {
		
		private volatile ScanFileParser_ScanBatch_Root scanFileParser_ScanBatch_Root;
		
		private volatile byte[] webservice_ResponseBytes; 

		public ScanFileParser_ScanBatch_Root getScanFileParser_ScanBatch_Root() {
			return scanFileParser_ScanBatch_Root;
		}
		public byte[] getWebservice_ResponseBytes() {
			return webservice_ResponseBytes;
		}
	}
	
	/**
	 * @param pgmParams
	 * @return
	 * @throws Exception 
	 */
	public Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response get_NextScans_ParsingOf_ScanFile( Scan_File_Processor_MainProgram_Params pgmParams, String converter_identifier_for_scan_file ) throws Exception {
		
		String webserviceURL = pgmParams.getConverterBaseUrlString() + SCAN_FILE_PARSE_GET_NEXT_SCANS_URI_PATH;
		
		WebserviceCall_Request__Get_NextScans webserviceRequest = new WebserviceCall_Request__Get_NextScans();
//		webserviceRequest.spectr_core_version = ;
		webserviceRequest.scan_filename_with_path = pgmParams.getInputScanFile().getAbsolutePath();
		webserviceRequest.converter_identifier_for_scan_file = converter_identifier_for_scan_file;
//		webserviceRequest.previous_scan_batch_number = ;
		
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		byte[] requestBytesToSend = jacksonJSON_Mapper.writeValueAsBytes( webserviceRequest );

		byte[] responseBytes = 
				sendToServerSendByteArray_GetByteArrayResponseFromServer(requestBytesToSend, webserviceURL);
		
		Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response response = new Call_ScanFileParser_HTTP_CommunicationManagement__Get_NextScans_Response();

		if ( responseBytes.length > 100000 ) {
			
			//  Large response.  Responses with large batch size can be 10MB
			
			response.webservice_ResponseBytes = responseBytes;
			
		} else {

			//  Small response so parse it here.
			
			ScanFileParser_ScanBatch_Root scanFileParser_ScanBatch_Root = null;
			try {
				scanFileParser_ScanBatch_Root = jacksonJSON_Mapper.readValue( responseBytes, ScanFileParser_ScanBatch_Root.class );
			} catch ( Exception e ) {
				log.error( "Failed to parse webservice response. ", e );
				throw e;
			}

			if ( scanFileParser_ScanBatch_Root.isError != null && scanFileParser_ScanBatch_Root.isError ) {

				if ( StringUtils.isNotEmpty( scanFileParser_ScanBatch_Root.errorMessage_ScanFileContentsError_ForEndUser ) ) {
				

					String msg = "get_NextScans_ParsingOf_ScanFile: webserviceResponse: isError is true. errorMessageToLog: " + scanFileParser_ScanBatch_Root.errorMessageToLog
							+ "\n scanFileParser_ScanBatch_Root.errorMessage_ScanFileContentsError_ForEndUser: " + scanFileParser_ScanBatch_Root.errorMessage_ScanFileContentsError_ForEndUser;
					log.error( msg );
					
					throw new SpectralStorageDataException( scanFileParser_ScanBatch_Root.errorMessage_ScanFileContentsError_ForEndUser );
				}
				
				String msg = "get_NextScans_ParsingOf_ScanFile: webserviceResponse: isError is true. errorMessageToLog: " + scanFileParser_ScanBatch_Root.errorMessageToLog;
				log.error( msg );
				throw new SpectralStorageProcessingException(msg);
			}

			response.scanFileParser_ScanBatch_Root = scanFileParser_ScanBatch_Root;
		}
		
		return response;
	}

	public static class WebserviceCall_Request__Get_NextScans {
		
		private Integer spectr_core_version;
	    private String scan_filename_with_path;
		private String converter_identifier_for_scan_file;
		private Integer previous_scan_batch_number;
		
		public Integer getSpectr_core_version() {
			return spectr_core_version;
		}
		public String getScan_filename_with_path() {
			return scan_filename_with_path;
		}
		public String getConverter_identifier_for_scan_file() {
			return converter_identifier_for_scan_file;
		}
		public Integer getPrevious_scan_batch_number() {
			return previous_scan_batch_number;
		}
	}

	public static class ScanFileParser_ScanBatch_Root {
		   
		private int scan_batch_number;
	    private boolean endOfScans; //: <boolean>,  -- can be true when return scans
	    private List<ScanFileParser_ScanBatch_SingleScan> scans; //: [ <scan> ],   --  <scan> defined below
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser; // Limelight (Proxl, etc) End User Display text - Shown to end User

		public void setScan_batch_number(int scan_batch_number) {
			this.scan_batch_number = scan_batch_number;
		}
		public void setEndOfScans(boolean endOfScans) {
			this.endOfScans = endOfScans;
		}
		public void setScans(List<ScanFileParser_ScanBatch_SingleScan> scans) {
			this.scans = scans;
		}
		public void setIsError(Boolean isError) {
			this.isError = isError;
		}
		public void setErrorMessageCode(String errorMessageCode) {
			this.errorMessageCode = errorMessageCode;
		}
		public void setErrorMessageToLog(String errorMessageToLog) {
			this.errorMessageToLog = errorMessageToLog;
		}
		public int getScan_batch_number() {
			return scan_batch_number;
		}
		public boolean isEndOfScans() {
			return endOfScans;
		}
		public List<ScanFileParser_ScanBatch_SingleScan> getScans() {
			return scans;
		}
		public Boolean getIsError() {
			return isError;
		}
		public String getErrorMessageCode() {
			return errorMessageCode;
		}
		public String getErrorMessageToLog() {
			return errorMessageToLog;
		}
		public String getErrorMessage_ScanFileContentsError_ForEndUser() {
			return errorMessage_ScanFileContentsError_ForEndUser;
		}
		public void setErrorMessage_ScanFileContentsError_ForEndUser(String errorMessage_ScanFileContentsError_ForEndUser) {
			this.errorMessage_ScanFileContentsError_ForEndUser = errorMessage_ScanFileContentsError_ForEndUser;
		}
	}
	

	public static class ScanFileParser_ScanBatch_SingleScan {
		
		int scanLevel; // : <number>,
		int scanNumber; // : <number>,
		float retentionTime; // : <number>,  --  Java float
		boolean isCentroid; // : <boolean>,
		Float totalIonCurrent; // : <number>,  --  Java float: null if not on scan
		Float ionInjectionTime; // : <number>,  --  Java float: null if not on scan

		List<ScanFileParser_ScanBatch_SingleScan_SinglePeak> scanPeaks; // : [ <scanPeak> ]

		// -- Only applicable where level > 1

		Integer parentScanNumber; // : <number>
		Integer precursorCharge; // : <number>
		Double precursor_M_Over_Z; // : <number> - Java double
		
		public int getScanLevel() {
			return scanLevel;
		}
		public void setScanLevel(int scanLevel) {
			this.scanLevel = scanLevel;
		}
		public int getScanNumber() {
			return scanNumber;
		}
		public void setScanNumber(int scanNumber) {
			this.scanNumber = scanNumber;
		}
		public float getRetentionTime() {
			return retentionTime;
		}
		public void setRetentionTime(float retentionTime) {
			this.retentionTime = retentionTime;
		}
		public boolean isCentroid() {
			return isCentroid;
		}
		public void setCentroid(boolean isCentroid) {
			this.isCentroid = isCentroid;
		}
		public Float getTotalIonCurrent() {
			return totalIonCurrent;
		}
		public void setTotalIonCurrent(Float totalIonCurrent) {
			this.totalIonCurrent = totalIonCurrent;
		}
		public Float getIonInjectionTime() {
			return ionInjectionTime;
		}
		public void setIonInjectionTime(Float ionInjectionTime) {
			this.ionInjectionTime = ionInjectionTime;
		}
		public List<ScanFileParser_ScanBatch_SingleScan_SinglePeak> getScanPeaks() {
			return scanPeaks;
		}
		public void setScanPeaks(List<ScanFileParser_ScanBatch_SingleScan_SinglePeak> scanPeaks) {
			this.scanPeaks = scanPeaks;
		}
		public Integer getParentScanNumber() {
			return parentScanNumber;
		}
		public void setParentScanNumber(Integer parentScanNumber) {
			this.parentScanNumber = parentScanNumber;
		}
		public Integer getPrecursorCharge() {
			return precursorCharge;
		}
		public void setPrecursorCharge(Integer precursorCharge) {
			this.precursorCharge = precursorCharge;
		}
		public Double getPrecursor_M_Over_Z() {
			return precursor_M_Over_Z;
		}
		public void setPrecursor_M_Over_Z(Double precursor_M_Over_Z) {
			this.precursor_M_Over_Z = precursor_M_Over_Z;
		}

	}

	public static class ScanFileParser_ScanBatch_SingleScan_SinglePeak {

		double m_over_Z; // ,  --  Java double
		float intensity; // ,  --  Java float
		
		public double getM_over_Z() {
			return m_over_Z;
		}
		public float getIntensity() {
			return intensity;
		}
		public void setIntensity(float intensity) {
			this.intensity = intensity;
		}
	}
	
	
	//////////////////////////////
	//////////////////////////////
	//////////////////////////////

	//  Common Call webservice code
	
	/**
	 * @param byteArrayOutputStream_ToSend
	 * @param webserviceURL
	 * @return
	 * @throws YRCSpectralStorageGetDataWebserviceCallErrorException
	 */
	private byte[] sendToServerSendByteArray_GetByteArrayResponseFromServer(
			byte[] requestBytesToSend,
			String webserviceURL) throws YRCSpectralStorageGetDataWebserviceCallErrorException {
		
		byte[] serverResponseByteArray = null;
		
		//  Get number of bytes to send to specify in httpURLConnection.setFixedLengthStreamingMode(...)
		//  (This causes httpURLConnection to not buffer the sent data to get the length,
		//   allowing > 2GB to be sent and also no memory is needed for the buffering)
		long numberOfBytesToSend = requestBytesToSend.length;
		
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
				//  Send bytes to server
				outputStream.write(requestBytesToSend);

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
	
	
	
}
