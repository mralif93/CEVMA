package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amigold.fundapter.BindDictionary;
import com.amigold.fundapter.FunDapter;
import com.amigold.fundapter.extractors.StringExtractor;
import com.amigold.fundapter.interfaces.DynamicImageLoader;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.alip.evcma.app.AppConfig;
import com.example.alip.evcma.app.AppController;
import com.example.alip.evcma.helper.SQLiteHandler;
import com.example.alip.evcma.helper.SessionManager;
import com.kosalgeek.android.json.JsonConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Alip on 3/1/2017.
 */

public class ResultActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ResultActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_poll);
        listView = (ListView) findViewById(R.id.listResultPoll);
        listView.setDivider(null);

        if(getIntent() != null) {

        }

        getPoll();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getPoll();
    }

    private void getPoll() {
        pDialog.setMessage("Loading...");
        showDialog();
        swipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_POLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Poll Response: " + response);
                hideDialog();

                final ArrayList<Poll> pollList = new JsonConverter<Poll>().toArrayList(response, Poll.class);
                BindDictionary<Poll> dictionary = new BindDictionary<>();

                dictionary.addDynamicImageField(R.id.imageViewPicturePoll, new StringExtractor<Poll>() {
                            @Override
                            public String getStringValue(Poll poll, int position) {
                                String path = AppConfig.URL_POLL_IMAGE + poll.picture;
                                Log.d(TAG, path);
                                return path;
                            }
                        }, new DynamicImageLoader() {
                            @Override
                            public void loadImage(String url, ImageView imageView) {
                                Picasso.with(getApplicationContext())
                                        .load(url)
                                        .placeholder(R.drawable.no_image)
                                        .error(R.drawable.no_image)
                                        .into(imageView);
                            }
                        }
                );

                dictionary.addStringField(R.id.textViewTitlePoll, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.title;
                    }
                });

                dictionary.addStringField(R.id.textViewStartDate, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.start_date;
                    }
                });

                /*dictionary.addStringField(R.id.textViewEndDate, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.end_date;
                    }
                });*/

                dictionary.addStringField(R.id.textViewStartTime, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.start_time + " - " + poll.end_time;
                    }
                });

                /*dictionary.addStringField(R.id.textViewEndTime, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.end_time;
                    }
                });*/

                /*dictionary.addStringField(R.id.textViewUserPoll, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.user;
                    }
                });*/

                /*dictionary.addStringField(R.id.textViewDatePoll, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.created_date;
                    }
                });*/

                dictionary.addStringField(R.id.textViewStatusPoll, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.status;
                    }
                });

                FunDapter<Poll> adapter = new FunDapter<>(
                        getApplicationContext(),
                        pollList,
                        R.layout.layout_poll,
                        dictionary);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(getApplicationContext(), ResultPageActivity.class);
                        Poll selectedPoll = pollList.get(position);
                        myIntent.putExtra("poll", selectedPoll);
                        startActivity(myIntent);
                    }
                });

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error while reading data", Toast.LENGTH_LONG).show();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
                hideDialog();
            }
        });

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
