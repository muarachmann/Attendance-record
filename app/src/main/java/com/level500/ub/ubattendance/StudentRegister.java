package com.level500.ub.ubattendance;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import io.apptik.widget.multiselectspinner.MultiSelectSpinner;

public class StudentRegister extends AppCompatActivity implements MultiSelectSpinner.MultiSpinnerListener{
    private static final String KEY_STATUS = "status";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_FIRST_NAME = "firstname";
    private static final String KEY_LAST_NAME = "lastname";
    private ProgressDialog pDialog;

    private Spinner spinner1, spinner2;
    private String programme;
    boolean isRegistered = false;
    Button registerBtn;
    EditText matriculeTxt;
    EditText phoneTxt;
    EditText fnTxt;
    EditText lnTxt;
    ArrayList<String> courseList;
    ArrayList<String> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_register);
        registerBtn  = (Button) findViewById(R.id.register);
        matriculeTxt = (EditText) findViewById(R.id.register_matricule);
        phoneTxt     = (EditText) findViewById(R.id.register_phone);
        fnTxt        = (EditText) findViewById(R.id.register_firstName);
        lnTxt        = (EditText) findViewById(R.id.register_lastName);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            isRegistered = extras.getBoolean("isRegistered");
            if(isRegistered){
                registerBtn.setText("UPDATE PROFILE");
                registerBtn.setBackgroundColor(Color.GREEN);
                matriculeTxt.setText(extras.getString("matricule"));
                phoneTxt.setText(extras.getString("phone"));
                fnTxt.setText(extras.getString("fN"));
                lnTxt.setText(extras.getString("lN"));
            }
        }

        // get all courses into the spinner 1.
        spinner1 = (Spinner) findViewById(R.id.programme);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.programme_list,
                        android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner1.setAdapter(staticAdapter);

        spinner2 = (Spinner) findViewById(R.id.level);
        // Create an ArrayAdapter using the string array and a default spinner
        ArrayAdapter<CharSequence> staticAdapter2 = ArrayAdapter
                .createFromResource(this, R.array.level,
                        android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        staticAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner2.setAdapter(staticAdapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });



    }



    @SuppressLint("ResourceType")
    public void RegisterStudent(View view){
        final EditText matricule = (EditText) findViewById(R.id.register_matricule);
        final EditText phone = (EditText) findViewById(R.id.register_phone);
        final EditText fName = (EditText) findViewById(R.id.register_firstName);
        final EditText lName = (EditText) findViewById(R.id.register_lastName);

        final int programme = spinner1.getSelectedItemPosition();
        final String level     = spinner2.getSelectedItem().toString();
        final String strPhone     = phone.getText().toString().trim();
        final String strFname     = fName.getText().toString().trim();
        final String strLname     = lName.getText().toString().trim();
        final String strMat       = matricule.getText().toString().trim().toUpperCase();


        if (TextUtils.isEmpty(strMat)) {
            Toast.makeText(StudentRegister.this, "Please enter your matricule!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strFname)) {
            Toast.makeText(StudentRegister.this, "Please enter your first name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strLname)) {
            Toast.makeText(StudentRegister.this, "Please enter your last name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(strPhone)) {
            Toast.makeText(StudentRegister.this, "Please enter a phone number!", Toast.LENGTH_SHORT).show();
        }else {
            if(!digitsOnly(strPhone)){
                Toast.makeText(StudentRegister.this, "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        displayLoader();
        String login_url = "http://192.168.8.100/school/register.php";

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest sr = new StringRequest(Request.Method.POST,login_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if(jsonObject.getInt(KEY_STATUS) == 1){
                        Toast.makeText(StudentRegister.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
                        String fN =  jsonObject.getString(KEY_FIRST_NAME);
                        String lN =  jsonObject.getString(KEY_LAST_NAME);
                        String tt =  jsonObject.getString("title");
                    }
                    else {
                        Toast.makeText(StudentRegister.this, jsonObject.getString(KEY_MESSAGE),Toast.LENGTH_LONG).show();
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
                Toast.makeText(StudentRegister.this, cause,Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                final String address = getMacAddr();
                params.put("mac",address);
                params.put("matricule",strMat);
                params.put("fName",strFname);
                params.put("lName",strLname);
                params.put("phone",strPhone);
                params.put("programme", String.valueOf(programme + 1));
                params.put("level", level);
                if(isRegistered){
                    params.put("check","true");
                }
                else {

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


    private boolean digitsOnly(String phone){

        return TextUtils.isDigitsOnly(phone);
    }



    private void displayLoader() {
        pDialog = new ProgressDialog(StudentRegister.this);
        pDialog.setMessage("Creating Student account...");
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


    @Override
    public void onItemsSelected(boolean[] selected) {
        for (int i = 0; i < coursesList.size(); i++) {
            if (selected[i]) {
                Toast.makeText(StudentRegister.this, courseList.get(i),Toast.LENGTH_LONG).show();
            } else {
              //pass
            }
        }

    }
}
