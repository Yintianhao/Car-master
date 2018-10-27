package Tool;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;

import org.json.JSONException;
import org.json.JSONObject;

public class MyPoi {
    public static Poi getTransforPoi(JSONObject jsonObject) throws JSONException{
        double[] converted  = GPSConvert.bd09_To_Gcj02(jsonObject.getDouble("startplacey"),jsonObject.getDouble("startplacex"));
        return new Poi(jsonObject.getString("startplace"),
                new LatLng(converted[0],converted[1]),"");
    }
}
