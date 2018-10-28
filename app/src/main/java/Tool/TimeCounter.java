package Tool;

import android.os.CountDownTimer;
import android.widget.TextView;

import com.example.car.R;

/**
 * Created by 31786 on 2018/7/31.
 * 计时器,用于短信验证
 */

public class TimeCounter extends CountDownTimer{
    private TextView textView;
    //参数依次为控件,总时长,和计时的时间间隔
    public TimeCounter(TextView textView,long millisInFuture, long countDownInterval){
        super(millisInFuture,countDownInterval);
        this.textView = textView;
    }
    //计时过程显示
    @Override
    public void onTick(long millisUntilFinished) {
        String time = "(" + millisUntilFinished / 1000 + ")秒";
        textView.setClickable(false);
        //textView.setBackgroundResource(R.drawable.grey_background);
        textView.setText(time);
    }
    //计时完毕时触发
    @Override
    public void onFinish() {
        textView.setClickable(true);
        textView.setBackgroundResource(R.drawable.input_bg);
        textView.setText("重新获取");
    }
}
