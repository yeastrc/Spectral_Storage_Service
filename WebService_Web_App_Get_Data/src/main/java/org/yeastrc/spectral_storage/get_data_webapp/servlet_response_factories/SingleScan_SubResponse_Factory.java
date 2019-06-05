package org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScanPeak_SubResponse;
import org.yeastrc.spectral_storage.get_data_webapp.shared_server_client.webservice_request_response.sub_parts.SingleScan_SubResponse;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScanPeak_Common;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Build SingleScan_SubResponse and sub objects
 * 
 * Filter response based on singleScan_SubResponse_Factory_Parameters
 *
 */
public class SingleScan_SubResponse_Factory {

	private static final Logger log = LoggerFactory.getLogger( SingleScan_SubResponse_Factory.class );

	//  private constructor
	private SingleScan_SubResponse_Factory() { }
	
	/**
	 * @return instance
	 */
	public static SingleScan_SubResponse_Factory getInstance() {
		return new SingleScan_SubResponse_Factory();
	}
	
	/**
	 * @param spectralFile_SingleScan_Common
	 * @return
	 */
	public SingleScan_SubResponse buildSingleScan_SubResponse( 
			SpectralFile_SingleScan_Common spectralFile_SingleScan_Common,
			SingleScan_SubResponse_Factory_Parameters singleScan_SubResponse_Factory_Parameters) {
		
		SingleScan_SubResponse singleScan_SubResponse = new SingleScan_SubResponse();
		
		singleScan_SubResponse.setLevel( spectralFile_SingleScan_Common.getLevel() );
		singleScan_SubResponse.setScanNumber( spectralFile_SingleScan_Common.getScanNumber() );
		singleScan_SubResponse.setRetentionTime( spectralFile_SingleScan_Common.getRetentionTime() );
		singleScan_SubResponse.setIsCentroid( spectralFile_SingleScan_Common.getIsCentroid() );
		
		//  Only applicable where level > 1
		
		singleScan_SubResponse.setParentScanNumber( spectralFile_SingleScan_Common.getParentScanNumber() );
		singleScan_SubResponse.setPrecursorCharge( spectralFile_SingleScan_Common.getPrecursorCharge() );
		singleScan_SubResponse.setPrecursor_M_Over_Z( spectralFile_SingleScan_Common.getPrecursor_M_Over_Z() );
		
		//  Copy over peaks
		
		if ( spectralFile_SingleScan_Common.getScanPeaksAsObjectArray() != null ) {	
			
			List<SingleScanPeak_SubResponse> peaks = new ArrayList<>( spectralFile_SingleScan_Common.getScanPeaksAsObjectArray().size() );
			singleScan_SubResponse.setPeaks( peaks );
			
			for ( SpectralFile_SingleScanPeak_Common peakCommon : spectralFile_SingleScan_Common.getScanPeaksAsObjectArray() ) {
				if ( singleScan_SubResponse_Factory_Parameters.getMzLowCutoff() != null
						&& singleScan_SubResponse_Factory_Parameters.getMzLowCutoff() > peakCommon.getM_over_Z() ) {
					continue;  // Skip Peak since MZ below low cutoff
				}
				if ( singleScan_SubResponse_Factory_Parameters.getMzHighCutoff() != null
						&& singleScan_SubResponse_Factory_Parameters.getMzHighCutoff() < peakCommon.getM_over_Z() ) {
					continue;  // Skip Peak since MZ above high cutoff
				}
				SingleScanPeak_SubResponse peak = new SingleScanPeak_SubResponse();
				peak.setMz( peakCommon.getM_over_Z() );
				peak.setIntensity( peakCommon.getIntensity() );
				peaks.add( peak );
			}
		}
		
		return singleScan_SubResponse;
	}
}
