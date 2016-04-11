package com.myhebut.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.JsonCommon;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Pattern;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingPassActivity extends SwipeBackActivity {

    @ViewInject(R.id.et_setting_personal_oldpass)
    private EditText mEtOldPass;

    @ViewInject(R.id.et_setting_personal_newpass)
    private EditText mEtNewPass;

    @ViewInject(R.id.et_setting_personal_confirmpass)
    private EditText mExConfirmPass;

    private int userId;

    private HttpUtils http;

    private Gson gson;

    @OnClick(R.id.btn_setting_userpass_submit)
    private void submit(View view) {
        String oldPass = mEtOldPass.getText().toString();
        String newPass = mEtNewPass.getText().toString();
        String confirmPass = mExConfirmPass.getText().toString();
        if (Pattern.matches(".{6,16}", oldPass) && Pattern.matches(".{6,16}", newPass)
                && Pattern.matches(".{6,16}", confirmPass)) {
            if (newPass.equals(confirmPass)) {
                alterUserPass(oldPass, newPass);
            } else {
                Toast.makeText(SettingPassActivity.this,
                        "您两次输入的新密码不匹配,请重新输入", Toast.LENGTH_LONG).show();
                mEtNewPass.setText("");
                mExConfirmPass.setText("");
            }

        } else {
            Toast.makeText(SettingPassActivity.this,
                    "你输入的密码长度有误\n密码长度至少6位,至多16位", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personal_userpass);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    private void initData() {
        MyApplication application = (MyApplication) getApplication();
        userId = application.getUser().getUserId();

        http = HttpUtil.getHttp();
        gson = new Gson();
    }

    private void alterUserPass(String oldPass, String newPass) {

        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("oldPass", oldPass);
        params.addBodyParameter("newPass", newPass);
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.alterUserPassUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JsonCommon jsonCommon = gson.fromJson(responseInfo.result, JsonCommon.class);
                    // 密码正确
                    if (jsonCommon.getStatus()) {
                        Toast.makeText(SettingPassActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                        mEtNewPass.setText("");
                        mExConfirmPass.setText("");
                    } else { // 密码错误
                        Toast.makeText(SettingPassActivity.this, "您输入的旧密码有误", Toast.LENGTH_LONG).show();
                    }
                    // 清空旧密码
                    mEtOldPass.setText("");
                } catch (Exception e) {
                    Toast.makeText(SettingPassActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(SettingPassActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
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
