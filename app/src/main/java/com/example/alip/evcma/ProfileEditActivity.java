package com.example.alip.evcma;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Alip on 29/12/2016.
 */

public class ProfileEditActivity extends AppCompatActivity implements View.OnClickListener {

//    private Spinner spinnerSemeseter, spinnerCourse;
//    private ArrayAdapter adapter, adapter1;

    private static final String TAG = ProfileEditActivity.class.getSimpleName();

    private static final int RESULT_LOAD_IMAGE = 1;

    private SimpleDateFormat DatePickerFormatter;
    private DatePickerDialog selectDatePickerDialog;
    //private SpinnerAdapter adapter;

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhoneNo, editTextDOB, editTextGender,
            editTextAddressLine1, editTextAddressLine2, editTextPostcode, editTextCity, editTextState, editTextCountry;
    private Button buttonUpdate, buttonDate, buttonSGender;
    private ImageView imageViewProfilePicture, imageViewUpdate;
    //private Spinner spinnerGender;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog pDialog;

    private String userID, picture, rGender, eGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Simple Date Picker
        DatePickerFormatter = new SimpleDateFormat("dd.MM.yyyy");

        // TextView
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPhoneNo = (EditText) findViewById(R.id.editTextMobileNo);
        editTextDOB = (EditText) findViewById(R.id.editTextDOB);
        editTextGender = (EditText) findViewById(R.id.editTextGender);
        editTextAddressLine1 = (EditText) findViewById(R.id.editTextAddressLine1);
        editTextAddressLine2 = (EditText) findViewById(R.id.editTextAddressLine2);
        editTextPostcode = (EditText) findViewById(R.id.editTextPostcode);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        editTextState = (EditText) findViewById(R.id.editTextState);
        editTextCountry = (EditText) findViewById(R.id.editTextCountry);
        // ImageView
        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        imageViewUpdate = (ImageView) findViewById(R.id.imageViewUpdate);
        // Spinner
        //spinnerGender = (Spinner) findViewById(R.id.spinnerGender);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* String[] gender = getResources().getStringArray(R.array.gender);
        spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, gender);
        spinnerGender.setAdapter(adapter);*/

        // Get User ID from Sqlite Database
        userID = db.searchUser();
        searchUserDetails(userID);

        // Set onclick Listener button
        //buttonUpdate.setOnClickListener(this);
        editTextDOB.setOnClickListener(this);
        editTextGender.setOnClickListener(this);
        imageViewProfilePicture.setOnClickListener(this);
        imageViewUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imageViewUpdate:

                Bitmap image = ((BitmapDrawable) imageViewProfilePicture.getDrawable()).getBitmap();
                new UploadImage(image).execute();

                String id = userID;
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String phoneNo = editTextPhoneNo.getText().toString().trim();
                String dob = editTextDOB.getText().toString().trim();
                String gender = editTextGender.getText().toString().trim();
                String addressLine1 = editTextAddressLine1.getText().toString().trim();
                String addressLine2 = editTextAddressLine2.getText().toString().trim();
                String postcode = editTextPostcode.getText().toString().trim();
                String city = editTextCity.getText().toString().trim();
                String state = editTextState.getText().toString().trim();
                String country = editTextCountry.getText().toString().trim();

                updateUserDetails(id, firstName, lastName, email, phoneNo, dob, gender,
                        addressLine1, addressLine2, postcode, city, state, country);

                Thread timerThread = new Thread() {
                    @Override
                    public void run() {
                        try{
                            sleep(3000);
                        }catch(InterruptedException e){
                            e.printStackTrace();
                        }finally{
                            finish();
                            Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(myIntent);
                        }
                    }
                };
                timerThread.start();


                break;

            case R.id.imageViewProfilePicture:

                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                break;

            case R.id.editTextDOB:

                final Calendar newCalendar = Calendar.getInstance(Locale.US);
                selectDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        Log.d(TAG, "DatePicker : " + newDate.getTime());
                        editTextDOB.setText(DatePickerFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                selectDatePickerDialog.show();

                break;

            case R.id.editTextGender:

                eGender = editTextGender.getText().toString();
                int value = -1;
                if(eGender.equals("Male")) {
                    value = 0;
                } else if(eGender.equals("Female")) {
                    value = 1;
                } else {
                    value = -1;
                }

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Gender");
                alertDialogBuilder.setSingleChoiceItems(R.array.gender, value, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(PackageActivity.this, getResources().getStringArray(R.array.my_package)[which], Toast.LENGTH_LONG).show();
                        //etPackage.setText(getResources().getStringArray(R.array.my_package)[which]);
                        rGender = getResources().getStringArray(R.array.gender)[which];
                    }
                });

                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(PackageActivity.this, "You clicked yes button!", Toast.LENGTH_LONG).show();
                        //finish();
                        editTextGender.setText(rGender);
                    }
                });

                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            imageViewProfilePicture.setImageURI(selectedImage);
        }
    }

    private class UploadImage extends AsyncTask<Void, Void, Void> {
        Bitmap image;

        public UploadImage(Bitmap image) {
            this.image = image;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
            final String encodedImage = Base64.encodeToString(byteArrayInputStream.toByteArray(), Base64.DEFAULT);

            StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_UPDATE_PROFILE_USER, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Profile Edit Response: " + response.toString());
                    hideDialog();
                    //Successfully Updated Profile Picture
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Profile Edit Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("id", userID);
                    params.put("current_picture", picture);
                    params.put("picture", encodedImage);
                    return params;
                }
            };

            AppController.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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

    private void searchUserDetails(final String userID) {
        pDialog.setMessage("Searching ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_SEARCH_USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Searching Response: " + response.toString());
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
                        String phone = user.getString("phone_no");
                        String email = user.getString("email");
                        picture = user.getString("picture");
                        String dob = user.getString("dob");
                        String gender = user.getString("gender");
                        String address1 = user.getString("address1");
                        String address2 = user.getString("address2");
                        String postcode = user.getString("postcode");
                        String city = user.getString("city");
                        String state = user.getString("state");
                        String country = user.getString("country");

                        new DownloadImage(picture.toString()).execute();
                        editTextFirstName.setText(first_name);
                        editTextLastName.setText(last_name);
                        editTextPhoneNo.setText(phone);
                        editTextEmail.setText(email);
                        editTextDOB.setText(dob);
                        editTextGender.setText(gender);
                        editTextAddressLine1.setText(address1);
                        editTextAddressLine2.setText(address2);
                        editTextPostcode.setText(postcode);
                        editTextCity.setText(city);
                        editTextState.setText(state);
                        editTextCountry.setText(country);

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Searching Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userID);
                return params;
            }
        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void updateUserDetails(final String id, final String firstName, final String lastName, final String email, final String phone,
                                   final String dob, final String gender, final String addressLine1, final String addressLine2, final String postcode, final String city,
                                   final String state, final String country) {
        pDialog.setMessage("Waiting...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Method.POST, AppConfig.URL_UPDATE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(error) {
                        // Now store the user in SQLite
                        String uid = jObj.getString("error_msg");

                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("user_id", id);
                params.put("email", email);
                params.put("phone_no", phone);
                params.put("dob", dob);
                params.put("gender", gender);
                params.put("address1", addressLine1);
                params.put("address2", addressLine2);
                params.put("postcode", postcode);
                params.put("city", city);
                params.put("state", state);
                params.put("country", country);
                return params;
            }
        };

        AppController.getInstance(this).addToRequestQueue(stringRequest);
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
