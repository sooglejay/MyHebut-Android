package com.myhebut.manager;

import com.myhebut.listener.SettingListener;

public class SettingManager {

	private SettingListener listener;

	private static SettingManager manager = null;

	public static synchronized SettingManager getInstance() {
		if (manager == null) {
			manager = new SettingManager();
		}
		return manager;
	}

	public void setListener(SettingListener listener) {
		this.listener = listener;
	}

	// 修改个人信息后更新数据
	public void sendInitDataAgain() {
		listener.initDataAgain();
	}

	public void sendSetVisible(boolean flag) {
		if (listener != null){
			listener.setPointVisible(flag);
		}
	}

}
