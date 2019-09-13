package com.level500.ub.ubattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class LecturerRecords extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturer_record);

        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b != null) {
            id = b.getString("course_id");

        }

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.course_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case R.id.print_attendance:
                Toast.makeText(LecturerRecords.this, "about to print", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_help:
                Intent intent2 = new Intent(LecturerRecords.this, About_Help.class);
                startActivity(intent2);
                break;

            case R.id.menu_more:
                Intent intent3 = new Intent(LecturerRecords.this, MoreActivity.class);
                startActivity(intent3);
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    public void init() {
        final TableLayout stk = (TableLayout) findViewById(R.id.table_main);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setPadding(5,5,5,5);
        tbrow0.setBackgroundColor(Color.BLUE);

        TextView tv0 = new TextView(this);
        tv0.setText("Matricule");
        tv0.setTextColor(Color.WHITE);
        tv0.layout(1,1,1,1);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText("Name");
        tv1.setTextColor(Color.WHITE);
        tv1.layout(1,1,1,1);
        tbrow0.addView(tv1);

        TextView tv2 = new TextView(this);
        tv2.setText("Remark1");
        tv2.setTextColor(Color.WHITE);
        tv2.layout(1,1,1,1);
        tbrow0.addView(tv2);

        TextView tv3 = new TextView(this);
        tv3.setText("Remark2");
        tv3.setTextColor(Color.WHITE);
        tv3.layout(1,1,1,1);
        tbrow0.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setText("Final");
        tv4.setTextColor(Color.WHITE);
        tv4.layout(1,1,1,1);
        tbrow0.addView(tv4);

        stk.addView(tbrow0);



        final ProgressDialog progressDialog = new ProgressDialog(LecturerRecords.this);
        progressDialog.setTitle("Retrieving attendance...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String login_url = "http://192.168.8.100/school/getattend.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                progressDialog.dismiss();
                try {
                    jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        JSONArray jsArr = jsonObject.getJSONArray("attendance");
                        if (jsArr.length() > 0) {
                            for (int i = 0; i < jsArr.length(); i++) {
                                String name     = jsArr.getJSONObject(i).getString("name");
                                String mat      = jsArr.getJSONObject(i).getString("matricule");
                                String remark1  = jsArr.getJSONObject(i).getString("remark1");
                                String remark2  = jsArr.getJSONObject(i).getString("remark2");
                                String finale   = jsArr.getJSONObject(i).getString("final");

                                TableRow tbrow = new TableRow(LecturerRecords.this);
                                tbrow.setPadding(5,5,5,5);
                                tbrow.setBackgroundColor(Color.WHITE);

                                TextView t1v = new TextView(LecturerRecords.this);
                                t1v.setText(mat);
                                t1v.setTextColor(Color.BLACK);
                                t1v.layout(1,1,1,1);
                                tbrow.addView(t1v);

                                TextView t2v = new TextView(LecturerRecords.this);
                                t2v.setText(name);
                                t2v.setTextColor(Color.BLACK);
                                t2v.layout(1,1,1,1);
                                tbrow.addView(t2v);

                                TextView t3v = new TextView(LecturerRecords.this);
                                t3v.setText(remark1);
                                t3v.layout(1,1,1,1);
                                t3v.setTextColor(Color.BLACK);
                                tbrow.addView(t3v);

                                TextView t4v = new TextView(LecturerRecords.this);
                                t4v.setText(remark2);
                                t4v.layout(1,1,1,1);
                                t4v.setTextColor(Color.BLACK);
                                tbrow.addView(t4v);

                                TextView t5v = new TextView(LecturerRecords.this);
                                t5v.setText(finale);
                                t5v.setTextColor(Color.BLACK);
                                t5v.layout(1,1,1,1);
                                tbrow.addView(t5v);

                                stk.addView(tbrow);
                            }
                        }
                        else {
                            Toast.makeText(LecturerRecords.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(LecturerRecords.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e1) {
                    progressDialog.dismiss();
                    e1.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                String cause = error.getMessage();
                Toast.makeText(LecturerRecords.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("course_id",id);
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


}