package in.co.cfcs.ehrmsone.Manager.ManagerActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import in.co.cfcs.ehrmsone.Adapter.ShortLeaveHistoryAdapter;
import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.Model.ShortLeaveHistoryModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

public class ProceedShortLeaveListActivity extends AppCompatActivity {

    public TextView titleTxt, noRecordFoundTxt;
    public RecyclerView proceedShortRecycler;
    public ShortLeaveHistoryAdapter adapter;
    public ArrayList<ShortLeaveHistoryModel> list = new ArrayList<>();
    public String userId = "", authCode = "";
    public ConnectionDetector conn;
    public String shortLeaveUrl = SettingConstant.BaseUrl + "AppManagerProceededShortLeaveRequestDashBoardList";

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_short_leave_list);

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

        titleTxt.setText("Proceeded Short Leave Request");

        proceedShortRecycler = (RecyclerView) findViewById(R.id.proceed_short_leave_recycler);
        noRecordFoundTxt = (TextView) findViewById(R.id.norecordfound);

        conn = new ConnectionDetector(ProceedShortLeaveListActivity.this);

        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(ProceedShortLeaveListActivity.this)));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(ProceedShortLeaveListActivity.this)));

        adapter = new ShortLeaveHistoryAdapter(ProceedShortLeaveListActivity.this, list, ProceedShortLeaveListActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ProceedShortLeaveListActivity.this);
        proceedShortRecycler.setLayoutManager(mLayoutManager);
        proceedShortRecycler.setItemAnimator(new DefaultItemAnimator());
        proceedShortRecycler.setAdapter(adapter);

        proceedShortRecycler.getRecycledViewPool().setMaxRecycledViews(0, 0);


        if (conn.getConnectivityStatus() > 0) {
            shortLeaveHistoryList(authCode, userId);
        } else {
            conn.showNoInternetAlret();
        }
    }

    //Short Leave History List
    public void shortLeaveHistoryList(final String AuthCode, final String AdminID) {

        final ProgressDialog pDialog = new ProgressDialog(ProceedShortLeaveListActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, shortLeaveUrl, new Response.Listener<String>() {
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
                            String LeaveApplication_Id = jsonObject.getString("LeaveApplication_Id");
                            String LeaveTypeName = jsonObject.getString("LeaveTypeName");
                            String StartDate = jsonObject.getString("StartDateText");
                            String TimeFrom = jsonObject.getString("TimeFrom");
                            String TimeTo = jsonObject.getString("TimeTo");
                            String AppliedDate = jsonObject.getString("AppliedDate");
                            String StatusText = jsonObject.getString("StatusText");
                            String CommentText = jsonObject.getString("CommentText");
                            String UserName = jsonObject.getString("UserName");

                            list.add(new ShortLeaveHistoryModel(UserName, LeaveApplication_Id, LeaveTypeName, StartDate, TimeFrom, TimeTo, AppliedDate,
                                    StatusText, CommentText, "0"));
                        }

                    }

                    if (list.size() == 0) {
                        noRecordFoundTxt.setVisibility(View.VISIBLE);
                        proceedShortRecycler.setVisibility(View.GONE);
                    } else {
                        noRecordFoundTxt.setVisibility(View.GONE);
                        proceedShortRecycler.setVisibility(View.VISIBLE);
                    }

                    adapter.notifyDataSetChanged();
                    pDialog.dismiss();

                } catch (JSONException e) {

                    Log.e("checking json excption", e.getMessage());
                    e.printStackTrace();

                } catch (StringIndexOutOfBoundsException ex) {
                    Toast.makeText(ProceedShortLeaveListActivity.this, "Error in request processing", Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                    //  Log.e("checking exception", ex.getMessage());
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
                params.put("AdminID", AdminID);


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
        startActivity(new Intent(ProceedShortLeaveListActivity.this, LoginActivity.class));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(ProceedShortLeaveListActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(ProceedShortLeaveListActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(ProceedShortLeaveListActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(ProceedShortLeaveListActivity.this,
                "")));

    }

}
