package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_003.index_file.reader_writer.SpectralFile_Index_File_Reader_V_003;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache the Contents objects from the Index File Reader
 *
 * Singleton Object
 */
public class IndexFileRootDataObjectCache {

	private static final Logger log = Logger.getLogger(IndexFileRootDataObjectCache.class);
	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 100;
//	private static final int CACHE_MAX_SIZE_SMALL = 10;

	//  Keep in memory always
//	private static final int CACHE_TIMEOUT_FULL_SIZE = 20; // in days
//	private static final int CACHE_TIMEOUT_SMALL = 1; // in days


	private static final AtomicLong cacheGetCount = new AtomicLong();
	private static final AtomicLong cacheDBRetrievalCount = new AtomicLong();
	
	private static volatile int prevDayOfYear = -1;

	private static boolean debugLogLevelEnabled = false;
	
	/**
	 * Static singleton instance
	 */
	private static IndexFileRootDataObjectCache _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized IndexFileRootDataObjectCache getInstance() throws Exception {

		if ( _instance == null ) {
			_instance = new IndexFileRootDataObjectCache();
		}
		return _instance; 
	}
	
	/**
	 * constructor
	 */
	private IndexFileRootDataObjectCache() {
		if ( log.isDebugEnabled() ) {
			debugLogLevelEnabled = true;
			log.debug( "debug log level enabled" );
		}

		cacheHolderInternal = new CacheHolderInternal( this );

		//  Register this class with the centralized Cached Data Registry, to support centralized cache clearing
//		CachedDataCentralRegistry.getInstance().register( this );
	}
	
	private CacheHolderInternal cacheHolderInternal;
	
	private File subDirForStorageFiles;
	
	private boolean initialized;
	
	
	/**
	 * Must call before using
	 * 
	 * @param subDirForStorageFiles
	 */
	public void init( File subDirForStorageFiles ) {
		
		this.subDirForStorageFiles = subDirForStorageFiles;
		
		initialized = true;
	}

	
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

	/**
	 * @param hashKey
	 * @return - retrieved from DB or null if not found
	 * @throws Exception
	 */
	public SpectralFile_Index_FileContents_Root_IF getSpectralFile_Index_FileContents_Root_IF( 
			String hashKey ) throws Exception {
		
		if ( ! initialized ) {
			String msg = "In getSpectralFile_Index_FileContents_Root_IF(): init() not yet called";
			log.error(msg);
			throw new IllegalStateException(msg);
		}

		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		try {
			LoadingCache<String, SpectralFile_Index_FileContents_Root_IF> cache = cacheHolderInternal.getCache();

			if ( cache != null ) {
				SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF = cache.get( hashKey );
				return spectralFile_Index_FileContents_Root_IF; // EARLY return
			}

			SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF = cacheHolderInternal.loadFromDB( hashKey );
			return spectralFile_Index_FileContents_Root_IF;
			
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
	 * Class to hold and create the cache object
	 *
	 */
	private static class CacheHolderInternal {

		private IndexFileRootDataObjectCache parentObject;
		
		private CacheHolderInternal( IndexFileRootDataObjectCache parentObject ) {
			this.parentObject = parentObject;
		}
		
		private boolean cacheDataInitialized;
		
		/**
		 * cached data, left null if no caching
		 */
		private LoadingCache<String, SpectralFile_Index_FileContents_Root_IF> dbRecordsDataCache = null;
		
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
		private synchronized LoadingCache<String, SpectralFile_Index_FileContents_Root_IF> getCache(  ) throws Exception {
			if ( ! cacheDataInitialized ) { 
//				CachedDataSizeOptions cachedDataSizeOptions = 
//						CachedDataCentralConfigStorageAndProcessing.getInstance().getCurrentSizeConfigValue();
				
//				if ( cachedDataSizeOptions == CachedDataSizeOptions.FEW ) {
//					//  No Cache, just mark initialized, dbRecordsDataCache already set to null;
//					cacheDataInitialized = true;
//					return dbRecordsDataCache;  //  EARLY RETURN
//				}
				
//				int cacheTimeout = CACHE_TIMEOUT_FULL_SIZE;
				cacheMaxSize = parentObject.CACHE_MAX_SIZE_FULL_SIZE;
//				if ( cachedDataSizeOptions == CachedDataSizeOptions.HALF ) {
////					cacheMaxSize = cacheMaxSize / 2;
//				} else if ( cachedDataSizeOptions == CachedDataSizeOptions.SMALL ) {
////					cacheMaxSize = parentObject.CACHE_MAX_SIZE_SMALL;
////					cacheTimeout = CACHE_TIMEOUT_SMALL;
//				}
				
				dbRecordsDataCache = CacheBuilder.newBuilder()
//						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS ) // always in cache
						.maximumSize( cacheMaxSize )
						.build(
								new CacheLoader<String, SpectralFile_Index_FileContents_Root_IF>() {
									public SpectralFile_Index_FileContents_Root_IF load( String hashKey ) throws Exception {
										
										//   WARNING  cannot return null.  
										//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
										
										//  value is NOT in cache so get it and return it
										return loadFromDB( hashKey );
									}
								});
			//			    .build(); // no CacheLoader
				cacheDataInitialized = true;
			}
			return dbRecordsDataCache;
		}

		private synchronized void invalidate() {
			dbRecordsDataCache = null;
			cacheDataInitialized = false;
		}
		

		/**
		 * @param linkerId
		 * @return
		 * @throws Exception
		 */
		private SpectralFile_Index_FileContents_Root_IF loadFromDB( String hashKey ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheDBRetrievalCount.incrementAndGet();
			}
			
			try {

				SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF =
						SpectralFile_Index_File_Reader_V_003.getInstance()
						.readIndexFile( hashKey, parentObject.subDirForStorageFiles );

				if ( spectralFile_Index_FileContents_Root_IF == null ) {
					// Throw this exception since cannot return null to Cache
					throw new SpectralStorageDataNotFoundException();
				}
				return spectralFile_Index_FileContents_Root_IF;

			} catch ( Exception e ) {
				
				log.error( "loadFromDB(...): readIndexFile(...) threw exception for hashKey: " + hashKey
						+ ", parentObject.subDirForStorageFiles: " + parentObject.subDirForStorageFiles, e );
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
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			if ( forcePrintNow ) {
				if ( debugLogLevelEnabled ) {
					log.debug( "Cache total gets and db loads(misses) since last print: 'total gets': " + cacheGetCount.intValue() 
					+ ", misses: " + cacheDBRetrievalCount.intValue() );
				}
			}
			
			prevDayOfYear = nowDayOfYear;
			//  Reset cache hit and miss counters
			cacheGetCount.set(0);
			cacheDBRetrievalCount.set(0);
		}
		
	}

	
}
