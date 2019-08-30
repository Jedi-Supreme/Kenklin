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

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class summary_recy_Adapter extends RecyclerView.Adapter {

    private static ArrayList<Basket_Items> items_list;

    private static double total_amt = 0;

    public summary_recy_Adapter(ArrayList<Basket_Items> items_list) {
        summary_recy_Adapter.items_list = items_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_summary_items, parent, false);
        return new item_list_holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((item_list_holder) holder).bind_views(items_list.get(position));
    }

    @Override
    public int getItemCount() {
        return items_list.size();
    }

    public class item_list_holder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_multp, tv_amt_sum;
        KlinDB klin_db;

        int x = 0;

        item_list_holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_item_name);
            tv_multp = itemView.findViewById(R.id.tv_item_multp);
            tv_amt_sum = itemView.findViewById(R.id.tv_item_amt_sum);

            //Todo calculations display

            WeakReference<Context> weak_mcontext = new WeakReference<>(itemView.getContext());
            klin_db = new KlinDB(weak_mcontext.get(),null);

        }

        void bind_views(final Basket_Items singleitem) {

            String itemdetails = singleitem.getItem_name();
            String qty = "x" + singleitem.getQuantity();
            if (singleitem.getPrice() > 0){
                itemdetails = singleitem.getItem_name() + " - " + singleitem.getPrice() + " Gh¢";

                if (singleitem.getQuantity() > 0){
                    String amt_sum = String.valueOf(singleitem.getPrice() * singleitem.getQuantity());
                    tv_amt_sum.setText(amt_sum);
                }
            }

            tv_name.setText(itemdetails);
            tv_multp.setText(qty);


        }

    }

    public static void setTotal_amt(double total_amt) {
        summary_recy_Adapter.total_amt = total_amt;
    }

    public static double getTotal_amt() {

        for (Basket_Items item : items_list){

            double amount_to_pay = item.getPrice() * item.getQuantity();

            total_amt+=amount_to_pay;
        }
        return total_amt;
    }

}
