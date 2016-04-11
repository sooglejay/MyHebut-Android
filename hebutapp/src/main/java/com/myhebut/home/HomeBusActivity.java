package com.myhebut.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeBusActivity extends SwipeBackActivity {


    @OnClick(R.id.tv_home_bus_649)
    public void searchBus649(View view) {
        Intent intent = new Intent(HomeBusActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getBusUrl(649));
        intent.putExtra("title", "查公交");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_bus_319)
    public void searchBus319(View view) {
        Intent intent = new Intent(HomeBusActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getBusUrl(319));
        intent.putExtra("title", "查公交");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_bus_5)
    public void searchBus5(View view) {
        Intent intent = new Intent(HomeBusActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getBusUrl(5));
        intent.putExtra("title", "查公交");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_bus_600)
    public void searchBus600(View view) {
        Intent intent = new Intent(HomeBusActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getBusUrl(600));
        intent.putExtra("title", "查公交");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_bus);
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
