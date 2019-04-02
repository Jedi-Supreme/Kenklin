package com.jedit.kenklin.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jedit.kenklin.R;
import com.jedit.kenklin.common;
import com.jedit.kenklin.databases.KlinDB;
import com.jedit.kenklin.models.User_Class;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    final long COUNTDOWN_TIME = 30000; // 30 seconds
    final long SECS = 1000;
    TextInputEditText et_code_1,et_code_2,et_code_3,et_code_4,et_code_5,et_code_6;
    CountDownTimer countDownTimer;

    TextInputEditText[] code_input_Array;

    WeakReference<VerificationActivity> weakverification;

    TextView tv_verify_downtime, tv_verify_resend, tv_verify_status;
    ProgressBar probar_verify_code;
    Button bt_verify_code;

    String verification_id;
    String country_code = "+233";
    PhoneAuthProvider.ForceResendingToken resendingToken;
    Bundle registration_bundle;

    KlinDB klinDB;


    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);


        weakverification = new WeakReference<>(this);
        klinDB = new KlinDB(getApplicationContext(),null);
        registration_bundle = getIntent().getExtras();

        tv_verify_downtime = findViewById(R.id.tv_verify_dwntime);
        tv_verify_resend = findViewById(R.id.tv_verify_resend);
        tv_verify_status = findViewById(R.id.tv_verify_Status);

        et_code_1 = findViewById(R.id.et_code_1);
        et_code_2 = findViewById(R.id.et_code_2);
        et_code_3 = findViewById(R.id.et_code_3);
        et_code_4 = findViewById(R.id.et_code_4);
        et_code_5 = findViewById(R.id.et_code_5);
        et_code_6 = findViewById(R.id.et_code_6);

        probar_verify_code = findViewById(R.id.probar_verify_code);
        bt_verify_code = findViewById(R.id.bt_verifycode);

        code_input_Array = new TextInputEditText[]{et_code_1,et_code_2,et_code_3,et_code_4,et_code_5,et_code_6};

        //--------------------------------------------COUNTDOWN TIMER-------------------------------
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, SECS) {

            @Override
            public void onTick(long millisUntilFinished) {

                String remaining_time;
                int timeleft = (int) (millisUntilFinished/SECS);

                if (timeleft < 10){
                    remaining_time = getResources().getString(R.string.resend_code_in) + "0"
                            + String.valueOf((millisUntilFinished/SECS));
                }else {
                    remaining_time = getResources().getString(R.string.resend_code_in)
                            + String.valueOf((millisUntilFinished/SECS));
                }

                tv_verify_downtime.setText(remaining_time);
            }

            @Override
            public void onFinish() {
                tv_verify_resend.setEnabled(true);
                tv_verify_downtime.setVisibility(View.GONE);
            }
        };
        //--------------------------------------------COUNTDOWN TIMER-------------------------------

        if (registration_bundle != null) {
            String mobile_number = registration_bundle.getString(User_Class.MOBILE_NUMBER);
            String usernumber = "+" + country_code + String.valueOf(mobile_number);
            send_Code_Method(usernumber);
        }else {
            Toast.makeText(getApplicationContext(),"bundle is empty",Toast.LENGTH_LONG).show();
        }

        editorSwitcher();


    }
    //=========================================ON CREATE============================================

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-DEFINED METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    void start_timer(){
        tv_verify_downtime.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

    void stop_timer(){
        tv_verify_downtime.setVisibility(View.GONE);
        countDownTimer.cancel();
    }

    void editorSwitcher(){

        for (final TextInputEditText et_input : code_input_Array){
            et_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    switch (et_input.getId()){
                        case R.id.et_code_1:
                            et_code_2.requestFocus();
                            break;
                        case R.id.et_code_2:
                            et_code_3.requestFocus();
                            break;
                        case R.id.et_code_3:
                            et_code_4.requestFocus();
                            break;
                        case R.id.et_code_4:
                            et_code_5.requestFocus();
                            break;
                        case R.id.et_code_5:
                            et_code_6.requestFocus();
                            break;
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    //--------------------------------------SAVE TO ONLINE DB---------------------------------------
    void save_Online(String firebase_id){

        DatabaseReference records_ref =
                FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.records_ref));
        DatabaseReference all_users_ref =
                FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.all_users));

        User_Class klinuser = common.userBundle(registration_bundle);

        if (klinuser != null){
            String cell_number = "0"+String.valueOf(klinuser.getNumber());
            records_ref.child(cell_number).child("uid").setValue(klinuser.getFirebaseID());

            //save user details to All_Users/Biography/Uid
            all_users_ref.child(firebase_id).setValue(klinuser);
            loadBioData_online(firebase_id);
        }


    }
    //--------------------------------------SAVE TO ONLINE DB---------------------------------------

    void loadBioData_online(String fireID){

        String all_users = getResources().getString(R.string.all_users);
        tv_verify_status.setText(getResources().getString(R.string.logging_in));

        DatabaseReference all_user_ref = FirebaseDatabase.getInstance().getReference(all_users);

        all_user_ref.child(fireID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User_Class userBio = dataSnapshot.getValue(User_Class.class);

                if (userBio != null){
                    try {
                        klinDB.addUser(userBio);
                        toDashboard();
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Error loading online data "
                                +e.toString(),Toast.LENGTH_LONG).show();

                    }
                }

                all_user_ref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //send verification code
    void send_Code_Method(String mobile_number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile_number,
                COUNTDOWN_TIME,
                TimeUnit.MILLISECONDS,
                weakverification.get(),
                verificationCallbacks);
        start_timer();
    }

    //resend verification code
    void resend_Code_Method(String mobile_number, PhoneAuthProvider.ForceResendingToken token){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile_number,
                COUNTDOWN_TIME,
                TimeUnit.MILLISECONDS,
                weakverification.get(),
                verificationCallbacks,
                token);
    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                String code;

                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    //Getting the code sent by SMS
                    code = phoneAuthCredential.getSmsCode();

                    //sometimes the code is not detected automatically
                    //in this case the code will be null
                    //so user has to manually enter the code
                    if (code != null) {

                        codeValues_into_views(code);

                        stop_timer();
                        //verify the code
                        verifyVerificationCode(code);
                    }
                }

                @Override
                public void onCodeAutoRetrievalTimeOut(String s) {
                    super.onCodeAutoRetrievalTimeOut(s);
                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(getApplicationContext(),"Verification failed with error: "
                            + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);

                    //storing the verification id that is sent to the user
                    verification_id = s;
                    resendingToken = forceResendingToken;
                }

            };

    //verify code
    private void verifyVerificationCode(String code) {

        //show progress bar
        probar_verify_code.setVisibility(View.VISIBLE);
        probar_verify_code.animate();

        //show status text
        tv_verify_status.setText(getResources().getString(R.string.verifying));
        tv_verify_status.setVisibility(View.VISIBLE);

        try {
            //creating the credential
            final PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_id, code);
            signInWithPhoneAuthCredential(credential);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(weakverification.get(), task -> {
                    if (task.isSuccessful()) {

                        tv_verify_status.setText(getResources().getString(R.string.loading));
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null){
                            save_Online(firebaseUser.getUid());
                        }
                    } else {
                        // Sign in failed, display a message and update the UI

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            common.Mysnackbar(findViewById(R.id.const_verify_layout),
                                    "Invalid Verification Code", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    void codeValues_into_views(String code){

        char[] codes_arr = code.toCharArray();

        if (codes_arr.length == code_input_Array.length){

            for (int x = 0; x < codes_arr.length; x++){
                code_input_Array[x].setText(String.valueOf(codes_arr[x]));
            }
        }

    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-DEFINED METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-


    //---------------------------------------------BUTTON CLICK LISTENERS---------------------------
    public void VerifyCodeRecieved(View view) {
        StringBuilder codebuilder = new StringBuilder();

        for (TextInputEditText et_code : code_input_Array){
            codebuilder.append(et_code.getText().toString());
        }

        if (!codebuilder.toString().isEmpty() && codebuilder.toString().length() == 6){
            //Toast.makeText(getApplicationContext(),codebuilder.toString(),Toast.LENGTH_LONG).show();
            verifyVerificationCode(codebuilder.toString());
        }else {
            common.Mysnackbar(findViewById(R.id.const_verify_layout),
                    "Enter Verification Code", Snackbar.LENGTH_LONG).show();
        }

    }

    public void ResendCode(View view) {

       if (registration_bundle != null){
           String mobile_number = registration_bundle.getString(User_Class.MOBILE_NUMBER);
           String usernumber = "+" + country_code + String.valueOf(mobile_number);

           resend_Code_Method(usernumber,resendingToken);
       }

    }
    //---------------------------------------------BUTTON CLICK LISTENERS---------------------------

    void toDashboard(){
        probar_verify_code.setVisibility(View.GONE);
        Intent dashboard_intent = new Intent(getApplicationContext(),Dashboard.class);
        dashboard_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dashboard_intent);
        super.finish();
    }
}
