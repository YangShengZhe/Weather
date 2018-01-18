package com.weather.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Now;
import com.weather.android.gson.Weather;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //下拉刷新的定义
    public SwipeRefreshLayout swipeRefresh;
    //右滑菜单的定义
    public DrawerLayout drawerLayout;
    private android.support.v4.widget.NestedScrollView weatherLayout;
    private android.support.v7.widget.Toolbar toolbar;
    private android.support.design.widget.CollapsingToolbarLayout collapsing;
    private TextView titleCity;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
//    public SwipeRefreshLayout refreshLayout;
    private String mWeatherId;
  //  public DrawerLayout drawerLayout;
    private android.support.design.widget.FloatingActionButton selectButton;
    private android.support.design.widget.FloatingActionButton menuButton;

    private com.kyleduo.switchbutton.SwitchButton refreshButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化各种控件
        swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        //设置下拉的颜色
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
//        //下拉距离
//        swipeRefresh.setDistanceToTriggerSync(100);
        //右滑菜单
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        weatherLayout=(android.support.v4.widget.NestedScrollView) findViewById(R.id.weather_layout);
        toolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.tb_toolbar);
        collapsing=(android.support.design.widget.CollapsingToolbarLayout)findViewById(R.id.Collapsing);
        titleCity=(TextView)findViewById(R.id.title_city);
        degreeText=(TextView)findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText=(TextView)findViewById(R.id.aqi_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
        //bing背景图片
        bingPicImg=(ImageView)findViewById(R.id.bing_pic_img);
        selectButton=(android.support.design.widget.FloatingActionButton)findViewById(R.id.select_button);
        refreshButton=(com.kyleduo.switchbutton.SwitchButton)findViewById(R.id.select_refresh);
        menuButton=(android.support.design.widget.FloatingActionButton)findViewById(R.id.menu_button);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString =prefs.getString("weather",null);
        if(weatherString!=null){
            //有缓存直接解析数据
            Weather weather= Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else{
            //没有缓存从网上获取后在进行解析
//            String weatherId=getIntent().getStringExtra("weather_id");
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.VISIBLE);
            requestWeather(mWeatherId);
        }
        String pictureString=prefs.getString("bing_pic",null);
        if (pictureString!=null){
            Glide.with(this).load(pictureString).into(bingPicImg);
        }else{
            //从网上下载并加载图片
            loadBingPic();
        }
        //下拉刷新的监听器
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    requestWeather(mWeatherId);
            }
        });
        //为选择城市添加监听器(右滑菜单)
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          //      drawerLayout.openDrawer(GravityCompat.START);
                //打开选择城市的列表
//                Intent intent=new Intent(WeatherActivity.this,Choose.class);
//                startActivity(intent);
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        //右下角菜单按钮添加监听控制器
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    //根据天气id查询天气
    public void requestWeather(final String weatherId){
        String weatherUrl="http://guolin.tech/api/weather?cityid="+weatherId+"&key=2203597c3f7747b3ab5365487e72ec60";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                         mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                            Toast.makeText(WeatherActivity.this, "获取天气成功!", Toast.LENGTH_SHORT).show();

                        }else{
                            if (weather==null){
                                Toast.makeText(WeatherActivity.this, "weather等于null", Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(WeatherActivity.this, "获取天气失败", Toast.LENGTH_SHORT).show();
                        }
                       swipeRefresh.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气失败...", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }


        });
        loadBingPic();
    }

    //处理并显示weather实体类中的数据
    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
//        String updateName=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature+"℃";
        String weatherInfo=weather.now.more.info;
        String NowWeatherDisplay=cityName+" "+degree+" "+weatherInfo;
//        titleCity.setText(cityName);
   //     degreeText.setText(degree);
//        weatherInfoText.setText(weatherInfo);
        toolbar.setTitle(NowWeatherDisplay);
       collapsing.setTitle(NowWeatherDisplay);
        //设置顶部图片的变化
        setWeatherPic(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi!=null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度"+weather.suggestion.comfort.info;
        String carWash="洗车"+weather.suggestion.carWash.info;
        String sport="运动"+weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
    private void loadBingPic(){
        String requestBingPic="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    //设置实时天气的变换，分别为阴天，雨天，晴天
    public void setWeatherPic(String weatherInfo){
        ImageView NowPic=(ImageView)findViewById(R.id.nowPic);
        if (weatherInfo.contains("云")){
            NowPic.setImageResource(R.mipmap.cloudy);
        }else if(weatherInfo.contains("雨")){
            NowPic.setImageResource(R.mipmap.rain);
        }else if(weatherInfo.contains("晴")){
            NowPic.setImageResource(R.mipmap.sunny);
        }else if(weatherInfo.contains("雪")){
            NowPic.setImageResource(R.mipmap.snow);
        }else if(weatherInfo.contains("阴")){
            NowPic.setImageResource(R.mipmap.cloudy);
        }else if(weatherInfo.contains("风")){
            NowPic.setImageResource(R.mipmap.wind);
        }
    }


}
