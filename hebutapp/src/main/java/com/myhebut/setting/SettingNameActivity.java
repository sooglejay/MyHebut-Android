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
import com.myhebut.entity.User;
import com.myhebut.manager.SettingManager;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.regex.Pattern;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingNameActivity extends SwipeBackActivity {

    @ViewInject(R.id.et_setting_personal_nickname)
    private EditText mEtNickName;

    private User user;

    private SettingManager manager;

    @OnClick(R.id.btn_setting_nickname_submit)
    private void submit(View view) {
        String nickName = mEtNickName.getText().toString();
        if (Pattern.matches("[\u4E00-\u9FA5a-zA-Z0-9_]{2,8}", nickName)) {
            alterNickName(nickName);
        } else {
            Toast.makeText(SettingNameActivity.this,
                    "你输入的昵称格式有误\n昵称必须是汉字,字母,数字和下划线组合\n长度至少2位,至多8位", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_personal_nickname);
        ViewUtils.inject(this);

        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = this.getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        initData();
    }

    private void initData() {
        manager = SettingManager.getInstance();

        MyApplication application = (MyApplication) getApplication();
        user = application.getUser();
    }

    private void alterNickName(final String nickName) {
        HttpUtils http = HttpUtil.getHttp();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", user.getUserId() + "");
        params.addBodyParameter("nickName", nickName);
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.alterNickNameUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    Gson gson = new Gson();
                    String jsonData = responseInfo.result;
                    JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);

                    Toast.makeText(SettingNameActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                    // 清空所填写内容
                    mEtNickName.setText("");
                    // 修改全局变量
                    user.setNickName(nickName);

                    manager.sendInitDataAgain();
                } catch (Exception e) {
                    Toast.makeText(SettingNameActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(SettingNameActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
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
