package com.jobxhub.common.job;

import java.io.Serializable;

public class ResponseFile implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1425307876096494974L;

	/**
	 * 开始 读取点
	 */
	private long start;
	/**
	 * 文件的 MD5值
	 */
	private String fileMD5;
	/**
	 * 上传是否结束
	 */
	private boolean end = false;
	/**
	 * 进度
	 */
	private int progress ;

	private int readBuffer;


	public ResponseFile(){

	}

	public ResponseFile(long start, String fileMD5) {
		this.start = start;
		this.fileMD5 = fileMD5;
		this.progress = 100;
	}

	public ResponseFile(long start, String fileMD5,long progress) {
		this.start = start;
		this.fileMD5 = fileMD5;
		this.progress = (int)progress;
	}

	public long getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public String getFileMD5() {
		return fileMD5;
	}

	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public int getReadBuffer() {
		return readBuffer;
	}

	public void setReadBuffer(int readBuffer) {
		this.readBuffer = readBuffer;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("progress:");
		sb.append(progress);
		sb.append("\t\tstart:");
		sb.append(start);
		return sb.toString();
		
	}
	
}
