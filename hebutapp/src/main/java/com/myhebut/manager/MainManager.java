package com.myhebut.manager;

import com.myhebut.listener.MainListener;

public class MainManager {

    private MainListener listener;

    private static MainManager manager = null;

    public static synchronized MainManager getInstance() {
        if (manager == null) {
            manager = new MainManager();
        }
        return manager;
    }

    public void setListener(MainListener listener) {
        this.listener = listener;
    }

    public void sendSetVisible(boolean flag) {
        if (listener != null){
            listener.setPointVisible(flag);
        }
    }

    public void sendAddNotification() {
        if (listener != null){
            listener.addNotification();
        }
    }

}
