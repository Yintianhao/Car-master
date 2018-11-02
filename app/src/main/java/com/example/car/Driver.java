package com.example.car;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.autonavi.ae.pos.GpsInfo;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
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
import com.baidu.mapapi.search.geocode.GeoCodeOption;
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
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Tool.Const;
import Tool.GPSConvert;
import Tool.MatchRecord;
import Tool.MyPoi;
import Tool.NearbyPassengerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class Driver extends AppCompatActivity {
    MapView mapView;//地图视图
    BaiduMap baiduMap;//地图实例
    DrawerLayout drawerLayout;//DrawerLayout布局
    CircleImageView leftHead;//左边的头像
    SharedPreferences sharedPreferences;
    String image_path;//侧滑栏头像存取路径
    TextView nickName;
    NavigationView navigationView;
    TextView latestLocationInfo;
    // 声明LocationClient类
    LocationClient location = null;
    //获取位置监听
    Driver.MyLocationListener listener = null;
    PoiSearch poiSearch;//poi检索实例
    PoiCitySearchOption poiCitySearchOption;//信息
    AutoCompleteTextView searchByInput;//输入内容
    Button search;//搜索按钮
    String myCity;//城市名
    String myAddress;
    List<String> searchResult;
    GeoCoder searchByAddress;
    MarkerOptions myLocationOption;
    LatLng start;//起点坐标
    LatLng end;//终点坐标
    RoutePlanSearch routePlanSearch;
    FloatingActionButton go;
    FloatingActionButton searchPeople;
    FloatingActionButton searchOnRoad;
    String backMessage;
    List<MatchRecord> setOfNearByPassenger;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    NearbyPassengerAdapter matchAdapter;
    LayoutInflater inflater;
    LinearLayout linearLayout;
    AlertDialog dialog;
    BDLocation startLocation;
    List<LatLng> wayNodes;
    List<String> wayNames;
    List<JSONObject> onRoadPassengers;
    LatLng markerLocation;
    FloatingActionButton setTraffic;
    List<Boolean> isTraffic;
    int clickNum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initDrawerLayout();//初始化控件
        //initRecyclerView();
        addListener();//添加监听器
        isAndroidSix();//判断安卓的版本
        //initMyTestLocation();
    }
    public void initRecyclerView(){
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) inflater.inflate(R.layout.nearby_passenger_list, null);
        recyclerView = (RecyclerView)linearLayout.findViewById(R.id.nearby_passengers);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        matchAdapter = new NearbyPassengerAdapter(setOfNearByPassenger);
        recyclerView.setAdapter(matchAdapter);
    }
    public void initDrawerLayout(){
        //initLocation();
        latestLocationInfo = (TextView) findViewById(R.id.driver_locationInfo);
        routePlanSearch =RoutePlanSearch.newInstance();
        go = (FloatingActionButton)findViewById(R.id.driver_start);
        searchOnRoad = (FloatingActionButton)findViewById(R.id.driver_onRoadPassenger);
        drawerLayout = (DrawerLayout)findViewById(R.id.driver_driverDrawerLayout);
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        image_path = sharedPreferences.getString("driver_image_path"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),"");
        Log.d("侧滑栏图片路径:",image_path+"....");
        navigationView = (NavigationView)findViewById(R.id.driver_navView);
        leftHead =  (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.driver_Left_head);//获得左边头像的View
        Const.leftHead = leftHead;//以便于之后设置头像同步更新
        if(!image_path.equals(""))//取出头像的图片的路径
            leftHead.setImageBitmap(BitmapFactory.decodeFile(image_path));
        //从SP里面取出用户昵称然后显示
        nickName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.driver_nick_text);
        nickName.setText(sharedPreferences.getString("NAME"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),""));
        ActionBar actionBar = getSupportActionBar();
        mapView = (MapView) findViewById(R.id.driver_mapView);
        mapView.showZoomControls(false);
        baiduMap = mapView.getMap();//得到地图实例
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);//设置为普通地图
        baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));//设置缩放比例
        baiduMap.setOnMarkerClickListener(new MarkerClickListener());
        UiSettings settings = baiduMap.getUiSettings();
        settings.setOverlookingGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        if(actionBar!=null){
            //actionBart的一些设置
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.tool_bar);
        }
        //实例化
        navigationView.setCheckedItem(R.id.driver_nav_setting);
        poiSearch = PoiSearch.newInstance();
        search  = (Button)findViewById(R.id.driver_search);
        searchByInput = (AutoCompleteTextView) findViewById(R.id.driver_searchContent);
        //searchByInput.setHint(null);
        poiCitySearchOption = new PoiCitySearchOption();
        searchByAddress = GeoCoder.newInstance();
        searchPeople = (FloatingActionButton)findViewById(R.id.driver_refreshPassenger);
        setTraffic = (FloatingActionButton)findViewById(R.id.driver_setTraffic);
        isTraffic = new ArrayList<>();
        isTraffic.add(false);
        isTraffic.add(true);
    }
    public void addListener(){
        //悬浮按钮监听
        go.setOnClickListener(new ViewClickListener());
        navigationView.setNavigationItemSelectedListener(new NavigationViewListener());
        //搜索按钮监听
        search.setOnClickListener(new ViewClickListener());
        //搜索
        poiSearch.setOnGetPoiSearchResultListener(new PoiSearchResultListener());
        //文本改变监听
        searchByInput.addTextChangedListener(new TextWatcher());
        searchByInput.setOnItemClickListener(new ItemClickListener());
        //根据选定的Item进行反地图编码得到经纬度
        searchByAddress.setOnGetGeoCodeResultListener(new GeoCoderResultListener());
        //路线规划
        routePlanSearch.setOnGetRoutePlanResultListener(new GetRoutePlanResultListener());
        baiduMap.setOnMapLongClickListener(new MapLongClickListener());
        searchPeople.setOnClickListener(new ViewClickListener());
        searchOnRoad.setOnClickListener(new ViewClickListener());
        baiduMap.setOnMapClickListener(new MapClickListener());
        setTraffic.setOnClickListener(new ViewClickListener());
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
        latestLocationInfo = (TextView) findViewById(R.id.driver_locationInfo);
        location = new LocationClient(getApplicationContext());
        listener = new Driver.MyLocationListener();
        location.registerLocationListener(listener);// 注册监听函数
        setViews();
        location.start();
    }
    public void addSearch(){
        //根据城市名和key搜索
        poiCitySearchOption = new PoiCitySearchOption();
        poiCitySearchOption.city("湘潭市").keyword(searchByInput.getText().toString()).pageNum(0).pageCapacity(10);
        poiSearch.searchInCity(poiCitySearchOption);
    }
    public void setViews() {
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
            ActivityCompat.requestPermissions(Driver.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},100);
        }else{
            initLocation();
        }
    }
    public void startGo(LatLng start,LatLng end){
        try{
            PlanNode begin = PlanNode.withLocation(start);
            PlanNode destination = PlanNode.withLocation(end);
            routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(begin).to(destination));
        }catch (NullPointerException e){
            Toast.makeText(Driver.this,"请点击地图标记终点",Toast.LENGTH_SHORT).show();
        }
    }
    public void voiceNavi(LatLng start,LatLng end,List<Poi> wayList) {
        double[] startConverted  = GPSConvert.bd09_To_Gcj02(start.latitude,start.longitude);
        double[] endConverted = GPSConvert.bd09_To_Gcj02(end.latitude,end.longitude);
        Poi startloc = new Poi("当前位置", new com.amap.api.maps.model.LatLng(startConverted[0],startConverted[1]), "");
        Poi endloc = new Poi("目的地", new com.amap.api.maps.model.LatLng(endConverted[0], endConverted[1]), "B000A83M61");
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(startloc, wayList, endloc, AmapNaviType.DRIVER), new INaviInfoCallback() {
            @Override
            public void onInitNaviFailure() {
                Toast.makeText(getApplicationContext(),"失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetNavigationText(String s) {
            }

            @Override
            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

            }

            @Override
            public void onArriveDestination(boolean b) {

            }

            @Override
            public void onStartNavi(int i) {

            }

            @Override
            public void onCalculateRouteSuccess(int[] ints) {

            }

            @Override
            public void onCalculateRouteFailure(int i) {

            }

            @Override
            public void onStopSpeaking() {

            }

            @Override
            public void onReCalculateRoute(int i) {

            }

            @Override
            public void onExitPage(int i) {

            }

            @Override
            public void onStrategyChanged(int i) {

            }

            @Override
            public View getCustomNaviBottomView() {
                return null;
            }

            @Override
            public View getCustomNaviView() {
                return null;
            }

            @Override
            public void onArrivedWayPoint(int i) {

            }
        });
    }
    public void drawRouteLine(DrivingRouteResult drivingRouteResult,int routeNum){
        if (wayNodes!=null){
            wayNodes.clear();
            wayNames.clear();
        }
        int[] color = {Color.BLACK,Color.BLUE,Color.CYAN,Color.DKGRAY
                ,Color.GRAY,Color.GREEN,Color.LTGRAY,Color.YELLOW, Color.RED,Color.MAGENTA};//颜色的数组，用来随机选一种颜色表示路线
        List<LatLng> linePoints = new ArrayList<>();//路线上点的集合
        wayNodes = new ArrayList<>();
        wayNames = new ArrayList<>();
        //百度地图的一条路线分为路段，getAllStep就是得到一条路线的所有路段，
        // 然后再一条路段上用getWayPoints路段的点，点一般为转弯处或者交叉路口
        for(int i = 0; i < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().size();i++){
            for (int j = 0 ;j < drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().size();j++){
                LatLng node = new LatLng(drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).latitude
                        ,drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getWayPoints().get(j).longitude);
                if(j==0){
                    OverlayOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_point))
                            .position(node);
                    wayNodes.add(node);
                    wayNames.add(drivingRouteResult.getRouteLines().get(routeNum).getAllStep().get(i).getInstructions());
                    baiduMap.addOverlay(options);
                }
                linePoints.add(node);//将点添加到集合上
            }
            OverlayOptions ooPolyLine = new PolylineOptions().width(5).color(color[(int)(Math.random()*10)]).points(linePoints);//设置折线的属性,颜色等
            Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyLine);//添加到地图
        }
    }
    public String getMessage(LatLng position){

        GeoCoder searchByLatLng = GeoCoder.newInstance();
        searchByLatLng.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                backMessage = reverseGeoCodeResult.getAddress();

            }
        });
        searchByLatLng.reverseGeoCode(new ReverseGeoCodeOption().location(position));
        return backMessage;
    }
    public void getNearbyCar(final String userID, final String latitude, final String longitude){

        String url = "http://47.106.72.170:8080/MyCarSharing/taxiRequestWithStart.action";
        String tag = "findNearbyCar";
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
                                Toast.makeText(Driver.this,"很抱歉,您附近无车辆信息",Toast.LENGTH_SHORT).show();
                            }else{
                                setOfNearByPassenger = new ArrayList<>();
                                Toast.makeText(getApplicationContext(),"你附近有"+new JSONObject(response).getJSONArray("result").length()+"个客人",Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                                for(int i = 0;i < jsonArray.length();i++){
                                    Log.d("经纬度"+String.valueOf(jsonArray.getJSONObject(i).getDouble("startplacey"))," "+jsonArray.getJSONObject(i).getDouble("startplacex"));
                                    LatLng node = new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),
                                            jsonArray.getJSONObject(i).getDouble("startplacex"));
                                    OverlayOptions options = new MarkerOptions()
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_user))
                                            .position(node);
                                    baiduMap.addOverlay(options);
                                    setOfNearByPassenger.add(new MatchRecord(jsonArray.getJSONObject(i)));
                                }
                                showNearbyPassenger();
                                OverlayOptions options = new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation))
                                        .position(start);
                                baiduMap.addOverlay(options);
                                matchAdapter.notifyDataSetChanged();
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
                Map<String,String> params = new HashMap<>();
                params.put("userid",userID);
                params.put("startplacex",longitude);
                params.put("startplacey",latitude);
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public void showNearbyPassenger(){
        initRecyclerView();
        dialog = new AlertDialog.Builder(this)
                .setTitle("附近乘客")
                .setView(linearLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("点击了","确定");
                        linearLayout.removeAllViews();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("点击了","取消");
                        linearLayout.removeAllViews();
                    }
                }).create();
        dialog.setTitle("附近乘客");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        /*
         * 调整对话框的参数
         * Gravity.BOTTOM设置显示的位置在屏幕下方
         * */
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(params);
    }
    public void showDirectionInfo(){
        try {
            if(wayNodes.size()==0){
                Toast.makeText(getApplicationContext(),"请先选择目的地",Toast.LENGTH_SHORT).show();
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i = 0;i < wayNodes.size();i++){
                            LatLng center = wayNodes.get(i);
                            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(center);
                            baiduMap.animateMapStatus(update);
                            initInfoWindow(wayNodes.get(i),wayNames.get(i));
                            try{
                                Thread.sleep(3000);
                            }catch (InterruptedException e){

                            }
                        }
                        initInfoWindow(end,"确认到达");
                    }
                }).start();
            }

        }catch (NullPointerException r){

        }
    }
    public void getRoadNearbyCar(final String phoneNum, final String nowLatitude, final String nowLongitude, final String endLatitude,
                                 final String endLongitude, final String startDate, final String endDate){

        onRoadPassengers = new ArrayList<>();
        String url = "http://47.106.72.170:8080/MyCarSharing/taxiRequestWithStartAndEnd.action";
        String tag = "findNearbyCar";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final Passenger.MyStringRequest request = new Passenger.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response",response);
                        try {
                            if(new JSONObject(response).getJSONArray("result").length()==0){
                                Toast.makeText(Driver.this,"顺路无车辆信息",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"目前您顺路有"+new JSONObject(response).getJSONArray("result").length()+"个客人",Toast.LENGTH_SHORT).show();
                                JSONArray jsonArray = new JSONObject(response).getJSONArray("result");
                                for(int i = 0;i < jsonArray.length();i++){
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("Number",i);
                                    onRoadPassengers.add(jsonArray.getJSONObject(i));
                                    MarkerOptions options = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_user))
                                            .position(new LatLng(jsonArray.getJSONObject(i).getDouble("startplacey"),jsonArray.getJSONObject(i).getDouble("startplacex")))
                                            .extraInfo(bundle);
                                    baiduMap.addOverlay(options);
                                }
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
                Toast.makeText(getApplicationContext(),"请稍后重试",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userid",phoneNum);
                params.put("startplacex",nowLongitude);
                params.put("startplacey",nowLatitude);
                params.put("startdate", startDate);
                params.put("enddate", endDate);
                params.put("destinationx",endLongitude);
                params.put("destinationy",endLatitude);
                //Log.d("Tel-->",phoneNum);
                Log.d("startTime-->",startDate);
                Log.d("endTime-->",endDate);
                Log.d("经纬度-->",nowLatitude+" "+endLatitude+" "+nowLongitude+" "+endLongitude);
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public void initInfoWindow(LatLng node,String info){
        TextView infoWindowTv=new TextView(Driver.this);
        infoWindowTv.setBackgroundResource(R.drawable.icon_location_tips);
        infoWindowTv.setText(info);
        infoWindowTv.setTextColor(Color.WHITE);
        infoWindowTv.setGravity(Gravity.CENTER);
        //在地图中显示一个信息窗口，可以设置一个View作为该窗口的内容，也可以设置一个 BitmapDescriptor 作为该窗口的内容
        InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(infoWindowTv), node, -47, new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                //当InfoWindow被点击后隐藏
            }
        });
        //InfoWindow infoWindow = new InfoWindow(button, latLng, -47);
        //显示信息窗口
        baiduMap.showInfoWindow(infoWindow);
    }
    public void getPredictedCar(){
        /*
         * 获得格式化的时间,YY-MM-DD-HH-MM
         * hour 当前小时 minute 当前分钟 requestSeconds 请求参数
         *  */
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int requestSeconds = hour*3600+minute*60;
        String url = "http://47.106.72.170:8080/";
        String tag = "findPredictedCar";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        Passenger.MyStringRequest request = new Passenger.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response",response);
                        /*try {

                        } catch (JSONException e) {
                            Log.d("JSON","解析问题");
                        }*/
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(10*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
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
    //返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(poiSearch!=null){
            poiSearch.destroy();
        }
    }
    //活动销毁
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(poiSearch!=null){
            poiSearch.destroy();
        }
        if(routePlanSearch!=null){
            routePlanSearch.destroy();
        }
    }


    private class MyLocationListener implements BDLocationListener {

        /*
         * 如要实现通过GPS定位起点 需要将mapView变为static
         * */
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location != null) {
                startLocation = location;
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                start = new LatLng(latitude,longitude);
                LatLng center = new LatLng(latitude,longitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(center);
                baiduMap.animateMapStatus(update);
                MarkerOptions options = new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation));
                myLocationOption = options;
                baiduMap.addOverlay(options);
                myCity = location.getCity();
                latestLocationInfo.setText("您的位置:"+location.getProvince()
                        + location.getCity()
                        + location.getDistrict()
                        + location.getStreet()
                        + location.getStreetNumber());
            }
        }
    }
    private class NavigationViewListener implements NavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //判断点击了哪个item
            switch (item.getItemId()){
                //设置
                case R.id.driver_nav_setting:
                    Intent to_setting = new Intent(Driver.this,Setting.class);
                    to_setting.putExtra("LEFT_HEAD_SCULPTURE",R.id.Left_head);
                    to_setting.putExtra("DriverOrPassenger","Driver");
                    startActivity(to_setting);
                    break;
                //关于
                case R.id.driver_nav_about:
                    Intent to_about = new Intent(Driver.this,About.class);
                    startActivity(to_about);
                    break;
                //注销
                case R.id.driver_nav_exit:
                    //点击了注销登录,将自动登录的选项还原
                    SharedPreferences sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("canLogin","no");
                    editor.commit();
                    Intent to_login = new Intent(Driver.this,Login.class);
                    startActivity(to_login);
                    finish();
                    break;
                case R.id.driver_car_setting:
                    startActivity(new Intent(Driver.this,CarSetting.class));
                    break;
                case R.id.driver_account_setting:
                    startActivity(new Intent(Driver.this,AccountSetting.class));
                    break;
                default:
                    break;
            }
            return true;
        }
    }
    private class PoiSearchResultListener implements OnGetPoiSearchResultListener{

        //poi搜索结果
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            searchResult = new ArrayList<>();
            if(poiResult.error == SearchResult.ERRORNO.NO_ERROR){
                if(searchResult.size()!=0){
                    //如果不是空  就先清除之前的查询结果
                    searchResult.clear();
                    for (int i = 0; i < poiResult.getAllPoi().size();i++){
                        //添加进入列表
                        searchResult.add(poiResult.getAllPoi().get(i).name+","+poiResult.getAllPoi().get(i).area);
                    }
                    //适配器的初始化和设置适配器
                    ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.search_content,searchResult);
                    searchByInput.setAdapter(resultAdapter);
                }else {
                    for (int i = 0; i < poiResult.getAllPoi().size();i++){
                        searchResult.add(poiResult.getAllPoi().get(i).name+","+poiResult.getAllPoi().get(i).address);
                    }
                    ArrayAdapter<String> resultAdapter = new ArrayAdapter<>(getApplicationContext(),R.layout.search_content,searchResult);
                    searchByInput.setAdapter(resultAdapter);
                }
            }
        }
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        }
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
        }
    }
    private class GeoCoderResultListener implements OnGetGeoCoderResultListener{

        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            if(geoCodeResult.error==SearchResult.ERRORNO.NO_ERROR){
                end = new LatLng(geoCodeResult.getLocation().latitude,geoCodeResult.getLocation().longitude);
                OverlayOptions destinationOption = new MarkerOptions()
                        .position(end).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_end));
                baiduMap.addOverlay(destinationOption);
                baiduMap.addOverlay(myLocationOption);
            }else {
                Toast.makeText(Driver.this,"查询失败",Toast.LENGTH_SHORT).show();
                baiduMap.addOverlay(myLocationOption);
            }
        }
        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        }
    }
    private class GetRoutePlanResultListener implements OnGetRoutePlanResultListener{
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
                drawRouteLine(drivingRouteResult,0);
            }
        }
        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        }
        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
        }

    }
    private class MapLongClickListener implements BaiduMap.OnMapLongClickListener{

        @Override
        public void onMapLongClick(LatLng latLng) {
            baiduMap.clear();
            OverlayOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mylocation))
                    .position(start);
            baiduMap.addOverlay(options);

        }

    }
    private class MapClickListener implements BaiduMap.OnMapClickListener {

        @Override
        public void onMapClick(final LatLng latLng) {
            baiduMap.hideInfoWindow();
            end = latLng;
            AlertDialog notice = new AlertDialog.Builder(Driver.this)
                    .setIcon(R.drawable.ic_start)
                    .setTitle(getMessage(latLng)+",确定要去这个地方吗?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OverlayOptions optionEnd = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_end))
                                    .position(latLng);
                            baiduMap.addOverlay(optionEnd);
                            OverlayOptions optionStart = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_latestlocation))
                                    .position(start);
                            baiduMap.addOverlay(optionStart);
                            startGo(start,latLng);
                            //setTimer();
                            dialog.dismiss();
                        }
                    }).create();
            notice.show();
        }
        @Override
        public boolean onMapPoiClick(final MapPoi mapPoi) {
            end = mapPoi.getPosition();
            AlertDialog notice = new AlertDialog.Builder(Driver.this)
                    .setIcon(R.drawable.ic_start)
                    .setTitle(mapPoi.getName()+",确定要去这个地方吗?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OverlayOptions optionEnd = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_end))
                                    .position(mapPoi.getPosition());
                            baiduMap.addOverlay(optionEnd);
                            OverlayOptions optionStart = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_latestlocation))
                                    .position(start);
                            baiduMap.addOverlay(optionStart);
                            startGo(start,mapPoi.getPosition());
                            //setTimer();
                            dialog.dismiss();
                        }
                    }).create();
            notice.show();
            return true;
        }

    }
    private class TextWatcher implements android.text.TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //改变前
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //改变中
            addSearch();
        }

        @Override
        public void afterTextChanged(Editable s) {
            //改变后
            addSearch();
        }

    }
    private class ItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try{
                //记录位置
                myAddress = searchResult.get(position);
                searchByAddress.geocode(new GeoCodeOption().city(myCity).address(myAddress));
                baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(13));
            }catch (NullPointerException e){
                Toast.makeText(Driver.this,"请输入内容",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class ViewClickListener implements View.OnClickListener{

        //全局点击监听
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.driver_setTraffic:
                    clickNum++;
                    baiduMap.setTrafficEnabled(isTraffic.get(clickNum%2));
                    break;
                case R.id.onroad_passenger_go:
                    baiduMap.hideInfoWindow();
                    startGo(start,markerLocation);
                    voiceNavi(start,markerLocation,new ArrayList<Poi>());
                    break;
                case R.id.onroad_passenger_chat:
                    baiduMap.hideInfoWindow();
                    Intent intent = new Intent(Driver.this,Chatting.class);
                    startActivity(intent);
                    break;
                case R.id.driver_onRoadPassenger:
                    try {
                        getRoadNearbyCar(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),String.valueOf(start.latitude),String.valueOf(start.longitude),String.valueOf(end.latitude),String.valueOf(end.longitude),"2018-09-02-9-00-00","2018-09-02-9-00-30");
                    }catch (NullPointerException e){
                        Toast.makeText(getApplicationContext(),"请先选择一个目的地",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.driver_start:
                    startGo(start,end);
                    List<Poi> wayList = new ArrayList();//途径点目前最多支持3个。
                    try{
                        if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(0)));
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(1)));
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(2)));
                        }
                        voiceNavi(start,end,wayList);
                    }catch (JSONException e){

                    }catch (NullPointerException e){
                        Toast.makeText(getApplicationContext(),"请在地图上选择一个目的地",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.driver_search:
                    baiduMap.clear();//将地图上的标记清空
                    Toast.makeText(Driver.this,"查询...",Toast.LENGTH_SHORT).show();
                    //开始查询 根据城市名+地址
                    myAddress = searchByInput.getText().toString();
                    searchByAddress.geocode(new GeoCodeOption().city("湘潭市").address(myAddress));
                    baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(13));
                    break;
                case R.id.driver_refreshPassenger:
                    baiduMap.clear();
                    getNearbyCar("1365743316","27.90553","112.92297");
                    break;
                default:
                    break;
            }
        }


    }
    private class MarkerClickListener implements BaiduMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            try{
                markerLocation = marker.getPosition();
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                View view = inflater.inflate(R.layout.on_road_passenger, null);
                view.setBackgroundResource(R.drawable.icon_info_background);
                TextView userName = (TextView)view.findViewById(R.id.onroad_passenger_name);
                TextView userTel  = (TextView)view.findViewById(R.id.onroad_passenger_tel);
                TextView startToEnd = (TextView)view.findViewById(R.id.onroad_passenger_startToEnd);
                TextView startTime = (TextView)view.findViewById(R.id.onroad_passenger_starttime);
                TextView endTime = (TextView)view.findViewById(R.id.onroad_passenger_endtime);
                Button toChat = (Button)view.findViewById(R.id.onroad_passenger_chat);
                Button toGo = (Button)view.findViewById(R.id.onroad_passenger_go);
                Button cancel = (Button)view.findViewById(R.id.onroad_passenger_cancel);
                Button toCall = (Button)view.findViewById(R.id.onroad_passenger_call);
                toChat.setOnClickListener(new ViewClickListener());
                toGo.setOnClickListener(new ViewClickListener());
                int i = marker.getExtraInfo().getInt("Number");
                try{
                    userName.setText(onRoadPassengers.get(i).getString("name"));
                    userTel.setText(onRoadPassengers.get(i).getString("userid"));
                    startToEnd.setText(onRoadPassengers.get(i).getString("startplace")+"---->"+onRoadPassengers.get(i).getString("destination"));
                    startTime.setText(onRoadPassengers.get(i).getString("startdate"));
                    endTime.setText(onRoadPassengers.get(i).getString("enddate"));
                    final String telephoneNumber = userTel.getText().toString();
                    SharedPreferences.Editor editor = getSharedPreferences("Setting",MODE_MULTI_PROCESS).edit();
                    editor.putString("destinationTel",userTel.getText().toString());
                    editor.commit();
                    Log.d("对方账号是",userTel.getText().toString());
                    toCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telephoneNumber)));
                        }
                    });
                    final AlertDialog dialog = new AlertDialog.Builder(Driver.this)
                            .setTitle("乘客信息")
                            .setView(view)
                            .create();
                    dialog.setTitle("附近乘客");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    //InfoWindow infoWindow = new InfoWindow(view, marker.getPosition(),10);
                    //baiduMap.showInfoWindow(infoWindow);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }catch (Exception e){

                }
            }catch (NullPointerException e){

            }
            return true;
        }
    }
}
