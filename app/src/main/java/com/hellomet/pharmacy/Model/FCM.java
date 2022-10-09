package com.hellomet.pharmacy.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FCM {
    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("token")
    @Expose
    String token;

    public FCM(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
