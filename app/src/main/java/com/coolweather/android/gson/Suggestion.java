package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wentaodeng on 2017/11/26.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
