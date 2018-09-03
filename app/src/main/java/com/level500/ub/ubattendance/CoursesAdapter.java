package com.level500.ub.ubattendance;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Courses> coursesList;

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


    public CoursesAdapter(Context mContext, List<Courses> coursesList) {
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
        final Courses course = coursesList.get(position);
        holder.title.setText(course.getName());
        holder.coursecode.setText(course.getCourseCode() + " " + course.getLecturerID());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String uid =  course.getCourseCode(); // Put anything what you want
                String coursename =  course.getName(); // Put anything what you want
                String lecId = course.getLecturerID();

                Intent intent = new Intent(mContext, SingleCourse.class);
                intent.putExtra("coursecode", uid);
                intent.putExtra("coursename", coursename);
                intent.putExtra("lecId", lecId);
                mContext.startActivity(intent);
            }
        });

        // loading album cover using Glide library
        Glide.with(mContext).load(course.getThumbnail()).into(holder.thumbnail);
    }



    @Override
    public int getItemCount() {
        return coursesList.size();
    }
}


