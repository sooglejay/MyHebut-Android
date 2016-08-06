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
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
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
import com.myhebut.entity.JsonData;
import com.myhebut.entity.JsonUser;
import com.myhebut.entity.Notification;
import com.myhebut.entity.User;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.StrUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.regex.Pattern;

public class LoginActivity extends Activity {

    @ViewInject(R.id.et_login_username)
    private EditText mEtLoginUserName;

    @ViewInject(R.id.et_login_userpass)
    private EditText mEtLoginUserPass;

    @ViewInject(R.id.et_forget_username)
    private EditText mEtForgetUserName;

    @ViewInject(R.id.et_forget_email)
    private EditText mEtForgetEmail;

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

    @ViewInject(R.id.rl_forget_input)
    private RelativeLayout mRlForget;

    @ViewInject(R.id.rl_register_input)
    private RelativeLayout mRlRegister;

    @ViewInject(R.id.tv_login)
    private TextView mTvLogin;

    @ViewInject(R.id.tv_forget)
    private TextView mTvForget;

    @ViewInject(R.id.tv_register)
    private TextView mTvRegister;

    @ViewInject(R.id.tv_login_again)
    private TextView mTvLoginAgain;

    private HttpUtils http;

    private Gson gson;

    private MyApplication application;

    private KProgressHUD kProgressHUD;

    @OnClick(R.id.btn_login)
    private void login(View view) {
        String userName = mEtLoginUserName.getText().toString();
        String userPass = mEtLoginUserPass.getText().toString();
        // 显示等待信息
        kProgressHUD = KProgressHUD.create(LoginActivity.this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(StrUtil.waitLable)
                .setDetailsLabel(StrUtil.loginDetails)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        check(userName, userPass);
    }

    @OnClick(R.id.btn_forget)
    private void forget(View view) {
        String userName = mEtForgetUserName.getText().toString();
        String email = mEtForgetEmail.getText().toString();
        // 显示等待信息
        kProgressHUD = KProgressHUD.create(LoginActivity.this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(StrUtil.waitLable)
                .setDetailsLabel(StrUtil.waitLable)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        resetPwd(userName, email);
    }

    @OnClick(R.id.tv_forget)
    private void showForget(View view) {
        mRlForget.setVisibility(View.VISIBLE);
        mRlLogin.setVisibility(View.GONE);

        mTvForget.setVisibility(View.GONE);
        mTvRegister.setVisibility(View.GONE);
        mTvLogin.setVisibility(View.GONE);
        mTvLoginAgain.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_register)
    private void showRegister(View view) {
        mRlLogin.setVisibility(View.GONE);
        mRlRegister.setVisibility(View.VISIBLE);

        mTvLogin.setVisibility(View.VISIBLE);
        mTvRegister.setVisibility(View.GONE);
        mTvForget.setVisibility(View.GONE);
        mTvLoginAgain.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_login)
    private void showLogin(View view) {
        mRlLogin.setVisibility(View.VISIBLE);
        mRlRegister.setVisibility(View.GONE);

        mTvLogin.setVisibility(View.GONE);
        mTvLoginAgain.setVisibility(View.GONE);
        mTvRegister.setVisibility(View.VISIBLE);
        mTvForget.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv_login_again)
    private void showLoginAgain(View view) {
        mRlLogin.setVisibility(View.VISIBLE);
        mRlForget.setVisibility(View.GONE);

        mTvLogin.setVisibility(View.GONE);
        mTvRegister.setVisibility(View.VISIBLE);
        mTvForget.setVisibility(View.VISIBLE);
        mTvLoginAgain.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_register)
    private void register(View view) {
        String userName = mEtRegisterUserName.getText().toString();
        String userPass = mEtRegisterUserPass.getText().toString();
        String confirmPass = mEtRegisterConfirmUserPass.getText().toString();
        String email = mEtRegisterEmail.getText().toString();
        if (Pattern.matches("[a-zA-Z0-9_]{6,20}", userName)) {
            if (Pattern.matches(".{6,16}", userPass) && Pattern.matches(".{6,16}", confirmPass)) {
                if (userPass.equals(confirmPass)) {
                    if (Pattern.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", email)) {
                        // 显示等待信息
                        kProgressHUD = KProgressHUD.create(LoginActivity.this);
                        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setLabel(StrUtil.waitLable)
                                .setDetailsLabel(StrUtil.registerDetails)
                                .setCancellable(true)
                                .setAnimationSpeed(2)
                                .setDimAmount(0.5f)
                                .show();

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
                    "你输入的登陆用户名格式有误\n昵称必须是字母,数字和下划线组合\n长度至少6位,至多20位", Toast.LENGTH_LONG).show();
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
                        } finally {
                            kProgressHUD.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        kProgressHUD.dismiss();
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
                        } finally {
                            kProgressHUD.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        kProgressHUD.dismiss();
                        Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void resetPwd(String userName, String email) {
        RequestParams params = new RequestParams();
        params.addBodyParameter("userName", userName);
        params.addBodyParameter("email", email);
        http.send(HttpMethod.POST, UrlUtil.getForgetPwdUrl(), params,
                new RequestCallBack<String>() {

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String jsonData = responseInfo.result;
                        try {
                            judgeData4Reset(jsonData);
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "连接失败,请检查网络设置",
                                    Toast.LENGTH_SHORT).show();
                        } finally {
                            kProgressHUD.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        kProgressHUD.dismiss();
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
            // 获取消息通知
            getNotifications();
            // 获取并保存公告信息
            getBanners();
            // 跳转在banner异步获取成功时跳转
        } else {
            Toast.makeText(LoginActivity.this, jsonUser.getErrMsg(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void judgeData4Reset(String jsonData) {
        JsonData jsonResult = gson.fromJson(jsonData, JsonData.class);

        if (jsonResult.isStatus() == true) {
            Toast.makeText(LoginActivity.this, "重置密码的邮件已经发送到您的邮箱,请注意查收!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, jsonResult.getErrMsg(),
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
            }
        });
    }

    private void getNotifications() {
        http.send(HttpRequest.HttpMethod.GET, UrlUtil.getNotificationUrl(application.getUser().getUserId()), new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String jsonData = responseInfo.result;
                // 保存数据,用于离线访问
                SpUtil.setString(getApplicationContext(), MyConstants.NOTIFICATION,
                        jsonData);
                // 解析数据
                List<Notification> notifications = gson.fromJson(jsonData, new TypeToken<List<Notification>>() {  }.getType());
                for (Notification notification : notifications){
                    if (notification.getIsread() == 0){
                        SpUtil.setBoolean(getApplicationContext(), MyConstants.ISREAD,
                                false);
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
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
