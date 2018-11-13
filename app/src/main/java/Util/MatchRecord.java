package Util;

import android.util.Log;


import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by 31786 on 2018/8/10.
 */

public class MatchRecord {
    String name;
    String phoneNum;
    String start;
    String end;
    String startTime;
    String endTime;
    String moneyBefore;
    String moneyAfter;
    String distanceBefore;
    String distanceAfter;
    String carNumber;
    String commonPickLat;
    String commonPickLng;

    public String getCommonPickLat() {
        return commonPickLat;
    }

    public void setCommonPickLat(String commonPickLat) {
        this.commonPickLat = commonPickLat;
    }

    public String getCommonPickLng() {
        return commonPickLng;
    }

    public void setCommonPickLng(String commonPickLng) {
        this.commonPickLng = commonPickLng;
    }

    public MatchRecord(JSONObject jsonObject){
        try{
            setName(jsonObject.getString("name"));
            setPhoneNum(jsonObject.getString("userid"));
            setStart(jsonObject.getString("startplace"));
            setEnd(jsonObject.getString("destination"));
            setDistanceBefore(String.valueOf(jsonObject.getDouble("spendDistance")));
            setDistanceAfter(String.valueOf(jsonObject.getDouble("sharingdistance")));
            setStartTime(jsonObject.getString("startdate"));
            setEndTime(jsonObject.getString("enddate"));
            setMoneyBefore(String.valueOf(jsonObject.getDouble("spendMoney")));
            setMoneyAfter(String.valueOf(jsonObject.getDouble("sharingMoney")));
            setCarNumber(String.valueOf(jsonObject.getString("carnum")));
            setCommonPickLat(String.valueOf(jsonObject.getString("commPickLat")));
            setCommonPickLng(String.valueOf(jsonObject.getString("commPickLng")));
            Log.d("JsonObject----->",jsonObject.getString("name")+jsonObject.getString("userid"));
        }catch (JSONException e){
            Log.d("JsonException----->",e.getMessage());
        }
    }
    public MatchRecord(String name,String userid,String startPlace,String endPlace,String spendDistance,String sharingDistance,String startDate,String endDate,String spendMoney,String sharingMoney)
    {
        setName(name);
        setPhoneNum(userid);
        setStart(startPlace);
        setEnd(endPlace);
        setDistanceBefore(spendDistance);
        setDistanceAfter(sharingDistance);
        setStartTime(startDate);
        setEndTime(endDate);
        setMoneyBefore(spendMoney);
        setMoneyAfter(sharingMoney);
    }

    public String getDistanceAfter() {
        return distanceAfter;
    }

    public void setDistanceAfter(String distanceAfter) {
        this.distanceAfter = distanceAfter;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDistanceBefore() {
        return distanceBefore;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setDistanceBefore(String distanceBefore) {
        this.distanceBefore = distanceBefore;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMoneyAfter() {
        return moneyAfter;
    }

    public void setMoneyAfter(String moneyAfter) {
        this.moneyAfter = moneyAfter;
    }

    public String getMoneyBefore() {
        return moneyBefore;
    }

    public void setMoneyBefore(String moneyBefore) {
        this.moneyBefore = moneyBefore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
