package com.myhebut.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.myhebut.manager.MainManager;
import com.myhebut.manager.SettingManager;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;

public class PushReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("PushReceiver", "onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传（payload）数据
                byte[] payload = bundle.getByteArray("payload");
                if (payload != null) {
                    String data = new String(payload);
                    Log.d("PushReceiver", "Got Payload:" + data);

                    // 有消息未添加
                    SpUtil.setBoolean(context, MyConstants.ISWAITINGFORADD, false);
                    // 保存未添加的消息
                    SpUtil.setString(context, MyConstants.TEMPNOTIFICATION, data);
                    // 在程序启动的情况下,直接添加数据,并消除未添加的flag
                    MainManager.getInstance().sendAddNotification();
                    // 直接显示红点
                    MainManager.getInstance().sendSetVisible(true);
                    SettingManager.getInstance().sendSetVisible(true);
                    // 在程序没有启动的情况下接受数据,上述语句不能执行,只能通过sp来记录消息
                    SpUtil.setBoolean(context, MyConstants.ISREAD, false);
                }
                break;
            //添加其他case
            //.........
            default:
                break;
        }
    }
}
