package Util;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.baidu.mapapi.model.LatLng;
import com.example.car.Driver;
import com.example.car.Passenger;
import com.example.car.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private List<OrderRecord> list;
    ViewHolder viewHolder;
    Context context;
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item,parent,false);
        Button get = (Button)view.findViewById(R.id.order_item_get);
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("抢单按钮","run");
                //Toast.makeText(Driver.this,"接单成功,即将出发",Toast.LENGTH_SHORT).show();
                GateLatLng latLng =destinationGateLatLng(new LatLng(27.90955,112.937582));
                List<Poi> list = new ArrayList<>();
                double[] node= GPSConvert.bd09_To_Gcj02(27.899399,112.929271);
                Log.d("node0",node[0]+"");
                Log.d("node1",node[1]+"");
                list.add(new Poi("湖南科技大学南门",new com.amap.api.maps.model.LatLng(node[0],node[1]),""));
                voiceNavi(new LatLng(27.89907582,112.937081),new LatLng(27.90333,112.928449),list);
            }
        });
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        this.viewHolder = holder;
        OrderRecord orderRecord = list.get(position);
        //holder.orderId.setText(orderRecord.getPoolId());
        holder.orderId.setText("订单9");
        holder.startPlace.setText("湖南科技大学南校俱乐部");
        holder.endPlace.setText("湘潭火车站,湘潭火车站,交通驾校");
        //holder.num.setText(orderRecord.getNum());
        holder.num.setText("3");
        //holder.time.setText(orderRecord.getTime());
        holder.time.setText("9:00");
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView orderId;
        TextView startPlace;
        TextView endPlace;
        TextView time;
        TextView num;
        Button get;
        public ViewHolder(View view){
            super(view);
            this.view = view;
            orderId = (TextView)view.findViewById(R.id.order_item_count);
            startPlace = (TextView)view.findViewById(R.id.order_item_start);
            endPlace = (TextView)view.findViewById(R.id.order_item_end);
            time = (TextView)view.findViewById(R.id.order_item_time);
            num = (TextView)view.findViewById(R.id.order_item_man_num);
            //get = (Button)view.findViewById(R.id.order_item_get);
        }
    }
    public OrderAdapter(List<OrderRecord> list, Context context){
        this.list = list;
        this.context = context;
    }
    public void voiceNavi(LatLng start, LatLng end, List<Poi> wayList) {
        //将百度地图坐标转化为高德地图坐标
        LatLng latLng = start;
        double[] startConverted  = GPSConvert.bd09_To_Gcj02(start.latitude,start.longitude);
        double[] endConverted = GPSConvert.bd09_To_Gcj02(end.latitude,end.longitude);
        //包装成高德地图的点
        Poi startloc = new Poi("当前位置", new com.amap.api.maps.model.LatLng(startConverted[0],startConverted[1]), "");
        Poi endloc = new Poi("目的地", new com.amap.api.maps.model.LatLng(endConverted[0], endConverted[1]), "B000A83M61");
        //进入导航界面
        AmapNaviPage.getInstance().showRouteActivity(context, new AmapNaviParams(startloc, wayList, endloc, AmapNaviType.DRIVER), new INaviInfoCallback() {
            @Override
            public void onInitNaviFailure() {
                Toast.makeText(context,"导航失败,请手动打开GPS权限",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGetNavigationText(String s) {
            }
            /*
             * 位置改变将位置发给乘客
             * */
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
    public GateLatLng destinationGateLatLng(LatLng start){
        //北门112.923129,27.910684
        //南门112.928847,27.899361
        //东门112.932139,27.91149
        //返回隔司机当前位置最近的一个门的坐标
        double startLat = start.latitude;
        double startLng = start.longitude;
        double lenN = (startLat-27.910684)*(startLat-27.910684)+(startLng-112.923129)*(startLng-112.923129);
        double lenS = (startLat-27.899361)*(startLat-27.899361)+(startLng-112.928847)*(startLng-112.928847);
        double lenE = (startLat-27.91149)*(startLat-27.91149)+(startLng-112.932139)*(startLng-112.932139);
        if (Math.min(lenE,lenN)>lenS){
            return new GateLatLng(2,new LatLng(27.893114,112.92282));
        }else if(Math.min(lenE,lenS)>lenN){
            return new GateLatLng(113,new LatLng(27.904829,112.916586));
        }else {
            Log.d(" ","27.904928,112.926403");
            return new GateLatLng(62,new LatLng(27.904928,112.926403));
        }
    }
}
