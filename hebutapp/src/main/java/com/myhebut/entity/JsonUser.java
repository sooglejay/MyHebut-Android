package com.myhebut.entity;

public class JsonUser {

	/** 返回结果码 */
	private boolean status = true;
	/** 错误信息 */
	private String errMsg = "";
	/** 登录用户信息 */
	private User data;

	public boolean getStatus() {
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

	public User getData() {
		return data;
	}

	public void setData(User data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "JsonUser [status=" + status + ", errMsg=" + errMsg + ",data="
				+ data + "]";
	}

}
