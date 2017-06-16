package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.alip.evcma.app.AppConfig;
import com.example.alip.evcma.app.AppController;
import com.kosalgeek.android.json.JsonConverter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alip on 30/12/2016.
 */

public class ListCandidateActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ListCandidateActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String poll_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_candidate);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_candidate);
        listView = (ListView) findViewById(R.id.listCandidate);
        listView.setDivider(null);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if(getIntent() != null) {
            poll_id = getIntent().getStringExtra("poll_id");
        }

        getListOfCandidates(poll_id);
        swipeRefreshLayout.setOnRefreshListener(this);

        /*ListView listView = (ListView) findViewById(R.id.list);
        String[] dummyStrings = getResources().getStringArray(R.array.item_candidates);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dummyStrings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ListCandidateActivity.this, "You clicked at position: " + (position + 1), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListCandidateActivity.this, CandidateDetailsActivity.class);
                intent.putExtra("string", "No : " + (position + 1));
                startActivity(intent);
            }
        });*/
    }

    private void getListOfCandidates(final String poll_id) {
        swipeRefreshLayout.setRefreshing(true);

        pDialog.setMessage("Loading..");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_CANDIDATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Candidates Response: " + response);
                hideDialog();

                try
                {
                    final ArrayList<Candidate> candidateList = new JsonConverter<Candidate>().toArrayList(response, Candidate.class);
                    BindDictionary<Candidate> dictionary = new BindDictionary<>();

                    dictionary.addDynamicImageField(R.id.imageViewPictureCandidate, new StringExtractor<Candidate>() {
                            @Override
                            public String getStringValue(Candidate candidate, int position) {
                                String path = AppConfig.URL_CANDIDATES_IMAGE + candidate.picture;
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

                    dictionary.addStringField(R.id.textViewCandidateName, new StringExtractor<Candidate>() {
                        @Override
                        public String getStringValue(Candidate candidate, int position) {
                            return candidate.first_name + " " + candidate.last_name;
                        }
                    });

                    dictionary.addStringField(R.id.textViewProgramme, new StringExtractor<Candidate>() {
                        @Override
                        public String getStringValue(Candidate candidate, int position) {
                            return candidate.programme;
                        }
                    });

                    dictionary.addStringField(R.id.textViewSemester, new StringExtractor<Candidate>() {
                        @Override
                        public String getStringValue(Candidate candidate, int position) {
                            return candidate.semester;
                        }
                    });

                    dictionary.addStringField(R.id.textViewCandidateStatus, new StringExtractor<Candidate>() {
                        @Override
                        public String getStringValue(Candidate candidate, int position) {
                            return candidate.status;
                        }
                    });

                    FunDapter<Candidate> adapter = new FunDapter<>(
                            getApplicationContext(),
                            candidateList,
                            R.layout.layout_candidate,
                            dictionary
                    );

                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent myIntent = new Intent(getApplicationContext(), CandidateDetailsActivity.class);
                            Candidate selectedCandidate = candidateList.get(position);
                            myIntent.putExtra("candidate", selectedCandidate);
                            myIntent.putExtra("poll_id", poll_id);
                            startActivity(myIntent);
                            /*Candidate selectedCandidate = candidateList.get(position);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "You are selected : " + selectedCandidate,
                                    Toast.LENGTH_LONG).show();*/
                        }
                    });

                    swipeRefreshLayout.setRefreshing(false);
                }
                catch (Exception e)
                {
                    //Error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error Read Data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                hideDialog();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", poll_id);
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        alertDialogBuilder.setMessage("Are you sure want to out from poll?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked yes button!", Toast.LENGTH_LONG).show();
                finish();
                /*Intent myIntent = new Intent(getApplicationContext(), ListCandidateActivity.class);
                myIntent.putExtra("poll_id", poll_id);
                startActivity(myIntent);*/
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //finish();
    }

    @Override
    public void onRefresh() {
        getListOfCandidates(poll_id);
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
