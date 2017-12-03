package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Padge on 2017/11/29.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
