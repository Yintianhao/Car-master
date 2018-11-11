package com.example.car;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Text;
import com.bumptech.glide.disklrucache.DiskLruCache;
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
import java.io.File;


public class Identity extends AppCompatActivity implements TakePhoto.TakeResultListener, InvokeListener {

    TakePhoto takePhoto;
    InvokeParam invokeParam;
    File file;
    Uri uri;
    int size;
    CropOptions cropOptions;
    ImageView idCardPhoto;
    ImageView stuCardPhoto;
    Button summit;
    View clickedView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText name;
    EditText school;
    EditText idCard;
    EditText stuId;
    TextView status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identitify);
        init();
        showInfoIfExits();
    }
    public void init(){
        //控件
        idCardPhoto = (ImageView)findViewById(R.id.photo_id_card);
        stuCardPhoto = (ImageView)findViewById(R.id.photo_stu_card);
        summit = (Button)findViewById(R.id.identity_summit);
        name = (EditText)findViewById(R.id.identity_name);
        school = (EditText) findViewById(R.id.identity_school);
        idCard = (EditText) findViewById(R.id.identity_id_card);
        stuId = (EditText) findViewById(R.id.identity_stu_num);
        status = (TextView)findViewById(R.id.identity_status);
        //添加监听器
        idCardPhoto.setOnClickListener(new ViewClickListener());
        stuCardPhoto.setOnClickListener(new ViewClickListener());
        summit.setOnClickListener(new ViewClickListener());
        //TakePhoto
        file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".png");
        uri = Uri.fromFile(file);
        size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
        cropOptions = new CropOptions.Builder().setOutputX(size).setOutputX(size).setWithOwnCrop(false).create();
        //存储
        sharedPreferences = getSharedPreferences("Setting",MODE_MULTI_PROCESS);
        editor = sharedPreferences.edit();
    }
    public void showInfoIfExits(){
        String idImage = "";
        String stuImage = "";
        if(!sharedPreferences.getString("identity_name","").equals("")){
            name.setText(sharedPreferences.getString("identity_name",""));
            school.setText(sharedPreferences.getString("identity_school",""));
            stuId.setText(sharedPreferences.getString("identity_stu_num",""));
            idCard.setText(sharedPreferences.getString("identity_id_num",""));
            idImage = sharedPreferences.getString("id_card","");
            stuImage = sharedPreferences.getString("stu_card","");
            idCardPhoto.setImageBitmap(BitmapFactory.decodeFile(idImage));
            stuCardPhoto.setImageBitmap(BitmapFactory.decodeFile(stuImage));

            //状态设置
            String summitTime = sharedPreferences.getString("identity_summit_time","");
            if (!summitTime.equals("")&&System.currentTimeMillis()-Long.parseLong(summitTime)>3600*1000){
                status.setText("已通过");
                status.setTextColor(Color.GREEN);

            }
            else{
                status.setText("等待管理员审核...");
                status.setTextColor(Color.RED);
                name.setClickable(false);
                name.setEnabled(false);
                school.setClickable(false);
                school.setEnabled(false);
                stuId.setClickable(false);
                stuId.setEnabled(false);
                idCard.setClickable(false);
                idCard.setEnabled(false);
                idCardPhoto.setClickable(false);
                stuCardPhoto.setClickable(false);
            }
        }
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
        //获取照片成功
        Toast.makeText(Identity.this,"成功",Toast.LENGTH_SHORT).show();
        File file = new File(result.getImage().getOriginalPath());
        if(file.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(result.getImage().getOriginalPath());
            if(clickedView!=null){
                if(clickedView.getId()==R.id.photo_id_card){
                    idCardPhoto.setImageBitmap(bitmap);
                    editor.putString("id_card",result.getImage().getOriginalPath());
                }else {
                    stuCardPhoto.setImageBitmap(bitmap);
                    editor.putString("stu_card",result.getImage().getOriginalPath());
                }
                editor.putString("identity_name",name.getText().toString());
                editor.putString("identity_school",school.getText().toString());
                editor.putString("identity_id_num",idCard.getText().toString());
                editor.putString("identity_stu_num",stuId.getText().toString());
                editor.putString("identity_summit_time",String.valueOf(System.currentTimeMillis()));
                editor.commit();

            }
        }
    }


    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {
        //取消
    }
    private class ViewClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            clickedView = view;
            switch (view.getId()){
                case R.id.identity_summit:
                    startActivity(new Intent(Identity.this,Passenger.class));
                    Toast.makeText(Identity.this,"请等待系统管理员审核...",Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case R.id.photo_stu_card:
                case R.id.photo_id_card:
                    AlertDialog.Builder builder = new AlertDialog.Builder(Identity.this,android.R.style.Theme_Holo_Light_Dialog);
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
                    break;
            }
        }
    }
}
