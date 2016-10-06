package com.qf.blackcloud.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.qf.blackcloud.entity.BlackCloudDB;
import com.qf.blackcloud.entity.City;
import com.qf.blackcloud.entity.County;
import com.qf.blackcloud.entity.Province;
import com.qf.blackcloud.entity.WeatherinfoBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/10/6 0006.
 */

public class ResultUtil {
    public synchronized static boolean handleProvinceResult(String result, BlackCloudDB blackCloudDB) {
        if (!TextUtils.isEmpty(result)) {
            String[] allProvince = result.split(",");
            if (allProvince != null && allProvince.length > 0) {
                for (String s : allProvince) {
                    String[] arr = s.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(arr[1]);
                    province.setProvinceCode(arr[0]);
                    blackCloudDB.saveProvinceToDB(province);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized static boolean handleCityResult(String result, int provinceId, BlackCloudDB blackCloudDB) {
        if (!TextUtils.isEmpty(result)) {
            String[] allCity = result.split(",");
            if (allCity != null && allCity.length > 0) {
                for (String s : allCity) {
                    String[] arr = s.split("\\|");
                    City city = new City();
                    city.setCityName(arr[1]);
                    city.setCityCode(arr[0]);
                    city.setProvinceId(provinceId);
                    blackCloudDB.saveCityToDB(city);
                }
                return true;
            }
        }

        return false;
    }

    public synchronized static boolean handleCountyResult(String result, int cityId, BlackCloudDB blackCloudDB) {
        if (!TextUtils.isEmpty(result)) {
            String[] allCounty = result.split(",");
            if (allCounty != null && allCounty.length > 0) {
                for (String s : allCounty) {
                    String[] arr = s.split("\\|");
                    County county = new County();
                    county.setCountyCode(arr[0]);
                    county.setCountyName(arr[1]);
                    county.setCityId(cityId);
                    blackCloudDB.saveCountyToDB(county);
                }
                return true;
            }
        }

        return false;
    }


    public static void handleWeatherResult(Context context, String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }



}
