package com.jedit.kenklin.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jedit.kenklin.R;
import com.jedit.kenklin.common;
import com.jedit.kenklin.models.User_Class;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText et_login_number;
    ProgressBar probar_login;

    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_number = findViewById(R.id.et_login_numb);
        probar_login = findViewById(R.id.probar_login);

    }
    //=========================================ON CREATE============================================

    public void Signup(View view) {
        toSignup();
    }

    void toSignup(){
        Intent signup_intent = new Intent(getApplicationContext(),SignUpActivity.class);
        startActivity(signup_intent);
    }

    void testnmber(){
        probar_login.setVisibility(View.VISIBLE);
        if (!et_login_number.getText().toString().isEmpty()){
            try{
                int number = Integer.parseInt(et_login_number.getText().toString());

                if (String.valueOf(number).length() == 9){
                    fetchRecords(String.valueOf(number));
                }
            }catch (Exception ignored){
                probar_login.setVisibility(View.GONE);
                common.Mysnackbar(findViewById(R.id.const_login_layout),
                        "Enter Valid number", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchRecords(String number) {
        DatabaseReference recordsref = FirebaseDatabase.getInstance()
                .getReference(getResources().getString(R.string.records_ref));

        recordsref.child("0"+number).child("uid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String uid = dataSnapshot.getValue(String.class);

                if (uid !=null && !uid.equals("")){
                    fetchUser(uid);
                }else {
                    probar_login.setVisibility(View.GONE);
                    et_login_number.setText("");
                    common.Mysnackbar(findViewById(R.id.const_login_layout),
                            "0" + number + " not found, Sign up to proceed", Snackbar.LENGTH_LONG).show();
                }

                recordsref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchUser(String uid) {

        DatabaseReference userref = FirebaseDatabase.getInstance()
                .getReference(getResources().getString(R.string.all_users));

        userref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User_Class user = dataSnapshot.getValue(User_Class.class);

                if (user!= null){
                    toVerification(user);
                }else {
                    et_login_number.setText("");
                    probar_login.setVisibility(View.GONE);
                }

                userref.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void toVerification(User_Class userClass){
        Intent verify_intent = new Intent(getApplicationContext(), VerificationActivity.class);
        verify_intent.putExtra(User_Class.FIRSTNAME,userClass.getFn());
        verify_intent.putExtra(User_Class.LASTNAME,userClass.getLn());
        verify_intent.putExtra(User_Class.MOBILE_NUMBER,userClass.getNumber());
        startActivity(verify_intent);
    }

    public void confirm_login_number(View view) {
        testnmber();
    }
}
