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
 * Created by Alip on 20/12/2016.
 */

public class Fragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = Fragment1.class.getSimpleName();

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstancesState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) view.findViewById(R.id.list);
        listView.setDivider(null);

        /*String[] item_updates = getResources().getStringArray(R.array.item_updates);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, item_updates);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "You clicked at position: " + (position + 1), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), AnnouncementDetailsActivity.class);
                intent.putExtra("string", "Go to another activity, Activity from listView position: " + (position + 1));
                startActivity(intent);
            }
        });*/

        getAnnouncement();


        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getAnnouncement();
    }

    public void getAnnouncement() {
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_ANNOUNCEMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Announcement Response: " + response);

                final ArrayList<Announcement> announcementList = new JsonConverter<Announcement>().toArrayList(response, Announcement.class);
                BindDictionary<Announcement> dictionary = new BindDictionary<>();

                dictionary.addStringField(R.id.textViewTitle, new StringExtractor<Announcement>() {
                    @Override
                    public String getStringValue(Announcement announcement, int position) {
                        return announcement.title;
                    }
                });

                dictionary.addDynamicImageField(R.id.imageViewPicture, new StringExtractor<Announcement>() {
                            @Override
                            public String getStringValue(Announcement announcement, int position) {
                                String path = AppConfig.URL_ANNOUNCEMENT_IMAGE + announcement.picture;
                                Log.d(TAG, path);
                                return path;
                            }
                        },
                        new DynamicImageLoader() {
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

                dictionary.addStringField(R.id.textViewStatus, new StringExtractor<Announcement>() {
                    @Override
                    public String getStringValue(Announcement announcement, int position) {
                        return announcement.status;
                    }
                });

                dictionary.addStringField(R.id.textViewDate, new StringExtractor<Announcement>() {
                    @Override
                    public String getStringValue(Announcement announcement, int position) {
                        return announcement.created_date;
                    }
                });

                /*dictionary.addStringField(R.id.textViewUser, new StringExtractor<Announcement>() {
                    @Override
                    public String getStringValue(Announcement announcement, int position) {
                        return announcement.username;
                    }
                });*/

                FunDapter<Announcement> adapter = new FunDapter<>(
                        getActivity().getApplicationContext(),
                        announcementList,
                        R.layout.layout_announcement,
                        dictionary);

                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent myIntent = new Intent(getActivity(), AnnouncementActivity.class);
                        Announcement selectedAnnouncement = announcementList.get(position);
                        myIntent.putExtra("announcement", selectedAnnouncement);
                        startActivity(myIntent);
                    }
                });

                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);


                /*ArrayList<String> textList = new ArrayList<>();
                for(Announcement a : announcementList) {
                    //Log.d(TAG, a.username);
                    textList.add(a.username);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getActivity().getApplicationContext(),
                        android.R.layout.simple_list_item_1,
                        textList
                ){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        TextView textView = (TextView) super.getView(position, convertView, parent);
                        textView.setTextColor(Color.BLACK);
                        return textView;
                    }
                };

                listView.setAdapter(adapter);*/
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
