package com.kizalab.zog.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kizalab.zog.R;
import com.kizalab.zog.adapters.MessageAdapter;
import com.kizalab.zog.utils.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class ChatFragment extends Fragment {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.message_field) EditText message_field;
    @BindView(R.id.send_message) FloatingActionButton send_message;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String message = "", userId = "";

    private MessageAdapter messageAdapter;

    private SharedPreferences userPrefs;

    private List<Message> messages = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, v);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("version1");

        userPrefs = v.getContext().getSharedPreferences("user_prefs", MODE_PRIVATE);

        if (user != null){

            userId = user.getUid();

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

//            message_field.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (message.trim().isEmpty()){
//                        send_message.setEnabled(false);
//                    } else {
//                        send_message.setEnabled(true);
//                    }
//                }
//            });

            send_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    message = message_field.getText().toString();

                    if (message.trim().isEmpty()){
                        message_field.setError("No message");
                    }

                    else {
                        sendNewMessage();
                    }
                }
            });

        }

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (user != null){
            databaseReference.child("messages").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    messages.clear();

                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()){

                        String senderId = messageSnapshot.child("senderId").getValue().toString();
                        String name = messageSnapshot.child("name").getValue().toString();
                        String phone = messageSnapshot.child("phone").getValue().toString();
                        String message = messageSnapshot.child("message").getValue().toString();
                        String date = messageSnapshot.child("date").getValue().toString();

                        Message msg = new Message(name, phone, senderId, date, message);
                        messages.add(msg);
                    }

                    messageAdapter = new MessageAdapter(getActivity(), messages, user.getUid());
                    recyclerView.setAdapter(messageAdapter);
                    recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void sendNewMessage(){

        message_field.setText("");

        String time;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

        Date date = new Date();
        time = dateFormat.format(date);

        Map<String, String> messageData = new HashMap<>();
        messageData.put("name", userPrefs.getString("name", ""));
        messageData.put("phone", userPrefs.getString("phone", ""));
        messageData.put("message", message);
        messageData.put("date", time);
        messageData.put("senderId", userId);

        String key = databaseReference.child("messages").push().getKey();
        databaseReference.child("messages").child(key).setValue(messageData);

        message = "";

//        MessageItems message = new MessageItems(messageView.getText().toString().trim(), time, restId, clientId, orderKey, token);
//        KEY = databaseReference.child("messages").push().getKey();
//        databaseReference.child("messages").child(KEY).setValue(message);

    }

}
