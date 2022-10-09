package com.hellomet.pharmacy.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.R;

import java.util.Date;
import java.util.List;


public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.MyViewHolder>{
    private static final String TAG = "OrderDetailsAdapter";
    Context context;
    List<Order> orders;
    int[] detailsVisibility;
    OnItemClickListener onItemClickListener;
    int[] itemHeight;


    public OrderDetailsAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        detailsVisibility = new int[orders.size()];
        itemHeight = new int[orders.size()];

        for (int i = 0; i < orders.size(); i++) {
            detailsVisibility[i] = 0;
            itemHeight[i] = 0;
        }
    }

    @NonNull
    @Override
    public OrderDetailsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_order_details, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsAdapter.MyViewHolder holder, int position) {
        holder.txtv_order_id.setText(orders.get(position).getId());
        String statusCode = orders.get(position).getMeta_data().getStatus();
        holder.txtv_order_status.setText("Pending");
        holder.txtv_order_price.setText("৳ "+orders.get(position).getMeta_data().getTotal_price());
        Date date = new Date(Long.parseLong(orders.get(position).getMeta_data().getCreated_at()));
        //Date date = new Date(Long.parseLong("1612193842354"));
        holder.txtv_order_date.setText(date.toString());
        holder.txtv_user_name.setText("Pharmacy: "+orders.get(position).getMeta_data().getUser_name());
        holder.txtv_payment_method.setText("Payment Method: "+orders.get(position).getMeta_data().getPayment_method());
        holder.txtv_requirement.setText("Requirement: "+orders.get(position).getMeta_data().getRequirement());
        //holder.relative_layout_details.setVisibility(View.GONE);
        itemHeight[position] = holder.itemView.getHeight();
        itemHeight[position] = holder.itemView.getHeight()+ holder.recy_order_details.getHeight();
        onItemClickListener.onItemClick(holder.itemView, position,itemHeight);

        OrderItemAdapter orderItemAdapter = new OrderItemAdapter(context, orders.get(position).getItems());
        Log.d(TAG, "MyViewHolder: orderItemSize: "+orders.get(position).getItems().size());
        holder.recy_order_details.setAdapter(orderItemAdapter);



        if(detailsVisibility[position] == 0) {
            holder.relative_layout_details.setVisibility(View.GONE);
        } else {
            holder.relative_layout_details.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_order_id,txtv_order_status,txtv_order_price,txtv_order_date, txtv_user_name,txtv_payment_method,txtv_requirement;
        RecyclerView recy_order_details;
        RelativeLayout relative_layout_details;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtv_order_id = itemView.findViewById(R.id.txtv_order_id);
            txtv_order_status = itemView.findViewById(R.id.txtv_order_status);
            txtv_order_price = itemView.findViewById(R.id.txtv_order_price);
            txtv_order_date = itemView.findViewById(R.id.txtv_order_date);
            txtv_user_name = itemView.findViewById(R.id.txtv_user_name);
            txtv_payment_method = itemView.findViewById(R.id.txtv_payment_method);
            txtv_requirement = itemView.findViewById(R.id.txtv_requirement);
            relative_layout_details = itemView.findViewById(R.id.relative_layout_details);
            recy_order_details = itemView.findViewById(R.id.recy_order_details);
            recy_order_details.setHasFixedSize(true);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (detailsVisibility[getAdapterPosition()] == 0)
                        detailsVisibility[getAdapterPosition()] = 1;
                    else detailsVisibility[getAdapterPosition()] = 0;

                    notifyItemChanged(getAdapterPosition());

                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, int[] itemHeight);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
