package com.jedit.kenklin.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jedit.kenklin.R;
import com.jedit.kenklin.databases.KlinDB;
import com.jedit.kenklin.models.User_Class;

import java.lang.ref.WeakReference;

public class Dashboard extends AppCompatActivity {

    TextView tv_dash_numb, tv_dash_name, tv_dash_norecents;
    RecyclerView recy_laundry;

    WeakReference<Dashboard> weakdash;
    KlinDB klin_db;

    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        weakdash = new WeakReference<>(this);
        klin_db = new KlinDB(getApplicationContext(),null);

        if (!isUserLogged_in()){
            toLogin();
        }

        tv_dash_name = findViewById(R.id.tv_dash_name);
        tv_dash_numb = findViewById(R.id.tv_dash_usernumb);
        tv_dash_norecents = findViewById(R.id.tv_norecents);

        recy_laundry = findViewById(R.id.recy_dash_laundry);

        try {
            loadUserdata();
        }catch (Exception ignored){}

    }
    //=========================================ON CREATE============================================

    //------------------------------------------DEFINED METHODS-------------------------------------
    //test if user is logged in
    boolean isUserLogged_in(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    void loadUserdata(){

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            User_Class user_class = klin_db.fetchUser(firebaseUser.getUid());

            String fullname = user_class.getFn() + " " + user_class.getLn();
            String number = "0" + user_class.getNumber();

            tv_dash_name.setText(fullname);
            tv_dash_numb.setText(number);
        }
    }
    //------------------------------------------DEFINED METHODS-------------------------------------

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-OVERRIDE METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    @Override
    public void onBackPressed() {

        Intent startmain = new Intent(Intent.ACTION_MAIN);
        startmain.addCategory(Intent.CATEGORY_HOME);
        startmain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startmain);

    }

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-OVERRIDE METHODS-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    //---------------------------------BUTTON CLICK LISTENER----------------------------------------
    public void req_cleaning_service(View view) {
        toRequest();
    }
    //---------------------------------BUTTON CLICK LISTENER----------------------------------------

    //-----------------------------------------------INTENTS----------------------------------------
    void toRequest(){
        Intent req_intent = new Intent(getApplicationContext(),ServiceRequestActivity.class);
        startActivity(req_intent);
    }

    void toLogin(){
        Intent login_intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(login_intent);
        super.finish();
    }
    //-----------------------------------------------INTENTS----------------------------------------

}
