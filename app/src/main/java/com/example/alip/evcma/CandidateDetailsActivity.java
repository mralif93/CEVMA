package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alip on 30/12/2016.
 */

public class CandidateDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CandidateDetailsActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private ImageView imageViewProfileCandidate;
    private TextView textViewName, textViewPosition, textViewProgramme1, textViewDateOfBirth, textViewEmail, textViewSemester,
        textViewProgramme2, textViewUniversity, textViewLocation, textViewManifesto, textViewQuotes, textViewVision, textViewMission;
    private Button buttonVote;

    private String user_no, voter_id, poll_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_details);

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

        //Declare variable ImageView
        imageViewProfileCandidate = (ImageView) findViewById(R.id.imageViewProfilePicture);

        //Declare variable TextView
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewPosition = (TextView) findViewById(R.id.textViewPosition);
        textViewProgramme1 = (TextView) findViewById(R.id.textViewProgramme1);
        textViewDateOfBirth = (TextView) findViewById(R.id.textViewDateOfBirth);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewSemester = (TextView) findViewById(R.id.textViewSemester);
        textViewProgramme2  = (TextView) findViewById(R.id.textViewProgramme2);
        textViewUniversity = (TextView) findViewById(R.id.textViewUniversity);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
        textViewQuotes = (TextView) findViewById(R.id.textViewQuotes);
        textViewVision = (TextView) findViewById(R.id.textViewVision);
        textViewMission = (TextView) findViewById(R.id.textViewMission);
        textViewManifesto = (TextView) findViewById(R.id.textViewManifesto);

        //Declare variable Button
        buttonVote = (Button) findViewById(R.id.buttonVote);

        if(getIntent().getSerializableExtra("candidate") != null && getIntent().getStringExtra("poll_id") != null) {
            Candidate candidate = (Candidate) getIntent().getSerializableExtra("candidate");
            user_no = candidate.user_no;
            Log.d(TAG, "Candidate Details Response : " + user_no);
            poll_id = getIntent().getStringExtra("poll_id");
            Log.d(TAG, "Poll Details Response : " + poll_id);
        }

        getCandidateDetails(user_no);

        voter_id = db.searchUser();
        buttonVote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonVote) {
            pDialog.setMessage("Loading...");
            showDialog();

            StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_RESULT_VOTE, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Candidate Vote Response: " + response);
                    hideDialog();
                    Toast.makeText(getApplicationContext(), "Candidate Vote Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Candidate Vote Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("voter_id", voter_id);
                    params.put("poll_id", poll_id);
                    params.put("candidate_id", user_no);
                    return params;
                }
            };

            AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private void getCandidateDetails(final String candidate_id) {
        pDialog.setMessage("Loading...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_CANDIDATES_PAGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Candidate Details Response: " + response);
                hideDialog();

                try {
                    JSONArray details = new JSONArray(response);
                    JSONObject data = details.getJSONObject(0);

                    String user_id = data.getString("user_id");
                    String user_no = data.getString("user_no");
                    String semester = data.getString("semester");
                    String ic_no = data.getString("ic_no");
                    String dob = data.getString("dob");
                    String first_name = data.getString("first_name");
                    String last_name = data.getString("last_name");
                    String email = data.getString("email");
                    String category = data.getString("category");
                    String picture = data.getString("picture");
                    String phone_no = data.getString("phone_no");
                    String gender = data.getString("gender");
                    String address1 = data.getString("address1");
                    String address2 = data.getString("address2");
                    String postcode = data.getString("postcode");
                    String city = data.getString("city");
                    String state = data.getString("state");
                    String country = data.getString("country");
                    String university = data.getString("university");
                    String programmeS = data.getString("programmeS");
                    String programmeF = data.getString("programmeF");

                    String quotes = data.getString("quotes");
                    String vision = data.getString("vision");
                    String mission = data.getString("mission");
                    String manifesto = data.getString("manifesto");

                    new DownloadImage(picture.toString()).execute();
                    textViewName.setText(first_name + " " +last_name);
                    textViewPosition.setText(category);
                    textViewProgramme1.setText(programmeF);
                    textViewDateOfBirth.setText(dob);
                    textViewEmail.setText(email);
                    textViewSemester.setText(semester);
                    textViewProgramme2.setText(programmeF);
                    textViewUniversity.setText(university);
                    textViewLocation.setText(city + ", " + state + ", " + country);
                    textViewQuotes.setText(quotes);
                    textViewVision.setText(vision);
                    textViewMission.setText(mission);
                    textViewManifesto.setText(manifesto);

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Candidate Details Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", user_no);
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
            String url = AppConfig.URL_CANDIDATES_IMAGE + picture;

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
                imageViewProfileCandidate.setImageBitmap(bitmap);
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
