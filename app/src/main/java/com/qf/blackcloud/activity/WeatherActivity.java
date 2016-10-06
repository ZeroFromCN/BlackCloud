package com.qf.blackcloud.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qf.blackcloud.R;
import com.qf.blackcloud.util.HttpUtil;
import com.qf.blackcloud.util.ResultUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity {

    @InjectView(R.id.weather_info_layout)
     LinearLayout weatherInfoLayout;
    @InjectView(R.id.city_name)
     TextView cityNameText;
    @InjectView(R.id.publish_text)
     TextView publishText;
    @InjectView(R.id.weather_desp)
     TextView weatherDespText;
    @InjectView(R.id.temp1)
     TextView temp1Text;
    @InjectView(R.id.temp2)
     TextView temp2Text;
    @InjectView(R.id.current_date)
     TextView currentDateText;

     Button switchCity;
     Button refreshWeather;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.inject(this);
        String countyCode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countyCode)) {
            //有代码就去查询天气
            publishText.setText("同步中 . . .");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            //没有代码就显示本地天气
            showWeather();
        }
    }

    @OnClick({R.id.switch_city, R.id.refresh_weather})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.switch_city:
                Intent intent = new Intent(this, ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    private void queryWeatherCode(String countyCode) {
        String addr = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(addr, "countyCode");
    }

    private void queryFromServer(final String addr, final String countyCode) {
        HttpUtil.sendHttpRequest(addr, new HttpUtil.IHttpCallBack() {
            @Override
            public void finish(String result) {
                if ("countyCode".equals(countyCode)) {
                    if (!TextUtils.isEmpty(countyCode)) {
                        String[] arr = result.split("\\|");
                        if (arr != null && arr.length == 2) {
                            String weatherCode = arr[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(countyCode)) {
                    ResultUtil.handleWeatherResult(WeatherActivity.this, result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void error() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }


}
