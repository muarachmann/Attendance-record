package com.level500.ub.ubattendance;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentCoursesAdapter extends RecyclerView.Adapter<StudentCoursesAdapter.MyViewHolder> {

    private Context mContext;
    private List<StudentCourses> coursesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, coursecode;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            coursecode = (TextView) view.findViewById(R.id.coursecode);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public StudentCoursesAdapter(Context mContext, List<StudentCourses> coursesList) {
        this.mContext = mContext;
        this.coursesList = coursesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.courses_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final StudentCourses course = coursesList.get(position);
        holder.title.setText(course.getName());
        holder.coursecode.setText(course.getCourseCode());
        final String mat = course.getStudentID();
        final String courseCode = course.getCourseCode();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerStudentCourse(mat, courseCode);

            }
        });

        // loading album cover using Glide library
        Glide.with(mContext).load(course.getThumbnail()).into(holder.thumbnail);
    }



    @Override
    public int getItemCount() {
        return coursesList.size();
    }


    public void registerStudentCourse(final String matricule, final String coursecode){
        String login_url = "http://192.168.8.100/school/student_course.php";

        RequestQueue queue = Volley.newRequestQueue(mContext.getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if(jsonObject.getInt("status") == 1){
                        Toast.makeText(mContext.getApplicationContext(), message,Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(mContext.getApplicationContext(), message,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String cause = error.getMessage();
                Toast.makeText(mContext.getApplicationContext(), cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("mac",address);
                params.put("matricule",matricule);
                params.put("course_code",coursecode);
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


