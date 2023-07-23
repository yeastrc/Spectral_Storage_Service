package org.yeastrc.spectral_storage.get_data_webapp.servlet_response_factories;

import java.util.List;

/**
 * Parameters to SingleScan_SubResponse_Factory
 *
 */
public class SingleScan_SubResponse_Factory_Parameters {

	/**
	 * If populated, do not return any peaks with mz below this cutoff.
	 * 
	 * This cutoff is applied in Addition To the filters in property 'm_Over_Z_Range_Filters'
	 */
	private Double mzLowCutoff;

	/**
	 * If populated, do not return any peaks with mz above this cutoff.  
	 * 
	 * This cutoff is applied in Addition To the filters in property 'm_Over_Z_Range_Filters'
	 */
	private Double mzHighCutoff;
	
	/**
	 * Each m/z range is OR with each other
	 */
	private List<SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range> m_Over_Z_Range_Filters;
	
	
	///

	
	/**
	 * Sub Part for class SingleScan_SubResponse_Factory_Parameters
	 * 
	 * A Single m/z range to filter the returned scan peaks
	 * 
	 * Each m/z range is OR with each other and with the m/z range in the main request
	 *
	 */
	public static class SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range {

		/**
		 * A Single m/z range to filter the returned scan peaks
		 * 
		 * Each m/z range is OR with each other and with the m/z range in the main request
		 */
		private Double mzLowCutoff;

		/**
		 * A Single m/z range to filter the returned scan peaks
		 * 
		 * Each m/z range is OR with each other and with the m/z range in the main request
		 */
		private Double mzHighCutoff;
		
		

		public Double getMzLowCutoff() {
			return mzLowCutoff;
		}

		public void setMzLowCutoff(Double mzLowCutoff) {
			this.mzLowCutoff = mzLowCutoff;
		}

		public Double getMzHighCutoff() {
			return mzHighCutoff;
		}

		public void setMzHighCutoff(Double mzHighCutoff) {
			this.mzHighCutoff = mzHighCutoff;
		}
	}

	public Double getMzLowCutoff() {
		return mzLowCutoff;
	}

	public void setMzLowCutoff(Double mzLowCutoff) {
		this.mzLowCutoff = mzLowCutoff;
	}

	public Double getMzHighCutoff() {
		return mzHighCutoff;
	}

	public void setMzHighCutoff(Double mzHighCutoff) {
		this.mzHighCutoff = mzHighCutoff;
	}


	public List<SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range> getM_Over_Z_Range_Filters() {
		return m_Over_Z_Range_Filters;
	}

	public void setM_Over_Z_Range_Filters(
			List<SingleScan_SubResponse_Factory_Parameters__M_Over_Z_Range> m_Over_Z_Range_Filters) {
		this.m_Over_Z_Range_Filters = m_Over_Z_Range_Filters;
	}
}
