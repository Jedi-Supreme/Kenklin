package com.jedit.kenklin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jedit.kenklin.models.Basket_Items;
import com.jedit.kenklin.R;
import com.jedit.kenklin.databases.KlinDB;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Items_recy_Adapter extends RecyclerView.Adapter {

    private static ArrayList<Basket_Items> items_list;

    public Items_recy_Adapter(ArrayList<Basket_Items> items_list) {
        Items_recy_Adapter.items_list = items_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item_names, parent, false);
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

    public class item_list_holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_name, tv_add, tv_remove, tv_service, tv_qty;
        KlinDB klin_db;

        int x = 0;

        item_list_holder(View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_item_name);
            tv_add = itemView.findViewById(R.id.tv_item_add);
            tv_remove = itemView.findViewById(R.id.tv_item_minus);
            tv_qty = itemView.findViewById(R.id.tv_item_qty);
            tv_service = itemView.findViewById(R.id.tv_item_service);

            //tv_delete.setOnClickListener(this);
            tv_qty.setText(String.valueOf(x));
            tv_remove.setOnClickListener(this);
            tv_add.setOnClickListener(this);

            WeakReference<Context> weak_mcontext = new WeakReference<>(itemView.getContext());
            klin_db = new KlinDB(weak_mcontext.get(),null);


        }

        void bind_views(final Basket_Items singleitem) {

            String itemdetails = singleitem.getItem_name();

            if (singleitem.getPrice() > 0){
                itemdetails = singleitem.getItem_name() + " - " + singleitem.getPrice() + " Gh¢";
            }

            tv_name.setText(itemdetails);
            tv_service.setText(singleitem.getServ_name());

        }

        @Override
        public void onClick(View v) {
            Basket_Items item = items_list.get(getAdapterPosition());
            //klin_db.delete_ServiceItem(item.getServ_name(),item.getItem_name());
            //todo refresh item list in setup activity

            switch (v.getId()){

                case R.id.tv_item_add:
                    tv_qty.setText(String.valueOf(++x));
                    items_list.get(this.getAdapterPosition()).setQuantity(x);
                    item.setQuantity(x);
                    break;

                case R.id.tv_item_minus:

                    if (x>0){
                        tv_qty.setText(String.valueOf(--x));
                        items_list.get(this.getAdapterPosition()).setQuantity(x);
                        item.setQuantity(x);

                    }
                    break;
            }
        }
    }

    public static ArrayList<Basket_Items> getItems_list() {

        ArrayList<Basket_Items> added_items = new ArrayList<>();
        for (Basket_Items item : items_list){
            if (item.getQuantity() > 0){
                added_items.add(item);
            }
        }
        return added_items;
    }
}
