package com.example.car;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.Const;
import Util.MatchAdapter;
import Util.MatchRecord;

public class MatchShow extends AppCompatActivity {

    List<MatchRecord> records;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    MatchAdapter matchAdapter;
    MapView mapView;
    BaiduMap baiduMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_show);
        initEvents();
        addRequest(Const.userName);
    }
    public void initEvents(){
        records = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.match_record);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        matchAdapter = new MatchAdapter(records);
        recyclerView.setAdapter(matchAdapter);
        mapView = (MapView)findViewById(R.id.match_mapView);
        baiduMap = mapView.getMap();
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(Const.location);
        baiduMap.animateMapStatus(update);
        baiduMap.setTrafficEnabled(true);
    }
    public void addRequest(final String accountNumber){
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/orderRequest.action";
        String tag = "addRequest";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final Passenger.MyStringRequest request = new Passenger.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(new JSONObject(response).getJSONArray("result").length()==0){
                                Toast.makeText(MatchShow.this,"很抱歉,无车辆信息",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),Passenger.class));
                                finish();
                            }else{
                                JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                                Toast.makeText(MatchShow.this,"JsonArray.length="+String.valueOf(jsonArray.length()),Toast.LENGTH_SHORT).show();
                                for(int i = 0;i < jsonArray.length();i++){
                                    records.add(new MatchRecord(jsonArray.getJSONObject(i)));
                                    String info = "名字:"+jsonArray.getJSONObject(i).getString("name")+"\n"
                                            +"电话:"+jsonArray.getJSONObject(i).getString("userid")+"\n"
                                            +"地址:"+jsonArray.getJSONObject(i).getString("startplace");
                                    LatLng node = new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),
                                            jsonArray.getJSONObject(i).getDouble("startplacex"));
                                    OverlayOptions options = new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromBitmap(drawText(drawBitmap(),info)))
                                            .position(node);
                                    baiduMap.addOverlay(options);
                                    //Toast.makeText(MatchShow.this,"userID="+String.valueOf(jsonArray.getJSONObject(i).getString("userid")),Toast.LENGTH_SHORT).show();
                                }
                                matchAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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
                String startplacex = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startLatLng","").split(",")[0];
                String startplacey = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startLatLng","").split(",")[1];
                String destinationx = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endLatLng","").split(",")[0];
                String destinationy = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endLatLng","").split(",")[1];
                String start_place = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startPlace","");
                String end_place = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endPlace","");
                String start_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startTime","");
                String end_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endTime","");
                String supply_car = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("supplyCar","");
                String publish_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("publishTime","");
                Map<String,String> params = new HashMap<>();
                params.put("userid", accountNumber);  //注⑥
                params.put("startplacex",startplacey);
                params.put("startplacey",startplacex);
                params.put("startplace",start_place);
                params.put("destinationx",destinationy);
                params.put("destinationy",destinationx);
                params.put("destination",end_place);
                params.put("supplycar",supply_car);
                params.put("startdate",start_time);
                params.put("enddate",end_time);
                params.put("pulishtime",getPublishTime());
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public String getPublishTime(){
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return String.valueOf(year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-00");
    }
    private Bitmap drawBitmap() {
        // TODO Auto-generated method stub
        Bitmap photo = BitmapFactory.decodeResource(this.getResources(),R.drawable.icon_location_tips);
        int width = photo.getWidth()*5;
        int hight = photo.getHeight();
        Bitmap newb = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);// 初始化和方框一样大小的位图
        Paint photoPaint = new Paint(); // 建立画笔
        canvas.drawBitmap(photo, 0, 0, photoPaint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return newb;
    }
    private Bitmap drawText(Bitmap bitmap3,String info) {
        // TODO Auto-generated method stub
        int width = bitmap3.getWidth(), hight = bitmap3.getHeight();
        Bitmap btm= Bitmap.createBitmap(width                                                                                                    , hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
        Canvas canvas = new Canvas(btm);
        Paint photoPaint = new Paint(); //建立画笔
        photoPaint.setDither(true); //获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);//过滤一些
        Rect src = new Rect(0, 0, bitmap3.getWidth(), bitmap3.getHeight());//创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
        canvas.drawBitmap(bitmap3, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
        textPaint.setTextSize(20.0f);//字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
        textPaint.setColor(Color.parseColor("#FFFFFF"));//采用的颜色
        canvas.drawText(info, 23, 32, textPaint);//绘制上去字，中间参数为坐标点
        canvas.save(Canvas.ALL_SAVE_FLAG); //保存
        canvas.restore();
        return btm;
    }
}
