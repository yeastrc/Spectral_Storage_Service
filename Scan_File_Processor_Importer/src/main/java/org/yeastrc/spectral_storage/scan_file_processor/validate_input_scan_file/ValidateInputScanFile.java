package org.yeastrc.spectral_storage.scan_file_processor.validate_input_scan_file;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.constants_enums.IonInjectionTime_NotAvailable_OnDiskValue_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file.SpectralFile_SingleScan_Common;

/**
 * Validate Scan file while processing it.
 * 
 * Return Object of class ValidateInputScanFile_Result when file is processed.  It contains values that are found and also validated.
 *
 */
public class ValidateInputScanFile {

	private static final Logger log = LoggerFactory.getLogger(ValidateInputScanFile.class);
	/**
	 * private constructor
	 */
	private ValidateInputScanFile(){}
	public static ValidateInputScanFile getInstance( ) throws Exception {
		ValidateInputScanFile instance = new ValidateInputScanFile();
		return instance;
	}

	public static class ValidateInputScanFile_Result {

		boolean noScans_InScanFile_Have_TotalIonCurrent_Populated;
		boolean noScans_InScanFile_Have_IonInjectionTime_Populated;

		public boolean isNoScans_InScanFile_Have_TotalIonCurrent_Populated() {
			return noScans_InScanFile_Have_TotalIonCurrent_Populated;
		}
		public void setNoScans_InScanFile_Have_TotalIonCurrent_Populated(
				boolean noScans_InScanFile_Have_TotalIonCurrent_Populated) {
			this.noScans_InScanFile_Have_TotalIonCurrent_Populated = noScans_InScanFile_Have_TotalIonCurrent_Populated;
		}
		public boolean isNoScans_InScanFile_Have_IonInjectionTime_Populated() {
			return noScans_InScanFile_Have_IonInjectionTime_Populated;
		}
		public void setNoScans_InScanFile_Have_IonInjectionTime_Populated(
				boolean noScans_InScanFile_Have_IonInjectionTime_Populated) {
			this.noScans_InScanFile_Have_IonInjectionTime_Populated = noScans_InScanFile_Have_IonInjectionTime_Populated;
		}

	}

	//  Ensure no scan number is duplicated

	Set<Integer> scanNumbersInFile = new HashSet<>();

	boolean foundScanWith_IonInjectionTime_YES = false;
	boolean foundScanWith_IonInjectionTime_NO = false;

	boolean foundScanWith_TotalIonCurrent_YES = false;
	boolean foundScanWith_TotalIonCurrent_NO = false;

	/**
	 * Validate Scan file before processing it.
	 * 
	 * For now, just read the whole file using the scan file reader
	 * @param scanFile
	 * @throws Exception
	 */
	public void validate_SingleScan( SpectralFile_SingleScan_Common spectralFile_SingleScan ) throws Exception, SpectralStorageDataException {

		//  Ensure no scan number is duplicated

		int scanNumber = spectralFile_SingleScan.getScanNumber();

		if ( ( ! scanNumbersInFile.add( scanNumber ) ) ) {
			String msg = "Error in Scan File: Duplicate scan number in file: " + scanNumber;
			log.error( msg );
			throw new SpectralStorageDataException( msg );
		}

		if ( spectralFile_SingleScan.getIonInjectionTime() != null ) {

			foundScanWith_IonInjectionTime_YES = true;

			if ( foundScanWith_IonInjectionTime_NO ) {
				String msg = "Error in Scan File: At least 1 scan found with IonInjectionTime populated and at least 1 scan found with IonInjectionTime NOT populated ";
				log.error( msg );
				throw new SpectralStorageDataException( msg );
			}

			if ( spectralFile_SingleScan.getIonInjectionTime() == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE ) {
				String msg = "Error in Scan File: IonInjectionTime == IonInjectionTime_NotAvailable_OnDiskValue_Constants.ION_INJECTION_TIME_NOT_AVAILABLE_ON_DISK_VALUE == Float.NEGATIVE_INFINITY. scanNumber: " + scanNumber;
				log.error( msg );
				throw new SpectralStorageDataException( msg );
			}

		} else {

			foundScanWith_IonInjectionTime_NO = true;

			if ( foundScanWith_IonInjectionTime_YES ) {
				String msg = "Error in Scan File: At least 1 scan found with IonInjectionTime populated and at least 1 scan found with IonInjectionTime NOT populated ";
				log.error( msg );
				throw new SpectralStorageDataException( msg );
			}
		}

		if ( spectralFile_SingleScan.getTotalIonCurrent() != null ) {

			foundScanWith_TotalIonCurrent_YES = true;

			if ( foundScanWith_TotalIonCurrent_NO ) {
				String msg = "Error in Scan File: At least 1 scan found with TotalIonCurrent populated and at least 1 scan found with TotalIonCurrent NOT populated ";
				log.error( msg );
				throw new SpectralStorageDataException( msg );
			}

		} else {

			foundScanWith_TotalIonCurrent_NO = true;

			if ( foundScanWith_TotalIonCurrent_YES ) {
				String msg = "Error in Scan File: At least 1 scan found with TotalIonCurrent populated and at least 1 scan found with TotalIonCurrent NOT populated ";
				log.error( msg );
				throw new SpectralStorageDataException( msg );
			}
		}
	}
	
	
	/**
	 * @return
	 */
	public ValidateInputScanFile_Result get_ValidateInputScanFile_Result() {
		
		ValidateInputScanFile_Result validateResult = new ValidateInputScanFile_Result();

		validateResult.noScans_InScanFile_Have_TotalIonCurrent_Populated = foundScanWith_TotalIonCurrent_NO;
		validateResult.noScans_InScanFile_Have_IonInjectionTime_Populated = foundScanWith_IonInjectionTime_NO;

		return validateResult;
	}
}
