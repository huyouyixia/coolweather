package com.coolweather.android.gson;

/**
 * Created by wentaodeng on 2017/11/26.
 */

public class AQI {
    //这里创建的AQICity的对象名字一定要和json里面的名字对应，不然得不到对应数据
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }

}
