package com.qf.blackcloud.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qf.blackcloud.R;
import com.qf.blackcloud.entity.BlackCloudDB;
import com.qf.blackcloud.entity.City;
import com.qf.blackcloud.entity.County;
import com.qf.blackcloud.entity.Province;
import com.qf.blackcloud.util.HttpUtil;
import com.qf.blackcloud.util.ResultUtil;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {
    /**
     * 申明每级菜单
     */
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private List<Province> listProvince = new ArrayList<>();
    private List<City> listCity = new ArrayList<>();
    private List<County> listCounty = new ArrayList<>();
    private List<String> listData = new ArrayList<>();

    ListView mListView;
    TextView mTextView;

    ArrayAdapter adapter;

    BlackCloudDB blackCloudDB;

    int currentLevel;

    Province selectedProvince;
    City selectedCity;

    ProgressDialog mProgressDialog;

    int pressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        setContentView(R.layout.activity_choose_area);
        mListView = (ListView) findViewById(R.id.mListViewArea);
        mTextView = (TextView) findViewById(R.id.mTextViewTitle);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
        blackCloudDB = BlackCloudDB.getInstance(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = listProvince.get(position);
                    querCity();   //加载市级数据
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = listCity.get(position);
                    queryCounty(); //加载县级数据
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=listCounty.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();  //加载省级数据

    }

    /**
     * 查询县级数据
     */
    private void queryCounty() {
        listCounty = blackCloudDB.loadCountyFromDB(selectedCity.getId());
        if (listCounty.size() > 0) {
            listData.clear();
            for (County county : listCounty) {
                listData.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }


    /**
     * 查询市级数据
     */
    private void querCity() {
        listCity = blackCloudDB.loadCityFromDB(selectedProvince.getId());
        if (listCity.size() > 0) {
            listData.clear();
            for (City city : listCity) {
                listData.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询省级数据
     */
    private void queryProvince() {
        listProvince = blackCloudDB.loadProvinceFromDB();
        if (listProvince.size() > 0) {  //数据库有数据
            listData.clear();
            for (Province province : listProvince) {
                listData.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTextView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }


    /**
     * 从服务器获取
     *
     * @param
     */
    private void queryFromServer(final String code, final String type) {
        String addr;
        if (!TextUtils.isEmpty(code)) {
            addr = "http://www.weather.com.cn/data/list3/city" + code +
                    ".xml";
        } else {
            addr = "http://www.weather.com.cn/data/list3/city.xml";

        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(addr, new HttpUtil.IHttpCallBack() {
            @Override
            public void finish(String result) {
                boolean isHandleSuccess = false;
                if ("province".equals(type)) {
                    isHandleSuccess = ResultUtil.handleProvinceResult(result, blackCloudDB);
                } else if ("city".equals(type)) {
                    isHandleSuccess = ResultUtil.handleCityResult(result, selectedProvince.getId(), blackCloudDB);
                } else if ("county".equals(type)) {
                    isHandleSuccess = ResultUtil.handleCountyResult(result, selectedCity.getId(), blackCloudDB);
                }
                if (isHandleSuccess) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeprogressDialog();
                            if ("province".equals(type)) {
                                queryProvince();
                            } else if ("city".equals(type)) {
                                querCity();
                            } else if ("county".equals(type)) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void error() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeprogressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    /**
     * 关闭进度对话框
     */
    private void closeprogressDialog() {

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     * 重写返回键
     */
    @Override
    public void onBackPressed() {

        if (currentLevel == LEVEL_COUNTY) {
            querCity();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvince();
        } else if (currentLevel == LEVEL_PROVINCE) {
            pressCount++;
            if (pressCount == 1) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pressCount = 0;
                }
            }).start();
        }
        if (pressCount == 2) {
            finish();
        }

    }
}
