package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonCourse implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -6781968810773357539L;
	/** 返回结果码 */
	private boolean status = true;
	/** 错误信息 */
	private String errMsg;
	/** 返回数据 */
	private List<Course> courses;

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

	public List<Course> getCourses() {
		return courses;
	}

	public void setCourses(List<Course> courses) {
		this.courses = courses;
	}

}
