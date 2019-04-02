package com.jedit.kenklin.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.jedit.kenklin.R;

public class ServiceRequestActivity extends AppCompatActivity {

    TabHost serv_tabhost;

    final String SERVICE_TAG = "Service";
    final String SUMMARY_TAG = "Summary";

    FloatingActionButton fab_req_next, fab_req_back, fab_req_confirm;

    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        //------------------------------------------TAB HOST----------------------------------------
        serv_tabhost = findViewById(R.id.serv_tabhost);
        serv_tabhost.setup();

        TabHost.TabSpec serviceSpec = serv_tabhost.newTabSpec(SERVICE_TAG);
        TabHost.TabSpec sumSpec = serv_tabhost.newTabSpec(SUMMARY_TAG);

        serviceSpec.setIndicator(SERVICE_TAG);
        sumSpec.setIndicator(SUMMARY_TAG);

        serviceSpec.setContent(R.id.serv_request);
        sumSpec.setContent(R.id.serv_summary);

        serv_tabhost.addTab(serviceSpec);
        serv_tabhost.addTab(sumSpec);
        //------------------------------------------TAB HOST----------------------------------------

    }
    //=========================================ON CREATE============================================
}
