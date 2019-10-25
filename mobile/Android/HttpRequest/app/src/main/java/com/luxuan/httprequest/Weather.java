package com.luxuan.httprequest;

import com.alibaba.fastjson.JSONObject;

public class Weather {

    // 根据数据库的字段来设置类的成员
    String time;
    String cityName;
    String aqi;
    String maxAqi;
    String minAqi;
    String pm25;
    String pm10;
    String so2;
    String no2;
    String co;
    String o3;
    String rank;
    String quality;

    public Weather(JSONObject jsonObject){
        cityName=jsonObject.getString("cityname");
        time=jsonObject.getString("time_point");
        aqi=jsonObject.getString("aqi");
        maxAqi=jsonObject.getString("max_aqi");
        minAqi=jsonObject.getString("min_aqi");
        pm25=jsonObject.getString("pm2_5");
        pm10=jsonObject.getString("pm10");
        so2=jsonObject.getString("so2");
        no2=jsonObject.getString("no2");
        co=jsonObject.getString("co");
        o3=jsonObject.getString("o3");
        rank=jsonObject.getString("rank");
        quality=jsonObject.getString("quality");
    }

    @Override
    public String toString(){
        return "city_name: "+cityName+", time: "+time+", rank: "+rank;
    }
}
