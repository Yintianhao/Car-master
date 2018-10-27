package com.example.car;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarSetting extends AppCompatActivity implements View.OnClickListener{

    EditText carId;//车牌号
    EditText carBrand;//品牌
    EditText carColor;//车辆颜色
    EditText carCapacity;//容量
    EditText carModel;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_setting);
        initEvents();
        addListener();
        showCarInfo(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""));
    }
    public void addListener(){
        save.setOnClickListener(this);
    }
    public void initEvents(){
        carId = (EditText)findViewById(R.id.carId);
        carBrand = (EditText)findViewById(R.id.carBrand);
        carCapacity = (EditText)findViewById(R.id.carCapacity);
        carColor = (EditText)findViewById(R.id.carColor);
        save = (Button)findViewById(R.id.carSettingSave);
        carModel = (EditText)findViewById(R.id.carModel);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.carSettingSave:
                saveCarInfo();
                break;
            default:
                break;
        }
    }

    public void showCarInfo(final String driverId){
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/selectcarinfo.action";
        String tag = "QueryCarInfo";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final Setting.MyStringRequest request = new Setting.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(new JSONObject(response).getJSONArray("result").length()==0){
                                Toast.makeText(CarSetting.this,"无车辆信息,请你手动添加",Toast.LENGTH_SHORT).show();
                            }else{
                                JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                                carId.setText(jsonArray.getJSONObject(0).getString("carnum"));
                                carCapacity.setText(jsonArray.getJSONObject(0).getString("carcapacity"));
                                carColor.setText(jsonArray.getJSONObject(0).getString("carcolor"));
                                carBrand.setText(jsonArray.getJSONObject(0).getString("carbrand"));
                                carModel.setText(jsonArray.getJSONObject(0).getString("carmodel"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CarSetting.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Toast.makeText(getApplicationContext(),error.getMessage()+error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("phonenum", driverId);  //注⑥
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public void saveCarInfo(){
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/updatecarinfo.action";
        String tag = "UpdateCarInfo";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final Setting.MyStringRequest request = new Setting.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(new JSONObject(response).getBoolean("result")){
                                Toast.makeText(CarSetting.this,"修改成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(CarSetting.this,"修改失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(CarSetting.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Toast.makeText(getApplicationContext(),error.getMessage()+error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("driverid",getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""));
                params.put("carnum",carId.getText().toString());
                params.put("carbrand",carBrand.getText().toString());
                params.put("carmodel",carModel.getText().toString());
                params.put("carcapacity",carCapacity.getText().toString());
                params.put("carColor",carColor.getText().toString());
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
}
