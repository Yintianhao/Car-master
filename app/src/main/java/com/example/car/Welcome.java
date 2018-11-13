package com.example.car;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.Iterator;
import java.util.List;

import Util.BeautyTextView;

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
        initMob();
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

    public void initMob(){
        Context appContext = this;
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
// 如果APP启用了远程的service，此application:onCreate会被调用2次
// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
// 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
        if (processAppName == null ||!processAppName.equalsIgnoreCase(appContext.getPackageName())) {
            Log.e("YTH", "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        EMClient.getInstance().init(this,new EMOptions());
        EMClient.getInstance().setDebugMode(true);
    }
    private String getAppName(int pID) {
        //获得APP的名字
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
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
