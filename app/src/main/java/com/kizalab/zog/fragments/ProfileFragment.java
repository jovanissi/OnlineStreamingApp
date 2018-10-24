package com.kizalab.zog.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kizalab.zog.R;
import com.kizalab.zog.activities.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    @BindView(R.id.name) EditText name;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.phone) EditText phone;
    @BindView(R.id.logoutBtn) Button logoutBtn;

    private SharedPreferences userPrefs;

    FirebaseAuth mAuth;
    FirebaseUser user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);

        userPrefs = v.getContext().getSharedPreferences("user_prefs", MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        name.setText(userPrefs.getString("name", ""));
        email.setText(userPrefs.getString("email", ""));
        phone.setText(userPrefs.getString("phone", ""));

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPrefs.edit()
                        .putString("uid", "")
                        .putString("name", "")
                        .putString("email", "")
                        .putString("phone", "")
                        .apply();
                mAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        return v;
    }

}
