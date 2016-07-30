package com.myhebut.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.myhebut.entity.Banner;
import com.myhebut.entity.User;
import com.myhebut.greendao.DaoMaster;
import com.myhebut.greendao.DaoSession;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

public class MyApplication extends Application {

    private User user;

    private List<Banner> banners;

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static SQLiteDatabase db;
    //数据库名，表名是自动被创建的
    public static final String DB_NAME = "notifications-db.db";


    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }


    public static SQLiteDatabase getSQLDatebase(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            db = daoMaster.getDatabase();
        }
        return db;
    }


    @Override
    public void onCreate() {
        // 分享配置
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setSinaWeibo("1219115396", "64ea8b23c7672f5e27384571be1d6874");
        PlatformConfig.setQQZone("1105503845", "VGLtv7ph9SMT48DA");

        super.onCreate();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }
}
