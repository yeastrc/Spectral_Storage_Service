package org.yeastrc.spectral_storage.get_data_webapp.webservice_connect.json_helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.yeastrc.spectral_storage.shared_server_client_importer.accum_scan_rt_mz_binned.dto.MS1_IntensitiesBinnedSummedMapRoot;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A helper class for deserializing JSON
 * 
 * Relies on Jackson for JSON parsing
 *
 * !!!!!!!  Requires Jackson Jars
 */
public class CallSpectralStorageGetDataWebservice_JSON_Parse_Helper {

	//  private constructor
	private CallSpectralStorageGetDataWebservice_JSON_Parse_Helper() { }
	/**
	 * @return newly created instance
	 */
	public static CallSpectralStorageGetDataWebservice_JSON_Parse_Helper getInstance() { 
		return new CallSpectralStorageGetDataWebservice_JSON_Parse_Helper(); 
	}
	
	/**
	 * @param serverResponseBytes
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public MS1_IntensitiesBinnedSummedMapRoot deserialize_unGzip_Get_ScanPeakIntensityBinnedOn_RT_MZ_Response( 
			byte[] serverResponseBytes ) throws JsonParseException, JsonMappingException, IOException {

		//  Deserialize Server response

		MS1_IntensitiesBinnedSummedMapRoot summedDataRoot = null;
		
		//  Jackson JSON Mapper object for JSON deserialization and serialization
		ObjectMapper jacksonJSON_Mapper = new ObjectMapper();  //  Jackson JSON library object
		
		ByteArrayInputStream bais = new ByteArrayInputStream( serverResponseBytes );
		
		try ( InputStream is = new GZIPInputStream( bais ) ) {
			summedDataRoot = 
					jacksonJSON_Mapper.readValue( is, MS1_IntensitiesBinnedSummedMapRoot.class );
		}
		
		return summedDataRoot;
	}
}
