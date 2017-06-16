package com.example.alip.evcma;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Alip on 4/25/2017.
 */

public class Candidate implements Serializable {

    @SerializedName("candidate_id")
    public String candidate_id;

    @SerializedName("user_no")
    public String user_no;

    @SerializedName("first_name")
    public String first_name;

    @SerializedName("last_name")
    public String last_name;

    @SerializedName("gender")
    public String gender;

    @SerializedName("programme")
    public String programme;

    @SerializedName("programmeS")
    public String programmeS;

    @SerializedName("programmeF")
    public String programmeF;

    @SerializedName("university")
    public String university;

    @SerializedName("semester")
    public String semester;

    @SerializedName("status")
    public String status;

    @SerializedName("picture")
    public String picture;

    @SerializedName("created_date")
    public String created_date;
}
