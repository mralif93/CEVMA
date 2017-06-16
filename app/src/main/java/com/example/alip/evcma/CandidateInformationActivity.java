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
import com.android.volley.Request;
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

public class CandidateInformationActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = CandidateDetailsActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private SwipeRefreshLayout swipeRefreshLayoutCandidateInfo;
    private ImageView imageViewUpdateCandidateInfo, imageViewProfilePicture;
    private TextView textViewName, textViewPosition, textViewProgramme1,
            textViewDateOfBirth, textViewEmail, textViewSemester, textViewProgramme2, textViewUniversity, textViewLocation,
            textViewQuotes, textViewVision, textViewMission, textViewManifesto;

    private String user_id, poll_id, picture, uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_information);

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

        //SwipeRefreshLayout
        swipeRefreshLayoutCandidateInfo = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutCandidateInfo);
        //ImageView
        imageViewUpdateCandidateInfo = (ImageView) findViewById(R.id.imageViewUpdateCandidateInfo);
        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        //TextView
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewPosition = (TextView) findViewById(R.id.textViewPosition);
        textViewProgramme1 = (TextView) findViewById(R.id.textViewProgramme1);
        textViewDateOfBirth = (TextView) findViewById(R.id.textViewDateOfBirth);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewSemester = (TextView) findViewById(R.id.textViewSemester);
        textViewProgramme2 = (TextView) findViewById(R.id.textViewProgramme2);
        textViewUniversity = (TextView) findViewById(R.id.textViewUniversity);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewQuotes = (TextView) findViewById(R.id.textViewQuotes);
        textViewVision = (TextView) findViewById(R.id.textViewVision);
        textViewMission = (TextView) findViewById(R.id.textViewMission);
        textViewManifesto = (TextView) findViewById(R.id.textViewManifesto);

        if(getIntent() != null) {
            Poll poll = (Poll) getIntent().getSerializableExtra("poll");
            poll_id = poll.poll_id;
            Log.d(TAG, "Poll Details Response : " + poll_id);
        }

        //SetOnClickListener
        imageViewUpdateCandidateInfo.setOnClickListener(this);
        swipeRefreshLayoutCandidateInfo.setOnRefreshListener(this);

        user_id = db.searchUser();
        SearchCandidateInformation();
    }

    @Override
    public void onClick(View v) {
        if(v == imageViewUpdateCandidateInfo) {
            Intent myIntent = new Intent(this, CandidateUpdateInformationActivity.class);
            myIntent.putExtra("poll_id", poll_id);
            startActivity(myIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SearchCandidateInformation();
    }

    @Override
    public void onRefresh() {
        SearchCandidateInformation();
    }

    private void SearchCandidateInformation() {
        pDialog.setMessage("Loading..");
        showDialog();
        swipeRefreshLayoutCandidateInfo.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_CANDIDATE_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Searching Candidate Info Response : " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (error) {
                        // Now store the user in SQLite
                        uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String user_no = user.getString("user_no");
                        String poll_id = user.getString("poll_id");
                        picture = user.getString("picture");
                        String name = user.getString("name");
                        String position = user.getString("category");
                        String programme = user.getString("programme");
                        String dob = user.getString("dob");
                        String email = user.getString("email");
                        String semester = user.getString("semester");
                        String university = user.getString("university");
                        String location = user.getString("location");
                        String quotes = user.getString("quotes");
                        String vision = user.getString("vision");
                        String mission = user.getString("mission");
                        String manifesto = user.getString("manifesto");

                        new DownloadImage(picture.toString()).execute();
                        textViewName.setText(name);
                        textViewPosition.setText(position);
                        textViewProgramme1.setText(programme);
                        textViewDateOfBirth.setText(dob);
                        textViewEmail.setText(email);
                        textViewSemester.setText(semester);
                        textViewUniversity.setText(university);
                        textViewLocation.setText(location);
                        textViewQuotes.setText(quotes);
                        textViewVision.setText(vision);
                        textViewMission.setText(mission);
                        textViewManifesto.setText(manifesto);

                    } else {
                        // Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                swipeRefreshLayoutCandidateInfo.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Searching Candidate Info Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

                swipeRefreshLayoutCandidateInfo.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("poll_id", poll_id);
                return params;
            }
        };

        AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
