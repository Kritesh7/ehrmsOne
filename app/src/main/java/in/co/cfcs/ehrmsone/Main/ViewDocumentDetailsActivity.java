package in.co.cfcs.ehrmsone.Main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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

import in.co.cfcs.ehrmsone.Adapter.RequestedItemAdapter;
import in.co.cfcs.ehrmsone.Model.AddNewStationoryRequestModel;
import in.co.cfcs.ehrmsone.Model.BookMeaPrevisionModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

public class ViewDocumentDetailsActivity extends AppCompatActivity {

    public TextView titleTxt, requestByTxt, requestDateTxt, empNameTxt, statusTxt, hrCommentTxt, closerDateTxt, hrTxt;
    public in.co.cfcs.ehrmsone.Source.MyListLayout requestItemList;
    public RequestedItemAdapter adapter;
    public ArrayList<BookMeaPrevisionModel> list = new ArrayList<>();
    public ArrayList<AddNewStationoryRequestModel> itemBindList = new ArrayList<>();
    public ConnectionDetector conn;
    public String authCode = "", rid = "", ridStr = "", IdealClosureDateText = "", userId = "";
    public Button updateDetails;
    public String stationoryUrl = SettingConstant.BaseUrl + "AppEmployeeStationaryRequestDetail";

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document_details);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.viewleavtoolbar);
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

        titleTxt.setText("Document Details");

        Intent intent = getIntent();
        if (intent != null) {
            rid = intent.getStringExtra("Rid");
        }

        conn = new ConnectionDetector(ViewDocumentDetailsActivity.this);
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(ViewDocumentDetailsActivity.this)));
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(ViewDocumentDetailsActivity.this)));

        requestItemList = (in.co.cfcs.ehrmsone.Source.MyListLayout) findViewById(R.id.request_item_list);
        requestByTxt = (TextView) findViewById(R.id.staionory_request);
        requestDateTxt = (TextView) findViewById(R.id.staionory_request_date);
        empNameTxt = (TextView) findViewById(R.id.staionory_empname);
        statusTxt = (TextView) findViewById(R.id.stationory_current_status);
        hrCommentTxt = (TextView) findViewById(R.id.statonory_hr_comment);
        closerDateTxt = (TextView) findViewById(R.id.staionory_closer_date);
        hrTxt = (TextView) findViewById(R.id.hrcommenttxt);
        updateDetails = (Button) findViewById(R.id.editstaionry);
        //deleteBtn = (Button) findViewById(R.id.deleteBtn);
        adapter = new RequestedItemAdapter(list, ViewDocumentDetailsActivity.this);

        requestItemList.setAdapter(adapter);

        if (conn.getConnectivityStatus() > 0) {
            viewStationryDetails(authCode, rid, "2", userId);
        } else {
            conn.showNoInternetAlret();
        }

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(ViewDocumentDetailsActivity.this, AddDocumentActivity.class);
                i.putExtra("Mode", "Edit");
                i.putExtra("mylist", itemBindList);
                i.putExtra("Rid", ridStr);
                i.putExtra("IdealClosureDateText", IdealClosureDateText);
                startActivity(i);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (conn.getConnectivityStatus() > 0) {
            viewStationryDetails(authCode, rid, "2", userId);
        } else {
            conn.showNoInternetAlret();
        }
    }

    //View Stationry Details
    public void viewStationryDetails(final String AuthCode, final String RID, final String ItemCatID, final String userId) {

        final ProgressDialog pDialog = new ProgressDialog(ViewDocumentDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, stationoryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("Login", response);
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));

                    JSONArray requestDetailsArray = jsonObject.getJSONArray("RequestDetail");
                    for (int i = 0; i < requestDetailsArray.length(); i++) {
                        JSONObject object = requestDetailsArray.getJSONObject(i);

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
                            String EmpName = object.getString("EmpName");
                            String AddDateText = object.getString("AddDateText");
                            String AppBy = object.getString("ApprovedBy");
                            String HrComment = object.getString("HrComment");
                            String AppStatusText = object.getString("AppStatusText");
                            IdealClosureDateText = object.getString("IdealClosureDateText");
                            String Visibility = object.getString("Visibility");
                            ridStr = object.getString("RID");

                            closerDateTxt.setText(IdealClosureDateText);
                            empNameTxt.setText(EmpName);
                            requestByTxt.setText(AppBy);
                            requestDateTxt.setText(AddDateText);
                            hrCommentTxt.setText(HrComment);
                            statusTxt.setText(AppStatusText);
                            if (HrComment.equalsIgnoreCase("")) {
                                hrTxt.setVisibility(View.GONE);
                                hrCommentTxt.setVisibility(View.GONE);
                            }

                        }


                    }

                    JSONArray itemdetaislArray = jsonObject.getJSONArray("ItemsDetail");
                    if (list.size() > 0) {
                        list.clear();
                    }
                    for (int j = 0; j < itemdetaislArray.length(); j++) {
                        JSONObject object = itemdetaislArray.getJSONObject(j);

                        String ItemName = object.getString("ItemName");
                        String NoOfItem = object.getString("NoOfItem");
                        //String Quantity = object.getString("Quantity");
                        String Remark = object.getString("Remark");
                        String ItemID = object.getString("ItemID");
                        String chkValue = object.getString("chkValue");


                        list.add(new BookMeaPrevisionModel(ItemName, ItemID, NoOfItem, Remark, chkValue, "0"));


                    }

                    JSONArray itemsBindDataArray = jsonObject.getJSONArray("ItemsBindData");

                    if (itemBindList.size() > 0) {
                        itemBindList.clear();
                    }
                    for (int k = 0; k < itemsBindDataArray.length(); k++) {
                        JSONObject object = itemsBindDataArray.getJSONObject(k);
                        String ItemID = object.getString("ItemID");
                        String ItemName = object.getString("ItemName");
                        String Quantity = object.getString("NoOfItem");
                        String maxQuantity = object.getString("Quantity");
                        String Remark = object.getString("Remark");
                        String chkValue = object.getString("chkValue");

                        itemBindList.add(new AddNewStationoryRequestModel(ItemName, maxQuantity, ItemID, Quantity, Remark));
                    }

                    Log.e("Inner List size in edit", itemBindList.size() + "");
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
                params.put("RID", RID);
                params.put("ItemCatID", ItemCatID);
                params.put("AdminID", userId);

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
        startActivity(new Intent(ViewDocumentDetailsActivity.this, LoginActivity.class));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(ViewDocumentDetailsActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(ViewDocumentDetailsActivity.this,
                "")));
    }

}
