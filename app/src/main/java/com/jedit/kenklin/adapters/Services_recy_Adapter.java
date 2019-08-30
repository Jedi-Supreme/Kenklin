package com.jedit.kenklin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jedit.kenklin.R;
import com.jedit.kenklin.activities.ServiceRequestActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Services_recy_Adapter extends RecyclerView.Adapter {

    private ArrayList<String> services_list;
    private AlertDialog alertDialog;

    public Services_recy_Adapter(ArrayList<String> services_list, AlertDialog dialog) {
        this.services_list = services_list;
        this.alertDialog = dialog;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_service_names, parent, false);
        return new Services_list_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Services_list_holder) holder).bind_views(services_list.get(position));
    }

    @Override
    public int getItemCount() {
        return services_list.size();
    }

    public class Services_list_holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name;
        WeakReference<Context> weak_mcontext;
        String servicename;

        Services_list_holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_serv_rname);
            itemView.setOnClickListener(this);

            weak_mcontext = new WeakReference<>(itemView.getContext());
        }

        void bind_views(final String service) {
            servicename = service;
            tv_name.setText(service);
        }

        @Override
        public void onClick(View v) {

            if (weak_mcontext.get() instanceof ServiceRequestActivity){
                //((ServiceRequestActivity) weak_mcontext.get()).setService_name(servicename);
                //((ServiceRequestActivity) weak_mcontext.get()).load_item_list();
                alertDialog.dismiss();
            }

        }
    }
}
