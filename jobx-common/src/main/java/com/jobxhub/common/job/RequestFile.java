package com.jobxhub.common.job;

import com.jobxhub.common.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class RequestFile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1425307876096494974L;

	private File file;// 文件

	private String savePath;//保存目标路径
	private long starPos;// 开始位置
	private byte[] bytes;// 文件字节数组
	private int endPos;// 结尾位置
	private String fileMD5; //文件的MD5值
	private long fileSize; //文件总长度
	private int readBuffer; //读取长度
	private String postCmd;

	public RequestFile(){}

	public RequestFile(File file){
		try {
			this.file = file;
			this.starPos = 0;
			this.fileMD5 = IOUtils.getFileMD5(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public long getStarPos() {
		return starPos;
	}

	public void setStarPos(long starPos) {
		this.starPos = starPos;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	public String getFileMD5() {
		return fileMD5;
	}

	public void setFileMD5(String fileMD5) {
		this.fileMD5 = fileMD5;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
		this.readBuffer = (int) (this.fileSize/100);
	}

	public String getPostCmd() {
		return postCmd;
	}

	public void setPostCmd(String postCmd) {
		this.postCmd = postCmd;
	}

	public int getReadBuffer() {
		return readBuffer;
	}

	public void setReadBuffer(int readBuffer) {
		this.readBuffer = readBuffer;
	}
}
