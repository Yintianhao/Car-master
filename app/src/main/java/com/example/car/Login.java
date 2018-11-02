package com.example.car;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Tool.Const;

public class Login extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    TextView login;//登录
    TextView register;//注册
    EditText user;//用户
    EditText passWord;//密码
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText registerTel;
    EditText registerPassWord;
    EditText registerAgainPass;
    RelativeLayout relativeLayout, relativeLayout2;
    LinearLayout mainLinear,img;
    RadioGroup userType;
    ImageView logo,back;
    LinearLayout.LayoutParams params, params2;
    FrameLayout.LayoutParams params3;
    FrameLayout mainFrame;
    ObjectAnimator animator2, animator1;
    RadioButton checkedButton;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //SDKInitializer.initialize(getApplicationContext());//百度地图SDK初始化
        //initMob();
        initComponents();
        showPreviousInfo();
    }
    /*
     * 参数处理函数
     * */
    private int inDp(int dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int px = (int) (dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /*
     * 初始化各个控件
     * */
    public void initComponents(){

        //sharedprefrence
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();

        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params3 = new FrameLayout.LayoutParams(inDp(50), inDp(50));

        register = (TextView) findViewById(R.id.register);
        login = (TextView) findViewById(R.id.login_login);
        user = (EditText) findViewById(R.id.login_userNum);
        passWord = (EditText) findViewById(R.id.login_passWord);
        img = (LinearLayout) findViewById(R.id.img);
        registerTel = (EditText) findViewById(R.id.registerTel);

        userType = (RadioGroup) findViewById(R.id.userType);
        userType.setOnCheckedChangeListener(this);
        checkedButton = (RadioButton)findViewById(userType.getCheckedRadioButtonId());
        registerPassWord = (EditText) findViewById(R.id.registerPassWord);
        mainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        registerAgainPass = (EditText) findViewById(R.id.registerAgainPassWord);
        back = (ImageView) findViewById(R.id.backImg);
        title = (TextView)findViewById(R.id.title);


        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.relative2);
        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);

        logo = new ImageView(this);
        logo.setLayoutParams(params3);

        login.setOnClickListener(new ClickerListener());
        register.setOnClickListener(new ClickerListener());

        relativeLayout.post(new Runnable() {
            @Override
            public void run() {
                logo.setX((relativeLayout2.getRight() / 2));
                logo.setY(inDp(50));
                mainFrame.addView(logo);
            }
        });
        params.weight = (float) 0.75;
        params2.weight = (float) 4.25;
        mainLinear.getViewTreeObserver().addOnGlobalLayoutListener(new GlobalLayoutListener(mainLinear,params,params2,animator1,animator2));

    }

    /*
     * 监听器类
     * */
    class GlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener{

        LinearLayout mainLinear;
        LinearLayout.LayoutParams params,params2;
        ObjectAnimator animator1,animator2;
        public GlobalLayoutListener(LinearLayout mainLinear, LinearLayout.LayoutParams params, LinearLayout.LayoutParams params2,ObjectAnimator animator1,ObjectAnimator animator2){
            this.mainLinear = mainLinear;
            this.params = params;
            this.animator1 = animator1;
            this.animator2 = animator2;
            this.params2 = params2;
        }
        @Override
        public void onGlobalLayout() {

            Rect r = new Rect();
            mainLinear.getWindowVisibleDisplayFrame(r);
            int screenHeight = mainFrame.getRootView().getHeight();


            int keypadHeight = screenHeight - r.bottom;


            if (keypadHeight > screenHeight * 0.15) {
                // keyboard is opened
                if (params.weight == 4.25) {
                    animator1 = ObjectAnimator.ofFloat(back, "scaleX", (float) 1.95);
                    animator2 = ObjectAnimator.ofFloat(back, "scaleY", (float) 1.95);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(animator1, animator2);
                    set.setDuration(1000);
                    set.start();
                } else {

                    animator1 = ObjectAnimator.ofFloat(back, "scaleX", (float) 1.75);
                    animator2 = ObjectAnimator.ofFloat(back, "scaleY", (float) 1.75);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(animator1, animator2);
                    set.setDuration(500);
                    set.start();
                }
            } else {
                // keyboard is closed
                animator1 = ObjectAnimator.ofFloat(back, "scaleX", 3);
                animator2 = ObjectAnimator.ofFloat(back, "scaleY", 3);
                AnimatorSet set = new AnimatorSet();
                set.playTogether(animator1, animator2);
                set.setDuration(500);
                set.start();
            }
        }
    }
    class ClickerListener implements View.OnClickListener{
        /*
         * 点击事件监听
         * */
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.register:
                    if (params.weight == 4.25) {
                        signUp();
                        return;
                    }
                    actionRegister();
                    break;
                case R.id.login_login:
                    if (params2.weight == 4.25) {
                        LoginRequest(user.getText().toString(),passWord.getText().toString());
                        return;
                    }
                    actionLogin();
                    break;

            }
        }
    }

    public void actionLogin(){
        user.setVisibility(View.VISIBLE);
        passWord.setVisibility(View.VISIBLE);
        userType.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        final ChangeBounds bounds_ = new ChangeBounds();
        bounds_.setDuration(1500);
        bounds_.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {


                ObjectAnimator animator1 = ObjectAnimator.ofFloat(login, "translationX", mainLinear.getWidth() / 2 - relativeLayout.getWidth() / 2 - login.getWidth() / 2);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "translationX", (relativeLayout.getX()));
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(login, "rotation", 0);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(user, "alpha", 0, 1);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(passWord, "alpha", 0, 1);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(userType, "alpha", 0, 1);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(register, "rotation", 90);
                ObjectAnimator animator8 = ObjectAnimator.ofFloat(register, "y", relativeLayout.getHeight() / 2);
                ObjectAnimator animator9 = ObjectAnimator.ofFloat(registerTel, "alpha", 1, 0);

                ObjectAnimator animator10 = ObjectAnimator.ofFloat(registerAgainPass, "alpha", 1, 0);
                ObjectAnimator animator11 = ObjectAnimator.ofFloat(registerPassWord, "alpha", 1, 0);
                ObjectAnimator animator12 = ObjectAnimator.ofFloat(login, "y", register.getY());

                ObjectAnimator animator13 = ObjectAnimator.ofFloat(back, "translationX", -img.getX());
                ObjectAnimator animator14 = ObjectAnimator.ofFloat(login, "scaleX", 2);
                ObjectAnimator animator15 = ObjectAnimator.ofFloat(login, "scaleY", 2);

                ObjectAnimator animator16 = ObjectAnimator.ofFloat(register, "scaleX", 1);
                ObjectAnimator animator17 = ObjectAnimator.ofFloat(register, "scaleY", 1);
                ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo, "x", logo.getX()+relativeLayout2.getWidth());


                AnimatorSet set = new AnimatorSet();
                set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7,
                        animator8, animator9, animator10, animator11, animator12, animator13, animator14, animator15, animator16, animator17,animator18);
                set.setDuration(1500).start();

            }

            @Override
            public void onTransitionEnd(Transition transition) {

                registerTel.setVisibility(View.INVISIBLE);
                registerPassWord.setVisibility(View.INVISIBLE);
                registerAgainPass.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        TransitionManager.beginDelayedTransition(mainLinear, bounds_);
        params.weight = (float) 0.75;
        params2.weight = (float) 4.25;
        relativeLayout.setLayoutParams(params);
        relativeLayout2.setLayoutParams(params2);
    }
    public void actionRegister(){
        registerTel.setVisibility(View.VISIBLE);//registerTel
        registerPassWord.setVisibility(View.VISIBLE);//registerPassword
        registerAgainPass.setVisibility(View.VISIBLE);//Again

        final ChangeBounds bounds = new ChangeBounds();
        bounds.setDuration(1500);
        bounds.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {


                ObjectAnimator animator1 = ObjectAnimator.ofFloat(register, "translationX", mainLinear.getWidth() / 2 - relativeLayout2.getWidth() / 2 - register.getWidth() / 2);
                ObjectAnimator animator2 = ObjectAnimator.ofFloat(img, "translationX", -relativeLayout2.getX());
                ObjectAnimator animator3 = ObjectAnimator.ofFloat(register, "rotation", 0);

                ObjectAnimator animator4 = ObjectAnimator.ofFloat(user, "alpha", 1, 0);
                ObjectAnimator animator5 = ObjectAnimator.ofFloat(passWord, "alpha", 1, 0);
                ObjectAnimator animator6 = ObjectAnimator.ofFloat(userType, "alpha", 1, 0);

                ObjectAnimator animator7 = ObjectAnimator.ofFloat(login, "rotation", 90);
                ObjectAnimator animator8 = ObjectAnimator.ofFloat(login, "y", relativeLayout2.getHeight() / 2);
                ObjectAnimator animator9 = ObjectAnimator.ofFloat(registerTel, "alpha", 0, 1);

                ObjectAnimator animator10 = ObjectAnimator.ofFloat(registerAgainPass, "alpha", 0, 1);
                ObjectAnimator animator11 = ObjectAnimator.ofFloat(registerPassWord, "alpha", 0, 1);
                ObjectAnimator animator12 = ObjectAnimator.ofFloat(register, "y", login.getY());

                ObjectAnimator animator13 = ObjectAnimator.ofFloat(back, "translationX", img.getX());
                ObjectAnimator animator14 = ObjectAnimator.ofFloat(register, "scaleX", 2);
                ObjectAnimator animator15 = ObjectAnimator.ofFloat(register, "scaleY", 2);

                ObjectAnimator animator16 = ObjectAnimator.ofFloat(login, "scaleX", 1);
                ObjectAnimator animator17 = ObjectAnimator.ofFloat(login, "scaleY", 1);
                ObjectAnimator animator18 = ObjectAnimator.ofFloat(logo, "x", relativeLayout2.getRight() / 2 - relativeLayout.getRight());

                AnimatorSet set = new AnimatorSet();
                set.playTogether(animator1, animator2, animator3, animator4, animator5, animator6, animator7,
                        animator8, animator9, animator10, animator11, animator12, animator13, animator14, animator15, animator16, animator17, animator18);
                set.setDuration(1500).start();


            }

            @Override
            public void onTransitionEnd(Transition transition) {
                user.setVisibility(View.INVISIBLE);
                passWord.setVisibility(View.INVISIBLE);
                userType.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {


            }
        });
        TransitionManager.beginDelayedTransition(mainLinear, bounds);
        params.weight = (float) 4.25;
        params2.weight = (float) 0.75;
        relativeLayout.setLayoutParams(params);
        relativeLayout2.setLayoutParams(params2);
    }


    public void showPreviousInfo() {
        String userContent = sharedPreferences.getString("user","");
        String passWordContent = sharedPreferences.getString("passWord","");
        user.setText(userContent);
        passWord.setText(passWordContent);

    }

    /*
    * 登录请求,accountNumber,账号,password密码
    * */
    public boolean LoginRequest(final String accountNumber, final String password) {
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/login.action";
        String tag = "Login";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("login")){
                               if(checkedButton.getId()==R.id.passenger){
                                   Const.userName = accountNumber;
                                   Const.passWord = password;
                                   //changeInfo(user.getText().toString(),"0");
                                   Toast.makeText(getApplicationContext(),"乘客身份登录",Toast.LENGTH_SHORT).show();
                                   editor.putString("user",accountNumber);
                                   editor.putString("passWord",password);
                                   editor.putString("canLogin","passenger");
                                   editor.commit();
                                   startActivity(new Intent(Login.this,Passenger.class));
                                   finish();
                               }else {
                                   Const.userName = accountNumber;
                                   Const.passWord = password;
                                   editor.putString("user",accountNumber);
                                   editor.putString("passWord",password);
                                   editor.putString("canLogin","driver");
                                   editor.commit();
                                   Toast.makeText(getApplicationContext(),"司机身份登录",Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(Login.this, Driver.class));
                                   finish();
                               }
                            }else{
                                editor.putString("canLogin","no");
                                editor.commit();
                                Toast.makeText(getApplicationContext(),"账号或者密码错误",Toast.LENGTH_SHORT).show();
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
                params.put("phonenum", accountNumber);  //注⑥
                params.put("pwd", password);
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));
        //将请求添加到队列中
        requestQueue.add(request);
        return true;
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // 获取选中的RadioButton的id
        int id = group.getCheckedRadioButtonId();
        // 通过id实例化选中的这个RadioButton
       checkedButton = (RadioButton)findViewById(id);
    }

    public boolean signUp(){
        if(registerTel.getText().toString().length()!=11){
            Toast.makeText(getApplicationContext(),"请输入正确的电话号码!",Toast.LENGTH_SHORT).show();
        }else if(!registerAgainPass.getText().toString().equals(registerPassWord.getText().toString())){
            Toast.makeText(getApplicationContext(),"请保持两次密码一致!",Toast.LENGTH_SHORT).show();
        }else if(registerAgainPass.getText().toString().equals("")||registerPassWord.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"密码不能为空!",Toast.LENGTH_SHORT).show();
        }else{
            singUpRequest(registerTel.getText().toString(),registerPassWord.getText().toString());
        }
        return true;
    }
    public void singUpRequest(final String accountNumber,final String password){
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/userregister.action";
        String tag = "Register_passenger";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("register")){
                                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                                actionLogin();
                            }else{
                                Toast.makeText(getApplicationContext(),"注册失败,此手机号是否之前注册过本系统?",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"请稍后重试",Toast.LENGTH_SHORT).show();
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
}

