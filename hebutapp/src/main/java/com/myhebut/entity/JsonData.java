package com.myhebut.entity;

import java.io.Serializable;

public class JsonData implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -6781968810773357539L;
	/** 返回结果码 */
	private boolean status = true;
	/** 错误信息 */
	private String errMsg;
	/** 返回数据 */
	private Object data;

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
