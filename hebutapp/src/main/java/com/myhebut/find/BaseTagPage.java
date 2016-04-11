package com.myhebut.find;

import android.view.View;

import com.myhebut.activity.MainActivity;

public class BaseTagPage {

	protected View root;

	protected MainActivity mainActivity;

	public BaseTagPage(MainActivity mainActivity) {
		this.mainActivity = mainActivity;

		initView();
		initEvent();
	}

	public void initView() {

	}

	public void initEvent() {

	}

	public void initData() {

	}

	public View getRoot() {
		return root;
	}

}
