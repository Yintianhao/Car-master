package Util;

import android.util.Log;

import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * {
 "result": [
 {
 "num": 3,
 "commPickLat": 27.9051,
 "commPickLng": 112.92,
 "time": "2018-09-02 09:00:00",
 "poolId": 9,
 "ends": [
 {
 "userid": null,
 "longtitude": 112.91949,
 "latitude": 27.881441,
 "start": false
 },
 {
 "userid": null,
 "longtitude": 112.91949,
 "latitude": 27.881441,
 "start": false
 },
 {
 "userid": null,
 "longtitude": 112.91224,
 "latitude": 27.887728,
 "start": false
 }
 ]
 }
 ]
 }
 *
 * */
public class OrderRecord {
    private int num;//订单人数
    private String time;//时间
    private double commonPickLat;//共乘点纬度
    private double commonPickLng;//共乘点经度
    private int poolId;//订单ID
    private List<LatLng> nodes;
    public OrderRecord(){}
    public OrderRecord(JSONObject jsonObject){
        try{
            nodes = new ArrayList<>();
            num = jsonObject.getInt("num");
            Log.d("num",num+"");
            time = jsonObject.getString("time");
            Log.d("time",time);
            commonPickLat = jsonObject.getDouble("commPickLat");
            Log.d("lat ",String.valueOf(commonPickLat));
            commonPickLng = jsonObject.getDouble("commPickLng");
            Log.d("lng",String.valueOf(commonPickLng));
            poolId = jsonObject.getInt("poolId");
            Log.d("poolId",poolId+"");
            JSONArray array = jsonObject.getJSONArray("ends");
            for (int i = 0;i < array.length();i++){
                Log.d("Array Size = ",array.length()+"");
                nodes.add(new LatLng(array.getJSONObject(i).getDouble("latitude"),array.getJSONObject(i).getDouble("longitude")));
            }

        }catch (JSONException e){

        }
    }

    public int getNum() {
        return num;
    }

    public String getTime() {
        return time;
    }

    public double getCommonPickLat() {
        return commonPickLat;
    }

    public double getCommonPickLng() {
        return commonPickLng;
    }

    public int getPoolId() {
        return poolId;
    }

    public List<LatLng> getNodes() {
        return nodes;
    }
}
