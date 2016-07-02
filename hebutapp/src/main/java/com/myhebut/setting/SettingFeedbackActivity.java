package com.myhebut.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class SettingFeedbackActivity extends SwipeBackActivity {

    @ViewInject(R.id.et_setting_feedback_contact)
    private EditText mEtContact;

    @ViewInject(R.id.et_setting_feedback_content)
    private EditText mEtContent;

    @ViewInject(R.id.btn_setting_feedback_submit)
    private Button mBtnSubmit;

    @OnClick(R.id.btn_setting_feedback_submit)
    private void submit(View view) {
        // 提交按钮禁用(反之重复提交)
        mBtnSubmit.setEnabled(false);

        MyApplication application = (MyApplication) getApplication();
        User user = application.getUser();
        int userId = user.getUserId();
        String contact = mEtContact.getText().toString();
        String content = mEtContent.getText().toString();

        addFeedback(userId, contact, content);
    }

    private void addFeedback(int userId, String contact, String content) {
        HttpUtils http = HttpUtil.getHttp();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("contact", contact);
        params.addBodyParameter("content", content);
        http.send(HttpRequest.HttpMethod.POST, UrlUtil.addFeedbackUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    Gson gson = new Gson();
                    String jsonData = responseInfo.result;
                    JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);

                    Toast.makeText(SettingFeedbackActivity.this, "反馈成功,感谢您的支持", Toast.LENGTH_LONG).show();
                    // 清空所填写内容
                    mEtContact.setText("");
                    mEtContent.setText("");
                } catch (Exception e) {
                    Toast.makeText(SettingFeedbackActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                } finally {
                    // 回复提交按钮的可点击性
                    mBtnSubmit.setEnabled(true);
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(SettingFeedbackActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                // 回复提交按钮的可点击性
                mBtnSubmit.setEnabled(true);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_feedback);
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
