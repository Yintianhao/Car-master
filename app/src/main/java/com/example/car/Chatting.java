package com.example.car;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EasyUtils;

import java.util.ArrayList;
import java.util.List;

import Tool.Const;
import Tool.Msg;
import Tool.MsgAdapter;

public class Chatting extends AppCompatActivity implements View.OnClickListener, EMMessageListener {

    private List<Msg> msgList = new ArrayList<>();//消息列表
    private EditText inputText;//输入框
    private Button send;//发送
    private RecyclerView msgViewRecyclerView;
    private MsgAdapter adapter;
    private String chatId;//自己传进来的跟自己聊天的人的ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        initEvents();
        addListener();
    }

    public void addListener() {
        send.setOnClickListener(this);
    }

    public void signUp(){
        EMClient.getInstance().login(Const.userName,
                Const.passWord,
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        //
                        Toast.makeText(getApplicationContext(),"可以跟该用户聊天了",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(int code, String error) {

                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
    }
    public void initEvents() {
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgViewRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgViewRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgViewRecyclerView.setAdapter(adapter);
    }

    public void sendMessage(String content,String chatWithId) {
        EMMessage message = EMMessage.createTxtSendMessage(content,"13657433916");
//如果是群聊，设置chattype，默认是单聊
        message.setChatType(EMMessage.ChatType.Chat);
//发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.d("发送信息成功","发送信息成功");
                //Toast.makeText(Chatting.this, "发送信息成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int code, String error) {
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

    @Override
    public void onMessageReceived(List<EMMessage> messages) {
        //将恢复的信息加入到消息列表里面并刷新
        for (int i = 0; i < messages.size(); i++) {
            String content = ((EMTextMessageBody) messages.get(i).getBody()).getMessage();
            msgList.add(new Msg(content, Msg.TYPE_RECIEVED));
            //Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
            Log.d("size="+messages.size(),"回复的信息"+content);
        }
        adapter.notifyItemInserted(msgList.size() - 1);
        adapter.notifyDataSetChanged();
        msgViewRecyclerView.scrollToPosition(msgList.size() - 1);

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
        EMClient.getInstance().chatManager().addMessageListener(this);
    }
    public void onStop(){
        super.onStop();
        EMClient.getInstance().chatManager().removeMessageListener(this);
    }
}