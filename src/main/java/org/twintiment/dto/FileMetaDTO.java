package org.twintiment.dto;

import java.io.Serializable;

/**
 * A DTO representing a file (data set) on the server.
 */
public class FileMetaDTO implements Serializable {

	private static final long serialVersionUID = -3619128356979513155L;

	private String fileName;
	private long fileSize;
	
	public String getFileName() {
		return fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	
	public FileMetaDTO(String fileName, long fileSize) {
		this.fileName = fileName;
		this.fileSize = fileSize;
	}
	
	/**
	 * Whether or not a file is equal is solely determined by name and filesize. 
	 * Date or contents are not inspected.
	 * @return {@code true} if the object is equal to the passed file, {@code false}
	 * 			otherwise. 
	 */
	@Override
	public boolean equals(Object obj) {
		FileMetaDTO fm = (FileMetaDTO) obj;
		return (fileName.equals(fm.fileName) && fileSize == fm.fileSize);
	}
	
	/**
	 * HashCode created based on the filename.
	 */
	@Override
	public int hashCode() {
		char[] fn = fileName.toCharArray();
		int hash = (int)fileSize;
		for(char c : fn) {
			hash += c;
		}
		return hash;
	}
}
