package com.myhebut.application;

import android.app.Application;

import com.myhebut.entity.Banner;
import com.myhebut.entity.User;
import com.umeng.socialize.PlatformConfig;

import java.util.List;

public class MyApplication extends Application {

    private User user;

    private List<Banner> banners;

    @Override
    public void onCreate() {
        // 分享配置
        PlatformConfig.setWeixin("wx2e7aca1525e83402", "4cef3455dd1d530329450eae806aedcb");
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
