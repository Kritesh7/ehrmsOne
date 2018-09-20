package in.co.cfcs.ehrmsone.Main;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import in.co.cfcs.ehrmsone.Adapter.AddStationoryAndDocumentRequestNewAdapter;
import in.co.cfcs.ehrmsone.Interface.AddItemInterface;
import in.co.cfcs.ehrmsone.Model.AddNewStationoryRequestModel;
import in.co.cfcs.ehrmsone.Model.SendListModel;
import in.co.cfcs.ehrmsone.Model.getQuantAndRemarkModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

public class AddNewStationaryRequestActivity extends AppCompatActivity implements AddItemInterface {

    public TextView titleTxt;
    //   public BookMeaPrevisonAdapter adapter;
    public AddStationoryAndDocumentRequestNewAdapter adapter;
    //  public ArrayList<BookMeaPrevisionModel> list = new ArrayList<>();
    public ArrayList<AddNewStationoryRequestModel> list = new ArrayList<>();
    public ArrayList<getQuantAndRemarkModel> innerlist = new ArrayList<>();
    // public ListView listView;
    public RecyclerView addStaRecy;
    public String stationoryUrl = SettingConstant.BaseUrl + "AppEmployeeStationaryItemDetail";
    public String addUrl = SettingConstant.BaseUrl + "AppEmployeeStationaryRequestInsUpdt";
    public ConnectionDetector conn;
    public String authCode = "", modeString = "", editList = "", userId = "";
    public ArrayList<AddNewStationoryRequestModel> myList = new ArrayList<>();
    public Button addBtn;
    public ArrayList<SendListModel> sendListInner = new ArrayList<>();
    public ArrayList<SendListModel> emptyList = new ArrayList<>();
    public AddItemInterface itemInterface;
    ArrayList<String> secondQuant = new ArrayList<String>();
    public LinearLayout closerDateBtn;
    public TextView closerDateTxt;
    private int yy, mm, dd;
    private int mYear, mMonth, mDay, mHour, mMinute;
    public String rIdStr = "", IdealClosureDateText = "";

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_stationary_request);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_color));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.staionaory_tollbar);
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

                onBackPressed();

            }
        });

        titleTxt.setText("Add New Stationary Request");

        Intent intent = getIntent();
        if (intent != null) {
            modeString = intent.getStringExtra("Mode");
            rIdStr = intent.getStringExtra("Rid");
            IdealClosureDateText = intent.getStringExtra("IdealClosureDateText");
            myList = (ArrayList<AddNewStationoryRequestModel>) getIntent().getSerializableExtra("mylist");

        }

        conn = new ConnectionDetector(AddNewStationaryRequestActivity.this);
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(AddNewStationaryRequestActivity.this)));
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(AddNewStationaryRequestActivity.this)));

        //listView = (ListView)findViewById(R.id.listview);
        addStaRecy = (RecyclerView) findViewById(R.id.stationory_recycler);
        addBtn = (Button) findViewById(R.id.newrequestbtn);
        closerDateBtn = (LinearLayout) findViewById(R.id.closerdatebtn);
        closerDateTxt = (TextView) findViewById(R.id.closerdatetxt);

        //closer Date Picker
        closerDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewStationaryRequestActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                yy = year;
                                mm = monthOfYear;
                                dd = dayOfMonth;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.MONTH, monthOfYear);
                                String sdf = new SimpleDateFormat("LLL", Locale.getDefault()).format(calendar.getTime());
                                sdf = new DateFormatSymbols().getShortMonths()[monthOfYear];

                                Log.e("checking,............", sdf + " null");
                                closerDateTxt.setText(dayOfMonth + "-" + sdf + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                if(Build.VERSION.SDK_INT < 23){
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                }else {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                }
            }
        });

        closerDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewStationaryRequestActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                yy = year;
                                mm = monthOfYear;
                                dd = dayOfMonth;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.MONTH, monthOfYear);
                                String sdf = new SimpleDateFormat("LLL", Locale.getDefault()).format(calendar.getTime());
                                sdf = new DateFormatSymbols().getShortMonths()[monthOfYear];

                                Log.e("checking,............", sdf + " null");
                                closerDateTxt.setText(dayOfMonth + "-" + sdf + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                if(Build.VERSION.SDK_INT < 23){
                    datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());

                }else {
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                }

            }
        });


        //Edit Mode;
        if (modeString.equalsIgnoreCase("Edit")) {
            closerDateTxt.setText(IdealClosureDateText);
            titleTxt.setText("Update Stationary Request");
            addBtn.setText("Update Stationary Request");

            adapter = new AddStationoryAndDocumentRequestNewAdapter(AddNewStationaryRequestActivity.this, myList);

        } else {
            adapter = new AddStationoryAndDocumentRequestNewAdapter(AddNewStationaryRequestActivity.this, list);
        }


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddNewStationaryRequestActivity.this);
        addStaRecy.setLayoutManager(mLayoutManager);
        addStaRecy.setItemAnimator(new DefaultItemAnimator());
        addStaRecy.setAdapter(adapter);

        addStaRecy.getRecycledViewPool().setMaxRecycledViews(0, 0);


        if (conn.getConnectivityStatus() > 0) {

            stationryData(authCode, "1", userId);

        } else {
            conn.showNoInternetAlret();
        }

        //add new data
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Log.e("checking the my list", " checking list size is: " + adapter.getListData().size());

                if (innerlist.size() > 0) {
                    innerlist.clear();
                }

                for (int i = 0; i < list.size(); i++) {

                    adapter.getListData().get(i).getQuantity();

                    if (!adapter.getListData().get(i).getQuantity().equalsIgnoreCase("")) {

                        Log.e("checking the main list size", list.size() + "");

                        Log.e("checking the item id", adapter.getListData().get(i).getItemId());
                        Log.e("checking the item name", adapter.getListData().get(i).getItemName());
                        Log.e("checking the remark", adapter.getListData().get(i).getRemark());
                        Log.e("checking the quantity", adapter.getListData().get(i).getQuantity());

                        innerlist.add(new getQuantAndRemarkModel(adapter.getListData().get(i).getItemName(),
                                adapter.getListData().get(i).getQuantity(), adapter.getListData().get(i).getRemark(),
                                adapter.getListData().get(i).getItemId()));

                    }
                }

                Log.e("checking the inner list size", "Inner Log......" + innerlist.size());

                //old function
               /* //Making json format
                String splitString  = String.valueOf(adapter.getSelectedString());
                String id = String.valueOf(adapter.getSelectedId());
                String quanty = String.valueOf(adapter.getSelectedQuan());
                String remark = String.valueOf(adapter.getSelectedRemark());*/

               /* //remove first and last character
                String removeHip = splitString.substring(1, splitString.length() - 1);
                String removeId = id.substring(1, id.length() - 1);
                String removeQuant = quanty.substring(1, quanty.length() - 1);
                String removeRemark = remark.substring(1, remark.length() - 1);
*/
             /*  // str.replaceAll("\[|\]", "");
                String[] separated = removeHip.split(",");
                String[] separatedId = removeId.split(",");
                String[] separatedquant = removeQuant.split(",");
                String[] separatedRemark = removeRemark.split(",");*/

                //prevent save duplicate data in array list
               /* Set<AddNewStationoryRequestModel> hs = new LinkedHashSet<AddNewStationoryRequestModel>(innerlist);
                innerlist.clear();

                Log.e("checking the clear list size is", innerlist.size() + "");
                innerlist.addAll(hs);*/


                //Make json
                JSONArray mainArray = new JSONArray();
                JSONObject object = new JSONObject();
                try {


                    for (int i = 0; i < innerlist.size(); i++) {

                        JSONObject filterJson = new JSONObject();
                        filterJson.put("ItemID", innerlist.get(i).getItemId());
                        filterJson.put("ItemName", innerlist.get(i).getItemName());
                        filterJson.put("Qty", innerlist.get(i).getItemQuantity());
                        filterJson.put("Remark", innerlist.get(i).getItemRemark());

                        mainArray.put(filterJson);
                    }

                    object.put("members", mainArray);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //Log.e("checking the json is",mainArray.toString());

                //Validation condtion and add data
                if (closerDateTxt.getText().toString().equalsIgnoreCase("")) {
                    // closerDateTxt.setError("Please select closer date");
                    Toast.makeText(AddNewStationaryRequestActivity.this, "Please select closer date", Toast.LENGTH_SHORT).show();
                } else if (innerlist.size() == 0) {
                    Toast.makeText(AddNewStationaryRequestActivity.this, "At least one item required", Toast.LENGTH_SHORT).show();
                } else {

                    if (conn.getConnectivityStatus() > 0) {

                        if (modeString.equalsIgnoreCase("Edit")) {
                            addStaionoryItem(userId, rIdStr, "1", closerDateTxt.getText().toString(), authCode, object);

                        } else {
                            addStaionoryItem(userId, "", "1", closerDateTxt.getText().toString(), authCode, object);

                        }
                    } else {
                        conn.showNoInternetAlret();
                    }
                }


            }
        });

    }


    //Stationary Item Data
    public void stationryData(final String AuthCode, final String ItemCatID, final String userId) {

        final ProgressDialog pDialog = new ProgressDialog(AddNewStationaryRequestActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, stationoryUrl, new Response.Listener<String>() {
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

                            String ItemID = jsonObject.getString("ItemID");
                            String ItemName = jsonObject.getString("ItemName");
                            String MaxQuantity = jsonObject.getString("MaxQuantity");

                            //  list.add(new BookMeaPrevisionModel(ItemName,ItemID,MaxQuantity,"","false","0"));

                            list.add(new AddNewStationoryRequestModel(ItemName, MaxQuantity, ItemID, "", ""));
                        }

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

    //add new staionry Data
    public void addStaionoryItem(final String AdminID, final String RID, final String ItemCatID, final String IdealCosureDate,
                                 final String AuthCode, final JSONObject mainArray) {

        final ProgressDialog pDialog = new ProgressDialog(AddNewStationaryRequestActivity.this, R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, addUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("Login", response);
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    if (jsonObject.has("status")) {
                        LoginStatus = jsonObject.getString("status");
                        msgstatus = jsonObject.getString("MsgNotification");
                        if (LoginStatus.equals(invalid)) {
                            Logout();
                            Toast.makeText(getBaseContext(), msgstatus, Toast.LENGTH_LONG).show();
                        } else if (LoginStatus.equalsIgnoreCase("success")) {
                            onBackPressed();
                        } else {
                            Toast.makeText(getBaseContext(), msgstatus, Toast.LENGTH_LONG).show();

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
                params.put("AdminID", AdminID);
                params.put("RID", RID);
                params.put("ItemCatID", ItemCatID);
                params.put("IdealCosureDate", IdealCosureDate);
                params.put("ItemDetailJson", mainArray.toString());
                params.put("AuthCode", AuthCode);

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
    public void getAllItem(ArrayList<String> sendList) {

        // sendListInner = sendList;

        secondQuant = sendList;
        Log.e("checking the size", secondQuant.size() + "");
    }

    private void Logout() {


        finishAffinity();
        startActivity(new Intent(AddNewStationaryRequestActivity.this, LoginActivity.class));

//        Intent ik = new Intent(ManagerRequestToApproveActivity.this, LoginActivity.class);
//        startActivity(ik);


        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(AddNewStationaryRequestActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(AddNewStationaryRequestActivity.this,
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(AddNewStationaryRequestActivity.this,
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(AddNewStationaryRequestActivity.this,
                "")));

//        Intent intent = new Intent(NewAddLeaveMangementActivity.this, LoginActivity.class);
//        startActivity(intent);
//        finish();


    }
}
