package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonScore implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = -6781968810773357539L;
	/** 返回结果码 */
	private boolean status = true;
	/** 错误信息 */
	private String errMsg;
	/** 返回数据 */
	private List<Term> terms;
	/** 绩点 */
	private double gpa;
	/** 已修读课程总学分 */
	private double credit;

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

	public List<Term> getTerms() {
		return terms;
	}

	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	public double getGpa() {
		return gpa;
	}

	public void setGpa(double gpa) {
		this.gpa = gpa;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}
}
