package org.yeastrc.spectral_storage.web_app.servlet_response_factories;

/**
 * Parameters to SingleScan_SubResponse_Factory
 *
 */
public class SingleScan_SubResponse_Factory_Parameters {

	/**
	 * If populated, do not return any peaks with mz below this cutoff.  
	 */
	private Float mzLowCutoff;

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 */
	private Float mzHighCutoff;

	public Float getMzLowCutoff() {
		return mzLowCutoff;
	}

	public void setMzLowCutoff(Float mzLowCutoff) {
		this.mzLowCutoff = mzLowCutoff;
	}

	public Float getMzHighCutoff() {
		return mzHighCutoff;
	}

	public void setMzHighCutoff(Float mzHighCutoff) {
		this.mzHighCutoff = mzHighCutoff;
	}
}
