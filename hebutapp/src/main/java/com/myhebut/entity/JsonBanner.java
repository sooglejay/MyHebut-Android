package com.myhebut.entity;

import java.io.Serializable;
import java.util.List;

public class JsonBanner implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Banner> banners;

	public List<Banner> getBanners() {
		return banners;
	}

	public void setBanners(List<Banner> banners) {
		this.banners = banners;
	}

}
