package com.hellomet.pharmacy.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Pharmacy {

    public static class MetaData{

        @SerializedName("name")
        @Expose
        String name;

        @SerializedName("founder")
        @Expose
        String founder;
        
        @SerializedName("image_url")
        @Expose
        String image_url;

        @SerializedName("address")
        @Expose
        String address;
        
        @SerializedName("phone_number")
        @Expose
        String phone_number;

        @SerializedName("latitude")
        @Expose
        String latitude;

        @SerializedName("longitude")
        @Expose
        String longitude;

       @SerializedName("status")
        @Expose
        String status;

        public MetaData(String name, String founder, String image_url, String address, String phone_number, String latitude, String longitude, String status) {
            this.name = name;
            this.founder = founder;
            this.image_url = image_url;
            this.address = address;
            this.phone_number = phone_number;
            this.latitude = latitude;
            this.longitude = longitude;
            this.status = status;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFounder() {
            return founder;
        }

        public void setFounder(String founder) {
            this.founder = founder;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPhone_number() {
            return phone_number;
        }

        public void setPhone_number(String phone_number) {
            this.phone_number = phone_number;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    @SerializedName("_id")
    @Expose
    String id;

    @SerializedName("meta_data")
    @Expose
    MetaData meta_data;

    @SerializedName("auth")
    @Expose
    Auth auth;


    public Pharmacy(MetaData meta_data) {
        this.meta_data = meta_data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MetaData getMeta_data() {
        return meta_data;
    }

    public void setMeta_data(MetaData meta_data) {
        this.meta_data = meta_data;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }
}
