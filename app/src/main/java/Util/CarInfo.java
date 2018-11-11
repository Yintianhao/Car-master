package Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 31786 on 2018/8/5.
 */

public class CarInfo {
    String carId;
    String driverId;
    String carBrand;
    String carModel;
    String carColor;
    String carCapacity;
    public CarInfo(JSONObject jsonObject){
        try{
            this.carId = jsonObject.getString("carnum");
            this.carBrand = jsonObject.getString("carbrand");
            this.driverId = jsonObject.getString("driverid");
            this.carModel = jsonObject.getString("carmodel");
            this.carColor = jsonObject.getString("carcolor");
            this.carCapacity = jsonObject.getString("carcapacity");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarCapacity() {
        return carCapacity;
    }

    public void setCarCapacity(String carCapacity) {
        this.carCapacity = carCapacity;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
