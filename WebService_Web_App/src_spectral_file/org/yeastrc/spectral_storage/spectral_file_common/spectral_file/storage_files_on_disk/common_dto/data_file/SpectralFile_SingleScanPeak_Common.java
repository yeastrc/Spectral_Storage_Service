package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

/**
 * A single peak in the spectral file
 * 
 * Contains properties in all versions
 *
 */
public class SpectralFile_SingleScanPeak_Common {

	private float m_over_Z;
	private float intensity;
	
	public float getM_over_Z() {
		return m_over_Z;
	}
	public void setM_over_Z(float m_over_Z) {
		this.m_over_Z = m_over_Z;
	}
	public float getIntensity() {
		return intensity;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
}
