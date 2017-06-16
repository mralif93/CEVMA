package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alip on 5/23/2017.
 */

public class CandidateUpdateInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CandidateUpdateInformationActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private EditText editTextQuotes, editTextVision, editTextMission, editTextManifesto;
    private ImageView imageViewUpdateInfo;

    private String poll_id, user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_update_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //EditText
        editTextQuotes = (EditText) findViewById(R.id.editTextQuotes);
        editTextVision = (EditText) findViewById(R.id.editTextVission);
        editTextMission = (EditText) findViewById(R.id.editTextMission);
        editTextManifesto = (EditText) findViewById(R.id.editTextManifesto);

        imageViewUpdateInfo = (ImageView) findViewById(R.id.imageViewUpdateInfo);

        if(getIntent().getSerializableExtra("poll_id") != null) {
            poll_id = getIntent().getStringExtra("poll_id");
        }

        //SetOnClickListener
        imageViewUpdateInfo.setOnClickListener(this);

        //Get User ID
        user_id = db.searchUser();

        //Get Candidate Information
        SearchCandidateInformation();
    }

    @Override
    public void onClick(View v) {
        if(v == imageViewUpdateInfo) {
            UpdateCandidateInformation();
        }
    }

    private void SearchCandidateInformation() {
        pDialog.setMessage("Loading..");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_SEARCH_CANDIDATE_INFO, new Response.Listener<String>() {
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
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String user_no = user.getString("user_no");
                        String poll_id = user.getString("poll_id");
                        String picture = user.getString("picture");
                        String name = user.getString("name");
                        String category = user.getString("category");
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

                        editTextQuotes.setText(quotes);
                        editTextVision.setText(vision);
                        editTextMission.setText(mission);
                        editTextManifesto.setText(manifesto);

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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Searching Candidate Info Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
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

    private void UpdateCandidateInformation() {
        pDialog.setMessage("Loading..");
        showDialog();

        final String quotes = editTextQuotes.getText().toString().trim();
        final String vision = editTextVision.getText().toString().trim();
        final String mission = editTextMission.getText().toString().trim();
        final String manifesto = editTextManifesto.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_CANDIDATES_UPDATE_INFO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Info Response : " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(error) {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();

                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Info Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("poll_id", poll_id);
                params.put("quotes", quotes);
                params.put("vision", vision);
                params.put("mission", mission);
                params.put("manifesto", manifesto);
                return params;
            }
        };

        AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
