package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonWeibo implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Weibo> weibos;

	public List<Weibo> getWeibos() {
		return weibos;
	}

	public void setWeibos(List<Weibo> weibos) {
		this.weibos = weibos;
	}

}
