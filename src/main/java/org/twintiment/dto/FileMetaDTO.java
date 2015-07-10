package org.twintiment.dto;

public class FileMetaDTO {
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
	
	@Override
	public boolean equals(Object obj) {
		FileMetaDTO fm = (FileMetaDTO) obj;
		return (fileName.equals(fm.fileName) && fileSize == fm.fileSize);
	}
	
	@Override
	public int hashCode() {
		char[] fn = fileName.toCharArray();
		int hash= (int)fileSize;
		for(char c : fn) {
			hash+=c;
		}
		return hash;
	}
}
