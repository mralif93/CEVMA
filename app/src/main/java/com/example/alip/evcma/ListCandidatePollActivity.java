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
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alip on 5/9/2017.
 */

public class ListCandidatePollActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ListCandidatePollActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidate_poll);

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

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_candidate);

        listView = (ListView) findViewById(R.id.listCandidatePoll);
        listView.setDivider(null);

        user_id = db.searchUser();

        getPoll(user_id);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getPoll(user_id);
    }

    private void getPoll(final String user_id) {
        swipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_CANDIDATES_POLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Candidate Poll Response: " + response);

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

                dictionary.addStringField(R.id.textViewStartTime, new StringExtractor<Poll>() {
                    @Override
                    public String getStringValue(Poll poll, int position) {
                        return poll.start_time + " - " + poll.end_time;
                    }
                });

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
                        Intent myIntent = new Intent(getApplicationContext(), CandidateInformationActivity.class);
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
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
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
