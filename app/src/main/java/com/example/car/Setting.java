package com.example.car;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import Tool.Const;

//实现TakePhoto.TakeResultListener, InvokeListener接口
public class Setting extends AppCompatActivity implements TakePhoto.TakeResultListener, InvokeListener {
    TakePhoto takePhoto;
    InvokeParam invokeParam;
    String imagePath;
    File file;
    Uri uri;
    int size;
    CropOptions cropOptions;
    ImageView headSculpture;
    Bitmap bitmap;
    File test;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText userName;
    EditText userSex;
    EditText userAge;
    TextView saveInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        query(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""));
        //Toast.makeText(getApplicationContext(),getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),Toast.LENGTH_SHORT).show();
        initEvents();
        addListener();
    }
    public void initEvents(){
        //各控件初始化
        file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".png");
        uri = Uri.fromFile(file);
        size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        cropOptions = new CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create();
        headSculpture = (ImageView)findViewById(R.id.head_sculpture);
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        if(getIntent().getStringExtra("DriverOrPassenger").equals("Passenger")){
            imagePath = sharedPreferences.getString("passenger_image_path"+sharedPreferences.getString("user",""),"");
        }else {
            imagePath = sharedPreferences.getString("driver_image_path"+sharedPreferences.getString("user",""),"");
        }

        if(!imagePath.equals("")){
            headSculpture.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
        userName = (EditText)findViewById(R.id.user_name);
        userAge = (EditText)findViewById(R.id.user_age);
        userSex = (EditText)findViewById(R.id.user_sex);
        saveInfo = (TextView) findViewById(R.id.setting_save);

    }

    public void addListener(){
        headSculpture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出框框
                AlertDialog.Builder builder = new AlertDialog.Builder(Setting.this,android.R.style.Theme_Holo_Light_Dialog);
                builder.setIcon(R.drawable.ic_choice_pic);
                builder.setTitle("选择");
                String[] choices = {"拍照","从相机里选择"};
                builder.setItems(choices, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                //拍照并裁剪
                                takePhoto.onPickFromCaptureWithCrop(uri, cropOptions);
                                break;
                            case 1:
                                //从照片选择并裁剪
                                takePhoto.onPickFromGalleryWithCrop(uri, cropOptions);
                                break;
                            default:
                                break;
                        }
                    }
                });
                builder.show();
            }
        });
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInfo(getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""));
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    public TakePhoto getTakePhoto() {
        //获得TakePhoto实例
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        //设置压缩规则，最大500kb
        takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
        return takePhoto;
    }

    @Override
    public void takeSuccess(final TResult result) {
        //成功取得照片
        test = new File(result.getImage().getOriginalPath());
        editor = sharedPreferences.edit();
        if(getIntent().getStringExtra("DriverOrPassenger").equals("Passenger"))
            editor.putString("passenger_image_path"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),result.getImage().getOriginalPath());
        else
            editor.putString("driver_image_path"+getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("user",""),result.getImage().getOriginalPath());
        editor.commit();
        if(test.exists()){
            bitmap = BitmapFactory.decodeFile(result.getImage().getOriginalPath());
            headSculpture.setImageBitmap(bitmap);
            Const.leftHead.setImageBitmap(bitmap);
        }
    }


    @Override
    public void takeFail(TResult result, String msg) {
        //取得失败
        Toast.makeText(Setting.this,"设置失败",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void takeCancel() {
        //取消
    }
    public void query(final String accountNumber) {
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/selectuserinfo.action";
        String tag = "Query";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final MyStringRequest request = new MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response).getJSONObject("result");
                            userName.setText(jsonObject.getString("name"));
                            editor = sharedPreferences.edit();
                            editor.putString("NAME"+accountNumber,jsonObject.getString("name"));
                            editor.commit();
                            userAge.setText(String.valueOf(jsonObject.getInt("age")));
                            userSex.setText(jsonObject.getString("sex"));

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
                return params;
            }

        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }
    public void changeInfo(final String accountNumber) {
        //请求地址
        String url = "http://47.106.72.170:8080/MyCarSharing/changeinfo.action";
        String tag = "ChangeInfo";
        //取得请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);
        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final MyStringRequest request = new MyStringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("result")){
                                Toast.makeText(getApplicationContext(),"修改成功",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(),"修改失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.d("Json","解析错误");
                            Log.d("Json",e.getMessage());
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
                params.put("name", userName.getText().toString());
                params.put("age",userAge.getText().toString());
                params.put("sex",userSex.getText().toString());
                return params;
            }


        };

        //设置Tag标签
        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(20*1000,1,1.0f));

        //将请求添加到队列中
        requestQueue.add(request);
    }

    static class MyStringRequest extends StringRequest {

        public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(url, listener, errorListener);
        }
        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // TODO Auto-generated method stub
            String str = null;
            try {
                str = new String(response.data,"utf-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
        }
    }
}

