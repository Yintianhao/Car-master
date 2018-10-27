package Tool;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 31786 on 2018/7/18.
 */

public class Const {
    public static CircleImageView leftHead;
    public static String people = "112.927826,27.909375\n" +
            "112.920784,27.913716\n" +
            "112.928725,27.908704\n" +
            "112.927,27.912056\n" +
            "112.919526,27.91445\n" +
            "112.930162,27.906502\n" +
            "112.928725,27.901968\n" +
            "112.931348,27.90813\n" +
            "112.931276,27.90548\n" +
            "112.940403,27.920898\n" +
            "112.946008,27.907108\n" +
            "112.941553,27.899446\n" +
            "112.92258,27.898808";
    public static List<AppCompatActivity> activityManager = new ArrayList<>();
    public static LatLng start;
    public static LatLng end;
    public static String userName;
    public static String passWord;
    public static LatLng location;
}
