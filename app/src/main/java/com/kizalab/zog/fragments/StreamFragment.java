package com.kizalab.zog.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.kizalab.zog.R;
import com.kizalab.zog.activities.TVActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StreamFragment extends Fragment {


    @BindView(R.id.watch_tv) ImageButton watch_tv;

    public StreamFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_stream, container, false);
        ButterKnife.bind(this, v);

        watch_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TVActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        return v;
    }

}
