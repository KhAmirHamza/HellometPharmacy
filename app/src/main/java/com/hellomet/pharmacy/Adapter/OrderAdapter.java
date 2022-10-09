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

import java.util.Date;
import java.util.List;



public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>{
    Context context;
    List<Order> orders;

    public OrderAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.MyViewHolder holder, int position) {
        holder.txtv_order_id.setText(orders.get(position).getId());
        holder.txtv_order_status.setText(orders.get(position).getMeta_data().getStatus());
        holder.txtv_order_price.setText(orders.get(position).getMeta_data().getTotal_price());
        Date date = new Date(Long.parseLong(orders.get(position).getMeta_data().getCreated_at()));
        holder.txtv_order_date.setText(date.toString());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_order_id,txtv_order_status,txtv_order_price,txtv_order_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_order_id = itemView.findViewById(R.id.txtv_order_id);
            txtv_order_status = itemView.findViewById(R.id.txtv_order_status);
            txtv_order_price = itemView.findViewById(R.id.txtv_order_price);
            txtv_order_date = itemView.findViewById(R.id.txtv_order_date);
        }
    }
}
