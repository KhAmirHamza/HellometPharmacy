package com.hellomet.pharmacy;

import androidx.lifecycle.LiveData;

import com.hellomet.pharmacy.Model.FCM;
import com.hellomet.pharmacy.Model.Order;
import com.hellomet.pharmacy.Model.Pharmacy;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequests {

    @Multipart
    @POST("uploadImageToGenarateUrl")//mvvm
    Call<JsonObject> uploadImageToGenerateUrl(
            @Part MultipartBody.Part multipartImageFile
    );

    @POST("pharmacy")//mvvm
    Call<Pharmacy> createPharmacy(@Body Pharmacy pharmacy);

    @GET("pharmacy")
    Call<Pharmacy> getPharmacyUsingId(@Query("id") String id);

    @GET("pharmacy")//mvvm
    Call<Pharmacy> getPharmacyUsingPhoneNumber(@Query("phone_number") String phone_number);

    @GET("pharmacy")
    Call<Pharmacy> getPharmacyUsingEmail(@Query("email") String email);

    @GET("auth")
    Call<Pharmacy> authenticatePharmacy(@Query("phone_number") String phone_number, @Query("password") String password);


    @GET("order")
    Call<Order> getOrder(@Query("id") String order_id);

    @GET("order")
    Call<List<Order>> getOrdersByPhoneNumber(@Query("phone_number") String phone_number);

    @GET("order")
    Call<List<Order>> getOrdersByPhoneNumberAndStatus(@Query("phone_number") String phone_number,
                                                      @Query("status") String status);
    @GET("order")
    Call<List<Order>> getOrdersByIdAndStatus(@Query("pharmacy_id") String pharmacy_id,
                                             @Query("status") String status);
    @GET("order")
    Call<List<Order>> getOrdersById(@Query("pharmacy_id") String pharmacy_id);

    @PATCH("order/{id}")
    Call<Order> updateOrder(@Path("id") String id, @Body Order order);

    @PATCH("pharmacy/{id}")
    Call<Pharmacy> updatePharmacyStatus(@Path("id") String id, @Body Pharmacy pharmacy);

    @POST("fcm")
    Call<FCM> createFcmToken(@Body FCM fcm);
    @GET("fcm")
    Call<FCM> getFcmToken(@Query("id") String id);

    @PATCH("fcm/{id}")
    Call<FCM> updateFcmToken(@Path("id") String id, @Body FCM fcm);


   /* @GET("getAllProducts/{cat_id}")
    Call<List<ProductShortDetails>> getallProducts(@Path("cat_id") String cat_id);*/
}