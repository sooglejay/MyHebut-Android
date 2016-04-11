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

public class HomeMapActivity extends SwipeBackActivity {


    @OnClick(R.id.tv_home_map_beichen)
    public void mapOfbeichen(View view) {
        Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getMapUrlOfbc());
        intent.putExtra("title", "校园全景地图");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_map_dongyuan)
    public void mapOfdongyuan(View view) {
        Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getMapUrlOfdy());
        intent.putExtra("title", "校园全景地图");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_map_nanyuan)
    public void mapOfnanyuan(View view) {
        Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getMapUrlOfny());
        intent.putExtra("title", "校园全景地图");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_map_beiyuan)
    public void mapOfbeiyuan(View view) {
        Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getMapUrlOfby());
        intent.putExtra("title", "校园全景地图");
        startActivity(intent);
    }

    @OnClick(R.id.tv_home_map_langfang)
    public void mapOflangfang(View view) {
        Intent intent = new Intent(HomeMapActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getMapUrlOflf());
        intent.putExtra("title", "校园全景地图");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_map);
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
