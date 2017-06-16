package com.example.alip.evcma;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Alip on 2/1/2017.
 */

public class Fragment2 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = Fragment2.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh_layout_poll);
        listView = (ListView) getActivity().findViewById(R.id.listPoll);
        listView.setDivider(null);

       /* ListView listView = (ListView) view.findViewById(R.id.list);
        String[] item_votes = getResources().getStringArray(R.array.item_vote);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, item_votes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "You clicked at position: " + (position + 1), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), VoteActivity.class);
                intent.putExtra("string", "Vote No. : " + (position + 1));
                startActivity(intent);
            }
        });*/

       getPoll();
       swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getPoll();
    }

    private void getPoll() {
        swipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_POLL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Poll Response: " + response);

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
                                Picasso.with(getActivity().getApplicationContext())
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
                        getActivity().getApplicationContext(),
                        pollList,
                        R.layout.layout_poll,
                        dictionary);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(getActivity(), PollActivity.class);
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
                Toast.makeText(getActivity().getApplicationContext(), "Error while reading data", Toast.LENGTH_LONG).show();
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        AppController.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
