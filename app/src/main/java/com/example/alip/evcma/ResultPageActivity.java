package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.example.alip.evcma.app.AppConfig;
import com.example.alip.evcma.helper.SQLiteHandler;
import com.example.alip.evcma.helper.SessionManager;

/**
 * Created by Alip on 5/13/2017.
 */

public class ResultPageActivity extends AppCompatActivity {

    private static final String TAG = ResultPageActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private WebView myWebView;
    private Button btnStartVote;
    private String poll_id, voter_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // WebView
        myWebView = (WebView) findViewById(R.id.webViewResultPoll);
        // Button
        btnStartVote = (Button) findViewById(R.id.btnStartVote);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if(getIntent().getSerializableExtra("poll") != null) {
            Poll poll = (Poll) getIntent().getSerializableExtra("poll");
            poll_id = poll.poll_id;
            Log.d(TAG, "Result Poll Page Details Response : " + poll_id);
        }

        openResultPollPage(poll_id);
        voter_id = db.searchUser();
    }

    private void openResultPollPage(final String poll_id) {
        pDialog.setMessage("Loading...");
        showDialog();
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        myWebView.loadUrl(AppConfig.URL_RESULT_POLL + "?id=" + poll_id);
        hideDialog();
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
