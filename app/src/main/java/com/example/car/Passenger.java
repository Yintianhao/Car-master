package com.example.car;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tool.Const;
import WheelView.Adapter.NumericWheelAdapter;
import WheelView.WheelView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Passenger extends AppCompatActivity{

    MapView mapView;//地图视图
    BaiduMap baiduMap;//地图实例
    DrawerLayout drawerLayout;//DrawerLayout布局
    CircleImageView leftHead;//左边的头像
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String image_path;//侧滑栏头像存取路径
    TextView nickName;
    NavigationView navigationView;
    TextView latestLocationInfo;
    MarkerOptions myLocationOption;
    RoutePlanSearch routePlanSearch;
    FloatingActionButton inputInformation;
    String startPlace;
    String endPlace;
    String startTime;
    String endTime;
    String supplyCar;
    List<PlanNode> wayPoints = new ArrayList<>();
    LatLng a;
    LatLng b;
    AutoCompleteTextView startInput;
    AutoCompleteTextView endInput;
    WheelView startTimeInput_h;
    WheelView startTimeInput_m;
    WheelView endTimeInput_h;
    WheelView endTimeInput_m;
    List<JSONObject> onRoadPassengers;
    List<Boolean> isTraffic;
    int clickNum = 0;

    FloatingActionButton setTraffic;
    View view;

    LayoutInflater inflater;
    LinearLayout parent;
    LocationClient location = null;
    MyLocationListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        initActivityEvents();
        initLocation();
        isAndroidSix();
        initDialogEvents();
        addListener();//添加监听器
    }
    public void isAndroidSix(){
        //初始化经纬度以及详细地址，判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT>=23){
            showContacts();
        }else{
            initLocation();//init为定位方法
        }
    }
    public void initLocation(){
        //位置监听,将起始位置设置为设备当前位置
        location = new LocationClient(getApplicationContext());
        listener = new MyLocationListener();
        location.registerLocationListener(listener);// 注册监听函数
        setViews();
        location.start();

    }
    public void setViews() {
        //相关属性配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setAddrType("all");
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        location.setLocOption(option);
    }
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"请手动开启定位权限,并重启APP",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(Passenger.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},100);
        }else{
            initLocation();
        }
    }
    public void initDialogEvents(){
        //动态加载布局,将Dialog中的各个控件初始化
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        parent = (LinearLayout) inflater.inflate(R.layout.order_layout, null);
        //view  = View.inflate(this,R.layout.order_layout,null);
        startInput = (AutoCompleteTextView) parent.findViewById(R.id.startPlace);
        endInput = (AutoCompleteTextView) parent.findViewById(R.id.endPlace);
        startTimeInput_h = (WheelView) parent.findViewById(R.id.startTime_h);
        startTimeInput_h.setViewAdapter(new NumericWheelAdapter(getApplicationContext(),00,24));
        startTimeInput_h.setBackground(getResources().getDrawable(R.drawable.input_bg));
        startTimeInput_m = (WheelView) parent.findViewById(R.id.startTime_m);
        startTimeInput_m.setViewAdapter(new NumericWheelAdapter(getApplicationContext(),00,60));
        endTimeInput_h = (WheelView) parent.findViewById(R.id.endTime_h);
        endTimeInput_h.setViewAdapter(new NumericWheelAdapter(getApplicationContext(),00,24));;
        endTimeInput_m = (WheelView) parent.findViewById(R.id.endTime_m);
        endTimeInput_m.setViewAdapter(new NumericWheelAdapter(getApplicationContext(),00,60));
        startInput.addTextChangedListener(new MyTextWatcher(startInput));
        endInput.addTextChangedListener(new MyTextWatcher(endInput));
    }
    public void initActivityEvents(){
        /*
        * 交通状况和路上乘客集合
        * */
        isTraffic = new ArrayList<>();
        isTraffic.add(false);
        isTraffic.add(true);
        onRoadPassengers = new ArrayList<>();
        /*
        * instance
        * */
        latestLocationInfo = (TextView) findViewById(R.id.passenger_passenger_locationInfo);
        routePlanSearch =RoutePlanSearch.newInstance();//路线规划对象实例化
        inputInformation = (FloatingActionButton)findViewById(R.id.passenger_passenger_start);
        setTraffic = (FloatingActionButton)findViewById(R.id.passenger_passenger_setTraffic);
        drawerLayout = (DrawerLayout)findViewById(R.id.passenger_passenger_drawerLayout);
        /*
        * sharedPreferences实例化
        * 初始化头像控件和名称
        * */
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
        image_path = sharedPreferences.getString("passenger_image_path"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),"");
        Log.d("侧滑栏图片路径:",image_path+"....");
        navigationView = (NavigationView)findViewById(R.id.passenger_passenger_navView);
        navigationView.setCheckedItem(R.id.nav_setting);
        leftHead =  (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.Left_head);//获得左边头像的View
        Const.leftHead = leftHead;
        //取出头像路径
        if(!image_path.equals(""))
            leftHead.setImageBitmap(BitmapFactory.decodeFile(image_path));
        //从SP里面取出用户昵称然后显示
        nickName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nick_text);
        String name_nick = sharedPreferences.getString("NAME"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),"");
        if(!name_nick.equals("")||name_nick.equals("null"))
            nickName.setText(name_nick);
        //actionBar设置
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.tool_bar);
        }
        mapView = (MapView) findViewById(R.id.passenger_passenger_mapView);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();//得到地图实例
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置为普通地图
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));//设置缩放比例
        baiduMap.setOnMarkerClickListener(new MarkerClickerListener());
        baiduMap.setTrafficEnabled(true);
        //baiduMap.setOnMapClickListener(new MapClickerListener());
        UiSettings settings = baiduMap.getUiSettings();
        settings.setOverlookingGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
    }
    public void addListener(){
        //悬浮按钮监听
        inputInformation.setOnClickListener(new ViewClickListener());
        setTraffic.setOnClickListener(new ViewClickListener());
        //侧栏
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener());
        //路线规划
        routePlanSearch.setOnGetRoutePlanResultListener(new RoutePlanResultListener());
        //地图监听 长按清空坐标
        baiduMap.setOnMapLongClickListener(new MapLongClickerListener());

    }
    public void setOrder(){
        /*
        * 得到起点终点,起点经纬度,终点经纬度,后台请求
        * */
        final AlertDialog setTheOrder = new AlertDialog.Builder(Passenger.this)
                .setView(parent)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*getLocation("湘潭",startInput.getText().toString(),0);
                        getLocation("湘潭",endInput.getText().toString(),1);
                        startPlace = startInput.getText().toString();
                        endPlace = endInput.getText().toString();
                        startTime = getFormatTime(startTimeInput_h.getText().toString(),startTimeInput_m.getText().toString());
                        endTime = getFormatTime(endTimeInput_h.getText().toString(),endTimeInput_m.getText().toString());
                        supplyCar = getSupplyCar(canSupplyCar.getText().toString());
                        editor.putString("startPlace",startPlace);
                        editor.putString("endPlace",endPlace);
                        editor.putString("startTime",startTime);
                        editor.putString("endTime",endTime);
                        editor.putString("supplyCar",supplyCar);
                        editor.putString("publishTime",getPublishTime());
                        Log.d("startPlace-->",startPlace);
                        Log.d("endPlace-->",endPlace);
                        Log.d("startTime-->",startTime);
                        Log.d("endTime-->",endTime);
                        Log.d("supplyCar-->",supplyCar);
                        Log.d("publishTime-->",getPublishTime());
                        addRequest(Const.userName);*/
                        addPeopleToMap(Const.userName);
                        parent.removeAllViews();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parent.removeAllViews();
                    }
                }).create();
        setTheOrder.show();
    }
    public String getSupplyCar(String supplyCar){
        /*
        * 是否能够提供车辆
        * */
        if(supplyCar.trim().equals("是")){
            return "1";
        }else if (supplyCar.trim().equals("否")){
            return  "0";
        }
        return "";
    }
    public String getFormatTime(String hour,String minute){
        /*
        *获得格式化的时间,YY-MM-DD-HH-MM
         *  */
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        return year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-00";
    }
    public String getPublishTime(){
        /*
        * 获得发布订单的时间
        * */
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return String.valueOf(year+"-"+month+"-"+day+"-"+hour+"-"+minute+"-00");
    }
    public void addRequest(final String accountNumber){
        /*
        * 添加拼车请求
        * */
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/addcommuterequest.action";
        String tag = "addRequest";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final MyStringRequest request = new MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("result")){
                                Toast.makeText(getApplicationContext(),"添加请求成功,马上为您查找匹配的用户",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Passenger.this, MatchShow.class));
                            }else {
                                Toast.makeText(getApplicationContext(),"添加请求失败,请检查您的网络设置",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.d("JSONException--->",e.getMessage());
                            Toast.makeText(getApplicationContext(),"无网络连接",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.d("VolleyError",error.getCause().toString());
                Toast.makeText(getApplicationContext(),"请稍后重试"+error.getCause().toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.d("startLatLng-->",String.valueOf(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startLatLng","")));
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
    public void addPeopleToMap(final String accountNumber){
        Toast.makeText(Passenger.this,"正在为你找寻...",Toast.LENGTH_LONG).show();
        /*
        * 将匹配到的用户添加地图,分自己,其他乘客,司机三种标识
        * */
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/orderRequest.action?" +
                "userid=13531335537&startplacex=112.922279&startplacey=27.898291&destinationx=112.919487" +
                "&destinationy=27.881441&startdate=2018-09-02-09-00-00";
        String tag = "addRequest";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        Passenger.MyStringRequest request = new Passenger.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if(new JSONObject(response).getJSONArray("result").length()==0){
                                Toast.makeText(Passenger.this,"很抱歉,无车辆信息",Toast.LENGTH_SHORT).show();
                            }else{

                                JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                                Toast.makeText(Passenger.this,"为你找到1个司机和"+(jsonArray.length()-2)+"个顺路乘客",Toast.LENGTH_SHORT).show();
                                for(int i = 0;i < jsonArray.length();i++){

                                    Bundle bundle = new Bundle();
                                    bundle.putInt("Number",i);//将标记都标上号,即添加额外信息
                                    onRoadPassengers.add(jsonArray.getJSONObject(i));
                                    if (jsonArray.getJSONObject(i).getString("supplycar").equals("1")){
                                        a = new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex"));
                                        b = new LatLng(jsonArray.getJSONObject(i).getDouble("destinationy"),jsonArray.getJSONObject(i).getDouble("destinationx"));
                                        //司机
                                        MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car))
                                                .position(new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex")))
                                                .extraInfo(bundle);
                                        baiduMap.addOverlay(options);
                                    }else {
                                        if(jsonArray.getJSONObject(i).getString("userid").equals("13531335537")){
                                            //自己
                                            OverlayOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_myself))
                                                    .position(new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex")))
                                                    .extraInfo(bundle);
                                            baiduMap.addOverlay(options);
                                        }else {
                                            //其他乘客
                                            OverlayOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_user))
                                                    .position(new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex")))
                                                    .extraInfo(bundle);
                                            baiduMap.addOverlay(options);
                                        }

                                    }
                                    wayPoints.add(PlanNode.withLocation(new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex"))));
                                }
                                startGo(a,b,wayPoints);
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
               /* String startplacex = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startLatLng","").split(",")[0];
                String startplacey = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startLatLng","").split(",")[1];
                String destinationx = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endLatLng","").split(",")[0];
                String destinationy = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endLatLng","").split(",")[1];
                String start_place = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startPlace","");
                String end_place = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endPlace","");
                String start_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("startTime","");
                String end_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("endTime","");
                String supply_car = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("supplyCar","");
                String publish_time = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("publishTime","");*/
                Map<String,String> params = new HashMap<>();
                //params.put("userid", "i");  //注⑥
                //params.put("startplacex","112.922279");
                //params.put("startplacey","27.898291");
                //params.put("destinationx","112.919487");
                //params.put("destinationy","27.881441");
                //params.put("pulishtime","2018-09-02-09-00");
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public void startGo(LatLng start,LatLng end,List<PlanNode> wayPoints){
        try{
            PlanNode begin = PlanNode.withLocation(start);
            PlanNode destination = PlanNode.withLocation(end);
            routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(begin).to(destination).passBy(wayPoints));
        }catch (NullPointerException e){
            Toast.makeText(Passenger.this,"请点击地图标记终点",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(routePlanSearch!=null){
            routePlanSearch.destroy();
        }
        if (location!=null){
            location.unRegisterLocationListener(listener);
        }
    }
    public void drawRouteLine(DrivingRouteResult drivingRouteResult,int routeNum){
        /*
        * drivingRouteResult,结果,routeNum,路线编号
        * */
        int[] color = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DKGRAY
                ,Color.GRAY,Color.GREEN,Color.LTGRAY,Color.YELLOW, Color.RED,Color.MAGENTA};//颜色的数组，用来随机选一种颜色表示路线
        List<LatLng> linePoints = new ArrayList<>();//路线上点的集合
        //百度地图的一条路线分为路段，getAllStep就是得到一条路线的所有路段，
        // 然后再一条路段上用getWayPoints路段的点，点一般为转弯处或者交叉路口
        for(int i = 0; i < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().size();i++){
            for (int j = 0 ;j < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().size();j++){
                LatLng node = new LatLng(drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).latitude
                ,drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).longitude);
                linePoints.add(node);//将点添加到集合上
            }
            OverlayOptions ooPolyLine = new PolylineOptions().width(5).color(color[(int)(Math.random()*10)]).points(linePoints);//设置折线的属性,颜色等
            Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyLine);//添加到地图
        }
    }

    static class MyStringRequest extends StringRequest {

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }
        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // TODO Auto-generated method stub
            String str = null;
            try {
                str = new String(response.data,"utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
        }
    }
    private class MyTextWatcher implements TextWatcher,OnGetPoiSearchResultListener,AdapterView.OnItemClickListener{

        List<String> searchResult;
        AutoCompleteTextView textView;
        PoiSearch poiSearch;
        PoiCitySearchOption poiCitySearchOption;
        ArrayAdapter<String> resultAdapter;
        MyTextWatcher(AutoCompleteTextView autoCompleteTextView){
            textView = autoCompleteTextView;
            poiSearch = PoiSearch.newInstance();
            poiCitySearchOption = new PoiCitySearchOption();
            searchResult = new ArrayList<>();
            resultAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.search_content, searchResult);
            textView.setOnItemClickListener(this);
        }
        public void addSearch(String address){
            //根据城市名和key搜索
            poiCitySearchOption.city("湘潭市").keyword(address).pageNum(0).pageCapacity(10);
            poiSearch.searchInCity(poiCitySearchOption);
            poiSearch.setOnGetPoiSearchResultListener(this);
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            addSearch(charSequence.toString());
            //Toast.makeText(getApplicationContext(),"onTextChanged--"+charSequence.toString(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            addSearch(editable.toString());
            //Toast.makeText(getApplicationContext(),"afterTextChanged--"+editable.toString(),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if(poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                if (searchResult.size() != 0) {
                    //如果不是空  就先清除之前的查询结果
                    searchResult.clear();
                    searchResult.add("将当前位置设置为起点");
                    for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                        //添加进入列表
                        searchResult.add(poiResult.getAllPoi().get(i).name + "," + poiResult.getAllPoi().get(i).address);
                    }
                    //适配器的初始化和设置适配器
                    ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.search_content, searchResult);
                    textView.setAdapter(resultAdapter);
                }else {
                    searchResult.add("将当前位置设置为起点");
                    for (int i = 0; i < poiResult.getAllPoi().size(); i++) {
                        //添加进入列表
                        searchResult.add(poiResult.getAllPoi().get(i).name + "," + poiResult.getAllPoi().get(i).address);
                    }
                    //适配器的初始化和设置适配器
                    ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.search_content, searchResult);
                    textView.setAdapter(resultAdapter);
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(i==0&&textView.getId()==R.id.startPlace){
                textView.setText(latestLocationInfo.getText().toString().split(":")[1]);
            }else {
                textView.setText(searchResult.get(i));
            }
        }
    }
    private class MyLocationListener implements BDLocationListener {
        /*
        * 如要实现通过GPS定位起点 需要将mapView变为static
        * */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng center = new LatLng(latitude,longitude);
                Const.location = center;
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(Const.location);
                baiduMap.animateMapStatus(update);
                MarkerOptions options = new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation));
                myLocationOption = options;
                baiduMap.addOverlay(options);
                //myCity = location.getCity();
                latestLocationInfo.setText("您的位置:"+location.getProvince()
                        + location.getCity()
                        + location.getDistrict()
                        + location.getStreet()
                        + location.getStreetNumber());
            }
        }
    }
    private class ViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.passenger_passenger_start:
                    initDialogEvents();
                    setOrder();
                    break;
                case R.id.passenger_passenger_setTraffic:
                    clickNum++;
                    baiduMap.setTrafficEnabled(isTraffic.get(clickNum%2));
                    break;
            }
        }
    }
    private class RoutePlanResultListener implements OnGetRoutePlanResultListener {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //驾车路线规划
            if(drivingRouteResult.error== SearchResult.ERRORNO.NO_ERROR){
                for(int i = 0;i < drivingRouteResult.getRouteLines().size();i++){
                    drawRouteLine(drivingRouteResult,i);
                }

            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }
    }
    private class GeoCoderResultListener implements OnGetGeoCoderResultListener{

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult==null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
                Toast.makeText(Passenger.this,"请适当移动你的位置来获得你的位置信息",Toast.LENGTH_SHORT).show();
            }else {
                latestLocationInfo.setText("您的位置:"+reverseGeoCodeResult.getAddressDetail().city+reverseGeoCodeResult.getAddressDetail().district+reverseGeoCodeResult.getAddressDetail().street);
            }
        }
    }
    private class LocationListener implements android.location.LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            //实时对位置进行监听并更新位置
            GeoCoder geoCoder = GeoCoder.newInstance();
            geoCoder.setOnGetGeoCodeResultListener(new GeoCoderResultListener());
            LatLng center = new LatLng(location.getLatitude(),location.getLongitude());
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(center));
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(center);
            baiduMap.animateMapStatus(update);
            MarkerOptions options = new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation));
            myLocationOption = options;
            baiduMap.clear();
            baiduMap.addOverlay(options);
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }
        @Override
        public void onProviderEnabled(String s) {
        }
        @Override
        public void onProviderDisabled(String s) {
        }
    }
    private class MarkerClickerListener implements BaiduMap.OnMarkerClickListener{

        @Override
        public boolean onMarkerClick(Marker marker) {
            Toast.makeText(getApplicationContext(),"标记监听",Toast.LENGTH_SHORT).show();
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.on_road_pandd, null);
            view.setBackgroundResource(R.drawable.icon_info_background);
            TextView userName;
            TextView userTel;
            TextView startToEnd;
            TextView startTime;
            TextView endTime;
            TextView moneyBefore;
            TextView moneyAfter;
            TextView carNum;
            Button toChat;
            Button cancel;
            Button toCall;
            int i = marker.getExtraInfo().getInt("Number");
            try{
                if(onRoadPassengers.get(i).getString("supplycar").equals("1")){
                    view = inflater.inflate(R.layout.on_road_driver,null);
                    view.setBackgroundResource(R.drawable.icon_info_background);
                    userName = (TextView)view.findViewById(R.id.onroad_passenger_name);
                    userTel  = (TextView)view.findViewById(R.id.onroad_passenger_tel);
                    startToEnd = (TextView)view.findViewById(R.id.onroad_passenger_startToEnd);
                    startTime = (TextView)view.findViewById(R.id.onroad_passenger_starttime);
                    endTime = (TextView)view.findViewById(R.id.onroad_passenger_endtime);
                    moneyBefore = (TextView)view.findViewById(R.id.onroad_passenger_moneybefore);
                    moneyAfter = (TextView)view.findViewById(R.id.onroad_passenger_moneyafter);
                    carNum = (TextView)view.findViewById(R.id.onroad_passenger_carNum);
                    toChat = (Button)view.findViewById(R.id.onroad_passenger_chat);
                    cancel = (Button)view.findViewById(R.id.onroad_passenger_cancel);
                    toCall = (Button)view.findViewById(R.id.onroad_passenger_call);
                    SharedPreferences.Editor editor = getSharedPreferences("Setting",MODE_MULTI_PROCESS).edit();
                    editor.putString("destinationTel",userTel.getText().toString());
                    editor.commit();
                    Log.d("对方账号是",userTel.getText().toString());
                    toChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Passenger.this,Chatting.class);
                            startActivity(intent);
                        }
                    });

                    userName.setText(onRoadPassengers.get(i).getString("name"));
                    userTel.setText(onRoadPassengers.get(i).getString("userid"));
                    startToEnd.setText(onRoadPassengers.get(i).getString("startplace")+"---->"+onRoadPassengers.get(i).getString("destination"));
                    startTime.setText(onRoadPassengers.get(i).getString("startdate"));
                    endTime.setText(onRoadPassengers.get(i).getString("enddate"));
                    moneyBefore.setText(onRoadPassengers.get(i).getString("spendMoney")+"元");
                    moneyAfter.setText(onRoadPassengers.get(i).getString("sharingMoney")+"元");
                    carNum.setText(onRoadPassengers.get(i).getString("carnum"));

                    final String telephoneNumber = userTel.getText().toString();
                    toCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telephoneNumber)));
                        }
                    });
                    final AlertDialog dialog = new AlertDialog.Builder(Passenger.this)
                            .setTitle("乘客信息")
                            .setView(view)
                            .create();
                    dialog.setTitle("用户乘客");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    Toast.makeText(getApplicationContext(),"司机",Toast.LENGTH_SHORT).show();
                }else {
                    view = inflater.inflate(R.layout.on_road_pandd,null);
                    view.setBackgroundResource(R.drawable.icon_info_background);
                    userName = (TextView)view.findViewById(R.id.onroad_passenger_name);
                    userTel  = (TextView)view.findViewById(R.id.onroad_passenger_tel);
                    startToEnd = (TextView)view.findViewById(R.id.onroad_passenger_startToEnd);
                    startTime = (TextView)view.findViewById(R.id.onroad_passenger_starttime);
                    endTime = (TextView)view.findViewById(R.id.onroad_passenger_endtime);
                    moneyBefore = (TextView)view.findViewById(R.id.onroad_passenger_moneybefore);
                    moneyAfter = (TextView)view.findViewById(R.id.onroad_passenger_moneyafter);
                    toChat = (Button)view.findViewById(R.id.onroad_passenger_chat);
                    cancel = (Button)view.findViewById(R.id.onroad_passenger_cancel);
                    toCall = (Button)view.findViewById(R.id.onroad_passenger_call);
                    toChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Passenger.this,Chatting.class));
                        }
                    });
                    userName.setText(onRoadPassengers.get(i).getString("name"));
                    userTel.setText(onRoadPassengers.get(i).getString("userid"));
                    startToEnd.setText(onRoadPassengers.get(i).getString("startplace")+"---->"+onRoadPassengers.get(i).getString("destination"));
                    startTime.setText(onRoadPassengers.get(i).getString("startdate"));
                    endTime.setText(onRoadPassengers.get(i).getString("enddate"));
                    moneyBefore.setText(onRoadPassengers.get(i).getString("spendMoney")+"元");
                    moneyAfter.setText(onRoadPassengers.get(i).getString("sharingMoney")+"元");
                    SharedPreferences.Editor editor = getSharedPreferences("Setting",MODE_MULTI_PROCESS).edit();
                    editor.putString("destinationTel",userTel.getText().toString());
                    editor.commit();
                    Log.d("对方账号是",userTel.getText().toString());
                    final String telephoneNumber = userTel.getText().toString();
                    toCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telephoneNumber)));
                        }
                    });
                    final AlertDialog dialog = new AlertDialog.Builder(Passenger.this)
                            .setTitle("用户信息")
                            .setView(view)
                            .create();
                    dialog.setTitle("附近乘客");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"出问题了",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return true;
        }
    }
    private class MapLongClickerListener implements BaiduMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            baiduMap.clear();
        }
    }
    private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //判断点击了哪个item
            switch (item.getItemId()){
                //设置
                case R.id.nav_setting:
                    Intent to_setting = new Intent(Passenger.this,Setting.class);
                    to_setting.putExtra("LEFT_HEAD_SCULPTURE",R.id.Left_head);
                    to_setting.putExtra("DriverOrPassenger","Passenger");
                    startActivity(to_setting);
                    break;
                case R.id.account_setting:
                    Intent to_AccountSetting = new Intent(Passenger.this,AccountSetting.class);
                    startActivity(to_AccountSetting);
                    break;
                //关于
                case R.id.nav_about:
                    Intent to_about = new Intent(Passenger.this,About.class);
                    startActivity(to_about);
                    break;
                //注销
                case R.id.nav_exit:
                    //点击了注销登录,将自动登录的选项还原
                    SharedPreferences sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("canLogin","no");
                    editor.commit();
                    Intent to_login = new Intent(Passenger.this,Login.class);
                    startActivity(to_login);
                    finish();
                    break;
            }
            return true;
        }
    }

}
