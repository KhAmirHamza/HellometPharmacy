package com.hellomet.pharmacy.View.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hellomet.pharmacy.Adapter.OrderListAdapter;
import com.hellomet.pharmacy.ApiClient;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hellomet.pharmacy.Constants.MAIN_URL;

public class CancelledOrderFragment extends Fragment {
    private static final String TAG = "CancelledOrderFragment";

    RecyclerView recy_cancelled_order;
    String PHARMACY_ID, PHARMACY_Name, PHARMACY_PHONE_NUMBER, PHARMACY_PASSWORD;
    ProgressBar progressbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cancelled_order, container, false);
        Log.d(TAG, "onCreateView: Cancelled");
        progressbar = view.findViewById(R.id.progressbar);
        recy_cancelled_order = view.findViewById(R.id.recy_cancelled_order);
        recy_cancelled_order.setHasFixedSize(true);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        PHARMACY_ID = sharedPreferences.getString("PHARMACY_ID", "null");
        PHARMACY_Name = sharedPreferences.getString("PHARMACY_Name", "null");
        PHARMACY_PHONE_NUMBER = sharedPreferences.getString("PHARMACY_PHONE_NUMBER", "null");
        PHARMACY_PASSWORD = sharedPreferences.getString("PHARMACY_PASSWORD", "null");


        SharedPreferences sp_location = getContext().getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        String latitude = sp_location.getString("Lat","null");
        String longitude = sp_location.getString("Lng","null");

        getCancelledOrderThenSet(PHARMACY_ID);

        return view;
    }

    public void getCancelledOrderThenSet(String id) {
        recy_cancelled_order.removeAllViews();
        progressbar.setVisibility(View.VISIBLE);
        ApiClient.getInstance(MAIN_URL).getOrdersByIdAndStatus(id, "Cancelled").enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                progressbar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().size()>0) {
                        Log.d(TAG, "onResponse: Size: "+response.body().size());
                        Log.d(TAG, "onResponse: Id: "+response.body().get(0).getId());
                        Log.d(TAG, "onResponse: Status: "+response.body().get(0).getMeta_data().getStatus());
                        Log.d(TAG, "onResponse: D.Name: "+response.body().get(0).getMeta_data().getRider_name());

                        OrderListAdapter cancelledOrderAdapter = new OrderListAdapter(getContext(), response.body());
                        recy_cancelled_order.setAdapter(cancelledOrderAdapter);

                        cancelledOrderAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position, int[] itemHeight) {

                            }
                        });
                    } else {
                        Log.d(TAG, "onResponse: Nothing Found!");
                        //Toast.makeText(getContext(), "Nothing Found!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //Toast.makeText(MainActivity.this, "ResponseError: " + response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: Error: " + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                progressbar.setVisibility(View.GONE);
                //Toast.makeText(MainActivity.this, "Error: " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: Error: " + t.getLocalizedMessage());
            }
        });
    }
}