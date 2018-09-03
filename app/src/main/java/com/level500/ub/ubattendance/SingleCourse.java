package com.level500.ub.ubattendance;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class SingleCourse extends AppCompatActivity {

    private TextView courseName;
    private TextView courseCode;
    private Button activateBtn;
    private Button viewRecord;
    private Button getRecord;
    private ProgressDialog pDialog;
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private boolean isActivated = false;
    private String codeID;
    private String lectID;

    private String count = "0";
    private String acc_status = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_course);
        courseCode = (TextView) findViewById(R.id.coursecode);
        courseName = (TextView) findViewById(R.id.coursename);
        activateBtn = (Button) findViewById(R.id.activate_course);
        viewRecord = (Button) findViewById(R.id.view_attendance);
        getRecord = (Button) findViewById(R.id.get_attendance_record);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b != null) {
            String code = b.getString("coursecode");
            String name = b.getString("coursename");
            String lect = b.getString("lecId");
            courseName.setText(name);
            codeID = code;
            lectID = lect;
            courseCode.setText("("+code+")");
            performCheck(codeID, lectID);
        }



    }

    public void activateAttendance(View view){
        displayLoader();
        String login_url = "http://192.168.8.100/school/activate.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message =  jsonObject.getString(KEY_MESSAGE);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        Toast.makeText(SingleCourse.this, message,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SingleCourse.this, SingleCourse.class);
                        intent.putExtra("coursecode", codeID);
                        intent.putExtra("coursename", courseName.getText().toString().trim());
                        intent.putExtra("lecId", lectID);
                        startActivity(intent);
                        SingleCourse.this.finish();
                    }
                    else {
                        Toast.makeText(SingleCourse.this, message,Toast.LENGTH_SHORT).show();

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
                Toast.makeText(SingleCourse.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("mac",address);
                params.put("code", codeID);
                params.put("lect", lectID);
                if(acc_status.equalsIgnoreCase("0") && count.equalsIgnoreCase("0")){
                    params.put("checkpass", "first_activate");
                }

                if(acc_status.equalsIgnoreCase("1") && count.equalsIgnoreCase("1")){
                    params.put("checkpass", "first_deactivate");
                }

                if(acc_status.equalsIgnoreCase("0") && count.equalsIgnoreCase("1")){
                    params.put("checkpass", "second_activate");
                }

                if(acc_status.equalsIgnoreCase("1") && count.equalsIgnoreCase("2")){
                    params.put("checkpass", "second_deactivate");
                }
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

    public void performCheck(final String codeID, final String lectID){

        displayLoader2();
        String login_url = "http://192.168.8.100/school/check.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message =  jsonObject.getString(KEY_MESSAGE);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        String count1 = String.valueOf(jsonObject.getInt("count"));
                        String acc_status1 = String.valueOf(jsonObject.getInt("acc_status"));
                        Toast.makeText(SingleCourse.this, count1 + " " + acc_status1,Toast.LENGTH_LONG).show();

                        count =  count1;
                        acc_status =  acc_status1;
                        if(acc_status.equalsIgnoreCase("0") && count.equalsIgnoreCase("0")){
                            activateBtn.setText("Activate First pass");
                        }

                        if(acc_status.equalsIgnoreCase("1") && count.equalsIgnoreCase("1")){
                            activateBtn.setText("Deactivate First pass");
                        }

                        if(acc_status.equalsIgnoreCase("0") && count.equalsIgnoreCase("1")){
                            activateBtn.setText("Activate Second Pass");
                        }

                        if(acc_status.equalsIgnoreCase("1") && count.equalsIgnoreCase("2")){
                            activateBtn.setText("Deactivate second Attendance");
                        }

                    }
                    else {
                        Toast.makeText(SingleCourse.this, message,Toast.LENGTH_SHORT).show();

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
                Toast.makeText(SingleCourse.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("mac",address);
                params.put("code", codeID);
                params.put("lect", lectID);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.menu_new_settings:
                Toast.makeText(SingleCourse.this, "settings clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_logout:
                Intent intent = new Intent(SingleCourse.this, Lecturer.class);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_help:
                Intent intent2 = new Intent(SingleCourse.this, About_Help.class);
                startActivity(intent2);
                break;

            case R.id.menu_more:
                Intent intent3 = new Intent(SingleCourse.this, MoreActivity.class);
                startActivity(intent3);
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    public void viewAttendance(View view){
        Intent intent = new Intent(SingleCourse.this, LecturerRecords.class);
        intent.putExtra("course_id", codeID);
        startActivity(intent);
    }



    private void displayLoader() {
        pDialog = new ProgressDialog(SingleCourse.this);
        pDialog.setMessage("Activating course...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void displayLoader2() {
        pDialog = new ProgressDialog(SingleCourse.this);
        pDialog.setMessage("Checking status");
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
