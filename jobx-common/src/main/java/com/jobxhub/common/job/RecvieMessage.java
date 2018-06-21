package com.jobxhub.common.job;

import java.io.Serializable;

public class RecvieMessage  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6200390330718630934L;

	private short msgType;

	private String data;

	public short getMsgType() {
		return msgType;
	}

	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	
}
