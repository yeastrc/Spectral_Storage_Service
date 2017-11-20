package org.yeastrc.spectral_storage.scan_file_processor.input_scan_file.dto;

public class ScanPeak {

    private float mz;
    private float intensity;
    
	public void setMz(float mz) {
		this.mz = mz;
	}
	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}

	
	public float getMz() {
		return mz;
	}
	public float getIntensity() {
		return intensity;
	}
}
