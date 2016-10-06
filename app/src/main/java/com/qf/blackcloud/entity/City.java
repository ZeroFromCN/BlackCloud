package com.qf.blackcloud.entity;

/**
 * Created by Administrator on 2016/10/6 0006.
 */

public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public City() {
    }

    public City(String cityName, String cityCode, int provinceId, int id) {
        this.cityName = cityName;
        this.cityCode = cityCode;
        this.provinceId = provinceId;
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
