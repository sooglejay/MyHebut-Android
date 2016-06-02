package com.myhebut.utils;

public class UrlUtil {

    private static String URL = "http://api.myhebut.com";

    public static String getLoginUrl() {
        return URL + "/login";
    }

    public static String getRegisterUrl() {
        return URL + "/register";
    }

    public static String getExamUrl(String module, int subject) {
        return URL + "/" + module + "?subject=" + subject;
    }

    public static String getUrlWithSection(String module, String subject, int section) {
        return URL + "/" + module + "?subject=" + subject + "&&section=" + section;
    }

    public static String getUrlWithUserId(String module, int subject, int userId) {
        return URL + "/" + module + "?subject=" + subject + "&&userId=" + userId;
    }

    public static String getUrlWithKeyword(String subject, String keyword) {
        return URL + "/search" + "?subject=" + subject + "&&keyword=" + keyword;
    }

    public static String getRankUrl(int subject) {
        return URL + "/rank" + "?subject=" + subject;
    }

    public static String addCollectUrl() {
        return URL + "/addCollect";
    }

    public static String addNoteUrl() {
        return URL + "/addNote";
    }

    public static String addRecordUrl() {
        return URL + "/addRecord";
    }

    public static String deleteUrl(String module) {
        // 设置首字母大小
        module = module.substring(0, 1).toUpperCase() + module.substring(1);
        return URL + "/delete" + module;
    }

    public static String getJwcListUrl() {
        return URL + "/jwcList";
    }

    public static String getJwcItemUrl(String newsId) {
        return URL + "/jwcItem" + "?newsId=" + newsId;
    }

    public static String getWbListUrl() {
        return URL + "/wbList";
    }

    public static String getWbItemUrl(String href) {
        return URL + "/wbItem" + "?href=" + href;
    }

    public static String getsearchWbUrl(String href1, String href2, String href3, String href4, String href5, String keyword) {
        return URL + "/searchWb" + "?href1=" + href1 + "&&href2=" + href2
                + "&&href3=" + href3 + "&&href4=" + href4 + "&&href5=" + href5 + "&&keyword=" + keyword;
    }


    public static String getTbListUrl(int pn) {
        return URL + "/tbList" + "?pn=" + pn;
    }

    public static String getTbItemUrl(String href) {
        return "http://tieba.baidu.com" + href;
    }

    public static String getBannerUrl() {
        return URL + "/banner";
    }

    public static String getAvatarUrl(String avatar) {
        return "http://myhebut.oss-cn-hangzhou.aliyuncs.com/touxiang%2F" + avatar + ".jpg";
    }

    public static String getLogoutUrl() {
        return "http://115.24.160.55:8080/Self/nav_login";
    }

    public static String getMusicUrl() {
        return "https://jinshuju.net/f/k6SzpE";
    }

    public static String getMusicListUrl() {
        return URL + "/music";
    }

    public static String getMusisTimesUrl() {
        return URL + "/getTimes";
    }


    public static String getCardUrl() {
        return "http://ecard.hebut.edu.cn:8070/";
    }

    public static String getCetUrl() {
        return "http://cet.yunban.com/?plg_nld=1&plg_uin=1&plg_auth=1&plg_nld=1&plg_usr=1&plg_vkey=1&plg_dev=1";
    }

    public static String getGuideUrl() {
        return "http://qq.campusplus.com/hebut/service-guide.jsp;jsessionid=016A8737B565F9F0B84ADBADD7749C1C-n1?plg_nld=1&plg_uin=1&plg_auth=1&plg_nld=1&plg_usr=1&plg_vkey=1&plg_dev=1";
    }

    public static String getBusUrl(int bus) {
        return "http://web.chelaile.net.cn/ch5/index.html?showFav=1&utm_source=weixinmp&wxckey=CK1298484483775&src=webapp_weixin_mp&s=browser&utm_medium=menu&homePage=linearound&userId=ou7QDj2Q3dLUCa98MuYVtWGGWnhA&cityId=006&cityVersion=0&supportSubway=0&cityName=%E5%A4%A9%E6%B4%A5#!/linedetail/022-" + bus + "-0/0/";
    }

    public static String getMapUrlOfbc() {
        return "http://map.baidu.com/mobile/webapp/index/streetview/pid=01024300001405011220368316H&from=indexMap&ss_heading=-67.91714612&ss_pitch=-14.1121305881672&ss_panoType=street/";
    }

    public static String getMapUrlOfdy() {
        return "http://map.baidu.com/mobile/webapp/index/streetview/ss_id=beabdbf4a82288997069d3d4&ss_panoType=inter&ss_heading=359.8708053333333&ss_pitch=-10.724637681159416";
    }

    public static String getMapUrlOfny() {
        return "http://map.baidu.com/mobile/webapp/index/streetview/ss_id=b5f26c35a2e22579bda24015&ss_panoType=street&ss_heading=29.186091779710125&ss_pitch=-14.492753623188406";
    }

    public static String getMapUrlOfby() {
        return "http://map.baidu.com/mobile/webapp/index/streetview/ss_id=be54cde968897c4179f796ae&ss_panoType=street&ss_heading=51.98550724637681&ss_pitch=-5.072463768115942";
    }

    public static String getMapUrlOflf() {
        return "http://map.baidu.com/mobile/webapp/index/streetview/ss_id=5e662776d5326f19a1046457&ss_panoType=inter&ss_heading=240.91941101449277&ss_pitch=-11.73913043478261?qq-pf-to=pcqq.c2c";
    }

    public static String getJwcAuthCode(int userId) {
        return URL + "/getAuthCode?userId=" + userId;
    }

    public static String getJwcAuthCodeUrl(int userId) {
        return "http://myhebut.oss-cn-hangzhou.aliyuncs.com/yzm%2F" + userId + ".png";
    }

    public static String getJwcModuleUrl(String stuId, String stuPass, String authCode,String module) {
        // 设置首字母大小
        module = module.substring(0, 1).toUpperCase() + module.substring(1);
        return URL + "/get" + module + "?stuId=" + stuId + "&&stuPass=" + stuPass + "&&authCode=" + authCode;
    }

    public static String addFeedbackUrl() {
        return URL + "/addFeedback";
    }

    public static String alterNickNameUrl() {
        return URL + "/alterNickName";
    }

    public static String alterUserPassUrl() {
        return URL + "/alterUserPass";
    }

    public static String updateAvatarUrl() {
        return URL + "/updateAvatar";
    }

    public static String getDakaUrl() {
        return "http://ygty.hebut.edu.cn/admin/login.zul";
    }

    public static String getKuaidiUrl(String nu) {
        return "http://m.kuaidi100.com/result.jsp?nu=" + nu;
    }

    public static String getNotificationUrl() {
        return URL + "/notification";
    }

    public static String getDownloadUrl() {
        return "http://www.myhebut.com/download";
    }

    public static String getDonateUrl() {
        return "http://www.myhebut.com/donate";
    }

}
