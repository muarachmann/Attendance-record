package com.level500.ub.ubattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Lecturer extends AppCompatActivity {
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private static final String KEY_USERNAME = "matricule";
    private static final String KEY_PASSWORD = "password";
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    private EditText lectText;
    private EditText passText;
    private String matricule;
    private String password;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);

         lectText = (EditText) findViewById(R.id.lecturerID);
         passText = (EditText) findViewById(R.id.password_real);

    }

    public void lecturerLogin(View view){

        matricule = lectText.getText().toString();
        password = passText.getText().toString();
        login();
    }


    private void login() {

        if ((TextUtils.isEmpty(matricule)) && (TextUtils.isEmpty(password))){
            Toast.makeText(Lecturer.this, "Invalid credentials please fill in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(matricule)) {
            Toast.makeText(Lecturer.this, "Please enter lecturer matricule", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Lecturer.this, "Please enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        displayLoader();
        String login_url = "http://172.20.10.5:3000/api/v1.1/attendance_4pi/lecturers/login";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 200){
                        Toast.makeText(Lecturer.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        String fN =  jsonObject.getString(KEY_FIRST_NAME);
                        String lN =  jsonObject.getString(KEY_LAST_NAME);
                        String tt =  jsonObject.getString("title");
                        String id =  jsonObject.getString(KEY_USERNAME);
                        loadDashboard(fN, lN, tt, id);
                    }
                    else {
                        Toast.makeText(Lecturer.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Lecturer.this, "json exception:" + e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
               String cause = error.getMessage();
                Toast.makeText(Lecturer.this, "error:" + cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("matricule",matricule);
                params.put("password",password);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                String credentials = "webrecord";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization","Basic "+ base64EncodedCredentials);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }


    /**
     * Display the progress dialog
     */

    private void displayLoader() {
        pDialog = new ProgressDialog(Lecturer.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }


    /**
     * Launch LecturerCourse Activity on Successful Login
     */
    private void loadDashboard(String fN, String lN, String title, String matricule) {
        Intent i = new Intent(getApplicationContext(), LecturerCourse.class);
        i.putExtra("fN", fN);
        i.putExtra("lN", lN);
        i.putExtra("tt", title);
        i.putExtra("lecID", matricule);
        startActivity(i);
        finish();
    }



}
