package com.qf.blackcloud.entity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.qf.blackcloud.db.BlackCloudOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/6 0006.
 */

public class BlackCloudDB {
    public static final String DB_NAME="black_cloud";
    public static final int VERSION=1;
    private static BlackCloudDB blackCloudDB;
    private static SQLiteDatabase db;



    /**
     * 构造方法 里面初始化数据库
     * @param context
     */
    private  BlackCloudDB(Context context){
        BlackCloudOpenHelper blackCloudOpenHelper=new BlackCloudOpenHelper(context,DB_NAME,null,VERSION);
        db=blackCloudOpenHelper.getWritableDatabase();
    }

    /**
     * 返回本类实例
     * @param context
     * @return
     */
    public synchronized static BlackCloudDB getInstance(Context context){
        if(blackCloudDB==null){
            blackCloudDB=new BlackCloudDB(context);
        }
        return blackCloudDB;
    }
    /**
     *将省份信息储存到数据库
     */
    public void saveProvinceToDB(Province province){
        if(province!=null){
            ContentValues value=new ContentValues();
            value.put("province_name",province.getProvinceName());
            value.put("province_code",province.getProvinceCode());
            db.insert("Province",null,value);
        }
    }

    /**
     * 从数据库读取省份信息
     *
     */
    public List<Province> loadProvinceFromDB(){
        List<Province> list=new ArrayList<>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /**
     *将城市信息储存到数据库
     */
    public void saveCityToDB(City city){
        if(city!=null){
            ContentValues value=new ContentValues();
            value.put("city_name",city.getCityName());
            value.put("city_code",city.getCityCode());
            value.put("province_id",city.getProvinceId());
            db.insert("City",null,value);
        }
    }

    /**
     * 从数据库读取城市信息
     *
     */
    public List<City> loadCityFromDB(int provinceId){
        List<City> list=new ArrayList<>();
        Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将县信息储存到数据库
     */
    public void saveCountyToDB(County county){
        if(county!=null){
            ContentValues value=new ContentValues();
            value.put("county_name",county.getCountyName());
            value.put("county_code",county.getCountyCode());
            value.put("city_id",county.getCityId());
            db.insert("County",null,value);
        }
    }

    /**
     * 从数据路读取县信息
     */
    public List<County> loadCountyFromDB(int cityId){
        List<County> list=new ArrayList<>();
        Cursor cursor=db.query("County",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }


}
