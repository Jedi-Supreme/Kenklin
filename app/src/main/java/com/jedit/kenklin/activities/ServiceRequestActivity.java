package com.jedit.kenklin.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jedit.kenklin.R;
import com.jedit.kenklin.adapters.Items_recy_Adapter;
import com.jedit.kenklin.adapters.Services_recy_Adapter;
import com.jedit.kenklin.adapters.summary_recy_Adapter;
import com.jedit.kenklin.common;
import com.jedit.kenklin.databases.KlinDB;
import com.jedit.kenklin.models.Basket_Items;
import com.jedit.kenklin.models.Request_Class;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ServiceRequestActivity extends AppCompatActivity {

    TabHost serv_tabhost;
    TabWidget tabWidget;
    ConstraintLayout const_serv_req;
    TextInputEditText et_serv_req;
    RecyclerView recy_serv_items, recy_sum_laundry;
    SearchView sv_search_item;
    TextView tv_summary_total;

    final String ITEMS_TAG = "Items";
    final String SUMMARY_TAG = "Summary";

    WeakReference<ServiceRequestActivity> weakService;

    KlinDB klin_db;

    FloatingActionButton fab_req_next, fab_sum_back, fab_sum_confirm;

    //=========================================ON CREATE============================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);

        weakService = new WeakReference<>(this);
        klin_db = new KlinDB(getApplicationContext(),null);
        const_serv_req = findViewById(R.id.const_serv_req);
        recy_serv_items = findViewById(R.id.recy_serv_items);
        recy_sum_laundry = findViewById(R.id.recy_sum_laundry);
        sv_search_item = findViewById(R.id.sv_search_items);
        tv_summary_total = findViewById(R.id.tv_sum_total);

        //------------------------------------------TAB HOST----------------------------------------
        serv_tabhost = findViewById(R.id.serv_tabhost);
        serv_tabhost.setup();

        TabHost.TabSpec serviceSpec = serv_tabhost.newTabSpec(ITEMS_TAG);
        TabHost.TabSpec sumSpec = serv_tabhost.newTabSpec(SUMMARY_TAG);

        serviceSpec.setIndicator(ITEMS_TAG);
        sumSpec.setIndicator(SUMMARY_TAG);

        serviceSpec.setContent(R.id.serv_request);
        sumSpec.setContent(R.id.serv_summary);

        serv_tabhost.addTab(serviceSpec);
        serv_tabhost.addTab(sumSpec);

        et_serv_req = findViewById(R.id.et_serv_req);

        tabWidget = serv_tabhost.getTabWidget();
        tabWidget.setEnabled(false);
        //------------------------------------------TAB HOST----------------------------------------

        fab_req_next = findViewById(R.id.fab_req_nxt);
        fab_sum_back = findViewById(R.id.fab_sum_back);
        fab_sum_confirm = findViewById(R.id.fab_sum_fin);

        fab_req_next.setOnClickListener(v -> move_to_summary());
        fab_sum_back.setOnClickListener(v -> move_to_items());
        fab_sum_confirm.setOnClickListener(v -> send_req_to_firebase());

        et_serv_req.setOnClickListener( v-> pick_service_dialog());

        try {
            load_item_list();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error loading items " + e.toString(),Toast.LENGTH_LONG).show();
        }

    }
    //=========================================ON CREATE============================================

    //public void setService_name(String service_name){
        //et_serv_req.setText(service_name);
    //}

    public void load_item_list(){
        ArrayList<Basket_Items> bskt_items = klin_db.fetch_all_items();

        if (bskt_items.size() > 0){
            //tv_set_noshow.setVisibility(View.GONE);
            Items_recy_Adapter servicesRecyAdapter = new Items_recy_Adapter(bskt_items);
            recy_serv_items.setLayoutManager(new LinearLayoutManager(weakService.get()));
            recy_serv_items.setAdapter(servicesRecyAdapter);
        }else {
            recy_serv_items.setAdapter(null);
            //tv_set_noshow.setVisibility(View.VISIBLE);
        }
    }

    public void load_summary_list(){

        ArrayList<Basket_Items> bskt_items = Items_recy_Adapter.getItems_list();

        if (bskt_items.size() > 0){
            //tv_set_noshow.setVisibility(View.GONE);
            summary_recy_Adapter servicesRecyAdapter = new summary_recy_Adapter(bskt_items);
            recy_sum_laundry.setLayoutManager(new LinearLayoutManager(weakService.get()));
            recy_sum_laundry.setAdapter(servicesRecyAdapter);
            String total = summary_recy_Adapter.getTotal_amt() + " GH¢";
            tv_summary_total.setText(total);
        }else {
            recy_sum_laundry.setAdapter(null);
            //tv_set_noshow.setVisibility(View.VISIBLE);
        }

    }

    void move_to_summary(){
        serv_tabhost.setCurrentTab(1);

        try {
            load_summary_list();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error loading summary " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void move_to_items(){
        summary_recy_Adapter.setTotal_amt(0);
        serv_tabhost.setCurrentTab(0);
    }

    //build dialog for user to pick service
    void pick_service_dialog() {
        AlertDialog servlist_dialog = new AlertDialog.Builder(weakService.get()).create();

        View servlist_view = LayoutInflater.from(weakService.get())
                .inflate(R.layout.diag_service_picker, const_serv_req, false);

        servlist_dialog.setCancelable(true);
        servlist_dialog.setView(servlist_view);

        RecyclerView recy_serv_list = servlist_view.findViewById(R.id.recy_diag_servlist);
        TextView tv_no_show = servlist_view.findViewById(R.id.tv_no_show);

        if (klin_db.service_names().size() > 0){
            recy_serv_list.setVisibility(View.VISIBLE);
            load_serv_diag_list(recy_serv_list, servlist_dialog);

        }else{
            tv_no_show.setVisibility(View.VISIBLE);
        }

        servlist_dialog.show();
    }

    void load_serv_diag_list(RecyclerView recyclerView, AlertDialog dialog){
        Services_recy_Adapter servicesRecyAdapter = new Services_recy_Adapter(klin_db.service_names(), dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(weakService.get()));
        recyclerView.setAdapter(servicesRecyAdapter);
    }

    void send_req_to_firebase(){

        SimpleDateFormat date_timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        DatabaseReference req_ref = FirebaseDatabase.getInstance().getReference("Orders");

        if (FirebaseAuth.getInstance().getCurrentUser() != null){

           String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Request_Class request_items = new Request_Class(uid,
                    date_timeFormat.format(calendar.getTime()),"",
                    common.STATE_PENDING,Items_recy_Adapter.getItems_list());

            req_ref.child(String.valueOf(calendar.getTimeInMillis())).child(uid).setValue(request_items);
            request_items.setReqTime_stamp(String.valueOf(calendar.getTimeInMillis()));
            save_toLocal(request_items);
        }else {
            Toast.makeText(getApplicationContext(),"Unable to send request, please try again later",Toast.LENGTH_LONG).show();
        }

    }

    void save_toLocal(Request_Class request){

        try {
            klin_db.addRequest(request);
            toDashboard();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Saving request failed with error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    void toDashboard(){
        Intent dash_intent = new Intent(getApplicationContext(),Dashboard.class);
        dash_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(dash_intent);
    }


}
