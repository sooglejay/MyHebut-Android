package com.myhebut.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.activity.WebViewActivity;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class HomeKuaidiActivity extends SwipeBackActivity {

    @ViewInject(R.id.et_home_kuaidi_num)
    private EditText mEtNum;

    @OnClick(R.id.bt_home_kuaidi_submit)
    private void kuaidiSubmit(View view){
        Intent intent = new Intent(HomeKuaidiActivity.this, WebViewActivity.class);
        intent.putExtra("href", UrlUtil.getKuaidiUrl(String.valueOf(mEtNum.getText())));
        intent.putExtra("title", "快递查询结果");
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_kuaidi);
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
