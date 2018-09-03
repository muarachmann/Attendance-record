package com.level500.ub.ubattendance;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentCourse extends AppCompatActivity {

    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";

    private TextView lectName;
    private Fragment courseFragment;
    private GridLayout gridLayout;
    private ProgressDialog pDialog;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    StudentCoursesAdapter mAdapter;
    private List<StudentCourses> courseList;
    private String level;
    private String matricule;
    private String id;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_course);

        final int[] comics = new int[]{
                R.drawable.electric_machines,
                R.drawable.stats_probability,
                R.drawable.data_transmission,
                R.drawable.database
        };



        Intent iin= getIntent();
        Bundle b = iin.getExtras();


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            level = extras.getString("level");
            matricule = extras.getString("matricule");
            id = extras.getString("id");

        }

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        courseList = new ArrayList<>();
        mAdapter = new StudentCoursesAdapter(getApplication(), courseList);

        mLayoutManager = new GridLayoutManager(getApplication(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final ProgressDialog progressDialog = new ProgressDialog(StudentCourse.this);
        progressDialog.setTitle("Retrieving courses...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String login_url = "http://192.168.8.100/school/getcourses.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                progressDialog.dismiss();
                try {
                    jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        JSONArray jsArr = jsonObject.getJSONArray("courses");
                        if (jsArr.length() > 0) {
                            courseList= new ArrayList<>();
                            for (int i = 0; i < jsArr.length(); i++) {
                                String coursecode = jsArr.getJSONObject(i).getString("id");
                                String title = jsArr.getJSONObject(i).getString("title");
                                courseList.add(new StudentCourses(title, coursecode, id , comics[0]));
                            }
                            mAdapter = new StudentCoursesAdapter(getApplication(), courseList);
                            mRecyclerView.setAdapter(mAdapter);
                        }
                        else {
                            Toast.makeText(StudentCourse.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        }

                    }else {
                        Toast.makeText(StudentCourse.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();

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
                Toast.makeText(StudentCourse.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("level",level);
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

        try {
            Glide.with(this).load(R.drawable.stats_probability).into((ImageView)findViewById(R.id.thumbnail));
        } catch (Exception e) {
            e.printStackTrace();
        }


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
                Toast.makeText(StudentCourse.this, "settings clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_logout:
                Intent intent = new Intent(StudentCourse.this, Lecturer.class);
                startActivity(intent);
                finish();
                break;

            case R.id.menu_help:
                Intent intent2 = new Intent(StudentCourse.this, About_Help.class);
                startActivity(intent2);
                break;

            case R.id.menu_more:
                Intent intent3 = new Intent(StudentCourse.this, MoreActivity.class);
                startActivity(intent3);
                break;

        }
        return super.onOptionsItemSelected(item);

    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
