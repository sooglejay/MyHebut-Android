package com.myhebut.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonBanner;
import com.myhebut.entity.JsonUser;
import com.myhebut.entity.User;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends Activity {

    private ImageView splashImg;

    private MyApplication application;

    private HttpUtils http;

    private Gson gson;
    // 历史用户数据
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImg = (ImageView) findViewById(R.id.iv_splash);
        application = (MyApplication) getApplication();

        initAnimation();
    }


    private void initAnimation() {

        AlphaAnimation alpha = new AlphaAnimation(0.2f, 1);
        alpha.setDuration(1500);
        alpha.setFillAfter(true);

        splashImg.startAnimation(alpha);

        alpha.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 判断是否有登录记录
                if (SpUtil.getBoolean(getApplicationContext(), MyConstants.HASLOGINRECORD, false)) {
                    http = HttpUtil.getHttp();
                    gson = new Gson();

                    String jsonUserData = SpUtil.getString(getApplicationContext(), MyConstants.USERDATA,
                            null);
                    JsonUser jsonUser = gson.fromJson(jsonUserData, JsonUser.class);
                    user = jsonUser.getData();

                    check(user.getUserName(), user.getUserPass());

                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

    }

    private void check(String userName, String userPass) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("userPass", userPass);
        http.send(HttpRequest.HttpMethod.POST, "http://121.42.169.129/login", params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {

                try {
                    String jsonData = responseInfo.result;
                    JsonUser jsonUser = gson.fromJson(jsonData, JsonUser.class);

                    if (jsonUser.getStatus() == true) {
                        SpUtil.setBoolean(getApplicationContext(), MyConstants.ISLOGIN,
                                true);
                        User user = jsonUser.getData();
                        application.setUser(user);
                        // 获取并保存banner
                        getBanners();
                        // 跳转在banner异步获取成功时跳转
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    failedHttp();
                }

            }

            @Override
            public void onFailure(HttpException error, String msg) {
                failedHttp();
            }
        });

    }
    private void failedHttp() {
        SpUtil.setBoolean(getApplicationContext(), MyConstants.ISLOGIN,
                false);
        // 未联网情况下获取历史用户数据
        application.setUser(user);

        String jsonBannerData = SpUtil.getString(getApplicationContext(), MyConstants.BANNERDATA,
                null);
        JsonBanner jsonBanner = gson.fromJson(jsonBannerData, JsonBanner.class);
        application.setBanners(jsonBanner.getBanners());

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getBanners() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getBannerUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonData = responseInfo.result;
                // 解析数据
                JsonBanner jsonBanner = gson.fromJson(jsonData, JsonBanner.class);
                application.setBanners(jsonBanner.getBanners());
                // 保存数据,用于离线访问
                SpUtil.setString(getApplicationContext(), MyConstants.BANNERDATA,
                        jsonData);
                // 跳转
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(SplashActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
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
