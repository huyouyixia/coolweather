package com.coolweather.android.db;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.android.R;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by wentaodeng on 2017/11/25.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LENVL_COUNTY = 2;
    private AlertDialog.Builder progressDialog;
    private TextView titleText;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    private static final String TAG = "ChooseAreaFragment";
    AlertDialog dialog;
    boolean b = true;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        progressDialog = new AlertDialog.Builder(this.getActivity());
        progressDialog.setTitle("正在查询");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在加载中，请稍候……");
        progressDialog.setPositiveButton("取消", (dialogInterface, i) -> {
            b = false;
        });
        dialog = progressDialog.create();
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        Log.d(TAG, "onCreateView: listView--"+listView);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        Log.d(TAG, "onCreateView: 我靠，少了一句搞了半天"+dataList);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            b = true;
            Log.d(TAG, "onActivityCreated: "+currentLevel);
            if(currentLevel == LEVEL_PROVINCE){
                selectedProvince = provinceList.get(i);
                Log.d(TAG, "onActivityCreated: selectedProvince" + selectedProvince);
                queryCities();
            }else if(currentLevel == LEVEL_CITY){
                selectedCity = cityList.get(i);
                queryCounties();
            }
        });
        backButton.setOnClickListener(view ->{
            if(currentLevel == LENVL_COUNTY){
                queryCities();
            }else if(currentLevel == LEVEL_CITY){
                queryProvinces();
            }
        });
        queryProvinces();

    }
    private void queryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
                Log.d(TAG, "queryProvinces: "+province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            Log.d(TAG, "queryProvinces: listView.setSelection(0)后面：");
            currentLevel = LEVEL_PROVINCE;
            Log.d(TAG, "queryProvinces: currentLevel="+currentLevel);
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(
                selectedProvince.getId())).find(City.class);
        if(cityList.size()>0){
            //清空数据，用来装新数据
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }
    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid=?",String.valueOf(
                selectedCity.getId())).find(County.class);
        if(countyList.size()>0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LENVL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }
    private void queryFromServer(String address, final String type){
        if(b && !dialog.isShowing()){
            dialog.show();
        }
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑

                getActivity().runOnUiThread(()->{
                    if(dialog.isShowing()){
                        dialog.dismiss();
                        b = true;
                    }
                    Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.e(TAG, "onResponse: " + responseText);
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCounty(responseText,selectedCity.getId());
                }
                Log.d(TAG, "onResponse: result====="+result + "type ===" + type);
                if(result){
                    getActivity().runOnUiThread(()->{
                        dialog.dismiss();
                        b = true;
                        if("province".equals(type)){
                            queryProvinces();
                        }else if("city".equals(type)){
                            queryCities();
                        }else if("county".equals(type)){
                            queryCounties();
                        }
                    });
                }
            }
        });
    }

}
