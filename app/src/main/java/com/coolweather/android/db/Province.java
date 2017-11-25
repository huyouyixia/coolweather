package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wentaodeng on 2017/11/25.
 */

public class Province extends DataSupport {
    private int id;
    private String provinceName;
    private int provinceCode;
    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setProvinceCode(int provinceCode) {

        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {

        return provinceName;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public int getId(){
        return id;
    }
}
