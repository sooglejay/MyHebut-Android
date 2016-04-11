package com.myhebut.tab;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.LoginActivity;
import com.myhebut.activity.R;
import com.myhebut.application.MyApplication;
import com.myhebut.entity.User;
import com.myhebut.listener.SettingListener;
import com.myhebut.manager.SettingManager;
import com.myhebut.setting.SettingFeedbackActivity;
import com.myhebut.setting.SettingNotificationActivity;
import com.myhebut.setting.SettingPersonalActivity;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.squareup.picasso.Picasso;

public class SettingFragment extends BaseFragment implements SettingListener {

    @ViewInject(R.id.iv_setting_avatar)
    private ImageView mIvAvatar;

    @ViewInject(R.id.tv_setting_nickname)
    private TextView mTvNickName;

    @ViewInject(R.id.iv_setting_point)
    private ImageView mIvPoint;

    @OnClick(R.id.rl_setting_personal)
    private void person(View view) {
        Intent intent = new Intent(mainActivity, SettingPersonalActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_setting_notification)
    private void notification(View view) {
        Intent intent = new Intent(mainActivity, SettingNotificationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_setting_feedback)
    private void feedback(View view) {
        Intent intent = new Intent(mainActivity, SettingFeedbackActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_setting_about)
    private void about(View view) {

    }

    @OnClick(R.id.rl_setting_exit)
    private void exit(View view) {
        SpUtil.setBoolean(getContext(), MyConstants.ISLOGIN,
                false);
        SpUtil.setBoolean(getContext(), MyConstants.HASLOGINRECORD,
                false);
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        startActivity(intent);
        mainActivity.finish();
    }

    @Override
    public View initView() {
        View root = View.inflate(mainActivity, R.layout.fragment_setting, null);
        ViewUtils.inject(this, root);
        // 消息未读则显示红点
        if (!SpUtil.getBoolean(mainActivity, MyConstants.ISREAD, true)) {
            mIvPoint.setVisibility(View.VISIBLE);
        } else {
            mIvPoint.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void initData() {
        // 设置个人信息
        MyApplication application = (MyApplication) mainActivity.getApplication();
        User user = application.getUser();
        String url = UrlUtil.getAvatarUrl(user.getAvatar());
        Picasso.with(mainActivity).load(url).placeholder(R.mipmap.user_avatar_default).
                error(R.mipmap.user_avatar_default).into(mIvAvatar);
        mTvNickName.setText(user.getNickName());

        // 设置接口回调
        SettingManager manager = SettingManager.getInstance();
        manager.setListener(this);

        super.initData();
    }

    @Override
    public void initDataAgain() {
        initData();
    }

    @Override
    public void setPointVisible(boolean flag) {
        if (flag) {
            mIvPoint.setVisibility(View.VISIBLE);
        } else {
            mIvPoint.setVisibility(View.GONE);
        }
    }
}
