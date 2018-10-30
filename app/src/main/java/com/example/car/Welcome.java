package com.example.car;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;

import Tool.BeautyTextView;

public class Welcome extends AppCompatActivity {

    private BeautyTextView textView;
    private int time=3;

    final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    time--;
                    Log.e("TAG",time+"");
                    if (time>0){
                        handler.sendMessageDelayed(handler.obtainMessage(1),1000);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SDKInitializer.initialize(getApplicationContext());
        textView = (BeautyTextView) findViewById(R.id.welcome_text);
        //每隔一秒发送消息
        handler.sendMessageDelayed(handler.obtainMessage(1), 1000);

        //延迟3秒后进入主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //执行在主线程
                //启动页面
                startMainActivity();
            }
        },3000);

    }

    private void startMainActivity() {
        String canLogin = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("canLogin","");
        if(canLogin.equals("passenger")){
            startActivity(new Intent(Welcome.this,Passenger.class));
            finish();
        }else if(canLogin.equals("driver")){
            startActivity(new Intent(Welcome.this,Driver.class));
            finish();
        }else {
            startActivity(new Intent(Welcome.this,Login.class));
            finish();
        }
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击快速进入主界面
        if (event.getAction()==MotionEvent.ACTION_DOWN){
            startMainActivity();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //移除消息
        handler.removeCallbacksAndMessages(null);
    }
}
