package com.level500.ub.ubattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StudentLecturerLogin extends AppCompatActivity {
    private ProgressDialog pDialog;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_REGISTERED = "registered";

    private Button studentButton;
    private Button lecturerButton;
    private String macaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lecturer_login);

        studentButton = findViewById(R.id.student);
        lecturerButton = findViewById(R.id.lecturer);
        macaddress = getMacAddr();
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callStudent();
            }
        });

        lecturerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLecturer();
            }
        });
    }



    public void callStudent(){
        displayLoader();
        String login_url = "http://172.20.10.5:3000/api/v1.1/attendance_4pi/student/checkmac";
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString(KEY_REGISTERED).equalsIgnoreCase("yes")){
                        JSONObject student = jsonObject.getJSONObject("student");
                        Toast.makeText(StudentLecturerLogin.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        String mat = student.getString("matricule");
                        String id = student.getString("id");
                        String level = student.getString("level_id");
                        Intent intent = new Intent(StudentLecturerLogin.this, Student.class);
                        intent.putExtra("isRegistered", true);
                        intent.putExtra("matricule", mat);
                        intent.putExtra("id", id);
                        intent.putExtra("level", level);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(StudentLecturerLogin.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(StudentLecturerLogin.this, Student.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Toast.makeText(StudentLecturerLogin.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                NetworkResponse response = error.networkResponse;
                String errorMsg = "";
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    String message = trimMessage(errorString, "msg");
                    Toast.makeText(StudentLecturerLogin.this, message, Toast.LENGTH_LONG).show();
                }
                Toast.makeText(StudentLecturerLogin.this, response.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("macAddress", macaddress);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                String credentials = "fN" + ":" + "lN";
                String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                params.put("Authorization","Basic "+ base64EncodedCredentials);
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }


        public void gotoLecturer(){
        Intent intent = new Intent(StudentLecturerLogin.this, Lecturer.class);
        startActivity(intent);
    }

    public String trimMessage(String json, String key){
        String trimmedString = null;
        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }


    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ignored) {
        }
        return "02:00:00:00:00:00";
    }

    /**
     * Display the progress dialog
     */

    private void displayLoader() {
        pDialog = new ProgressDialog(StudentLecturerLogin.this);
        pDialog.setMessage("Logging In.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

}
