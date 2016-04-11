package com.myhebut.utils;


import com.lidroid.xutils.HttpUtils;

public class HttpUtil {

	private static HttpUtils http;

	public static HttpUtils getHttp() {
		if (http == null){
			http = new HttpUtils();
			// 设置缓存失效时间5s
			http.configCurrentHttpCacheExpiry(5);
		}
			
		return http;
	}

}
