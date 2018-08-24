package in.co.cfcs.ehrmsone.Manager.ManagerActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

import in.co.cfcs.ehrmsone.Fragment.ManagerDashBoardFragment;
import in.co.cfcs.ehrmsone.Main.HomeActivity;
import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

public class ManagerRequestToApproveActivity extends AppCompatActivity {

    public TextView titleTxt;
    public String countUrl = SettingConstant.BaseUrl + "AppManagerRequestToApproveDashBoard";
    public TextView leaveCountTxt, leaveCancelCpuntTxt, shortLeaveCountTxt, shortLeaveCancelCountTxt, traningCountTxt,attendanceApprove;
    public ConnectionDetector conn;
    public String userId = "", authCode = "";
    public LinearLayout thirdTilesLay, fourthTileLay, firstTileLat, secondTileLay, fivthLay,sixthlay;

    public Bundle bundle;
    String BackValue;

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_leave_management);

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

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            BackValue = extras.getString("BackValue");

        }

        titleTxt.setText("Request To Approve");

        conn = new ConnectionDetector(ManagerRequestToApproveActivity.this);
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(ManagerRequestToApproveActivity.this)));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(ManagerRequestToApproveActivity.this)));

        leaveCountTxt = (TextView) findViewById(R.id.leavecount);
        leaveCancelCpuntTxt = (TextView) findViewById(R.id.leavecancelcount);
        shortLeaveCountTxt = (TextView) findViewById(R.id.shortleavecount);
        shortLeaveCancelCountTxt = (TextView) findViewById(R.id.shortleavecancelcount);
        traningCountTxt = (TextView) findViewById(R.id.traningcount);
        thirdTilesLay = (LinearLayout) findViewById(R.id.thirdtiles);
        fourthTileLay = (LinearLayout) findViewById(R.id.fourthtile);
        firstTileLat = (LinearLayout) findViewById(R.id.firsttile);
        secondTileLay = (LinearLayout) findViewById(R.id.cancel_request);
        fivthLay = (LinearLayout) findViewById(R.id.fivthlay);
        attendanceApprove = (TextView) findViewById(R.id.attendanceApprove);
        sixthlay = (LinearLayout) findViewById(R.id.sixthlay);


        thirdTilesLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(ManagerRequestToApproveActivity.this, RequestToApprovedShortLeaveActivity.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        sixthlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(ManagerRequestToApproveActivity.this, AttendanceRequest.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        fourthTileLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(ManagerRequestToApproveActivity.this, RequestToApproveShortLeaveCancelationActivity.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        firstTileLat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(ManagerRequestToApproveActivity.this, RequestToApproveLeaveActivity.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        secondTileLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(ManagerRequestToApproveActivity.this, RequestToApproveLeaveCancelActivity.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        fivthLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(ManagerRequestToApproveActivity.this, ManagerRequestTraningListActivity.class);
                startActivity(ik);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get count of dashboard
        if (conn.getConnectivityStatus() > 0) {
            getCount(authCode, userId);
        } else {
            conn.showNoInternetAlret();
        }
    }

    //show dashbaord count api
    public void getCount(final String AuthCode, final String AdminID) {

        final ProgressDialog pDialog = new ProgressDialog(ManagerRequestToApproveActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, countUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("Login", response);
                    JSONArray jsonArray = new JSONArray(response.substring(response.indexOf("["), response.lastIndexOf("]") + 1));

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

                            String LeaveCount = jsonObject.getString("LeaveCount");
                            String CancelLeaveCount = jsonObject.getString("CancelLeaveCount");
                            String ShortLeaveCount = jsonObject.getString("ShortLeaveCount");
                            String ShortCancelLeaveCount = jsonObject.getString("ShortCancelLeaveCount");
                            String TrainingCount = jsonObject.getString("TrainingCount");
                            String AttendanceRequestCount = jsonObject.getString("AttendanceRequestCount");

                            leaveCountTxt.setText("(" + LeaveCount + ")");
                            leaveCancelCpuntTxt.setText("(" + CancelLeaveCount + ")");
                            shortLeaveCountTxt.setText("(" + ShortLeaveCount + ")");
                            shortLeaveCancelCountTxt.setText("(" + ShortCancelLeaveCount + ")");
                            traningCountTxt.setText("(" + TrainingCount + ")");
                            attendanceApprove.setText("(" + AttendanceRequestCount + ")");
                        }


                    }

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
        finish();
        overridePendingTransition(R.anim.push_left_in,
                R.anim.push_right_out);
    }


    private void Logout() {

        finishAffinity();
        startActivity(new Intent(ManagerRequestToApproveActivity.this, LoginActivity.class));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(ManagerRequestToApproveActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(ManagerRequestToApproveActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(ManagerRequestToApproveActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(ManagerRequestToApproveActivity.this,
                "")));

    }


}
