package com.example.alip.evcma;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alip on 4/24/2017.
 */

public class Poll implements Serializable {

    @SerializedName("poll_id")
    public String poll_id;

    @SerializedName("university_id")
    public String university_id;

    @SerializedName("shortname")
    public String shortname;

    @SerializedName("title")
    public String title;

    @SerializedName("start_date")
    public String start_date;

    @SerializedName("end_date")
    public String end_date;

    @SerializedName("start_time")
    public  String start_time;

    @SerializedName("end_time")
    public String end_time;

    @SerializedName("picture")
    public String picture;

    @SerializedName("status")
    public String status;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("user")
    public String user;

    @SerializedName("created_date")
    public String created_date;
}
