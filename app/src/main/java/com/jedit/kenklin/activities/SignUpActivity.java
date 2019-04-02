package com.jedit.kenklin.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.jedit.kenklin.R;
import com.jedit.kenklin.common;
import com.jedit.kenklin.models.User_Class;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText et_reg_fn, et_reg_ln, et_reg_number;

    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        et_reg_fn = findViewById(R.id.et_reg_fn);
        et_reg_ln = findViewById(R.id.et_reg_ln);
        et_reg_number = findViewById(R.id.et_reg_numb);

    }
    //=========================================ON CREATE============================================

    void testInputs(){

        if (et_reg_fn.getText().toString().isEmpty() || et_reg_fn.getText().toString().equals("")){
            common.Mysnackbar(findViewById(R.id.const_reg_layout),
                    "Please Enter First Name", Snackbar.LENGTH_SHORT).show();
        }else if (et_reg_ln.getText().toString().isEmpty() || et_reg_ln.getText().toString().equals("")){
            common.Mysnackbar(findViewById(R.id.const_reg_layout),
                    "Please Enter Last Name", Snackbar.LENGTH_SHORT).show();
        }else if (et_reg_number.getText().toString().isEmpty() || et_reg_number.getText().toString().equals("")){
            common.Mysnackbar(findViewById(R.id.const_reg_layout),
                    "Please Enter Mobile Number", Snackbar.LENGTH_SHORT).show();
        }else {

            try {
                int number = Integer.parseInt(et_reg_number.getText().toString());

                if (String.valueOf(number).length() == 9 ){
                    User_Class user = new User_Class(
                            et_reg_fn.getText().toString(),
                            et_reg_ln.getText().toString(),
                            String.valueOf(number));

                    toVerification(user);
                }
            }catch (Exception ignored){

                common.Mysnackbar(findViewById(R.id.const_reg_layout),
                        "Enter valid Mobile Number", Snackbar.LENGTH_SHORT).show();
            }

        }
    }

    public void VerifyInfo(View view) {
        testInputs();
    }

    void toVerification(User_Class userClass){
        Intent verify_intent = new Intent(getApplicationContext(), VerificationActivity.class);
        verify_intent.putExtra(User_Class.FIRSTNAME,userClass.getFn());
        verify_intent.putExtra(User_Class.LASTNAME,userClass.getLn());
        verify_intent.putExtra(User_Class.MOBILE_NUMBER,userClass.getNumber());
        startActivity(verify_intent);
    }
}
