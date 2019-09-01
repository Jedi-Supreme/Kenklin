package com.jedit.kenklin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jedit.kenklin.R;
import com.jedit.kenklin.activities.Dashboard;
import com.jedit.kenklin.common;
import com.jedit.kenklin.databases.KlinDB;
import com.jedit.kenklin.models.Request_Class;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

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

    public class request_list_holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tv_req_Date, tv_req_state, tv_req_compDate, tv_req_cancel;
        KlinDB klin_db;
        Request_Class request;

        WeakReference<Context> weak_mcontext;

        request_list_holder(View itemView) {
            super(itemView);

            tv_req_Date = itemView.findViewById(R.id.tv_req_date);
            tv_req_state = itemView.findViewById(R.id.tv_req_state);
            tv_req_compDate = itemView.findViewById(R.id.tv_req_compdate);
            tv_req_cancel = itemView.findViewById(R.id.tv_req_cancel);

            tv_req_cancel.setOnClickListener(this);

            weak_mcontext = new WeakReference<>(itemView.getContext());
            klin_db = new KlinDB(weak_mcontext.get(),null);

        }

        void bind_views(final Request_Class singlereq) {
            request = singlereq;
            tv_req_Date.setText(singlereq.getReqDate());
            tv_req_state.setText(singlereq.getStatus());

            switch (singlereq.getStatus()){

                case common.STATE_ACTIVE:
                    tv_req_compDate.setText("");
                    tv_req_state.setTextColor(weak_mcontext.get().getResources().getColor(R.color.blue));
                    break;
                case common.STATE_CANCELLED:
                    tv_req_compDate.setText("");
                    tv_req_cancel.setText(weak_mcontext.get().getResources().getString(R.string.delete_lbl));
                    tv_req_cancel.setOnClickListener(v -> klin_db.deleteRequest(singlereq.getReqTime_stamp(),weak_mcontext.get()));
                    tv_req_state.setTextColor(weak_mcontext.get().getResources().getColor(R.color.red));
                    break;
                case common.STATE_COMPLETED:
                    tv_req_cancel.setVisibility(View.INVISIBLE);
                    tv_req_compDate.setText(singlereq.getCompleteDate());
                    tv_req_compDate.setTextColor(weak_mcontext.get().getResources().getColor(R.color.green));
                    tv_req_state.setTextColor(weak_mcontext.get().getResources().getColor(R.color.green));
                    break;
                default:
                    tv_req_compDate.setText("");


            }

            /*if (singlereq.getStatus().equals(common.STATE_CANCELLED)){

            }else if (singlereq.getStatus().equals(common.STATE_COMPLETED)){
                tv_req_cancel.setVisibility(View.INVISIBLE);
                tv_req_compDate.setText(singlereq.getCompleteDate());
                tv_req_compDate.setVisibility(View.VISIBLE);
            }*/
        }

        @Override
        public void onClick(View v) {

            //Get request from firebase using time stamp, change status to cancelled and save again sing same time stamp
            DatabaseReference req_ref = FirebaseDatabase.getInstance().getReference("Orders");

            if (FirebaseAuth.getInstance().getCurrentUser()!= null){

                req_ref.child(common.time_to_date(request.getReqTime_stamp())).child(request.getReqTime_stamp())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               Request_Class update_req = dataSnapshot.getValue(Request_Class.class);

                               if (update_req != null){
                                   update_req.setStatus(common.STATE_CANCELLED);
                                   update_req.setReqTime_stamp(dataSnapshot.getKey());
                                   req_ref.child(common.time_to_date(update_req.getReqTime_stamp())).child(update_req.getReqTime_stamp()).setValue(update_req);
                                   klin_db.updateRequest(update_req);

                                   //Req_recy_Adapter.this.notifyDataSetChanged();
                                   ((Dashboard) weak_mcontext.get()).load_localRequests();

                               }

                               req_ref.removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
        }

    }

}
