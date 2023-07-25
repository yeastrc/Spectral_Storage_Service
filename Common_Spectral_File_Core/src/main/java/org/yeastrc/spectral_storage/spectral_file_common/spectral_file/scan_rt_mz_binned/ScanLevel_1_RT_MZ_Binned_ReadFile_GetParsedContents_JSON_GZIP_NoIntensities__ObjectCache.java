package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageCommonCore_InternalError_Exception;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.scan_rt_mz_binned.ScanLevel_1_RT_MZ_Binned_ReadFile_Contents_JSON_GZIP_NoIntensities__ObjectCache.ScanLevel_1_RT_MZ_Binned_ReadFile_Contents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Read and Cache the Contents ScanLevel_1_RT_MZ_Binned_JSON_GZIP_NoIntensities
 *
 * Singleton Object
 */
public class ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache {

	private static final Logger log = LoggerFactory.getLogger(ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache.class);
	

	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 4;
//	private static final int CACHE_MAX_SIZE_SMALL = 10;

	//  Keep in memory always so don't specify a timeout
//	private static final int CACHE_TIMEOUT_FULL_SIZE = 20; // in days
//	private static final int CACHE_TIMEOUT_SMALL = 1; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheGetIndexFileCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache getSingletonInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache() {
		if ( log.isDebugEnabled() ) {
			debugLogLevelEnabled = true;
			log.debug( "debug log level enabled" );
		}

		cacheHolderInternal = new CacheHolderInternal( this );

		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
//		CachedDataCentralRegistry.getInstance().register( this );
	}
	
	private CacheHolderInternal cacheHolderInternal;
	
	
//	@Override
//	public CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
//		return cacheHolderInternal.getCurrentCacheSizeAndMax();
//	}
//	
//
//	@Override
//	public void clearCacheData() throws Exception {
//		clearCache();
//	}

	/**
	 * Recreate the cache using current config values, if they exist, or else defaults
	 * @throws Exception 
	 */
	public void clearCache() throws Exception {
		printPrevCacheHitCounts( true /* forcePrintNow */ );
		cacheHolderInternal.invalidate();
	}
	
	public static class ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result {
		
		private ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;
		private boolean fileNotFound;
		
		public boolean isFileNotFound() {
			return fileNotFound;
		}
		public ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject getScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject() {
			return scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;
		}
	}

	/**
	 * @param hashKey
	 * @return - Result object
	 * @throws Exception
	 */
	public ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result getCacheValue( 
			String hashKey, 
			int rtBinSizeInSeconds,
			int mzBinSizeInMZ) throws Exception {
		
		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		try {
			LoadingCache<CacheKey, CacheValue> cache = cacheHolderInternal.getCache();
			
			CacheKey cacheKey = new CacheKey();
			
			cacheKey.hashKey = hashKey;
			cacheKey.rtBinSizeInSeconds = rtBinSizeInSeconds;
			cacheKey.mzBinSizeInMZ = mzBinSizeInMZ;

			if ( cache != null ) {
				CacheValue cacheValue = cache.get( cacheKey );
				ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result result = new ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result();
				result.scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject = cacheValue.scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;
				result.fileNotFound = cacheValue.fileNotFound;
				return result; // EARLY return
			}

			CacheValue cacheValue = cacheHolderInternal.get_CacheValue( hashKey, rtBinSizeInSeconds, mzBinSizeInMZ );
			ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result result = new ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result();
			result.scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject = cacheValue.scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;
			result.fileNotFound = cacheValue.fileNotFound;
			return result;
			
		} catch ( ExecutionException e ) {
			//  caught from LoadingCache when loadFromDB throws SpectralStorageDataNotFoundException
			if ( e.getCause() instanceof SpectralStorageDataNotFoundException ) {
				//  DB query returned null so return null here
				return null;
			}
			throw e;
		} catch ( SpectralStorageDataNotFoundException e ) {
			//  DB query returned null so return null here
			return null;
		}
	}
	
	/**
	 * Key to Cache
	 * 
	 */
	private static class CacheKey {
		
		String hashKey;
		int rtBinSizeInSeconds;
		int mzBinSizeInMZ;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((hashKey == null) ? 0 : hashKey.hashCode());
			result = prime * result + mzBinSizeInMZ;
			result = prime * result + rtBinSizeInSeconds;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CacheKey other = (CacheKey) obj;
			if (hashKey == null) {
				if (other.hashKey != null)
					return false;
			} else if (!hashKey.equals(other.hashKey))
				return false;
			if (mzBinSizeInMZ != other.mzBinSizeInMZ)
				return false;
			if (rtBinSizeInSeconds != other.rtBinSizeInSeconds)
				return false;
			return true;
		}
		
	}

	/**
	 * Key to Cache
	 * 
	 */
	private static class CacheValue {

		private ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;
		private boolean fileNotFound;
	}


	/**
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache parentObject;
		
		private CacheHolderInternal( ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache parentObject ) {
			this.parentObject = parentObject;
		}
		
		/**
		 * cached data, left null if no caching
		 */
		private SoftReference<LoadingCache<CacheKey, CacheValue>> dataCache_SoftReference = null;
		
		private int cacheMaxSize;

		/**
		 * @return
		 * @throws Exception
		 */
//		public synchronized CacheCurrentSizeMaxSizeResult getCurrentCacheSizeAndMax() throws Exception {
//			CacheCurrentSizeMaxSizeResult result = new CacheCurrentSizeMaxSizeResult();
//			if ( dbRecordsDataCache != null ) {
//				result.setCurrentSize( dbRecordsDataCache.size() );
//				result.setMaxSize( cacheMaxSize );
//			}
//			return result;
//		}
		
		/**
		 * @throws Exception
		 */
		@SuppressWarnings("static-access")
		private synchronized LoadingCache<CacheKey, CacheValue> getCache(  ) throws Exception {
			

			if ( dataCache_SoftReference == null ) {
				
				//  NO Cache
				
				return create_Cache();
			}
			
			LoadingCache<CacheKey, CacheValue> dataCache = dataCache_SoftReference.get();

			if ( dataCache != null ) {
				
				return dataCache;
			}

			//  NO Cache
			
			return create_Cache();
		}
		
		/**
		 * @return
		 * @throws Exception
		 */
		private LoadingCache<CacheKey, CacheValue> create_Cache() throws Exception {
			

			cacheMaxSize = ScanLevel_1_RT_MZ_Binned_ReadFile_GetParsedContents_JSON_GZIP_NoIntensities__ObjectCache.CACHE_MAX_SIZE_FULL_SIZE;

			LoadingCache<CacheKey, CacheValue>dataCache = CacheBuilder.newBuilder()
//						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS ) // always in cache
					.maximumSize( cacheMaxSize )
					.build(
							new CacheLoader<CacheKey, CacheValue>() {
								@Override
								public CacheValue load( CacheKey cacheKey ) throws Exception {

									String hashKey = cacheKey.hashKey;
									int rtBinSizeInSeconds = cacheKey.rtBinSizeInSeconds;
									int mzBinSizeInMZ = cacheKey.mzBinSizeInMZ;

									//   WARNING  cannot return null.  
									//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)

									//  value is NOT in cache so get it and return it
									return get_CacheValue( hashKey, rtBinSizeInSeconds, mzBinSizeInMZ );
								}
							});
			//			    .build(); // no CacheLoader

			dataCache_SoftReference = new SoftReference<>( dataCache );
			
			return dataCache;
		}

		/**
		 * 
		 */
		private synchronized void invalidate() {
			dataCache_SoftReference = null;
		}
		

		/**
		 * @param hashKey
		 * @param rtBinSizeInSeconds
		 * @param mzBinSizeInMZ
		 * @return
		 * @throws Exception
		 */
		private CacheValue get_CacheValue( String hashKey,  
				int rtBinSizeInSeconds,
				int mzBinSizeInMZ ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheGetIndexFileCount.incrementAndGet();
			}
			
			try {

				ScanLevel_1_RT_MZ_Binned_ReadFile_Contents_JSON_GZIP_NoIntensities__ObjectCache__Get_Result scanLevel_1_RT_MZ_Binned_ReadFile_JSON_GZIP_NoIntensities__ObjectCache__Get_Result =
						ScanLevel_1_RT_MZ_Binned_ReadFile_Contents_JSON_GZIP_NoIntensities__ObjectCache.getSingletonInstance().getCacheValue(
								hashKey, 
								rtBinSizeInSeconds,
								mzBinSizeInMZ );
				
				if ( scanLevel_1_RT_MZ_Binned_ReadFile_JSON_GZIP_NoIntensities__ObjectCache__Get_Result.isFileNotFound() ) {
					
					CacheValue cacheValue = new CacheValue();
					cacheValue.fileNotFound = true;
					
					return cacheValue;  // EARLY RETURN
				}
				
				byte[] fileContents = scanLevel_1_RT_MZ_Binned_ReadFile_JSON_GZIP_NoIntensities__ObjectCache__Get_Result.getFileContents();
				
				if ( fileContents == null ) {
					String msg = "invalid data: scanLevel_1_RT_MZ_Binned_ReadFile_JSON_GZIP_NoIntensities__ObjectCache__Get_Result.isFileNotFound() IS false and scanLevel_1_RT_MZ_Binned_ReadFile_JSON_GZIP_NoIntensities__ObjectCache__Get_Result.getFileContents() IS null";
					log.error(msg);
					throw new SpectralStorageCommonCore_InternalError_Exception(msg);
				}

				ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject =
						ScanLevel_1_RT_MZ_Binned_NoIntensities_DataObject.parse_fileContents_ByteArray(fileContents, rtBinSizeInSeconds, mzBinSizeInMZ);

				CacheValue cacheValue = new CacheValue();
				cacheValue.scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject = scanLevel_1_RT_MZ_Binned_NoIntensities_DataObject;

				return cacheValue;

			} catch ( SpectralStorageDataNotFoundException e ) {
				
				CacheValue cacheValue = new CacheValue();
				cacheValue.fileNotFound = true;
				return cacheValue;

			} catch ( Exception e ) {
				
				log.error( "get_CacheValue(...): readFile(...) threw exception for hashKey: " 
						+ hashKey
						+ ", Scan File Storage Location: " 
						+ CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3(), e );
				throw e;
			}
		}
 	}

	/**
	 * 
	 */
	private void printPrevCacheHitCounts( boolean forcePrintNow ) {
		
		Calendar now = Calendar.getInstance();
		
		int nowDayOfYear = now.get( Calendar.DAY_OF_YEAR );
		
		if ( prevDayOfYear != nowDayOfYear || forcePrintNow ) {

			if ( prevDayOfYear != -1 ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) for previous day (or since last cache recreate): 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheGetIndexFileCount.intValue() );
				}
			}
			if ( forcePrintNow ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) since last print: 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheGetIndexFileCount.intValue() );
				}
			}
			
			prevDayOfYear = nowDayOfYear;
			//  Reset cache hit and miss counters
			cacheGetCount.set(0);
			cacheGetIndexFileCount.set(0);
		}
		
	}

	
}
