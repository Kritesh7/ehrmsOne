package in.co.cfcs.ehrmsone.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import in.co.cfcs.ehrmsone.Adapter.StatinaryRequestAdapter;
import in.co.cfcs.ehrmsone.Main.AddNewStationaryRequestActivity;
import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.Model.BookMeaPrevisionModel;
import in.co.cfcs.ehrmsone.Model.StationaryRequestModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StationaryRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationaryRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationaryRequestFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecyclerView stainoryRecy;
    public StatinaryRequestAdapter adapter;
    public ArrayList<StationaryRequestModel> list = new ArrayList<>();
    public FloatingActionButton fab;
    public String stationoryUrl = SettingConstant.BaseUrl + "AppEmployeeStationaryRequestList";
    public ConnectionDetector conn;
    public String userId = "", authCode = "";
    public ArrayList<BookMeaPrevisionModel> itemBindList = new ArrayList<>();
    public TextView noCust;
    public String strtext = "";

    private OnFragmentInteractionListener mListener;

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;

    public StationaryRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationaryRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationaryRequestFragment newInstance(String param1, String param2) {
        StationaryRequestFragment fragment = new StationaryRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_stationary_request, container, false);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            strtext = bundle.getString("Count");
        } else {
            strtext = getArguments().getString("Count");
        }
        Log.e("checking count", strtext + " null");


        mListener.onFragmentInteraction(strtext);

        stainoryRecy = (RecyclerView) rootView.findViewById(R.id.stationary_recycler);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        noCust = (TextView) rootView.findViewById(R.id.no_record_txt);

        conn = new ConnectionDetector(getActivity());
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(getActivity())));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(getActivity())));

        adapter = new StatinaryRequestAdapter(getActivity(), list, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        stainoryRecy.setLayoutManager(mLayoutManager);
        stainoryRecy.setItemAnimator(new DefaultItemAnimator());
        stainoryRecy.setAdapter(adapter);

        stainoryRecy.getRecycledViewPool().setMaxRecycledViews(0, 0);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(), AddNewStationaryRequestActivity.class);
                i.putExtra("Mode", "Add");
                i.putExtra("mylist", itemBindList);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (conn.getConnectivityStatus() > 0) {

            stationryData(authCode, userId, "0", "1");

        } else {
            conn.showNoInternetAlret();
        }
    }


    //Staionry Data
    public void stationryData(final String AuthCode, final String AdminID, final String AppStatus, final String ItemCatID) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
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
                                Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            String EmployeeName = jsonObject.getString("EmployeeName");
                            String ZoneName = jsonObject.getString("ZoneName");
                            String Quantity = jsonObject.getString("Items");
                            String requestDate = jsonObject.getString("AddDateText");
                            String IdealClosureDateText = jsonObject.getString("IdealClosureDateText");
                            String followDate = jsonObject.getString("AppDateText");
                            String AppStatusText = jsonObject.getString("AppStatusText");
                            String RID = jsonObject.getString("RID");
                            String ItemCatID = jsonObject.getString("ItemCatID");
                            String AppStatus = jsonObject.getString("AppStatus");


                            list.add(new StationaryRequestModel(EmployeeName, ZoneName, Quantity, requestDate, IdealClosureDateText
                                    , followDate, AppStatusText, RID, ItemCatID, AppStatus));


                        }

                    }

                    if (list.size() == 0) {
                        noCust.setVisibility(View.VISIBLE);
                        stainoryRecy.setVisibility(View.GONE);
                    } else {
                        noCust.setVisibility(View.GONE);
                        stainoryRecy.setVisibility(View.VISIBLE);
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
                    Toast.makeText(getActivity(),
                            "Time Out Server Not Respond",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof AuthFailureError) {
                    //TODO
                } else if (error instanceof ServerError) {
                    //TODO

                    Toast.makeText(getActivity(),
                            "Server Error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof NetworkError) {
                    //TODO
                    Toast.makeText(getActivity(),
                            "Network Error",
                            Toast.LENGTH_LONG).show();
                } else if (error instanceof ParseError) {
                    //TODO
                    Toast.makeText(getActivity(),
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
                params.put("AppStatus", AppStatus);
                params.put("ItemCatID", ItemCatID);


                Log.e("Parms", params.toString());
                return params;
            }

        };
        historyInquiry.setRetryPolicy(new DefaultRetryPolicy(SettingConstant.Retry_Time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(historyInquiry, "Login");

    }

    private void Logout() {

        getActivity().finishAffinity();
        startActivity(new Intent(getActivity(), LoginActivity.class));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(getActivity(),
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(getActivity(),
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(getActivity(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(getActivity(),
                "")));


//
//        Intent intent = new Intent(getContext(), LoginActivity.class);
//        startActivity(intent);


    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String count);
    }
}
