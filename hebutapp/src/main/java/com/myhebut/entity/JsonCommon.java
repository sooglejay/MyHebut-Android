package com.myhebut.entity;

public class JsonCommon {

	/** 返回结果码 */
	private boolean status;

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "JsonBase [status=" + status + "]";
	}

}
