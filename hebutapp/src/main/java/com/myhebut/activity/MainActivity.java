package com.myhebut.activity;

import android.graphics.Typeface;
import android.os.Bundle;
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
import com.myhebut.application.MyApplication;
import com.myhebut.greendao.Notification;
import com.myhebut.greendao.NotificationDao;
import com.myhebut.listener.MainListener;
import com.myhebut.manager.MainManager;
import com.myhebut.tab.ExamFragment;
import com.myhebut.tab.FindFragment;
import com.myhebut.tab.HomeFragment;
import com.myhebut.tab.SettingFragment;
import com.myhebut.utils.MyConstants;
import com.myhebut.utils.SpUtil;
import com.umeng.analytics.MobclickAgent;

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
