package com.level500.ub.ubattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class StudentLecturerLogin extends AppCompatActivity {
    private ProgressDialog pDialog;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_lecturer_login);
    }


    public void gotoStudent(View view){

        displayLoader();
        String login_url = "http://192.168.8.100/school/checkmac.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        Toast.makeText(StudentLecturerLogin.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        String mat = jsonObject.getString("matricule");
                        String id = jsonObject.getString("id");
                        String level = jsonObject.getString("level");
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
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismiss();
                String cause = error.getMessage();
                Toast.makeText(StudentLecturerLogin.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("mac",address);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);

    }


    public void gotoLecturer(View view){
        Intent intent = new Intent(StudentLecturerLogin.this, Lecturer.class);
        startActivity(intent);
    }



    public static String getMacAddr() {
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
