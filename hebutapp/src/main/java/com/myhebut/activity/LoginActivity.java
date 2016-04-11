package com.myhebut.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonBanner;
import com.myhebut.entity.JsonCommon;
import com.myhebut.entity.JsonUser;
import com.myhebut.entity.User;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Pattern;

public class LoginActivity extends Activity {

    @ViewInject(R.id.et_login_username)
    private EditText mEtLoginUserName;

    @ViewInject(R.id.et_login_userpass)
    private EditText mEtLoginUserPass;

    @ViewInject(R.id.et_register_username)
    private EditText mEtRegisterUserName;

    @ViewInject(R.id.et_register_userpass)
    private EditText mEtRegisterUserPass;

    @ViewInject(R.id.et_register_confirm_userpass)
    private EditText mEtRegisterConfirmUserPass;

    @ViewInject(R.id.et_register_email)
    private EditText mEtRegisterEmail;

    @ViewInject(R.id.rl_login_input)
    private RelativeLayout mRlLogin;

    @ViewInject(R.id.rl_register_input)
    private RelativeLayout mRlRegister;

    @ViewInject(R.id.tv_login)
    private TextView mTvLogin;

    @ViewInject(R.id.tv_register)
    private TextView mTvRegister;

    private HttpUtils http;

    private Gson gson;

    private MyApplication application;

    @OnClick(R.id.btn_login)
    private void login(View view) {
        String userName = mEtLoginUserName.getText().toString();
        String userPass = mEtLoginUserPass.getText().toString();

        check(userName, userPass);
    }

    @OnClick(R.id.tv_register)
    private void showRegister(View view) {
        mRlLogin.setVisibility(View.GONE);
        mRlRegister.setVisibility(View.VISIBLE);

        mTvLogin.setVisibility(View.VISIBLE);
        mTvRegister.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_login)
    private void showLogin(View view) {
        mRlLogin.setVisibility(View.VISIBLE);
        mRlRegister.setVisibility(View.GONE);

        mTvLogin.setVisibility(View.GONE);
        mTvRegister.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.btn_register)
    private void register(View view) {
        String userName = mEtRegisterUserName.getText().toString();
        String userPass = mEtRegisterUserPass.getText().toString();
        String confirmPass = mEtRegisterConfirmUserPass.getText().toString();
        String email = mEtRegisterEmail.getText().toString();
        if (Pattern.matches("[\u4E00-\u9FA5a-zA-Z0-9_]{6,20}", userName)) {
            if (Pattern.matches(".{6,16}", userPass) && Pattern.matches(".{6,16}", confirmPass)) {
                if (userPass.equals(confirmPass)) {
                    if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", email)) {
                        addUser(userName, userPass, email);
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "您输入的邮箱格式有误,请重新输入", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this,
                            "您两次输入的新密码不匹配,请重新输入", Toast.LENGTH_LONG).show();
                    mEtRegisterUserPass.setText("");
                    mEtRegisterConfirmUserPass.setText("");
                }

            } else {
                Toast.makeText(LoginActivity.this,
                        "你输入的密码长度有误\n密码长度至少6位,至多16位", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(LoginActivity.this,
                    "你输入的登陆用户名格式有误\n昵称必须是汉字,字母,数字和下划线组合\n长度至少6位,至多20位", Toast.LENGTH_LONG).show();
        }

    }

    private void addUser(String userName, String userPass, String email) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("userPass", userPass);
        params.addBodyParameter("email", email);
        http.send(HttpMethod.POST, UrlUtil.getRegisterUrl(), params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        try {
                            String jsonData = responseInfo.result;
                            JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);
                            if (jsonCommon.getStatus() == true) {
                                // 清空内容
                                mEtRegisterUserName.setText("");
                                mEtRegisterUserPass.setText("");
                                mEtRegisterConfirmUserPass.setText("");
                                mEtRegisterEmail.setText("");
                                //　进入登陆界面
                                mRlLogin.setVisibility(View.VISIBLE);
                                mRlRegister.setVisibility(View.GONE);

                                mTvLogin.setVisibility(View.GONE);
                                mTvRegister.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(LoginActivity.this, "注册失败,用户名已存在",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);

        gson = new Gson();
        http = HttpUtil.getHttp();
    }

    private void check(String userName, String userPass) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("userPass", userPass);
        http.send(HttpMethod.POST, UrlUtil.getLoginUrl(), params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonData = responseInfo.result;
                        try {
                            judgeData(jsonData);
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void judgeData(String jsonData) {
        JsonUser jsonUser = gson.fromJson(jsonData, JsonUser.class);

        if (jsonUser.getStatus() == true) {
            application = (MyApplication) this.getApplication();
            // 保存登录用户信息
            User user = (User) jsonUser.getData();
            application.setUser(user);
            SpUtil.setBoolean(getApplicationContext(), MyConstants.ISLOGIN,
                    true);
            SpUtil.setBoolean(getApplicationContext(), MyConstants.HASLOGINRECORD,
                    true);
            SpUtil.setString(getApplicationContext(), MyConstants.USERDATA,
                    jsonData);
            // 获取并保存公告信息
            getBanners();
            // 跳转在banner异步获取成功时跳转
        } else {
            Toast.makeText(LoginActivity.this, jsonUser.getErrMsg(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getBanners() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getBannerUrl(), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonData = responseInfo.result;
                // 保存数据,用于离线访问
                SpUtil.setString(getApplicationContext(), MyConstants.BANNERDATA,
                        jsonData);
                // 解析数据
                JsonBanner jsonBanner = gson.fromJson(jsonData, JsonBanner.class);
                application.setBanners(jsonBanner.getBanners());
                // 跳转
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
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
