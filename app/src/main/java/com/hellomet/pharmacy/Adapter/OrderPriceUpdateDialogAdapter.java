package com.hellomet.pharmacy.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.R;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class OrderPriceUpdateDialogAdapter extends Dialog {
    private static final String TAG = "OrderPriceUpdateDialogA";

    EditText edt_price;
    MaterialButton btn_cancel,btn_update_price;
    Order order;
    RecyclerView.Adapter adapter;
    Activity activity;
    public OrderPriceUpdateDialogAdapter(@NonNull Activity activity, Order order, RecyclerView.Adapter adapter ) {
        super(activity);
        this.activity = activity;
        this.order = order;
        this.adapter = adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_order_price_update);

        edt_price = findViewById(R.id.edt_price);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_update_price = findViewById(R.id.btn_update_price);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn_update_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = edt_price.getText().toString();
                if (!price.isEmpty()) {
                    order.getMeta_data().setTotal_price(price);
                    //Updating Order with some new value.....
                    ApiClient.getInstance(MAIN_URL).updateOrder(order.getId(), order).enqueue(new Callback<Order>() {
                        @Override
                        public void onResponse(Call<Order> call, Response<Order> response) {
                            dismiss();
                            if (response.isSuccessful()){
                                if (response.body()==null){
                                    Log.d(TAG, "onResponse: Can Not Get Data.");
                                    Toast.makeText(getContext(), "Nothing Found.", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getContext(), "Order Update Successfully", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();

                                }
                            }else {
                                Log.d(TAG, "onResponse: ResponseError: "+ response.errorBody().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<Order> call, Throwable t) {
                            Log.d(TAG, "onFailure: Error: "+t.getMessage());
                            dismiss();
                        }
                    });
                }
            }
        });
    }
}
