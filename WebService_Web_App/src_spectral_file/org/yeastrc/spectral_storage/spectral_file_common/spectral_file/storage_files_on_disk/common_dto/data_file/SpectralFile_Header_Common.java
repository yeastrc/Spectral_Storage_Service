package org.yeastrc.spectral_storage.spectral_file_common.spectral_file.storage_files_on_disk.common_dto.data_file;

/**
 * Data at beginning of Spectral file
 *
 */
public class SpectralFile_Header_Common {

	//  Ignored when writing.  Writer will write it's version number
	private short version;
	
	private long scanFileLength_InBytes;
	
	private byte[] mainHash;

	private byte[] altHashSHA512;
	private byte[] altHashSHA1;

	/**
	 * Ignored when writing.  Writer will write it's version number
	 * @return
	 */
	public short getVersion() {
		return version;
	}

	/**
	 * Ignored when writing.  Writer will write it's version number
	 * @param version
	 */
	public void setVersion(short version) {
		this.version = version;
	}


	public long getScanFileLength_InBytes() {
		return scanFileLength_InBytes;
	}

	public void setScanFileLength_InBytes(long scanFileLength_InBytes) {
		this.scanFileLength_InBytes = scanFileLength_InBytes;
	}

	public byte[] getMainHash() {
		return mainHash;
	}

	public void setMainHash(byte[] mainHash) {
		this.mainHash = mainHash;
	}

	public byte[] getAltHashSHA512() {
		return altHashSHA512;
	}

	public void setAltHashSHA512(byte[] altHashSHA512) {
		this.altHashSHA512 = altHashSHA512;
	}

	public byte[] getAltHashSHA1() {
		return altHashSHA1;
	}

	public void setAltHashSHA1(byte[] altHashSHA1) {
		this.altHashSHA1 = altHashSHA1;
	}
}
