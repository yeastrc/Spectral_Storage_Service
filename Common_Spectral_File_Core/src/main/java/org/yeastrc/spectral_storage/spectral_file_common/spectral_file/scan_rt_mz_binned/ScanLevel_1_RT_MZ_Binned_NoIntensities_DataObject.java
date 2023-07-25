package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageCommonCore_InternalError_Exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Data Object representation of file <hashkey>.intbin_json_gz_no_intensities_1_1  -  ONLY accepts bin size 1, 1
 *
 */
public class ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject {

	private static final Logger log = LoggerFactory.getLogger(ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject.class);
	
	/**
	 * Package Private method
	 * 
	 * Main Parsing in nested class ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer at bottom of this class
	 * 
	 * @param fileContents - The actual contents read from the file - GZIP JSON
	 * @param rtBinSizeInSeconds - Validated to be '1' since that is all that is supported
	 * @param mzBinSizeInMZ      - Validated to be '1' since that is all that is supported
	 * @return
	 * @throws Exception 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	static ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject parse_fileContents_ByteArray( 
			byte[] fileContents, 
			int rtBinSizeInSeconds,
			int mzBinSizeInMZ) throws Exception {
		

		if ( rtBinSizeInSeconds != 1 || mzBinSizeInMZ != 1 ) {
			String msg = "( rtBinSizeInSeconds != 1 || mzBinSizeInMZ != 1 ).  ONLY value accepted is 1 since hard coded expecting that.";
			log.error(msg);
			throw new IllegalArgumentException(msg);
		}

		if ( true ) {
			String msg = "FORCE Exception:  Coding is NOT Complete.  Returned Object is NOT yet Populated";
			log.error(msg);

        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
		}
		
		ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject resultObject = null; // new ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject();


		try ( GZIPInputStream gis = new GZIPInputStream( new ByteArrayInputStream( fileContents ) ) ) {


			//  Jackson JSON Mapper object for JSON deserialization and serialization
			ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object

			
			//  Main Parsing in nested class ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer at bottom of this class
			
			
			SimpleModule module = new SimpleModule();
			module.addDeserializer(ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject.class, new ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer());
			jacksonJSON_Mapper.registerModule(module);

			resultObject = jacksonJSON_Mapper.readValue( gis , ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject.class );
			
			
			//  Optional way to get a reader ahead of time.  No Java Generics protection on returned object
//			ObjectReader jacksonJSON_ObjectReader = jacksonJSON_Mapper.reader().forType(ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject.class);
//			
//			resultObject = jacksonJSON_ObjectReader.readValue( gis );

		}

		//  Optional Alternative solution to read the file as ASCII and parse it here
		//				
		//	
		//		 //  Read as ASCII file inside GZIP file
		//		 
		//		 try ( InputStreamReader inputStreamReader = new InputStreamReader( new GZIPInputStream( new ByteArrayInputStream( fileContents ) ) , StandardCharsets.US_ASCII ) ) {
		//			 
		//			 char[] readIntoArray = new char[ 2000 ];
		//			 
		//			 int charsRead = inputStreamReader.read(readIntoArray);
		//			 
		//				int offset = 0;
		//				int bytesRead = 0;
		//				while ( ( bytesRead = dataInputStream.read( hash, offset, hashLength - bytesRead ) ) != -1 ) {
		//					offset += bytesRead;
		//					if ( offset >= hashLength ) {
		//						break;
		//					}
		//				}
		//			 
		//			 sss
		//			 
		//		 }
		//

		return resultObject;
	}
	
	////////////////////
	
	
	/**
	 * Jackson JSON Deserializer class
	 *
	 */
	private static class ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer extends StdDeserializer<ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject> { 

		private static final long serialVersionUID = 1L;

		public ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer() { 
	        this(null); 
	    } 

	    public ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject__Deserializer(Class<?> vc) { 
	        super(vc); 
	    }
	    
	    private enum Prev_TopLevelElementType {
	    	NUMBER, ARRAY
	    }

	    @Override
	    public ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
	    	
	    	ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject resultObject = new ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject();
	    	
	        ObjectCodec codec = jsonParser.getCodec();
	        
	        //  Parses the JSON
	        JsonNode node_TopLevel = codec.readTree(jsonParser);
	        
	        if ( ! node_TopLevel.isArray() ) {
	        	//  Invalid data.  MUST have top level array
	        	
	        	String msg = "Invalid data.  MUST have top level array";
	        	log.error(msg);
	        	
	        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
	        }
	        
	        final long _PREV_VALUE_IS_NOT_SET_VALUE = Long.MIN_VALUE;
	        
	        long prev_RetentionTime = _PREV_VALUE_IS_NOT_SET_VALUE;
	        
	        Prev_TopLevelElementType prev_TopLevelElementType = null;


	        Iterator<JsonNode> iterator_TopLevelNode = node_TopLevel.iterator();

	        while ( iterator_TopLevelNode.hasNext() ) {

	        	JsonNode nodeSubNode_1 = iterator_TopLevelNode.next();

	        	if ( nodeSubNode_1.isIntegralNumber() ) {
	        		
	        		//  Top Level Array Element is a Number for the Retention Time (first entry) or Offset from Previous Retention Time (following entries)
	        		
	        		if ( prev_TopLevelElementType != null && prev_TopLevelElementType != Prev_TopLevelElementType.ARRAY ) {

	    	        	//  Invalid data.  Top level array: Current Element is number so previous element must be Array or this is first array element. 
	    	        	
	    	        	String msg = "Invalid data.  Top level array: Current Element is number so previous element must be Array or this is first array element.";
	    	        	log.error(msg);
	    	        	
	    	        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
	        		}
	        		
	        		prev_TopLevelElementType = Prev_TopLevelElementType.NUMBER;
	        		
	        		Number number = nodeSubNode_1.numberValue();
	        		
	        		long retentionTime_ValueOrOffset = number.longValue();
	        		
	        		long retentionTime;
	        		
	        		if ( prev_RetentionTime != _PREV_VALUE_IS_NOT_SET_VALUE ) {
	        			//  NOT First value.  Add previous value since this is offset.
	        			
	        			retentionTime = prev_RetentionTime + retentionTime_ValueOrOffset;
	        		} else {
	        			//  YES First value.

	        			retentionTime = retentionTime_ValueOrOffset;
	        		}
	        		
	        		prev_RetentionTime = retentionTime;
	        	
	        	} else if ( nodeSubNode_1.isArray() ) {

	        		if ( prev_TopLevelElementType != Prev_TopLevelElementType.NUMBER ) {

	    	        	//  Invalid data.  Top level array: Current Element is Array so previous element must be Number. 
	    	        	
	    	        	String msg = "Invalid data.  Top level array:  Current Element is Array so previous element must be Number.";
	    	        	log.error(msg);
	    	        	
	    	        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
	        		}
	        		
	        		prev_TopLevelElementType = Prev_TopLevelElementType.ARRAY;
	        		
	    	        long prev_M_Over_Z = _PREV_VALUE_IS_NOT_SET_VALUE;

	        		Iterator<JsonNode> iterator_nodeSubNode_1nodeSubNode_1 = nodeSubNode_1.iterator();

	        		while ( iterator_nodeSubNode_1nodeSubNode_1.hasNext() ) {

	        			JsonNode nodeSubNode_2 = iterator_nodeSubNode_1nodeSubNode_1.next();

	        			if ( nodeSubNode_2.isIntegralNumber() ) {
	    	        		
	    	        		//  Second Level Array Element is a Number for the M/Z (first entry) or Offset from Previous M/Z (following entries)

	        				Number number = nodeSubNode_2.numberValue();
	        				
	        				long m_Over_Z_ValueOrOffset = number.longValue();
	    	        		
	    	        		long m_Over_Z;
	    	        		
	    	        		if ( prev_M_Over_Z != _PREV_VALUE_IS_NOT_SET_VALUE ) {
	    	        			//  NOT First value.  Add previous value since this is offset.
	    	        			
	    	        			m_Over_Z = prev_M_Over_Z + m_Over_Z_ValueOrOffset;
	    	        			
	    	        		} else {
	    	        			//  YES First value.
	    	        			
		    	        		m_Over_Z = m_Over_Z_ValueOrOffset;
	    	        		}
	    	        		
	    	        		prev_M_Over_Z = m_Over_Z;
	    	        		
	        			} else {

	    	             	//  Invalid data.  Second level array elements MUST be a number 
	    		        	
	    		        	String msg = "Invalid data.  Second level array elements MUST be a number";
	    		        	log.error(msg);
	    		        	
	    		        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
	        				
	        			}

	        		}
	        	} else {
	             	//  Invalid data.  Top level array elements MUST be a number or an array
		        	
		        	String msg = "Invalid data.  Top level array elements MUST be a number or an array";
		        	log.error(msg);
		        	
		        	throw new SpectralStorageCommonCore_InternalError_Exception(msg);
	        	}
	        	

	        }

	        ///  TODO  REmove throw when have working to populate the object
	        if ( true )
	        	throw new SpectralStorageCommonCore_InternalError_Exception("FORCE FAKE");
        	
	        return resultObject;
	    }

	}
}
