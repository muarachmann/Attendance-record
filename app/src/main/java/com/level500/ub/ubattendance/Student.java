package com.level500.ub.ubattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class Student extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    boolean isRegistered = false;
    Button registerBtn;
    private String matricule;
    private String id2;
    private String level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        registerBtn = (Button) findViewById(R.id.register);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isRegistered = extras.getBoolean("isRegistered");
            matricule = extras.getString("matricule");
            id2 = extras.getString("id");
            level = extras.getString("level");
            if(isRegistered){
                registerBtn.setText("EDIT PROFILE");
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.menu_new_settings:
                Toast.makeText(Student.this, "settings clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_registered_courses:
                Intent intent = new Intent(Student.this, StudentCourse.class);
                intent.putExtra("level", level);
                intent.putExtra("matricule", matricule);
                intent.putExtra("id", id2);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_help:
                Intent intent2 = new Intent(Student.this, About_Help.class);
                startActivity(intent2);
                break;

            case R.id.menu_more:
                Intent intent3 = new Intent(Student.this, MoreActivity.class);
                startActivity(intent3);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    public void registerStudent(View view){
        if(isRegistered){

            displayLoader();
            String login_url = "http://192.168.8.100/school/getstudent.php";

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if(jsonObject.getInt(KEY_STATUS) == 1){
                            String fN =  jsonObject.getString("firstname");
                            String lN =  jsonObject.getString("lastname");
                            String prgId =  jsonObject.getString("programID");
                            String matricule =  jsonObject.getString("matricule");
                            String phone =  jsonObject.getString("phone");

                            Intent intent = new Intent(Student.this, StudentRegister.class);
                            intent.putExtra("isRegistered", true);
                            intent.putExtra("fN", fN);
                            intent.putExtra("lN", lN);
                            intent.putExtra("prgId", prgId);
                            intent.putExtra("matricule", matricule);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }
                        else {
                            // it should always return true except some fatal error occured with the server
                            // ;) rachmann says
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
                    Toast.makeText(Student.this, cause,Toast.LENGTH_LONG).show();
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
        else {
            Intent intent = new Intent(Student.this, StudentRegister.class);
            startActivity(intent);
        }

    }

    public void takeAttendance(View view){
        displayLoader2();
        String login_url = "http://192.168.8.100/school/tickattend.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        Toast.makeText(Student.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Student.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();

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
                Toast.makeText(Student.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("matricule",id2);
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

    public void showRecords(View view){
        Toast.makeText(Student.this, "show records not yet implemented", Toast.LENGTH_SHORT).show();
    }


    private void displayLoader() {
        pDialog = new ProgressDialog(Student.this);
        pDialog.setMessage("Getting  details...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void displayLoader2() {
        pDialog = new ProgressDialog(Student.this);
        pDialog.setMessage("Taking attendance.");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

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

}
