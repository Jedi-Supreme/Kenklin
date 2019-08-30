package com.jedit.kenklin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jedit.kenklin.R;
import com.jedit.kenklin.databases.KlinDB;
import com.jedit.kenklin.models.Basket_Items;
import com.jedit.kenklin.models.Request_Class;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Req_recy_Adapter extends RecyclerView.Adapter {

    private static ArrayList<Request_Class> requests_list;

    public Req_recy_Adapter(ArrayList<Request_Class> requests_list) {
        Req_recy_Adapter.requests_list = requests_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_requests_list, parent, false);
        return new request_list_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((request_list_holder) holder).bind_views(requests_list.get(position));
    }

    @Override
    public int getItemCount() {
        return requests_list.size();
    }

    public class request_list_holder extends RecyclerView.ViewHolder{

        TextView tv_req_Date, tv_req_state, tv_req_compDate;
        KlinDB klin_db;

        int x = 0;

        request_list_holder(View itemView) {
            super(itemView);

            tv_req_Date = itemView.findViewById(R.id.tv_req_date);
            tv_req_state = itemView.findViewById(R.id.tv_req_state);
            tv_req_compDate = itemView.findViewById(R.id.tv_req_compdate);

            WeakReference<Context> weak_mcontext = new WeakReference<>(itemView.getContext());
            klin_db = new KlinDB(weak_mcontext.get(),null);

        }

        void bind_views(final Request_Class singlereq) {
            tv_req_Date.setText(singlereq.getReqDate());
            tv_req_state.setText(singlereq.getStatus());
        }
    }

}
