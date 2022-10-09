package com.hellomet.pharmacy.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.R;
import com.hellomet.pharmacy.View.ChatActivity;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.CHAT_USER_PHARMACY;
import static com.hellomet.pharmacy.Constants.MAIN_URL;
import static com.hellomet.pharmacy.Constants.ORDER_ACTIVE;
import static com.hellomet.pharmacy.Constants.ORDER_CANCELLED;
import static com.hellomet.pharmacy.Constants.ORDER_COMPLETED;
import static com.hellomet.pharmacy.Constants.ORDER_ON_THE_WAY;
import static com.hellomet.pharmacy.Constants.ORDER_PENDING;


public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "OrderListAdapter";
    Context context;
    List<Order> orders;
    int[] detailsVisibility;
    OnItemClickListener onItemClickListener;
    int[] itemHeight;
    List<Order> copyOrders;
    Activity activity;

    public OrderListAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
        detailsVisibility = new int[orders.size()];
        itemHeight = new int[orders.size()];
        copyOrders = new ArrayList<>(orders);
        for (int i = 0; i < orders.size(); i++) {
            detailsVisibility[i] = 0;
            itemHeight[i] = 0;
        }
    }
    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.iv_order_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapter.MyViewHolder holder, int position) {
        holder.txtv_order_id.setText(orders.get(position).getId());
        String statusCode = orders.get(position).getMeta_data().getStatus();
        if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_PENDING)){
            holder.txtv_order_status.setText(ORDER_PENDING);
            holder.txtv_order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_txtv_pending));
        } else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ACTIVE)) {
            holder.txtv_order_status.setText(ORDER_ACTIVE);
            holder.txtv_order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_txtv_active));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ON_THE_WAY)) {
            holder.txtv_order_status.setText(ORDER_ON_THE_WAY);
            holder.txtv_order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_txtv_ontheway));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_COMPLETED)) {
            holder.txtv_order_status.setText(ORDER_COMPLETED);
            holder.txtv_order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_txtv_completed));
        }else if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase(ORDER_CANCELLED)) {
            holder.txtv_order_status.setText(ORDER_CANCELLED);
            holder.txtv_order_status.setBackground(context.getResources().getDrawable(R.drawable.bg_txtv_cancelled));
        }
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

        List<Order.PrescriptionImageUrl> prescriptionImageUrls = orders.get(position).getPrescriptionImageUrls();

        if (prescriptionImageUrls!=null && prescriptionImageUrls.size()>0 && prescriptionImageUrls.get(0)!=null){
            holder.imgv_prescription.setVisibility(View.VISIBLE);
            holder.recy_order_details.setVisibility(View.GONE);
            //Toast.makeText(context, prescriptionImageUrls.get(0), Toast.LENGTH_SHORT).show();
            Picasso.with(context).load(prescriptionImageUrls.get(0).getPrescriptionImageUrl()).placeholder(R.drawable.progress_animation).into(holder.imgv_prescription);

        }else if (orders.get(position).getItems()!=null) {
            holder.recy_order_details.setVisibility(View.VISIBLE);
            holder.imgv_prescription.setVisibility(View.GONE);

            OrderItemAdapter orderItemAdapter = new OrderItemAdapter(context, orders.get(position).getItems());
            Log.d(TAG, "MyViewHolder: orderItemSize: " + orders.get(position).getItems().size());
            holder.recy_order_details.setAdapter(orderItemAdapter);
        }

        //if order status is pending, we will display acceptation...
        if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase("Pending")
                || orders.get(position).getMeta_data().getStatus().equalsIgnoreCase("Active")) {
            holder.lay_order_acceptation.setVisibility(View.VISIBLE);

            if (orders.get(position).getMeta_data().getStatus().equalsIgnoreCase("Active")) {
                holder.btn_accept_order.setVisibility(View.GONE);
            }
        }

        if(detailsVisibility[position] == 0) {
            holder.relative_layout_details.setVisibility(View.GONE);
        } else {
            holder.relative_layout_details.setVisibility(View.VISIBLE);
        }

        if (!orders.get(position).getMeta_data().getStatus().equalsIgnoreCase("Completed")){
            holder.btn_chat_with_client.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<Order> filterOrderList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filterOrderList.addAll(copyOrders);
                }else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Order order : copyOrders) {
                        if (order.getId().toLowerCase().trim().contains(filterPattern)){
                            filterOrderList.add(order);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filterOrderList;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                orders.clear();
                orders.addAll((Collection<? extends Order>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtv_order_id,txtv_order_status,txtv_order_price,txtv_order_date, txtv_user_name,txtv_payment_method,txtv_requirement;
        RecyclerView recy_order_details;
        RelativeLayout relative_layout_details;
        MaterialButton btn_chat_with_client;
        ImageView imgv_prescription;

        LinearLayout lay_order_acceptation;
        MaterialButton btn_accept_order, btn_cancel_order;

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
            btn_chat_with_client = itemView.findViewById(R.id.btn_chat_with_client);
            imgv_prescription = itemView.findViewById(R.id.imgv_prescription);

            lay_order_acceptation = itemView.findViewById(R.id.lay_order_acceptation);
            btn_accept_order = itemView.findViewById(R.id.btn_accept_order);
            btn_cancel_order = itemView.findViewById(R.id.btn_cancel_order);

            recy_order_details = itemView.findViewById(R.id.recy_order_details);
            recy_order_details.setHasFixedSize(true);

            // When Order status is Pending and Clicked Accept Order...
            btn_accept_order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orders.get(getAdapterPosition()).getMeta_data().getStatus().equalsIgnoreCase(ORDER_PENDING)) {
                        orderAcceptation(orders.get(getAdapterPosition()), ORDER_ACTIVE, getAdapterPosition());

                    }
                }
            });

            // When Order status is Pending and Clicked Cancel Order...
            btn_cancel_order
                    .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (orders.get(getAdapterPosition()).getMeta_data().getStatus().equalsIgnoreCase(ORDER_PENDING)
                    || orders.get(getAdapterPosition()).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ACTIVE)) {
                        cancelOrder(orders.get(getAdapterPosition()), ORDER_CANCELLED , getAdapterPosition());
                    }
                }
            });

            btn_chat_with_client.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class)
                            .putExtra("owner_id", orders.get(getAdapterPosition()).getMeta_data().getPharmacy_id())
                            .putExtra("order_id", orders.get(getAdapterPosition()).getId())
                            .putExtra("chat_with", CHAT_USER_PHARMACY);

                    Log.d(TAG, "onCreate: user_id: "+orders.get(getAdapterPosition()).getMeta_data().getUser_id());
                    Log.d(TAG, "onCreate: order_id: "+orders.get(getAdapterPosition()).getId());
                    Log.d(TAG, "onCreate: chat_with: "+CHAT_USER_PHARMACY);

                    context.startActivity(intent);
                }
            });

            txtv_order_price.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity == null) {
                        return;
                    }
                    if (orders.get(getAdapterPosition()).getPrescriptionImageUrls()!=null
                            && orders.get(getAdapterPosition()).getPrescriptionImageUrls().size()>0
                            && (orders.get(getAdapterPosition()).getMeta_data().getStatus().equalsIgnoreCase(ORDER_PENDING)||
                            orders.get(getAdapterPosition()).getMeta_data().getStatus().equalsIgnoreCase(ORDER_ACTIVE))
                    ){
                        //todo.... update Order Price...

                        OrderPriceUpdateDialogAdapter priceUpdateDialogAdapter = new OrderPriceUpdateDialogAdapter(activity,
                                orders.get(getAdapterPosition()),OrderListAdapter.this);
                        priceUpdateDialogAdapter.show();
                    }
                }
            });


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

    private void cancelOrder(Order order, String status, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Make Sure...");
        builder.setMessage("Do your want to Cancelled Order?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                orderAcceptation(order, status, position);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    public void removeAt(int position) {
        orders.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orders.size());
    }

    private void orderAcceptation(Order order, String status, int position) {
        order.getMeta_data().setStatus(status);
        //Updating Order with some new value.....
        ApiClient.getInstance(MAIN_URL).updateOrder(order.getId(), order).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

                if (response.isSuccessful()){
                    if (response.body()==null){
                        Log.d(TAG, "onResponse: Can Not Get Data.");
                        Toast.makeText(context, "Nothing Found!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Order Update Successfully", Toast.LENGTH_SHORT).show();
                            removeAt(position);
                    }
                }else {
                    Log.d(TAG, "onResponse: ResponseError: "+ response.errorBody().toString());
                    Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.d(TAG, "onFailure: Error: "+t.getMessage());
                Toast.makeText(context, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, int[] itemHeight);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
