package com.myhebut.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.Notification;
import com.myhebut.manager.MainManager;
import com.myhebut.manager.SettingManager;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingNotificationActivity extends SwipeBackActivity {

    @ViewInject(R.id.lv_setting_notification_list)
    private ListView mLvNotification;

    List<Notification> notifications;

    private Gson gson;

    private HttpUtils http;

    private MyApplication application;

    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_notification_list);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
        initEvent();
    }

    private void initEvent() {
        mLvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notification notification = notifications.get(position);
                // 未读情况下才需要执行下列语句
                if (notification.getIsread() == 0) {
                    // 设为已读状态
                    notification.setIsread(1);
                    adapter.notifyDataSetChanged();
                    // 更新服务器状态
                    updateRead(application.getUser().getUserId(), notification.getNotificationId());
                    // 更新本地数据
                    SpUtil.setString(getApplicationContext(), MyConstants.NOTIFICATION,
                            gson.toJson(notifications));
                    // 若不存在未读消息,则主页面的红点隐藏
                    boolean flag = true;
                    for (Notification notificationTemp : notifications) {
                        // 如果存在未读消息,flag为false
                        if (notificationTemp.getIsread() == 0) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        MainManager.getInstance().sendSetVisible(true);
                        SettingManager.getInstance().sendSetVisible(true);
                        SpUtil.setBoolean(SettingNotificationActivity.this, MyConstants.ISREAD, true);
                    }
                }

                Intent intent;
                if (notification.getType() == 0) {
                    intent = new Intent(SettingNotificationActivity.this, SettingNotificationContentActivity.class);
                    intent.putExtra("content", notification.getContent());
                    intent.putExtra("title", notification.getTitle());
                } else {
                    intent = new Intent(SettingNotificationActivity.this, WebViewActivity.class);
                    intent.putExtra("href", notification.getContent());
                    intent.putExtra("title", notification.getTitle());
                }
                startActivity(intent);
            }
        });
    }

    private void updateRead(int userId, int notificationId) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", String.valueOf(userId));
        params.addBodyParameter("notificationId", String.valueOf(notificationId));
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.getIsReadUrl(), params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(SettingNotificationActivity.this, "网络连接失败,无法更新消息已读状态",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initData() {
        gson = new Gson();
        application = (MyApplication) getApplication();
        http = HttpUtil.getHttp();

        MyApplication application = (MyApplication) getApplication();
        // 读取消息通知
        String jsonData = SpUtil.getString(getApplicationContext(), MyConstants.NOTIFICATION, null);
        // 解析数据
        notifications = gson.fromJson(jsonData, new TypeToken<List<Notification>>() {
        }.getType());

        adapter = new MyAdapter(this, R.layout.item_setting_notification_view, notifications);
        mLvNotification.setAdapter(adapter);
    }


    private class MyAdapter extends ArrayAdapter<Notification> {

        private int resourceId;

        public MyAdapter(Context context, int resource, List<Notification> objects) {
            super(context, resource, objects);
            this.resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Notification notification = getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) view.findViewById(R.id.tv_setting_notification_title);
                viewHolder.outline = (TextView) view.findViewById(R.id.tv_setting_notification_outline);
                viewHolder.time = (TextView) view.findViewById(R.id.tv_setting_notification_time);
                viewHolder.point = (ImageView) view.findViewById(R.id.iv_setting_notification_point);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.title.setText(notification.getTitle());
            viewHolder.outline.setText(notification.getOutline());
            viewHolder.time.setText(notification.getCreate_time());
            // 未读消息显示红点
            if (notification.getIsread() == 0) {
                viewHolder.point.setVisibility(View.VISIBLE);
            } else {
                viewHolder.point.setVisibility(View.GONE);
            }
            return view;
        }

        class ViewHolder {
            private TextView title;
            private TextView outline;
            private TextView time;
            private ImageView point;
        }
    }

    public void back(View view) {
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
