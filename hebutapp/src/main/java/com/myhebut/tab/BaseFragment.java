package com.myhebut.tab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myhebut.activity.MainActivity;

public abstract class BaseFragment extends Fragment {

	protected MainActivity mainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = (MainActivity) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = initView();
		return root;
	}

	public abstract View initView();

	public void initEvent() {

	}

	public void initData() {

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();// 初始化数据
		initEvent();// 初始化事件
	}

}
