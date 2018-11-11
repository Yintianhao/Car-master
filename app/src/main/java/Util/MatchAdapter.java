package Util;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.car.Chatting;
import com.example.car.R;

import java.util.List;


/**
 * Created by 31786 on 2018/8/10.
 */

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder> {


    private List<MatchRecord> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_result_item,parent,false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(view.getContext(),Chatting.class));
            }
        });
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(position==0){
            MatchRecord matchRecord = list.get(position);
            holder.phoneNum.setText(matchRecord.getPhoneNum());
            holder.distanceAfter.setText(matchRecord.getDistanceAfter()+"米");
            holder.distanceBefore.setText(matchRecord.getDistanceBefore()+"米");
            holder.startTime.setText(matchRecord.getStartTime());
            holder.endTime.setText(matchRecord.getEndTime());
            holder.startToEnd.setText(matchRecord.getStart()+"------------>"+matchRecord.getEnd());
            holder.userName.setText(matchRecord.getName());
            holder.moneyAfter.setText(matchRecord.getMoneyAfter()+"元");
            holder.moneyBefore.setText(matchRecord.getMoneyBefore()+"元");
            holder.carNumber.setText(matchRecord.getCarNumber());
        }else {
            MatchRecord matchRecord = list.get(position);
            holder.phoneNum.setText(matchRecord.getPhoneNum());
            holder.distanceAfter.setText(matchRecord.getDistanceAfter()+"米");
            holder.distanceBefore.setText(matchRecord.getDistanceBefore()+"米");
            holder.startTime.setText(matchRecord.getStartTime());
            holder.endTime.setText(matchRecord.getEndTime());
            holder.startToEnd.setText(matchRecord.getStart()+"------------>"+matchRecord.getEnd());
            holder.userName.setText(matchRecord.getName());
            holder.moneyAfter.setText(matchRecord.getMoneyAfter()+"元");
            holder.moneyBefore.setText(matchRecord.getMoneyBefore()+"元");
            holder.carNumber.setText("乘客身份没有车牌信息");
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView userName;
        TextView phoneNum;
        TextView startToEnd;
        TextView startTime;
        TextView endTime;
        TextView moneyBefore;
        TextView moneyAfter;
        TextView distanceBefore;
        TextView distanceAfter;
        TextView carNumber;
        public ViewHolder(View view){
            super(view);
            this.view = view;
            userName = (TextView)view.findViewById(R.id.match_userName);
            phoneNum = (TextView)view.findViewById(R.id.match_userPhone);
            startToEnd = (TextView)view.findViewById(R.id.match_startToEnd);
            startTime = (TextView)view.findViewById(R.id.match_TimeStart);
            endTime = (TextView)view.findViewById(R.id.match_TimeEnd);
            moneyBefore = (TextView)view.findViewById(R.id.match_moneyBefore);
            moneyAfter = (TextView)view.findViewById(R.id.match_moneyAfter);
            distanceBefore = (TextView)view.findViewById(R.id.match_DistanceBefore);
            distanceAfter = (TextView)view.findViewById(R.id.match_DistanceAfter);
            carNumber = (TextView)view.findViewById(R.id.match_CarNum);
        }
    }
    public MatchAdapter(List<MatchRecord> list){
        this.list = list;
    }
}
