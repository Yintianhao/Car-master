package Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.car.Passenger;
import com.example.car.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by 31786 on 2018/8/10.
 */

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {


    private List<MatchRecord> list;
    ViewHolder viewHolder;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_result_item,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.getContext().startActivity(new Intent(view.getContext(),Chatting.class));
            }
        });
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        this.viewHolder = holder;
        MatchRecord matchRecord = list.get(position);
        holder.userName.setText(matchRecord.getName());
        holder.phoneNum.setText(matchRecord.getPhoneNum());
        holder.startPlace.setText(matchRecord.getStart());
        holder.endPlace.setText(matchRecord.getEnd());
        holder.startTimeToEndTime.setText(matchRecord.getStartTime().substring(11,19)+"-->"+matchRecord.getEndTime().substring(11,19));
        getCommonPickName("27.90333","112.928449",holder.commonPickName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView userName;
        TextView phoneNum;
        TextView startPlace;
        TextView endPlace;
        TextView startTimeToEndTime;
        TextView commonPickName;
        public ViewHolder(View view){
            super(view);
            this.view = view;
            userName = (TextView)view.findViewById(R.id.match_userName);
            phoneNum = (TextView)view.findViewById(R.id.match_userPhone);
            startPlace = (TextView)view.findViewById(R.id.match_start_place);
            endPlace = (TextView)view.findViewById(R.id.match_end_place);
            startTimeToEndTime = (TextView)view.findViewById(R.id.match_startTime_to_endTime);
            commonPickName = (TextView)view.findViewById(R.id.match_common_pick);
        }
    }
    public MatchAdapter(List<MatchRecord> list){
        this.list = list;
    }
    public String getCommonPickName(String lat, String lng,TextView name){
        Log.d("CommonPickName","  run");
        //请求地址
        String url = "http://api.map.baidu.com/geocoder/v2/?location=" +lat+","+lng+
                "&output=json&ak=67o67BXlV3mgev8rXfwSONqFPBIXuNte&mcode=57:60:39:16:90:D2:98:DE:08:5A:92:8A:98:0C:C5:67:CA:DD:F9:AF;com.example.car";
        String tag = "addRequest";
        Log.d("url",url);
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(viewHolder.view.getContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        final Handler handler = new MyHandler(name);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        Passenger.MyStringRequest request = new Passenger.MyStringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getInt("status")==0){
                                Message message = new Message();
                                message.what = 1;
                                Bundle bundle = new Bundle();
                                //Log.d("poiRegion size  = ","jsonObject.getJSONObject("result").getJSONArray("poiRegions").length())
                                bundle.putString("name",jsonObject.getJSONObject("result").getString("sematic_description"));
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }else{
                                Log.d("APi ","出错");
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.d("JSONException--->",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.d("VolleyError",error.getCause().toString());
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
        return "";
    }
    public class MyHandler extends Handler{
        private TextView it;
        public MyHandler(TextView name){
            it = name;
        }
        @Override
        public void handleMessage(Message message){
            if(message.what==1){
                it.setText("南校俱乐部");
                Log.d("name",message.peekData().getString("name"));
            }
        }
    }
}
