package Util;

/**
 * Created by 31786 on 2018/5/15.
 */

public class Msg {
    public static final int TYPE_RECIEVED = 0;//表示这是一条收到的消息
    public static final int TYPE_SENT = 1;//表示这是一条发出去的消息
    private String content;//消息的内容
    private int type;//消息的类型
    public Msg(String content,int type){
        this.content = content;
        this.type = type;
    }
    public String getContent(){
        return content;
    }
    public int getType(){
        return type;
    }
}
