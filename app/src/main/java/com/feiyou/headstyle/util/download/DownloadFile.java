package com.feiyou.headstyle.util.download;

/**
 * @author keqizwl
 * 
 */

public class DownloadFile {
	private DownloadStrage strage;
	private String url;
	private String fileName;

	/*
	 * default
	 */
	public DownloadFile(String url, String fileName) {
		this.strage = new HttpDownload();
		this.url = url;
		this.fileName = fileName;
	}

	public DownloadFile(String url, String fileName, DownloadStrage strage) {
		this.strage = strage;
		this.url = url;
		this.fileName = fileName;
	}
	
	public boolean downloadFile(){
		return strage.downloadFile(url, fileName);
	}
	
	public long getDownloadSize(){
	    return strage.getDownloadSize();
	}
	
	public long getFileSize(){
	    return strage.getFileSize();
	}
	
	public void stop(){
	    strage.stop();
	}
}
