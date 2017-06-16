package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.alip.evcma.app.AppConfig;
import com.example.alip.evcma.app.AppController;
import com.example.alip.evcma.helper.SQLiteHandler;
import com.example.alip.evcma.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alip on 2/1/2017.
 */

public class ProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    private TextView textViewName, textViewIC, textViewDOB, textViewEmail, textViewPhoneNo, textViewGender,
            textViewUniversity, textViewProgramme, textViewSemester;

    //private Button btnEditProfile;
    private ImageView imageViewProfilePicture, imageViewUpdateProfile;
    //private SwipeRefreshLayout swipeRefreshLayout;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private String userID, picture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // TextView
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewIC = (TextView) findViewById(R.id.textViewIC);
        textViewDOB = (TextView) findViewById(R.id.textViewDOB);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewPhoneNo = (TextView) findViewById(R.id.textViewPhoneNo);
        textViewGender = (TextView) findViewById(R.id.textViewGender);
        textViewUniversity = (TextView) findViewById(R.id.textViewUniversity);
        textViewProgramme = (TextView) findViewById(R.id.textViewProgramme);
        textViewSemester = (TextView) findViewById(R.id.textViewSemester);

        // ImageView
        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture1);
        imageViewUpdateProfile = (ImageView) findViewById(R.id.imageViewUpdateProfile);

        imageViewUpdateProfile.setOnClickListener(this);

        //swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_profile);
        //swipeRefreshLayout.setOnRefreshListener(this);

        // Get User ID from Sqlite Database
        userID = db.searchUser();
        //searchUserDetails(userID);
    }

    @Override
    public void onClick(View v) {
        if(v == imageViewUpdateProfile) {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    protected void onResume() {
        searchUserDetails(userID);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        //searchUserDetails(userID);
    }

    private void searchUserDetails(final String userID) {

        // showing refresh animation before making http call
        //swipeRefreshLayout.setRefreshing(true);

        pDialog.setMessage("Searching ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_SEARCH_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Searching Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(error) {
                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String first_name = user.getString("first_name");
                        String last_name = user.getString("last_name");
                        String ic = user.getString("ic_no");
                        String dob = user.getString("dob");
                        String phone = user.getString("phone_no");
                        String email = user.getString("email");
                        picture = user.getString("picture");
                        String gender = user.getString("gender");
                        String university = user.getString("university");
                        String programme = user.getString("programme");
                        String semester = user.getString("semester");

                        textViewName.setText(first_name + " " + last_name);
                        textViewIC.setText(ic);
                        textViewDOB.setText(dob);
                        textViewPhoneNo.setText(phone);
                        textViewEmail.setText(email);
                        new DownloadImage(picture.toString()).execute();
                        textViewGender.setText(gender);

                        textViewUniversity.setText(university);
                        textViewProgramme.setText(programme);
                        textViewSemester.setText(semester);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    String errorMsg = e.getMessage();
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                }

                // stopping swipe refresh
                //swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Searching User Details Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
                // stopping swipe refresh
                //swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userID);
                return params;
            }
        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    private class DownloadImage extends AsyncTask<Void, Void, Bitmap> {
        String picture;

        public DownloadImage(String picture) {
            this.picture = picture;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            String url = AppConfig.URL_PROFILE_IMAGE + picture;

            try {
                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                Log.d(TAG, "Image Error: " + connection);

                return BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null) {
                imageViewProfilePicture.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showDialog() {
        if(!pDialog.isShowing()) {
            pDialog.show();
        }
    }

    private void hideDialog() {
        if(pDialog.isShowing()) {
            pDialog.hide();
        }
    }
}
