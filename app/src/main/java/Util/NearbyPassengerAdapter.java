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

public class NearbyPassengerAdapter extends RecyclerView.Adapter<NearbyPassengerAdapter.ViewHolder>{
    private List<MatchRecord> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_passenger_item,parent,false);
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
        MatchRecord matchRecord = list.get(position);
        //设置文字
        holder.userName.setText(matchRecord.getName());
        holder.userTel.setText(matchRecord.getPhoneNum());
        holder.userProcess.setText(matchRecord.getStart()+"------->"+matchRecord.getEnd());
        holder.userStartTime.setText(matchRecord.getStartTime());
        holder.userEndTime.setText(matchRecord.getEndTime());
    }
    @Override
    public int getItemCount () {
        return list.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView userName;
        TextView userTel;
        TextView userProcess;
        TextView userStartTime;
        TextView userEndTime;
        public ViewHolder(View view){
            super(view);
            this.view = view;
            //初始化
            userName = (TextView)view.findViewById(R.id.nearby_passenger_name);
            userProcess = (TextView)view.findViewById(R.id.nearby_passenger_startToEnd);
            userTel = (TextView)view.findViewById(R.id.nearby_passenger_tel);
            userStartTime = (TextView)view.findViewById(R.id.nearby_passenger_starttime);
            userEndTime = (TextView)view.findViewById(R.id.nearby_passenger_endtime);
        }
    }
    public NearbyPassengerAdapter(List<MatchRecord> list){
        this.list = list;
    }
}
