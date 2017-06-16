package com.example.alip.evcma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Alip on 3/1/2017.
 */

public class HelpdeskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpdesk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //TextView textView = (TextView) findViewById(R.id.text_view);

        setSupportActionBar(toolbar);
        if(getIntent() != null) {
            //textView.setText(getIntent().getStringExtra("string"));
        }

        Button button = (Button) findViewById(R.id.btnSendTicket);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HelpdeskActivity.this, "Successfully send ticket!", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
