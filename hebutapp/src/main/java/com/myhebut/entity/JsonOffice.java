package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonOffice implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Office> offices;

	public List<Office> getOffices() {
		return offices;
	}

	public void setOffices(List<Office> offices) {
		this.offices = offices;
	}

}
