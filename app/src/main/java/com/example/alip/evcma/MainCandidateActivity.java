package com.example.alip.evcma;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.alip.evcma.app.AppConfig;
import com.example.alip.evcma.app.AppController;
import com.example.alip.evcma.helper.SQLiteHandler;
import com.example.alip.evcma.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MainCandidateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainCandidateActivity.class.getSimpleName();

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private ViewPager viewPager;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private String[] pageTitle =
            {
                    "Announcement",
                    "Poll"
//                    "Candidate",
//                    "Result"
            };
    private String name, email, picture, user_id;
    private ImageView imageViewProfilePicture;
    private TextView textViewFullname, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_candidate);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawerLayout);

        setSupportActionBar(toolbar);

        //create default navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //setting Tab layout (number of Tabs = number of viewPager pages)
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        for (int i = 0; i < 2; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(pageTitle[i]));
        }

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //handling navigation view item event
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        View header = navigationView.getHeaderView(0);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager)findViewById(R.id.view_pager);

        //set viewpager adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe viewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imageViewProfilePicture = (ImageView) header.findViewById(R.id.imageViewProfilePictureNav);
        textViewFullname = (TextView) header.findViewById(R.id.textViewFullName);
        textViewEmail = (TextView) header.findViewById(R.id.textViewEmail);

        user_id = db.searchUser();
        name = db.searchName();
        email = db.searchEmail();
        picture = db.searchPicture();

        if(!name.isEmpty() && !email.isEmpty() && !picture.isEmpty()) {
            new DownloadImage(picture.toString()).execute();
            textViewFullname.setText(name);
            textViewEmail.setText(email);
        }
    }

    @Override
    protected void onResume() {
        UpdateUserDetails(user_id);
        super.onResume();
    }

    protected void UpdateUserDetails(final String user_id) {
        pDialog.setMessage("Searching ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Searching User Response : " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(error) {
                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String first_name = user.getString("first_name");
                        String last_name = user.getString("last_name");
                        String ic = user.getString("ic_no");
                        String dob = user.getString("dob");
                        String phone = user.getString("phone_no");
                        String email = user.getString("email");
                        picture = user.getString("picture");
                        String gender = user.getString("gender");
                        String university = user.getString("university");
                        String programme = user.getString("programme");
                        String semester = user.getString("semester");

                        new DownloadImage(picture.toString()).execute();
                        textViewFullname.setText(first_name + " " + last_name);
                        textViewEmail.setText(email);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    String errorMsg = e.getMessage();
                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Searching Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
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
            String url = AppConfig.URL_PROFILE_IMAGE + picture;

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
                imageViewProfilePicture.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.fr1) {
            viewPager.setCurrentItem(0);
        } else if (id == R.id.fr2) {
            viewPager.setCurrentItem(1);
        } else if (id == R.id.fr3) {
            Toast.makeText(getApplicationContext(), "Candidate Info.", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(getApplicationContext(), ListCandidatePollActivity.class);
            startActivity(myIntent);
            //viewPager.setCurrentItem(2);
            //Intent intent = new Intent(this, ListCandidateActivity.class);
            //startActivity(intent);
        } else if (id == R.id.fr4) {
            //Toast.makeText(getApplicationContext(), "Result", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(this, ResultActivity.class);
            startActivity(myIntent);
            //viewPager.setCurrentItem(3);
        /*} else if (id == R.id.helpdesk) {
            Intent intent = new Intent(this, HelpdeskActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        /*} else if (id == R.id.go) {
            Intent intent = new Intent(this, DesActivity.class);
            intent.putExtra("string", "Go to other Activity by Navigation item clicked!");
            startActivity(intent);*/
        } else if (id == R.id.close) {

            session.setLogin(false);
            db.deleteUsers();

            // Launching the login activity
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
