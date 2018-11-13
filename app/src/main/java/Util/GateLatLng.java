package Util;

import com.baidu.mapapi.model.LatLng;

public class GateLatLng {
    private int gateID;
    private LatLng gateLatLng;
    public GateLatLng(int id,LatLng latLng){
        gateID = id;
        gateLatLng = latLng;
    }

    public LatLng getGateLatLng() {
        return gateLatLng;
    }

    public int getGateID() {
        return gateID;
    }

    public void setGateID(int gateID) {
        this.gateID = gateID;
    }
}
