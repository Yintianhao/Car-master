package com.example.car;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.Const;
import Util.GPSConvert;
import Util.GateLatLng;
import Util.MatchRecord;
import Util.MyPoi;
import Util.NearbyPassengerAdapter;
import Util.OrderAdapter;
import Util.OrderRecord;
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
    MyLocationListener listener = null;
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
    FloatingActionButton refreshOrder;
    List<OrderRecord> records;
    Button get;
    /*
    * UIhandler
    * */
    private Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          switch (msg.what){
              case 5:
                  Toast.makeText(Driver.this,"行程结束,收益会自动到达您的账户上",Toast.LENGTH_SHORT).show();
              case 4:
                  Toast.makeText(Driver.this,"到达第"+(msg.arg1+1)+"个乘客附近",Toast.LENGTH_LONG).show();
              case 3:
                  Toast.makeText(Driver.this,"正在将位置发给手机号为"+msg.getData().getString("data")+"的乘客",Toast.LENGTH_SHORT).show();
                  break;
              case 1:
                  Toast.makeText(Driver.this,"已通知各个顺路乘客",Toast.LENGTH_SHORT).show();
                  break;
              case 2:
                  Toast.makeText(Driver.this,"通知发送失败,请检查手机网络设置"+msg.getData().getString("data"),Toast.LENGTH_SHORT).show();
                  break;
          }
      }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        signUp();
        initDrawerLayout();//初始化控件
        //initRecyclerView();
        addListener();//添加监听器
        isAndroidSix();//判断安卓的版本
        //initMyTestLocation();
    }

    /*
    * 初始化RecyclerView
    * */
    public void initRecyclerView(){
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        linearLayout = (LinearLayout) inflater.inflate(R.layout.nearby_passenger_list, null);
        recyclerView = (RecyclerView)linearLayout.findViewById(R.id.nearby_passengers);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        matchAdapter = new NearbyPassengerAdapter(setOfNearByPassenger);
        recyclerView.setAdapter(matchAdapter);
    }

    /*
    * 初始化DrawerLayout
    * */
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
        refreshOrder = (FloatingActionButton)findViewById(R.id.driver_order_list);
    }

    /*
    * 各控件添加监听器
    * */
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
        refreshOrder.setOnClickListener(new ViewClickListener());
    }

    /*
    * 判断安卓版本
    * */
    public void isAndroidSix(){
        //初始化经纬度以及详细地址，判断是否为android6.0系统版本，如果是，需要动态添加权限
        if (Build.VERSION.SDK_INT>=23){
            showContacts();
        }else{
            initLocation();//init为定位方法
        }
    }
    /*
    * 初始化位置信息
    * */
    public void initLocation(){
        latestLocationInfo = (TextView) findViewById(R.id.driver_locationInfo);
        location = new LocationClient(getApplicationContext());
        listener = new Driver.MyLocationListener();
        location.registerLocationListener(listener);// 注册监听函数
        setViews();
        location.start();
    }
    /*
    * 添加按照POI搜索
    * */
    public void addSearch(){
        //根据城市名和key搜索
        poiCitySearchOption = new PoiCitySearchOption();
        poiCitySearchOption.city("湘潭市").keyword(searchByInput.getText().toString()).pageNum(0).pageCapacity(10);
        poiSearch.searchInCity(poiCitySearchOption);
    }
    /*
    * 配置位置监听器的相关属性
    * */
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

    /*
    * 弹出权限提醒
    * */
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

    /*
    * 路线规划画线
    * @param start 起点
    * @param end 终点
    * */
    public void startGo(LatLng start,LatLng end){
        try{
            PlanNode begin = PlanNode.withLocation(start);
            PlanNode destination = PlanNode.withLocation(end);
            routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(begin).to(destination));
        }catch (NullPointerException e){
            Toast.makeText(Driver.this,"请点击地图标记终点",Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * @param start开始点 end终点 wayList途经点
    * */
    public void voiceNavi(LatLng start, LatLng end, List<Poi> wayList) {
        //将百度地图坐标转化为高德地图坐标
        final LatLng latLng = start;
        double[] startConverted  = GPSConvert.bd09_To_Gcj02(start.latitude,start.longitude);
        double[] endConverted = GPSConvert.bd09_To_Gcj02(end.latitude,end.longitude);
        //包装成高德地图的点
        Poi startloc = new Poi("当前位置", new com.amap.api.maps.model.LatLng(startConverted[0],startConverted[1]), "");
        Poi endloc = new Poi("目的地", new com.amap.api.maps.model.LatLng(endConverted[0], endConverted[1]), "B000A83M61");
        //进入导航界面
        AmapNaviPage.getInstance().showRouteActivity(Driver.this, new AmapNaviParams(startloc, wayList, endloc, AmapNaviType.DRIVER), new INaviInfoCallback() {
            @Override
            public void onInitNaviFailure() {
                Toast.makeText(Driver.this,"导航失败,请手动打开GPS权限",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetNavigationText(String s) {
            }
            /*
            * 位置改变将位置发给乘客
            * */
            @Override
            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
                //高德地图转百度地图坐标
                NaviLatLng latLng = aMapNaviLocation.getCoord();
                double[] lng = GPSConvert.gcj02_To_Bd09(latLng.getLatitude(),latLng.getLongitude());
                //标志位location字段,代表位置
                String content = "location,"+String.valueOf(lng[0])+","+String.valueOf(lng[1]);
                try{
                    //这个List代表沿路的乘客
                    if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                        for (int j = 0;j < onRoadPassengers.size();j++){
                            String tel = onRoadPassengers.get(j).getString("userid");
                            //将此信息发给对应的乘客
                            sendMessage(content,tel);
                        }
                    }
                }catch (JSONException e){

                }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(),"请在地图上选择一个目的地",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onArriveDestination(boolean b) {
                //到达目的地,达到司机终点
                boolean onArrive = false;
                try{
                    //这个List代表沿路的乘客
                    if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                        for (int j = 0;j < onRoadPassengers.size();j++){
                            String tel = onRoadPassengers.get(j).getString("userid");
                            //将此信息发给对应的乘客
                            String content = "arrive,"+String.valueOf(onRoadPassengers.get(j).getString("sharingMoney"));
                            sendMessage(content,tel);
                            onArrive = true;
                        }
                    }
                }catch (JSONException e){

                }catch (NullPointerException e){
                    Log.d("司机","没有接乘客");
                }
                if(onArrive){
                    Message message = new Message();
                    message.what = 5;
                    handler.sendMessage(message);
                }
            }

            @Override
            public void onStartNavi(int i) {
                //启动导航
                //高德地图转百度地图坐标

                try{
                    if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                        for (int j = 0;j < onRoadPassengers.size();j++){
                            String tel = onRoadPassengers.get(j).getString("userid");
                            String content = "start,"+String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude);
                            Log.d("出发发送信息给",tel);
                            Log.d("发送内容",content);
                            //发送start 表示出发
                            sendMessage(content,tel);
                            Message message = new Message();
                            message.what = 3;
                            Bundle bundle = new Bundle();
                            bundle.putString("data",tel);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    }
                }catch (JSONException e){

                }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(),"请在地图上选择一个目的地",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCalculateRouteSuccess(int[] ints) {
                //算路成功
            }

            @Override
            public void onCalculateRouteFailure(int i) {
                //算路失败
            }

            @Override
            public void onStopSpeaking() {
                //停止语音
            }

            @Override
            public void onReCalculateRoute(int i) {

            }

            @Override
            public void onExitPage(int i) {
               try{
                    //这个List代表沿路的乘客
                    if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                        for (int j = 0;j < onRoadPassengers.size();j++){
                            String tel = onRoadPassengers.get(j).getString("userid");
                            //将此信息发给对应的乘客
                            String content = "arrive,"+String.valueOf(onRoadPassengers.get(j).getString("sharingMoney"));
                            sendMessage(content,tel);
                        }
                    }
                }catch (JSONException e){

                }catch (NullPointerException e){
                    Log.d("司机","没有接乘客");
                }
                Message message = new Message();
                message.what = 5;
                handler.sendMessage(message);
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
                //到达第几个wayPoint
                Message message = new Message();
                message.what = 4;
                message.arg1 = i;
                handler.sendMessage(message);
            }
        });
    }
    /*
    * 登录
    * */
    public void signUp(){
        String tel = getSharedPreferences("Setting", MODE_MULTI_PROCESS).getString("user","");
        String passWord = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("passWord","");
        Log.d("用户名---",tel);
        Log.d("密码---",passWord);
        EMClient.getInstance().login(tel,
                passWord,
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d("登录成功","--");
                    }

                    @Override
                    public void onError(int code, String error) {
                        Log.d("登录错误","--");
                        Log.d("code = ",code+"");
                        Log.d("error = ",error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        Log.d("正在登录","--");
                    }
                });
    }
    /*
    * 退出登录
    * */
    public void signDown(){
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("司机退出登录成功","--");
            }

            @Override
            public void onError(int code, String error) {
                Log.d("司机退出登录代码",code+"");
                Log.d("司机错误内容",error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
    /*
    * 发送信息
    * @param content 发送内容
    * @param chatId 对方账号
    * */
    public void sendMessage(String content,String chatWithId) {
        EMMessage message = EMMessage.createTxtSendMessage(content,chatWithId);
//如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        Log.d("发送内容",content);
        Log.d("聊天对象",chatWithId);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("司机发送信息成功","发送信息成功");
                //Toast.makeText(Chatting.this, "发送信息成功", Toast.LENGTH_SHORT).show();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onError(int code, String error) {

                Log.d("司机错误代码",String.valueOf(code));
                Log.d("司机发送信息失败",error);
                Message message = new Message();
                message.what = 2;
                Bundle bundle = new Bundle();
                bundle.putString("error",code+error);
                message.setData(bundle);
                handler.sendMessage(message);
                //Toast.makeText(Chatting.this, "发送信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress, String status) {
                //Toast.makeText(Chatting.this, "发送信息中"+status, Toast.LENGTH_SHORT).show();
                Log.d("司机发送信息中",status);
            }
        });
    }

    /*
    * 由一个经纬度获得该点的信息
    * @param position 某点的坐标(百度坐标)
    * */
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
    /*
    * 获得附近的车
    * @param userID 用户账号
    * @param latitude 经度
    * @param longitude 纬度
    * */
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

    /*
    * 获得附近的乘客
    * */
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

    /*
    * 获得缓存区中的车
    * @param phoneNum 电话号码
    * @param nowLatitude nowLongitude 当前位置的经纬度
    * @param endLatitude endLongitude 目的地的经纬度
    * @param startDate 开始的时间
    * @param endDate 结束日期
    * */
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
                                Toast.makeText(getApplicationContext(),"目前您顺路有"+(new JSONObject(response).getJSONArray("result").length()-1)+"个客人",Toast.LENGTH_SHORT).show();
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

    /*
    * 获得离司机当前位置最近的学校的门的信息
    * @param start 起点坐标
    * */
    public GateLatLng destinationGateLatLng(LatLng start){
        //北门112.916586,27.904829
        //南门112.92282,27.893114
        //东门112.926403,27.904928
        //返回隔司机当前位置最近的一个门的坐标
        double startLat = start.latitude;
        double startLng = start.longitude;
        double lenN = (startLat-27.904829)*(startLat-27.904829)+(startLng-112.916586)*(startLng-112.916586);
        double lenS = (startLat-27.893114)*(startLat-27.893114)+(startLng-112.92282)*(startLng-112.92282);
        double lenE = (startLat-27.904928)*(startLat-27.904928)+(startLng-112.926403)*(startLng-112.926403);
        if (Math.min(lenE,lenN)>lenS){
            return new GateLatLng(2,new LatLng(27.893114,112.92282));
        }else if(Math.min(lenE,lenS)>lenN){
            return new GateLatLng(113,new LatLng(27.904829,112.916586));
        }else {
            return new GateLatLng(62,new LatLng(27.904928,112.926403));
        }
    }
    /*
    * 初始化InfoWindow(或弃用)
    * */
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
    /*
    * 获得订单列表
    * */
    public void getOrders(){
        String url = "http://47.106.72.170:8080/MyCarSharing/searchAllOrders";
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
                        records = new ArrayList<>();
                        records.add(new OrderRecord());
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        LinearLayout parent = (LinearLayout) inflater.inflate(R.layout.activity_match_show, null);
                        RecyclerView recyclerView = (RecyclerView)parent.findViewById(R.id.match_record);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Driver.this);
                        recyclerView.setLayoutManager(layoutManager);
                        OrderAdapter adapter = new OrderAdapter(records,Driver.this);
                        recyclerView.setAdapter(adapter);
                        AlertDialog alertDialog = new AlertDialog.Builder(Driver.this)
                                .setView(parent)
                                .setTitle("订单列表")
                                .create();
                        alertDialog.show();
                        WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        alertDialog.getWindow().setAttributes(layoutParams);
                        OverlayOptions options = new MarkerOptions()
                                .position(new LatLng(27.90836,112.92749))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_near));
                        baiduMap.addOverlay(options);
                        OverlayOptions option2 = new MarkerOptions()
                                .position(new LatLng(27.911505,112.92713))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_near));
                        baiduMap.addOverlay(option2);
                        OverlayOptions option3 = new MarkerOptions()
                                .position(new LatLng(27.898918,112.925995))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_near));
                        baiduMap.addOverlay(option3);
                        startGo(new LatLng(27.899399,112.929271),new LatLng(27.90333,112.928449));
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
        signDown();//退出登录
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

    /*
    * 位置监听
    * */
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
    /*
    * NavigationView 监听
    * */
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
    /*
    * POI搜索监听
    * */
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
    /*
    * 地理编码
    * */
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
    /*
    * 路线规划
    * */
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
        /*
         * 画线
         * @param DrivingRouteResult 路线规划结果集
         * @param routeNum 路线号
         * */
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
                    linePoints.add(node);//将点添加到集合上
                }
                OverlayOptions ooPolyLine = new PolylineOptions().width(12).color(Color.YELLOW).points(linePoints);//设置折线的属性,颜色等
                Polyline polyline = (Polyline) baiduMap.addOverlay(ooPolyLine);//添加到地图
            }
        }

    }
    /*
    * 地图长按监听
    * */
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
    /*
    * 地图点击监听
    * */
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
    /*
    * 文本监听
    * */
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
    /*
    * Item点击监听
    * */
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
    /*
    * 视图点击
    * */
    private class ViewClickListener implements View.OnClickListener{

        //全局点击监听
        @Override
        public void onClick(View v){
            switch (v.getId()){
                case R.id.driver_order_list:
                    //获得订单列表
                    getOrders();
                    break;
                case R.id.driver_setTraffic:
                    clickNum++;
                    baiduMap.setTrafficEnabled(isTraffic.get(clickNum%2));
                    break;
                case R.id.onroad_passenger_go:
                    baiduMap.hideInfoWindow();
                    startGo(start,markerLocation);
                    voiceNavi(start,markerLocation,new ArrayList<Poi>());
                    break;
                case R.id.driver_onRoadPassenger:
                    try {
                        getRoadNearbyCar(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),String.valueOf(start.latitude),String.valueOf(start.longitude),String.valueOf(end.latitude),String.valueOf(end.longitude),"2018-09-02-9-00-00","2018-09-02-9-00-30");
                    }catch (NullPointerException e){
                        Toast.makeText(getApplicationContext(),"请先选择一个目的地",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.driver_start:
                    //startGo(start,end);
                    List<Poi> wayList = new ArrayList();//途径点目前最多支持3个。
                    try{
                        if (onRoadPassengers!=null&&onRoadPassengers.size()!=0){
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(1)));
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(2)));
                            wayList.add(MyPoi.getTransforPoi(onRoadPassengers.get(3)));
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
                    getNearbyCar("1365743316","27.90553","112.92297");
                    break;
                default:
                    break;
            }
        }
    }
    /*
    * 标注点击
    * */
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
                    final String telephoneNumber = onRoadPassengers.get(i).getString("userid");
                    Log.d("对方账号是",userTel.getText().toString());
                    toCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telephoneNumber)));
                        }
                    });
                    toChat.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            Intent intent = new Intent(Driver.this,Chatting.class);
                            intent.putExtra("destinationTel",telephoneNumber);
                            startActivity(intent);
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
