package com.example.car;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Tool.TimeCounter;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class AccountSetting extends AppCompatActivity implements ViewGroup.OnClickListener{
    EditText oldPassword;
    EditText newPassword;
    EditText againPassword;
    TextView getCode;
    EditText verification;
    TextView save;
    MyHandler myHandler;
    EventHandler eventHandler;
    String phoneNum;
    String passWord;
    static Boolean rightVerification;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        initEvents();
        addListener();
        initHandler();
    }
    public void initEvents(){
        oldPassword = (EditText)findViewById(R.id.Accountsetting_oldpwd);
        newPassword = (EditText)findViewById(R.id.Accountsetting_newpwd);
        againPassword = (EditText)findViewById(R.id.Accountsetting_againpwd);
        getCode = (TextView)findViewById(R.id.Accountsetting_getCode);
        verification = (EditText) findViewById(R.id.Accountsetting_verificationCode);
        save = (TextView)findViewById(R.id.Accountsetting_save);
        phoneNum = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user","");
        passWord = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("passWord","");
        rightVerification = false;
        editor = getSharedPreferences("Setteing",MODE_MULTI_PROCESS).edit();
    }
    public void addListener(){
        save.setOnClickListener(this);
        getCode.setOnClickListener(this);
    }
    public void initHandler() {
        myHandler = new MyHandler(this,AccountSetting.this);
        eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data){
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                myHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eventHandler);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.Accountsetting_getCode:
                SMSSDK.getVerificationCode("86", phoneNum);
                Toast.makeText(this,phoneNum,Toast.LENGTH_SHORT).show();
                new TimeCounter(getCode,30000,1000).start();
                break;
            case R.id.Accountsetting_save:
                SMSSDK.submitVerificationCode("86", phoneNum,verification.getText().toString());
                if(oldPassword.getText().toString()
                        .equals(passWord)){
                    if(newPassword.getText().toString()
                            .equals(againPassword.getText().toString())){
                        if(rightVerification){
                            changePassword(phoneNum,passWord);
                        }
                    }else{
                        Toast.makeText(this,"两次新密码不一致",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this,"旧密码输入错误",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void changePassword(final String accountNumber, final String password) {
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/changeinfo.action";
        String tag = "ChangeInfo";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final Setting.MyStringRequest request = new Setting.MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("result")){
                                editor.putString("user",accountNumber);
                                editor.putString("password",password);
                                editor.commit();
                                Toast.makeText(getApplicationContext(),"修改成功,请重新登录",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AccountSetting.this,Login.class));
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Toast.makeText(getApplicationContext(),error.getMessage()+error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("phonenum", accountNumber);
                params.put("password",password);
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }

    static class MyHandler extends Handler {
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
            if(msg.arg2== SMSSDK.RESULT_COMPLETE){
                if(msg.arg1==SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                    rightVerification = true;
                    Toast.makeText(context,"验证码正确",Toast.LENGTH_SHORT).show();
                }else if(msg.arg1==SMSSDK.EVENT_GET_VERIFICATION_CODE ){
                    Toast.makeText(context,"获取验证码成功",Toast.LENGTH_SHORT).show();
                }
            }else {
                ((Throwable) msg.obj).printStackTrace();
                Toast.makeText(context,msg.obj.toString(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
