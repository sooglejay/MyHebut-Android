package com.myhebut.find;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.R;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.StrUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class FindWbContentActivity extends SwipeBackActivity {

    @ViewInject(R.id.tv_main_module_title)
    private TextView mTvTitle;

    @ViewInject(R.id.tv_find_jwc_content)
    private TextView mTvContent;

    @ViewInject(R.id.ll_find_wb_content)
    private LinearLayout mLlContent;

    private KProgressHUD kProgressHUD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_wb_content);
        ViewUtils.inject(this);

        initData();
    }

    private void initData() {
        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        Intent intent = getIntent();
        String href = intent.getStringExtra("href");
        String time = intent.getStringExtra("time");
        // mode为0为普通的查看功能,1为搜索功能
        String mode = intent.getStringExtra("mode");
        String targetHref;
        if ("0".equals(mode)){
            // 设置标题头
            mTvTitle.setText(time + "失物招领信息总结");
            targetHref = UrlUtil.getWbItemUrl(href);
        } else {
            mTvTitle.setText("失物招领信息搜索结果");
            targetHref = href;
        }
        // 显示等待信息
        kProgressHUD = KProgressHUD.create(FindWbContentActivity.this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(StrUtil.waitLable)
                .setDetailsLabel(StrUtil.searchDetails)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();

        HttpUtils http = HttpUtil.getHttp();
        http.send(HttpRequest.HttpMethod.GET, targetHref, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    // 设置显示内容
                    mTvContent.setText(Html.fromHtml("<br>" + jsonData));
                    // 超链接跳转必须设置该方法
                    mTvContent.setMovementMethod(LinkMovementMethod.getInstance());
                } catch(Exception e){
                    Toast.makeText(FindWbContentActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                } finally {
                    kProgressHUD.dismiss();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                kProgressHUD.dismiss();
                Toast.makeText(FindWbContentActivity.this, "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
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
