package com.kizalab.zog.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kizalab.zog.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Context context;

    // Enter phone number view
    @BindView(R.id.loginRelativeLayout) RelativeLayout loginRelativeLayout;
    @BindView(R.id.phoneNbr) EditText phoneNbr;
    @BindView(R.id.avi) AVLoadingIndicatorView avi;
    @BindView(R.id.sendCodeBtn) Button sendCodeBtn;

    // Enter verification code view
    @BindView(R.id.codeRelativeLayout) RelativeLayout codeRelativeLayout;
    @BindView(R.id.sentCode) EditText sentCode;
    @BindView(R.id.avi2) AVLoadingIndicatorView avi2;
    @BindView(R.id.enterBtn) Button enterBtn;

    // Create profile
    @BindView(R.id.createProfileView) RelativeLayout createProfileView;
    @BindView(R.id.phone) EditText phone;
    @BindView(R.id.name) EditText name;
    @BindView(R.id.email) EditText email;
    @BindView(R.id.avi3) AVLoadingIndicatorView avi3;
    @BindView(R.id.okBtn) Button okBtn;

    private String phoneNbrStr = "", nameStr = "", emailStr = "null";

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private DatabaseReference databaseReference;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private boolean mVerificationInProgress = false;

    private SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        context = this;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("version1");

        userPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        TextWatcher phoneNbrWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phoneNbrStr = phoneNbr.getText().toString().trim();

                if (!phoneNbrStr.startsWith("+")){
                    phoneNbr.setError("Phone not valid.");
                }
            }
        };
        phoneNbr.addTextChangedListener(phoneNbrWatcher);

        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNbrStr = phoneNbr.getText().toString();

                if(!phoneNbrStr.trim().isEmpty()){
                    sendCodeBtn.setVisibility(View.INVISIBLE);
                    avi.setVisibility(View.VISIBLE);
                    phoneNbr.setEnabled(false);
                    startPhoneNumberVerification(phoneNbrStr);
                }
                else{
                    phoneNbr.setError("Please enter phone number");
                }
            }
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = sentCode.getText().toString().trim();
                if (code.isEmpty()){
                    sentCode.setError("This field is required!");
                }

                else if (code.length() < 6){
                    sentCode.setError("Invalid code!");
                }
                else{
                    sentCode.setEnabled(false);
                    enterBtn.setVisibility(View.INVISIBLE);
                    avi2.setVisibility(View.VISIBLE);
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // [END_EXCLUDE]

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                mVerificationInProgress = false;

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    phoneNbr.setError("Invalid phone number.");
                    phoneNbr.setEnabled(true);
                    avi.setVisibility(View.INVISIBLE);
                    sendCodeBtn.setVisibility(View.VISIBLE);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                    Toast.makeText(LoginActivity.this, "Nbr of sms exceeded...", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseNetworkException){
                    phoneNbr.setEnabled(true);
                    avi.setVisibility(View.INVISIBLE);
                    sendCodeBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(LoginActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
//                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

                codeView();
            }
        };


    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

//        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
//
                            user = task.getResult().getUser();
                            final String uid = user.getUid();

                            databaseReference.child("users").child(uid).child("phone").setValue(phoneNbrStr);

                            databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("name")){

                                        profileView();
                                        phone.setText(phoneNbrStr);

                                        okBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                avi3.setVisibility(View.VISIBLE);
                                                okBtn.setVisibility(View.GONE);
                                                nameStr = name.getText().toString();
                                                emailStr = email.getText().toString();

                                                if (nameStr.trim().isEmpty()){
                                                    name.setError("Please enter your name");
                                                }
                                                else {

                                                    Map<String, String> data = new HashMap<>();
                                                    data.put("name", nameStr);
                                                    data.put("phone", phoneNbrStr);
                                                    data.put("email", emailStr);

                                                    databaseReference.child("users").child(uid).setValue(data)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        userPrefs.edit()
                                                                                .putString("uid", uid)
                                                                                .putString("name", nameStr)
                                                                                .putString("email", emailStr)
                                                                                .putString("phone", phoneNbrStr)
                                                                                .apply();

                                                                        Intent intent = new Intent(context, MainActivity.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }
                                                                }
                                                            });

                                                }
                                            }
                                        });

                                    }

                                    else if (dataSnapshot.hasChild("name")) {

                                        userPrefs.edit()
                                                .putString("uid", uid)
                                                .putString("name", dataSnapshot.child("name").getValue().toString())
                                                .putString("email", dataSnapshot.child("email").getValue().toString())
                                                .putString("phone", phoneNbrStr)
                                                .apply();

                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    else {

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid

                                sentCode.setEnabled(true);
                                sentCode.setError("Invalid code!");
                                enterBtn.setVisibility(View.VISIBLE);
                                avi2.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                });
    }

    public void loginView(){
        loginRelativeLayout.setVisibility(View.VISIBLE);
        codeRelativeLayout.setVisibility(View.GONE);
        createProfileView.setVisibility(View.GONE);
    }

    public void codeView(){
        loginRelativeLayout.setVisibility(View.GONE);
        codeRelativeLayout.setVisibility(View.VISIBLE);
        createProfileView.setVisibility(View.GONE);
    }

    public void profileView(){
        loginRelativeLayout.setVisibility(View.GONE);
        codeRelativeLayout.setVisibility(View.GONE);
        createProfileView.setVisibility(View.VISIBLE);
    }
}
