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
import android.widget.TextView;

import com.example.alip.evcma.app.AppConfig;

/**
 * Created by Alip on 29/12/2016.
 */

public class AnnouncementActivity extends AppCompatActivity {

    private static final String TAG = AnnouncementActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private WebView myWebView;

    private String announcement_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        //Declare webview component in layout by id
        myWebView = (WebView) findViewById(R.id.webView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textView = (TextView) findViewById(R.id.text_view);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        setSupportActionBar(toolbar);
        if(getIntent() != null) {
            //textView.setText(getIntent().getStringExtra("string"));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getSerializableExtra("announcement") != null) {
            Announcement announcement = (Announcement) getIntent().getSerializableExtra("announcement");
            announcement_id = announcement.id;
            Log.d(TAG, "Announcement Details Response : " + announcement_id);
        }

        openAnnouncementPage(announcement_id);
    }

    private void openAnnouncementPage(final String announcementID) {
        pDialog.setMessage("Loading...");
        showDialog();
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(AppConfig.URL_ANNOUNCEMENT_PAGE + "?id=" + announcementID);
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
