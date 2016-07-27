package com.myhebut.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.utils.StrUtil;
import com.umeng.analytics.MobclickAgent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class WebViewActivity extends SwipeBackActivity {

    @ViewInject(R.id.web_view)
    private WebView webView;

    @ViewInject(R.id.tv_webview_title)
    private TextView mTvTitle;

    private KProgressHUD kProgressHUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_content);
        ViewUtils.inject(this);

        initData();
    }

    private void initData() {
        // 设置右滑返回
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        // 获取链接
        Intent intent = getIntent();
        String href = intent.getStringExtra("href");
        String title = intent.getStringExtra("title");
        // 显示等待信息
        kProgressHUD = KProgressHUD.create(WebViewActivity.this);
        kProgressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(StrUtil.waitLable)
                .setDetailsLabel(StrUtil.waitDetails)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        // 设置webview
        mTvTitle.setText(title);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);//允许DCOM
        webSettings.setBuiltInZoomControls(true); // 显示放大缩小 controler
        webSettings.setSupportZoom(true); // 可以缩放
        // TODO 四六级页面加载有问题
        if ("查四六级".equals(title)){
            kProgressHUD.dismiss();
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // 隐藏等待信息
                kProgressHUD.dismiss();
                super.onPageFinished(view, url);
            }
        });
        // 加载url
        webView.loadUrl(href);
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
        // 解决关闭webview后还有声音的问题
        webView.reload();
    }
}
