package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.index_file_root_data_object_cache;

import java.lang.ref.SoftReference;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.LoggerFactory;  import org.slf4j.Logger;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageDataNotFoundException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.exceptions.SpectralStorageProcessingException;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.index_file.SpectralFile_Index_FileContents_Root_IF;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_reader_file_and_s3.CommonReader_File_And_S3_Holder;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.StorageFile_Version_004_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_004.index_file.reader_writer.SpectralFile_Index_File_Reader_V_004;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.StorageFile_Version_005_Constants;
import org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.version_005.index_file.reader_writer.SpectralFile_Index_File_Reader_V_005;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Cache the Contents objects from the Index File Reader
 *
 * Singleton Object
 */
public class IndexFileRootDataObjectCache {

	private static final Logger log = LoggerFactory.getLogger(IndexFileRootDataObjectCache.class);
	
	private static final int CACHE_MAX_SIZE_FULL_SIZE = 100;
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
	private static IndexFileRootDataObjectCache _instance = null; //  Delay creating until first getInstance() call

	/**
	 * Static get singleton instance
	 * @return
	 * @throws Exception 
	 */
	public static synchronized IndexFileRootDataObjectCache getSingletonInstance() throws Exception {

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
			String hashKey, int indexVersion ) throws Exception {
		
		printPrevCacheHitCounts( false /* forcePrintNow */ );
		
		if ( debugLogLevelEnabled ) {
			cacheGetCount.incrementAndGet();
		}

		try {
			LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF> cache = cacheHolderInternal.getCache();
			
			CacheKey cacheKey = new CacheKey();
			
			cacheKey.hashKey = hashKey;
			cacheKey.indexVersion = indexVersion;

			if ( cache != null ) {
				SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF = cache.get( cacheKey );
				return spectralFile_Index_FileContents_Root_IF; // EARLY return
			}

			SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF = cacheHolderInternal.loadIndexFile( hashKey, indexVersion );
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
	 * Key to Cache
	 * 
	 */
	private static class CacheKey {
		
		String hashKey;
		int indexVersion;
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((hashKey == null) ? 0 : hashKey.hashCode());
			result = prime * result + indexVersion;
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
			if (indexVersion != other.indexVersion)
				return false;
			return true;
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
		
		/**
		 * cached data, left null if no caching
		 */
		private SoftReference<LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF>> dataCache_SoftReference = null;
		
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
		private synchronized LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF> getCache(  ) throws Exception {
			

			if ( dataCache_SoftReference == null ) {
				
				//  NO Cache
				
				return create_Cache();
			}
			
			LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF> dataCache = dataCache_SoftReference.get();

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
		private LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF> create_Cache() throws Exception {
			

			cacheMaxSize = IndexFileRootDataObjectCache.CACHE_MAX_SIZE_FULL_SIZE;

			LoadingCache<CacheKey, SpectralFile_Index_FileContents_Root_IF>dataCache = CacheBuilder.newBuilder()
//						.expireAfterAccess( cacheTimeout, TimeUnit.DAYS ) // always in cache
					.maximumSize( cacheMaxSize )
					.build(
							new CacheLoader<CacheKey, SpectralFile_Index_FileContents_Root_IF>() {
								@Override
								public SpectralFile_Index_FileContents_Root_IF load( CacheKey cacheKey ) throws Exception {

									String hashKey = cacheKey.hashKey;
									int indexVersion = cacheKey.indexVersion;

									//   WARNING  cannot return null.  
									//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)

									//  value is NOT in cache so get it and return it
									return loadIndexFile( hashKey, indexVersion );
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
		 * @param linkerId
		 * @return
		 * @throws Exception
		 */
		private SpectralFile_Index_FileContents_Root_IF loadIndexFile( String hashKey, int indexVersion ) throws Exception {
			
			//   WARNING  cannot return null.  
			//   If would return null, throw SpectralStorageDataNotFoundException and catch at the .get(...)
			
			//  value is NOT in cache so get it and return it
			if ( debugLogLevelEnabled ) {
				cacheGetIndexFileCount.incrementAndGet();
			}
			
			try {
				SpectralFile_Index_FileContents_Root_IF spectralFile_Index_FileContents_Root_IF = null;
				
				if ( indexVersion == StorageFile_Version_004_Constants.FILE_VERSION ) {
				
					spectralFile_Index_FileContents_Root_IF =
							SpectralFile_Index_File_Reader_V_004.getInstance()
							.readIndexFile( 
									hashKey, 
									CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );

				} else if ( indexVersion == StorageFile_Version_005_Constants.FILE_VERSION ) {
				
					spectralFile_Index_FileContents_Root_IF =
							SpectralFile_Index_File_Reader_V_005.getInstance()
							.readIndexFile( 
									hashKey, 
									CommonReader_File_And_S3_Holder.getSingletonInstance().getCommonReader_File_And_S3() );

				} else {
					
					String msg = "indexVersion not a supported value.  indexVersion: " + indexVersion;
					log.error(msg);
					throw new SpectralStorageProcessingException( msg );
				}
				
				if ( spectralFile_Index_FileContents_Root_IF == null ) {
					// Throw this exception since cannot return null to Cache
					throw new SpectralStorageDataNotFoundException();
				}
				return spectralFile_Index_FileContents_Root_IF;

			} catch ( Exception e ) {
				
				log.error( "loadIndexFile(...): readIndexFile(...) threw exception for hashKey: " 
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
