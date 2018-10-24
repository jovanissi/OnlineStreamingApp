package com.kizalab.zog.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.kizalab.zog.R;
import com.kizalab.zog.utils.Message;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private DataSnapshot dataSnapshot;
    private String userId;
    private int count = 0;

    private List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages, String userId) {
        this.context = context;
        this.messages = messages;
        this.userId = userId;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        return new MessageAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Message msg = messages.get(position);

        if (msg.getSenderId().equals(userId)){
            holder.sentBubble.setVisibility(View.VISIBLE);
            holder.sentMessage.setText(msg.getMessage());
            holder.sentTime.setText(msg.getDate());
        }

        else {
            holder.recBubble.setVisibility(View.VISIBLE);
            holder.recNamePhone.setText(msg.getName() + "  " + msg.getPhone());
            holder.recMessage.setText(msg.getMessage());
            holder.recTime.setText(msg.getDate());
        }

//        if (senderId.equals(userId)){
//            holder.sentBubble.setVisibility(View.VISIBLE);
//            holder.sentMessage.setText(message);
//            holder.sentTime.setText(date);
//        }
//
//        else {
//            holder.recBubble.setVisibility(View.VISIBLE);
//            holder.recNamePhone.setText(name + "  " + phone);
//            holder.recMessage.setText(message);
//            holder.recTime.setText(date);
//        }
//
//        count++;

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.sentBubble) RelativeLayout sentBubble;
        @BindView(R.id.sentMessage) TextView sentMessage;
        @BindView(R.id.sentTime) TextView sentTime;
        @BindView(R.id.recBubble) RelativeLayout recBubble;
        @BindView(R.id.recNamePhone) TextView recNamePhone;
        @BindView(R.id.recMessage) TextView recMessage;
        @BindView(R.id.recTime) TextView recTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
