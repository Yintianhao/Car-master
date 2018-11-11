package com.example.car;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Util.MD5;
import payUI.PayFragment;
import payUI.PayPwdView;

public class PaySetting extends AppCompatActivity {

    PayFragment fragment;
    EditText passWord;
    EditText againPassWord;
    TextView save;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message){
            switch (message.what){
                case 1:
                    Toast.makeText(PaySetting.this,"密码错误",Toast.LENGTH_SHORT).show();
                    break;
                default:break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_setting);
        testPassWord();
        initEvent();
    }
    public void initEvent(){
        passWord = (EditText)findViewById(R.id.pay_setting_newpwd);
        againPassWord = (EditText)findViewById(R.id.pay_setting_againpwd);
        save = (TextView)findViewById(R.id.pay_setting_save);
        save.setOnClickListener(new ViewClickListener());
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
    }
    public boolean correctPayPassWord(){
        String newPassWord = passWord.getText().toString();
        String againPass = againPassWord.getText().toString();
        if(newPassWord.length()>6||againPass.length()>6){
            Toast.makeText(PaySetting.this,"密码长度不能超过六位",Toast.LENGTH_SHORT).show();
        }else if(!newPassWord.equals(againPass)){
            Toast.makeText(PaySetting.this,"请保持两次密码相同",Toast.LENGTH_SHORT).show();
        }else {
            editor.putString("payPassWord",MD5.getMD5(newPassWord));
            editor.commit();
            Toast.makeText(PaySetting.this,"修改密码成功!",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public void testPassWord(){
        fragment = new PayFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PayFragment.EXTRA_CONTENT, "确认密码");
        fragment = new PayFragment();
        fragment.setArguments(bundle);
        fragment.setPaySuccessCallBack(new InputCallBack());
        fragment.show(getSupportFragmentManager(), "Pay");
    }
    private class InputCallBack implements PayPwdView.InputCallBack{

        @Override
        public void onInputFinish(String result) {
            String oldPassWord = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("payPassWord","");
            Log.d("旧密码",oldPassWord);
            Log.d("输入结果",MD5.getMD5(result));
            if(MD5.getMD5(result).equals(oldPassWord)){
                fragment.dismiss();
            }else {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }
    private class ViewClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                default:break;
                case R.id.pay_setting_save:
                    correctPayPassWord();
                    break;
            }
        }
    }
}
