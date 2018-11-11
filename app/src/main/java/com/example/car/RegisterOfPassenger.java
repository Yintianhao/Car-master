package com.example.car;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Util.TimeCounter;
import cn.smssdk.SMSSDK;

import cn.smssdk.EventHandler;

public class RegisterOfPassenger extends AppCompatActivity implements View.OnClickListener{
    static EditText userNumber;//用户
    static EditText passWord;//
    static EditText againPassWord;//第二次密码
    EditText telNumber;//电话号码
    TextView codeView;//获取验证码
    EditText verificationCode;//获得的验证码
    static CheckBox isChecked;//是否遵循用户条款
    TextView register_userAgree;//条款内容
    Button register;//注册
    String appKey = "27274b6e1307f";
    String appSecret = "008efbb4f9de892839c0ef3a878a7d10";
    EventHandler eventHandler;
    MyHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_of_passenger);
        initEvents();
        addListener();
        initHandler();
    }
    public void initEvents(){
        //初始化SDK 并对每个控件进行绑定
        MobSDK.init(this,appKey,appSecret);
        userNumber = (EditText)findViewById(R.id.register_userNumber);
        passWord = (EditText)findViewById(R.id.register_passenger_passWord);
        againPassWord = (EditText)findViewById(R.id.register_passenger_again_passWord);
        codeView = (TextView)findViewById(R.id.register_passenger_getCode);
        verificationCode = (EditText)findViewById(R.id.register_passenger_verificationCode);
        isChecked = (CheckBox)findViewById(R.id.register_passenger_agreecheckbox);
        register = (Button)findViewById(R.id.register_passenger);
        register_userAgree = (TextView)findViewById(R.id.register_passengerAgree);
        register_userAgree.setClickable(true);
        register_userAgree.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
    }
    public void addListener(){
        codeView.setOnClickListener(this);
        register.setOnClickListener(this);
        register_userAgree.setOnClickListener(this);
    }
    public void initHandler(){
        //初始化Handler
        handler = new MyHandler(this,RegisterOfPassenger.this);
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data){
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.register_passenger_getCode:
                SMSSDK.getVerificationCode("86", userNumber.getText().toString().trim());
                new TimeCounter(codeView,30000,1000).start();
                break;
            case R.id.register_passenger:
                SMSSDK.submitVerificationCode("86", userNumber.getText().toString(),verificationCode.getText().toString());
                break;
            case R.id.register_passengerAgree:
                //Toast.makeText(RegisterOfPassenger.this,"条款",Toast.LENGTH_SHORT).show();
                AlertDialog toDriver = new AlertDialog.Builder(RegisterOfPassenger.this)
                        .setTitle("我们需要读取你手机的相关信息," +"\n"+
                                "比如手机状态信息,位置等,我们保证用户隐私," +"\n"+
                                "不会泄露用户个人信息,维护用户的合法权益")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                isChecked.setChecked(true);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                toDriver.show();
                break;
            default:
                break;
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        //用完回调要注销掉，否则可能会出现内存泄露
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    static class MyHandler extends Handler{
        Context context;
        AppCompatActivity appCompatActivity;
        MyHandler(Context context,AppCompatActivity appCompatActivity){
            this.context = context;
            this.appCompatActivity = appCompatActivity;
        }
        @Override
        public void handleMessage(Message msg){
            //对消息进行处理
            /*
            * SMSSDK.RESULT_COMPLETE完成
            * SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE 提交成功
            * SMSSDK.EVENT_GET_VERIFICATION_CODE 已经验证
            * */
            if(msg.arg2==SMSSDK.RESULT_COMPLETE){
                if(msg.arg1==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    //Toast.makeText(context,"验证码提交成功",Toast.LENGTH_SHORT).show();
                    if(isChecked.isChecked()){
                        if (passWord.getText().toString().equals(againPassWord.getText().toString())){
                            register(userNumber.getText().toString(),
                                    passWord.getText().toString(),
                                    context,
                                    appCompatActivity);
                            registerMob();

                        }else {
                            Toast.makeText(context,"请保持两次密码相同",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context,"请你阅读服务条款并同意",Toast.LENGTH_SHORT).show();
                    }
                }else if(msg.arg1==SMSSDK.EVENT_GET_VERIFICATION_CODE ){
                    Toast.makeText(context,"获取验证码成功",Toast.LENGTH_SHORT).show();
                }
            }else {
                ((Throwable) msg.obj).printStackTrace();
                Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static void register(final String accountNumber, final String password, final Context context, final AppCompatActivity appCompatActivity) {
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/userregister.action";
        String tag = "Register_passenger";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("register")){
                                AlertDialog toDriver = new AlertDialog.Builder(context)
                                        .setTitle("注册成功,现在去登录?")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(context,Passenger.class);
                                                context.startActivity(intent);
                                                appCompatActivity.finish();//前面的销毁
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        }).create();
                                toDriver.show();
                            }else{
                                Toast.makeText(context,"注册失败,此手机号是否之前注册过本系统?",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Toast.makeText(context,"请稍后重试",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenum", accountNumber);  //注⑥
                params.put("password", password);
                params.put("isdriver","0");
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public static void registerMob(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    EMClient.getInstance().createAccount(userNumber.getText().toString(),
                            passWord.getText().toString());
                }catch (HyphenateException e){
                    Log.e("聊天功能","注册失败");
                }
            }
        }).start();
    }
}
