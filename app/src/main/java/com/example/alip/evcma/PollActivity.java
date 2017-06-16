package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
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
 * Created by Alip on 30/12/2016.
 */

public class PollActivity extends AppCompatActivity {

    private static final String TAG = PollActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private WebView myWebView;
    private Button btnStartVote;
    private String poll_id, voter_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // WebView
        myWebView = (WebView) findViewById(R.id.webViewPoll);
        // Button
        btnStartVote = (Button) findViewById(R.id.btnStartVote);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if(getIntent().getSerializableExtra("poll") != null) {
            Poll poll = (Poll) getIntent().getSerializableExtra("poll");
            poll_id = poll.poll_id;
            Log.d(TAG, "Poll Details Response : " + poll_id);
        }

        openPollPage(poll_id);
        voter_id = db.searchUser();
    }

    private void openPollPage(final String poll_id) {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(AppConfig.URL_POLL_PAGE + "?id=" + poll_id);
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

    public void onClick(View v) {
        if(v == btnStartVote) {

            pDialog.setMessage("Loading ...");
            showDialog();

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
            alertDialogBuilder.setTitle("Term and Conditions");
            alertDialogBuilder.setView(LayoutInflater.from(this).inflate(R.layout.layout_term_conditions,null));
            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getApplicationContext(), "You clicked yes button!", Toast.LENGTH_LONG).show();

                    StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_RESULT, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "Searching Poll Response : " + response);
                            hideDialog();
                            //Toast.makeText(getApplicationContext(), "Successfully!", Toast.LENGTH_SHORT).show();

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");

                                if(error) {
                                    String errorMsg = jObj.getString("error_msg");
                                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                } else {
                                    String errorMsg = jObj.getString("error_msg");
                                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                                    finish();

                                    Intent myIntent = new Intent(getApplicationContext(), ListCandidateActivity.class);
                                    myIntent.putExtra("poll_id", poll_id);
                                    startActivity(myIntent);
                                }

                            } catch (JSONException e) {
                                String errorMsg = e.getMessage();
                                Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Searching Poll Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("voter_id", voter_id);
                            params.put("poll_id", poll_id);
                            return params;
                        }
                    };

                    AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

                }
            });

            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hideDialog();
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
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
