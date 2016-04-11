package com.myhebut.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingPersonalActivity extends SwipeBackActivity {

    @OnClick(R.id.tv_setting_personal_avatar)
    private void avatar(View view) {
        Intent intent = new Intent(SettingPersonalActivity.this, SettingAvatarActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_setting_personal_nickname)
    private void nickName(View view) {
        Intent intent = new Intent(SettingPersonalActivity.this, SettingNameActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.tv_setting_personal_userpass)
    private void userPass(View view) {
        Intent intent = new Intent(SettingPersonalActivity.this, SettingPassActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personal);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

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
