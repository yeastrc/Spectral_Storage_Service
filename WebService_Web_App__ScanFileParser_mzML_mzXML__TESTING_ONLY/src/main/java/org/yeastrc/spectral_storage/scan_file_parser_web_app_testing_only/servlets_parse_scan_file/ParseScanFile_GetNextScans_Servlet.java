package org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlets_parse_scan_file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress.ScanFile_Parsing_InProgress_Container;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.scan_parsing_in_progress.ScanFile_Parsing_InProgress_Item;
import org.yeastrc.spectral_storage.scan_file_parser_web_app_testing_only.servlet_utils.ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.MzML_MzXmlScan;
import org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto.ScanPeak;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;

/**
 * Parse Scan File: GetNextScans
 *
 */
public class ParseScanFile_GetNextScans_Servlet extends HttpServlet {

	private static final Logger log = LoggerFactory.getLogger(ParseScanFile_GetNextScans_Servlet.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config)
	          throws ServletException {
		
		super.init(config); //  Must call this first

		log.warn( "INFO: init(...) called: ");
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.warn( "INFO: doPost(...) called: ");
		
		try {

			ByteArrayOutputStream outputStreamBufferOfClientRequest = 
					ServletUtil__Read_ServletRequest_Into_ByteArrayOutputStream.read_ServletRequest_Into_ByteArrayOutputStream(request);

			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			Webservice_Request webservice_Request = null;
			try {
				webservice_Request = jacksonJSON_Mapper.readValue( outputStreamBufferOfClientRequest.toByteArray(), Webservice_Request.class );

			} catch ( Exception e ) {
				log.error( "Failed to parse webservice request. ", e );
				throw e;
			}

			log.info( "webservice_Request.spectr_core_version: " + webservice_Request.spectr_core_version );

			log.info( "webservice_Request.scan_filename_with_path: " + webservice_Request.scan_filename_with_path );

			log.info( "webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file );


//			if ( true ) {
//				
//
//				Webservice_Response webservice_Response = new Webservice_Response();
//
//				webservice_Response.isError = true;
//				webservice_Response.errorMessageCode = "GetNextScanFailed";
//				webservice_Response.errorMessageToLog = "GetNextScanFailed: " + webservice_Request.scan_filename_with_path;
//				
//				webservice_Response.errorMessage_ScanFileContentsError_ForEndUser = "Fake Error Message from Parser App On GetNextScan";
//
//				jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
//				
//				return;
//			}
			
			Webservice_Response webservice_Response = new Webservice_Response();

			ScanFile_Parsing_InProgress_Item scanFile_Parsing_InProgress_Item =
					ScanFile_Parsing_InProgress_Container.get_SingletonInstance().getItem(webservice_Request.converter_identifier_for_scan_file);
			
			if ( scanFile_Parsing_InProgress_Item == null) {
				
				webservice_Response.isError = true;
				webservice_Response.errorMessageCode = "converter_identifier_for_scan_file not found";
				webservice_Response.errorMessageToLog = "converter_identifier_for_scan_file not found: " + webservice_Request.converter_identifier_for_scan_file;
				
			} else {

				try {
					
					webservice_Response.scan_batch_number = scanFile_Parsing_InProgress_Item.getNext_scanBatchNumber();
					
					log.warn("INFO::  scan_batch_number assigned and returned by Get Next Scan Batch: " + webservice_Response.scan_batch_number );
					
							
					final int scanReturnCountMax = scanFile_Parsing_InProgress_Item.getScanBatchSizeMaximum();
					
					int scanReadCount = 0;
					

				    List<Webservice_Response_SingleScan> scans_ResultList = new ArrayList<>( scanReturnCountMax );
				    webservice_Response.scans = scans_ResultList;
					
					while ( scanReadCount < scanReturnCountMax ) {
						
						scanReadCount++;
					
						MzML_MzXmlScan mzML_MzXmlScan_Input = scanFile_Parsing_InProgress_Item.getNextScan();
						if ( mzML_MzXmlScan_Input == null ) {
							
							webservice_Response.endOfScans = true;  // End of Scans Reached
							
							break;  // EARLY BREAK LOOP
						}
						
						Webservice_Response_SingleScan scan_Result = new Webservice_Response_SingleScan();
						scans_ResultList.add(scan_Result);
						
						scan_Result.scanLevel = mzML_MzXmlScan_Input.getMsLevel();
						scan_Result.scanNumber  = mzML_MzXmlScan_Input.getScanNumber();
						scan_Result.retentionTime  = mzML_MzXmlScan_Input.getRetentionTime();
						scan_Result.isCentroid  = mzML_MzXmlScan_Input.getIsCentroided() == 0 ? false : true;
						
						if ( scan_Result.scanLevel > 1 ) {
							scan_Result.parentScanNumber  = mzML_MzXmlScan_Input.getPrecursorScanNum();
							scan_Result.precursorCharge = (int) mzML_MzXmlScan_Input.getPrecursorCharge();
							scan_Result.precursor_M_Over_Z  = mzML_MzXmlScan_Input.getPrecursorMz();
						}
						scan_Result.ionInjectionTime  = mzML_MzXmlScan_Input.getIonInjectionTime();

						// Process Scan Peaks

		    			List<ScanPeak> scanPeakList_Input = mzML_MzXmlScan_Input.getScanPeakList();
		    			
		    			List<Webservice_Response_SingleScan_SinglePeak> scanPeakList_Output = new ArrayList<>( scanPeakList_Input.size() );
		    			
		    			for ( ScanPeak scanPeak_Input : scanPeakList_Input ) {
		    				
		    				Webservice_Response_SingleScan_SinglePeak webservice_Response_SingleScan_SinglePeak = new Webservice_Response_SingleScan_SinglePeak();
		    				webservice_Response_SingleScan_SinglePeak.m_over_Z = scanPeak_Input.getMz();
		    				webservice_Response_SingleScan_SinglePeak.intensity = scanPeak_Input.getIntensity();
		    				
		    				scanPeakList_Output.add( webservice_Response_SingleScan_SinglePeak );
		    			}
		    			
		    			scan_Result.scanPeaks = scanPeakList_Output;
		    			

		    			scan_Result.totalIonCurrent = mzML_MzXmlScan_Input.getTotalIonCurrent();  // Copy Float to Float
		    			
					}
					
					log.info( "Read of Scan File Parser: Found and Successful: webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file );
					
				} catch ( Throwable t ) {
					
					log.error( "Read of Scan File Parser: Found but NOT successful: webservice_Request.converter_identifier_for_scan_file: " + webservice_Request.converter_identifier_for_scan_file, t );

					webservice_Response.isError = true;
					webservice_Response.errorMessageCode = "converter_identifier_for_scan_file not found";
					webservice_Response.errorMessageToLog = "converter_identifier_for_scan_file not found: " + webservice_Request.converter_identifier_for_scan_file;
					
					//  Eat Exception
				} finally {
				}
			}
			
			jacksonJSON_Mapper.writeValue( response.getOutputStream(), webservice_Response );
			
		} catch ( Throwable t ) {
			log.error( "Exception in Servlet: ", t );
			throw t;
		}
	}
	
	
	public static class Webservice_Request {
		
		private Integer spectr_core_version;
	    private String scan_filename_with_path;
		private String converter_identifier_for_scan_file;
		   
		public void setSpectr_core_version(Integer spectr_core_version) {
			this.spectr_core_version = spectr_core_version;
		}
		public void setScan_filename_with_path(String scan_filename_with_path) {
			this.scan_filename_with_path = scan_filename_with_path;
		}
		public void setConverter_identifier_for_scan_file(String converter_identifier_for_scan_file) {
			this.converter_identifier_for_scan_file = converter_identifier_for_scan_file;
		}
	}

	public static class Webservice_Response {
		   
		private int scan_batch_number;
	    private boolean endOfScans; //: <boolean>,  -- can be true when return scans
	    private List<Webservice_Response_SingleScan> scans; //: [ <scan> ],   --  <scan> defined below
		private Boolean isError;
		private String errorMessageCode; // : <string>,   -- agreed upon strings like 'filenotfound', 'fileformatincorrect'
		private String errorMessageToLog; // : <string> --  Spectr Core Log Error Message
		private String errorMessage_ScanFileContentsError_ForEndUser;
		
		public Boolean getIsError() {
			return isError;
		}
		public String getErrorMessageCode() {
			return errorMessageCode;
		}
		public String getErrorMessageToLog() {
			return errorMessageToLog;
		}
		public int getScan_batch_number() {
			return scan_batch_number;
		}
		public boolean isEndOfScans() {
			return endOfScans;
		}
		public List<Webservice_Response_SingleScan> getScans() {
			return scans;
		}
		public String getErrorMessage_ScanFileContentsError_ForEndUser() {
			return errorMessage_ScanFileContentsError_ForEndUser;
		}
	}
	

	public static class Webservice_Response_SingleScan {
		
		int scanLevel; // : <number>,
		int scanNumber; // : <number>,
		float retentionTime; // : <number>,  --  Java float
		boolean isCentroid; // : <boolean>,
		Float totalIonCurrent; // : <number>,  --  Java float: null if not on scan
		Float ionInjectionTime; // : <number>,  --  Java float: null if not on scan

		List<Webservice_Response_SingleScan_SinglePeak> scanPeaks; // : [ <scanPeak> ]

		// -- Only applicable where level > 1

		Integer parentScanNumber; // : <number>
		Integer precursorCharge; // : <number>
		Double precursor_M_Over_Z; // : <number> - Java double

		public int getScanLevel() {
			return scanLevel;
		}
		public int getScanNumber() {
			return scanNumber;
		}
		public float getRetentionTime() {
			return retentionTime;
		}
		public boolean isCentroid() {
			return isCentroid;
		}
		public Float getTotalIonCurrent() {
			return totalIonCurrent;
		}
		public Float getIonInjectionTime() {
			return ionInjectionTime;
		}
		public List<Webservice_Response_SingleScan_SinglePeak> getScanPeaks() {
			return scanPeaks;
		}
		public Integer getParentScanNumber() {
			return parentScanNumber;
		}
		public Integer getPrecursorCharge() {
			return precursorCharge;
		}
		public Double getPrecursor_M_Over_Z() {
			return precursor_M_Over_Z;
		}
	}

	public static class Webservice_Response_SingleScan_SinglePeak {

		double m_over_Z; // ,  --  Java double
		float intensity; // ,  --  Java float
		
		public double getM_over_Z() {
			return m_over_Z;
		}
		public float getIntensity() {
			return intensity;
		}
	}
}
