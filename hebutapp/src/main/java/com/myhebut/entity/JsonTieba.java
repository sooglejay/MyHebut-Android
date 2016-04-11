package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonTieba implements Serializable {

	private static final long serialVersionUID = 1L;
 
	private List<Tieba> tiebas;

	public List<Tieba> getTiebas() {
		return tiebas;
	}

	public void setTiebas(List<Tieba> tiebas) {
		this.tiebas = tiebas;
	}
	

}
