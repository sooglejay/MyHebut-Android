package com.myhebut.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonCourse;
import com.myhebut.entity.JsonScore;
import com.myhebut.entity.User;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeJwcLoginActivity extends SwipeBackActivity {

    @ViewInject(R.id.iv_home_jwc_authCode)
    private ImageView mIvAuthCode;

    @ViewInject(R.id.et_home_jwc_stuId)
    private EditText mEtStuId;

    @ViewInject(R.id.et_home_jwc_stuPass)
    private EditText mEtStuPass;

    @ViewInject(R.id.et_home_jwc_authCode)
    private EditText mEtAuthCode;

    @ViewInject(R.id.tv_home_jwc_title)
    private TextView mTvTitle;

    private HttpUtils http;

    private int userId;

    private String module;

    @OnClick(R.id.btn_home_jwc_submit)
    private void submit(View view) {
        String stuId = mEtStuId.getText() + "";
        String stuPass = mEtStuPass.getText() + "";
        String authCode = mEtAuthCode.getText() + "";
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getJwcModuleUrl(stuId, stuPass, authCode, module), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    if (module.equals("score")){
                        parseScoreData(jsonData);
                    } else {
                        parseCourseData(jsonData);
                    }
                } catch(Exception e){
                    Toast.makeText(HomeJwcLoginActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(HomeJwcLoginActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseScoreData(String jsonData) {
        Gson gson = new Gson();
        JsonScore jsonScore = gson.fromJson(jsonData, JsonScore.class);
        if (jsonScore.getStatus() == true) {
            // 保存成绩信息
            SpUtil.setString(this, MyConstants.SCOREDATA, jsonData);
            Intent intent = new Intent(HomeJwcLoginActivity.this,HomeScoreActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 刷新验证码
            getAuthCode();
            Toast.makeText(HomeJwcLoginActivity.this, jsonScore.getErrMsg(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void parseCourseData(String jsonData) {
        Gson gson = new Gson();
        JsonCourse jsonCourse = gson.fromJson(jsonData, JsonCourse.class);

        if (jsonCourse.getStatus() == true) {
            // 保存课程信息
            SpUtil.setString(this, MyConstants.COURSEDATA, jsonData);
            Intent intent = new Intent(HomeJwcLoginActivity.this,HomeCourseActivity.class);
            startActivity(intent);
            finish();
        } else {
            // 刷新验证码
            getAuthCode();
            Toast.makeText(HomeJwcLoginActivity.this, jsonCourse.getErrMsg(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_jwc_login);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    private void initData() {
        User user = ((MyApplication) getApplication()).getUser();
        userId = user.getUserId();
        http = HttpUtil.getHttp();

        Intent intent = getIntent();
        module = intent.getStringExtra("module");
        if (module.equals("score")){
            mTvTitle.setText("成绩查询");
        } else {
            mTvTitle.setText("课表查询");
        }
        getAuthCode();

    }

    private void getAuthCode() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getJwcAuthCode(userId), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                // 清理缓存
                Picasso.with(getApplicationContext()).invalidate(UrlUtil.getJwcAuthCodeUrl(userId));
                // TODO 默认图片
                Picasso.with(getApplicationContext()).load(UrlUtil.getJwcAuthCodeUrl(userId))
                        .into(mIvAuthCode);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(HomeJwcLoginActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });
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
