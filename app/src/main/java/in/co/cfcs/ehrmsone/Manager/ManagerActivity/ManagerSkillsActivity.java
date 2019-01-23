package in.co.cfcs.ehrmsone.Manager.ManagerActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.co.cfcs.ehrmsone.Adapter.SkillAdapter;
import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.Model.SkillsModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

public class ManagerSkillsActivity extends AppCompatActivity {

    public TextView titleTxt;
    public String empId = "";
    public SkillAdapter adapter;
    public ArrayList<SkillsModel> list = new ArrayList<>();
    public RecyclerView skillRecycler;
    public String skillsListUrl = SettingConstant.BaseUrl + "AppEmployeeSkillList";
    public ConnectionDetector conn;
    public String userId = "", authCode = "";
    public TextView noCust;

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_skills);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.mgrtoolbar);
        setSupportActionBar(toolbar);

        titleTxt = (TextView) toolbar.findViewById(R.id.titletxt);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onBackPressed();
                onBackPressed();

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            empId = intent.getStringExtra("empId");
        }

        titleTxt.setText("Skills Details");

        skillRecycler = (RecyclerView) findViewById(R.id.skill_recycler);
        noCust = (TextView) findViewById(R.id.no_record_txt);

        conn = new ConnectionDetector(ManagerSkillsActivity.this);
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(ManagerSkillsActivity.this)));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(ManagerSkillsActivity.this)));


        adapter = new SkillAdapter(ManagerSkillsActivity.this, list, ManagerSkillsActivity.this, "secondOne");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ManagerSkillsActivity.this);
        skillRecycler.setLayoutManager(mLayoutManager);
        skillRecycler.setItemAnimator(new DefaultItemAnimator());
        skillRecycler.setAdapter(adapter);

        skillRecycler.getRecycledViewPool().setMaxRecycledViews(0, 0);

    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        if (conn.getConnectivityStatus() > 0) {

            skillsList(authCode, userId, empId);

        } else {
            conn.showNoInternetAlret();
        }
    }

    //Skills list
    public void skillsList(final String AuthCode, final String AdminID, final String EmployeeID) {

        final ProgressDialog pDialog = new ProgressDialog(ManagerSkillsActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, skillsListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("Login", response);
                    JSONArray jsonArray = new JSONArray(response.substring(response.indexOf("["), response.lastIndexOf("]") + 1));

                    if (list.size() > 0) {
                        list.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.has("status")) {
                            LoginStatus = jsonObject.getString("status");
                            msgstatus = jsonObject.getString("MsgNotification");
                            if (LoginStatus.equals(invalid)) {
                                Logout();
                                Toast.makeText(getBaseContext(), msgstatus, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getBaseContext(), msgstatus, Toast.LENGTH_LONG).show();
                            }
                        } else {

                            String SkillName = jsonObject.getString("SkillName");
                            String ProficiencyName = jsonObject.getString("ProficiencyName");
                            String SkillSourceName = jsonObject.getString("SkillSourceName");
                            String LastUsed = jsonObject.getString("LastUsed");
                            String CurrentlyUsed = jsonObject.getString("CurrentlyUsed");
                            String RecordID = jsonObject.getString("RecordID");

                            list.add(new SkillsModel(SkillName, ProficiencyName, SkillSourceName, LastUsed, CurrentlyUsed, RecordID));

                        }

                    }

                    if (list.size() == 0) {
                        noCust.setVisibility(View.VISIBLE);
                        skillRecycler.setVisibility(View.GONE);
                    } else {
                        noCust.setVisibility(View.GONE);
                        skillRecycler.setVisibility(View.VISIBLE);
                    }


                    adapter.notifyDataSetChanged();
                    pDialog.dismiss();

                } catch (JSONException e) {
                    Log.e("checking json excption", e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Login", "Error: " + error.getMessage());
                // Log.e("checking now ",error.getMessage());

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    Toast.makeText(getBaseContext(),
                            "Time Out Server Not Respond",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                } else if (error instanceof ServerError) {
                    //TODO

                    Toast.makeText(getBaseContext(),
                            "Server Error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(getBaseContext(),
                            "Network Error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(getBaseContext(),
                            "Parse Error",
                            Toast.LENGTH_LONG).show();
                }
                pDialog.dismiss();


            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AuthCode", AuthCode);
                params.put("LoginAdminID", AdminID);
                params.put("EmployeeID", EmployeeID);

                Log.e("Parms", params.toString());
                return params;
            }

        };
        historyInquiry.setRetryPolicy(new DefaultRetryPolicy(SettingConstant.Retry_Time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(historyInquiry, "Login");

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        overridePendingTransition(R.anim.push_left_in,
                R.anim.push_right_out);

    }

    private void Logout() {


        finishAffinity();
        startActivity(new Intent(ManagerSkillsActivity.this, LoginActivity.class));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(ManagerSkillsActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(ManagerSkillsActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(ManagerSkillsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(ManagerSkillsActivity.this,
                "")));

    }

}
