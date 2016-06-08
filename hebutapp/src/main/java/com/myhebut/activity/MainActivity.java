package com.myhebut.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.igexin.sdk.PushManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.myhebut.application.MyApplication;
import com.myhebut.greendao.Notification;
import com.myhebut.greendao.NotificationDao;
import com.myhebut.listener.MainListener;
import com.myhebut.manager.MainManager;
import com.myhebut.manager.SettingManager;
import com.myhebut.tab.ExamFragment;
import com.myhebut.tab.FindFragment;
import com.myhebut.tab.HomeFragment;
import com.myhebut.tab.SettingFragment;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.myhebut.utils.UrlUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public class MainActivity extends FragmentActivity implements MainListener {

    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;

    // 定义一个布局
    private LayoutInflater layoutInflater;

    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomeFragment.class, FindFragment.class,
            ExamFragment.class, SettingFragment.class};

    private int fongIconArray[] = {R.string.home, R.string.find, R.string.exam, R.string.setting};

    // Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "发现", "考试", "我"};

    // 用于在按一次返回键退出
    private long exitTime = 0;
    // 消息红点
    private ImageView point;

    private MyApplication application;

    private NotificationDao dao;

    private Gson gson;

    private ProgressDialog pBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_tab_main);

        PushManager.getInstance().initialize(this.getApplicationContext());

        // 设置接口回调
        MainManager manager = MainManager.getInstance();
        manager.setListener(this);

        initView();
        initData();
        // 检测版本更新
        new Thread() {
            public void run() {
                try {
                    HttpUtils http = HttpUtil.getHttp();
                    http.send(HttpRequest.HttpMethod.GET, UrlUtil.getVersionUrl(), new RequestCallBack<String>() {

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            try {
                                String versionStr = responseInfo.result;
                                int versionCode = Integer.parseInt(versionStr);
                                PackageManager packageManager = getPackageManager();
                                PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
                                int nowCode = info.versionCode;
                                // 如果有新版本则进行更新提示
                                if (versionCode > nowCode) {
                                    showUpdateDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("悦河工有新版本!");
        builder.setCancelable(false);

        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    // 下载文件
                    downFile(UrlUtil.getAppUrl());
                } else {
                    Toast.makeText(MainActivity.this, "SD卡不可用，请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void downFile(String appUrl) {
        //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar = new ProgressDialog(MainActivity.this);
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();
        // 下载apk
        HttpUtils http = HttpUtil.getHttp();
        HttpHandler handler = http.download(UrlUtil.getAppUrl(),
                "/sdcard/MyHebut.apk",
                false, // 如果目标文件存在，覆盖重新下载
                true, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        //testTextView.setText("conn...");
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        pBar.setMax((int) total);
                        pBar.setProgress((int) current);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                        //testTextView.setText("downloaded:" + responseInfo.result.getPath());
                        Toast.makeText(MainActivity.this, "下载成功",
                                Toast.LENGTH_SHORT).show();
                        // 安装apk
                        installApk();
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Toast.makeText(MainActivity.this, "下载失败" + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void installApk() {
        File file = new File("/sdcard/MyHebut.apk");
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void initData() {
        application = (MyApplication) getApplication();
        dao = application.getDaoSession(this).getNotificationDao();
        gson = new Gson();
    }

    private void initView() {
        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        // 实例化TabHost对象,得到TabHost
        mTabHost = (FragmentTabHost) findViewById(R.id.tab_host);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content);
        // 去除按钮间的分割线
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < fragmentArray.length; i++) {
            // 为每一个Tab按钮设置图标和标题
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
                    .setIndicator(getTabIntemView(i));
            // 将Tab按钮添加进Tab选项卡
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
        // 消息未读则显示红点
        if (!SpUtil.getBoolean(this, MyConstants.ISREAD, true)) {
            point.setVisibility(View.VISIBLE);
        } else {
            point.setVisibility(View.GONE);
        }
        // 如果用户设置直接进入考试模块,则自动进行模块跳转
        if (SpUtil.getBoolean(this, MyConstants.ISENTEREXAM, false)) {
            mTabHost.setCurrentTab(2);
        }
    }

    // 设置tab按钮样式
    private View getTabIntemView(int index) {
        View view = layoutInflater.inflate(R.layout.item_tab_view, null);

        TextView tv = (TextView) view.findViewById(R.id.tab_icon);
        tv.setText(fongIconArray[index]);
        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        tv.setTypeface(iconfont);

        TextView textView = (TextView) view.findViewById(R.id.tab_text);
        textView.setText(mTextviewArray[index]);

        if (index == 3) {
            point = (ImageView) view.findViewById(R.id.iv_notification_red_point);
        }

        return view;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回键退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setPointVisible(boolean flag) {
        if (flag) {
            point.setVisibility(View.VISIBLE);
        } else {
            point.setVisibility(View.GONE);
        }
    }

    @Override
    public void addNotification() {
        if (!SpUtil.getBoolean(this, MyConstants.ISWAITINGFORADD, true)) {
            try {
                String data = SpUtil.getString(this, MyConstants.TEMPNOTIFICATION, null);
                Notification notification = gson.fromJson(data, Notification.class);
                // 添加到数据库
                dao.insert(notification);
                // 消除未添加的flag
                SpUtil.setBoolean(this, MyConstants.ISWAITINGFORADD, true);
                // 直接显示红点
                MainManager.getInstance().sendSetVisible(true);
                SettingManager.getInstance().sendSetVisible(true);
            } catch (Exception e) {
                Log.d("PushReceiver", "json has an error");
            }
        }
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
