package com.myhebut.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeMusicActivity extends SwipeBackActivity {


    @OnClick(R.id.tv_home_music_diange)
    private void diange(View view) {
        HttpUtils http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getMusisTimesUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    int times = Integer.parseInt(responseInfo.result);
                    if (times > 0) {
                        Intent intent = new Intent(HomeMusicActivity.this, WebViewActivity.class);
                        intent.putExtra("href", UrlUtil.getMusicUrl());
                        intent.putExtra("title", "蜜思点歌台 - 点歌");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(HomeMusicActivity.this, HomeMusicErrActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(HomeMusicActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(HomeMusicActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @OnClick(R.id.tv_home_music_list)
    private void list(View view) {
        Intent intent = new Intent(HomeMusicActivity.this, HomeMusicListActivity.class);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_music);
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
