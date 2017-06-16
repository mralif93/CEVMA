package com.example.alip.evcma;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alip on 4/17/2017.
 */

public class Announcement implements Serializable {

    @SerializedName("id")
    public String id;

    @SerializedName("title")
    public String title;

    @SerializedName("picture")
    public String picture;

    @SerializedName("status")
    public String status;

    @SerializedName("created_date")
    public String created_date;

    @SerializedName("username")
    public String username;
}
