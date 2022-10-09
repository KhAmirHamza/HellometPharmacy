package com.hellomet.pharmacy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.R;

import java.util.List;



public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.MyViewHolder> {

    Context context;
    List<Order.Item> items;

    public OrderItemAdapter(Context context, List<Order.Item> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_order_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txtv_medicine_name.setText(items.get(position).getName()+" || "+items.get(position).getBrand());
        holder.txtv_medicine_features.setText(items.get(position).getFeatures());
        holder.txtv_medicine_sub_total.setText(items.get(position).getPrice()+" Tk || "+
                items.get(position).getQuantity()+" Pcs || SubTotal: "+
                items.get(position).getSub_total()+" Tk");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_medicine_name,txtv_medicine_features,txtv_medicine_sub_total;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_medicine_name = itemView.findViewById(R.id.txtv_medicine_name);
            txtv_medicine_features = itemView.findViewById(R.id.txtv_medicine_features);
            txtv_medicine_sub_total = itemView.findViewById(R.id.txtv_medicine_sub_total);
        }
    }
}
