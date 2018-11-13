package com.example.car;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;

import Util.Msg;
import Util.MsgAdapter;

public class Chatting extends AppCompatActivity implements View.OnClickListener, EMMessageListener {

    private List<Msg> msgList = new ArrayList<>();//消息列表
    private EditText inputText;//输入框
    private Button send;//发送
    private RecyclerView msgViewRecyclerView;
    private MsgAdapter adapter;
    private String chatId;//自己传进来的跟自己聊天的人的ID

    /**
     * handler
     * */
    private Handler UIHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                default:break;
                case 1:
                    adapter.notifyItemInserted(msgList.size() - 1);
                    adapter.notifyDataSetChanged();
                    msgViewRecyclerView.scrollToPosition(msgList.size() - 1);
                    Log.d("Handler","消息更新");
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        Log.d("进入chatting","activity");
        signUp();
        initEvents();
        addListener();
        addInfoIfExits();
    }
    /**
     * 如果存在那么将信息添加
     * */
    public void addInfoIfExits(){
        String content = getIntent().getStringExtra("message");
        if(content!=null){
            Log.d("信息",content);
            msgList.add(new Msg(content, Msg.TYPE_RECIEVED));
        }
    }

    /**
     * 添加监听器
     * */
    public void addListener() {
        send.setOnClickListener(this);
    }

    /*
    * 退出登录
    * */
    public void signDown(){
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("退出登录成功","--");
            }

            @Override
            public void onError(int code, String error) {
                Log.d("退出登录代码",code+"");
                Log.d("错误内容",error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }
    /*
    * 登录
    * */
    public void signUp(){
        String tel = getSharedPreferences("Setting", MODE_MULTI_PROCESS).getString("user","");
        String passWord = getSharedPreferences("Setting",MODE_MULTI_PROCESS).getString("passWord","");
        Log.d("用户名---",tel);
        Log.d("密码---",passWord);
        EMClient.getInstance().login(tel,
                passWord,
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d("登录成功","--");
                    }
                    @Override
                    public void onError(int code, String error) {
                        Log.d("登录错误","--");
                        Log.d("code = ",code+"");
                        Log.d("error = ",error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        Log.d("正在登录","--");
                    }
                });
    }
    /*
    * 初始化控件
    * */
    public void initEvents() {
        chatId  = getIntent().getStringExtra("destinationTel");
        Log.d("chatId-----",chatId);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgViewRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgViewRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgViewRecyclerView.setAdapter(adapter);
    }

    /**
     * @param content 信息内容
     * @param chatWithId 对方账号
     * */
    public void sendMessage(String content,String chatWithId) {
        EMMessage message = EMMessage.createTxtSendMessage(content,chatWithId);
//如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        Log.d("发送内容",content);
        Log.d("聊天对象",chatWithId);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("发送信息成功","发送信息成功");
                //Toast.makeText(Chatting.this, "发送信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String error) {
                Log.d("错误代码",String.valueOf(code));
                Log.d("发送信息失败",error);
                //Toast.makeText(Chatting.this, "发送信息失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress, String status) {
                //Toast.makeText(Chatting.this, "发送信息中"+status, Toast.LENGTH_SHORT).show();
                Log.d("发送信息中",status);
            }
        });
    }

    /**
     * 重写onClick
     * @param view 被点击的控件
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                Log.d("MainActivity", "Send......");
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    Log.d("Send", content);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1);//刷新
                    msgViewRecyclerView.scrollToPosition(msgList.size() - 1);//定位
                    inputText.setText("");
                    sendMessage(content,chatId);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 重写onMessageReceived方法
     * @param messages 收到的信息列表
     * */
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //将恢复的信息加入到消息列表里面并刷新
        for (int i = 0; i < messages.size(); i++) {
            String content = ((EMTextMessageBody) messages.get(i).getBody()).getMessage();
            msgList.add(new Msg(content, Msg.TYPE_RECIEVED));
            //Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
            Log.d("进入chatting","messageListener");
            Log.d("size="+messages.size(),"回复的信息"+content);
        }
        Message message = new Message();
        message.what = 1;
        UIHandler.sendMessage(message);

    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {

    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {

    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {

    }

    @Override
    public void onMessageChanged(EMMessage message, Object change) {

    }

    @Override
    public void onResume(){
        super.onResume();
        //添加消息监听
        EMClient.getInstance().chatManager().addMessageListener(this);
    }
    public void onStop(){
        super.onStop();
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //移除消息监听
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        signDown();
        //监听消息监听
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }

}
