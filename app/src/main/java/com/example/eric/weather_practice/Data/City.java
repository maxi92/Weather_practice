package com.example.eric.weather_practice.Data;

/**
 * Created by Eric on 2016/5/26.
 */
public class City {
    private String province_cn;
    private String district_cn;
    private String city_cn;
    private String city_code;
    private String printStr;

    public City(String province_cn, String district_cn, String city_cn, String city_code, String printStr)
    {
        this.province_cn = province_cn;
        this.district_cn = district_cn;
        this.city_cn = city_cn;
        this.city_code = city_code;
        this.printStr = printStr;
    }


    public void setPrintStr(String printStr) {
        this.printStr = printStr;
    }

    public String getProvince_cn() {
        return province_cn;
    }

    public void setProvince_cn(String province_cn) {
        this.province_cn = province_cn;
    }

    public String getDistrict_cn() {
        return district_cn;
    }

    public void setDistrict_cn(String district_cn) {
        this.district_cn = district_cn;
    }

    public String getCity_cn() {
        return city_cn;
    }

    public void setCity_cn(String city_cn) {
        this.city_cn = city_cn;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    @Override
    public String toString() {
        return printStr;
    }
}
