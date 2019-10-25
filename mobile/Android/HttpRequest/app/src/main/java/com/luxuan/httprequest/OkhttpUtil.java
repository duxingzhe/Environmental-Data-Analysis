package com.luxuan.httprequest;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkhttpUtil {

    protected OkHttpClient mOkHttpClient;
    private static OkhttpUtil okhttpUtil;
    private OkhttpUtil(){

    }

    public static OkhttpUtil getOkhttpUtil(){
        if(okhttpUtil==null) {
            okhttpUtil = new OkhttpUtil();
        }
        return okhttpUtil;
    }
    //初始化okHttpClient
    public void initOkHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS);
        mOkHttpClient = builder.build();
    }

    //请求方法
    public String getResponse(String cityName) throws IOException{
        //请求组合创建
        Request request = new Request.Builder()
                .url("http://192.168.0.3/test.php?key="+Utils.API+"&cityname="+cityName)
                .build();

        //发起请求
        Response response;
        response = mOkHttpClient.newCall(request).execute();
        return response.body().string();
    }

}
