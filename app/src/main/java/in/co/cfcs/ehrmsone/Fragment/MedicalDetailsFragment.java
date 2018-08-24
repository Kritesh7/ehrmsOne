package in.co.cfcs.ehrmsone.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.Model.BloodGroupModel;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MedicalDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MedicalDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MedicalDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public Spinner bloodGroupSpinner;
    public ArrayList<BloodGroupModel> bloodList = new ArrayList<>();
    public ArrayAdapter<BloodGroupModel> adapter;
    public ConnectionDetector conn;
    private OnFragmentInteractionListener mListener;
    public String personalDdlDetailsUrl = SettingConstant.BaseUrl + "AppddlEmployeePersonalData";
    public String medicalDetailsUrl = SettingConstant.BaseUrl + "AppEmployeeMedicalData";
    public String addUrl = SettingConstant.BaseUrl + "AppEmployeeMedicalDataInsUpdt";
    public EditText illnessTxt, allergiesTxt, familyDrNameTxt, familyDrNumberTxt;
    public Button editAddBtn;
    public String bloodGroupIdString = "", BloodGroup = "";
    public String userId = "", authCode = "";
    public boolean flag = true;
    public ProgressDialog pDialog;

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;


    public MedicalDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MedicalDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MedicalDetailsFragment newInstance(String param1, String param2) {
        MedicalDetailsFragment fragment = new MedicalDetailsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_medical_details, container, false);

        String strtext = getArguments().getString("Count");
        Log.e("checking count", strtext + " null");

        mListener.onFragmentInteraction(strtext);

        bloodGroupSpinner = (Spinner) rootView.findViewById(R.id.bloodgroupspinner);
        illnessTxt = (EditText) rootView.findViewById(R.id.serious_illness);
        allergiesTxt = (EditText) rootView.findViewById(R.id.allergies);
        familyDrNameTxt = (EditText) rootView.findViewById(R.id.family_doctor_name);
        familyDrNumberTxt = (EditText) rootView.findViewById(R.id.family_doctor_number);
        editAddBtn = (Button) rootView.findViewById(R.id.editmedicaldetailsBtn);

        conn = new ConnectionDetector(getActivity());
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(getActivity())));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(getActivity())));

        //Blood Group Spinner
        bloodGroupSpinner.getBackground().setColorFilter(getResources().getColor(R.color.status_color), PorterDuff.Mode.SRC_ATOP);

        adapter = new ArrayAdapter<BloodGroupModel>(getActivity(), R.layout.customizespinner,
                bloodList);
        adapter.setDropDownViewResource(R.layout.customizespinner);
        bloodGroupSpinner.setAdapter(adapter);

        if (conn.getConnectivityStatus() > 0) {
            personalDdlDetails();
        } else {
            conn.showNoInternetAlret();
        }
        // disable all widget
        bloodGroupSpinner.setEnabled(false);
        illnessTxt.setEnabled(false);
        allergiesTxt.setEnabled(false);
        familyDrNumberTxt.setEnabled(false);
        familyDrNameTxt.setEnabled(false);

        editAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // enable all widget
                bloodGroupSpinner.setEnabled(true);
                illnessTxt.setEnabled(true);
                allergiesTxt.setEnabled(true);
                familyDrNumberTxt.setEnabled(true);
                familyDrNameTxt.setEnabled(true);

                if (flag) {
                    editAddBtn.setText("Update Medical Details");
                    flag = false;
                } else {
                    //update medical details
                    if (bloodGroupIdString.equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Please select Blood Group", Toast.LENGTH_SHORT).show();
                    } else {
                        if (conn.getConnectivityStatus() > 0) {

                            updateMedicalDetails(userId, bloodGroupIdString, familyDrNameTxt.getText().toString(), familyDrNumberTxt.getText().toString(),
                                    allergiesTxt.getText().toString(), illnessTxt.getText().toString(), authCode);

                        } else {
                            conn.showNoInternetAlret();
                        }
                    }
                }


            }
        });

        bloodGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                bloodGroupIdString = bloodList.get(i).getBloodGroupId();


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        return rootView;
    }

    public void getMedicalDetails(final String AuthCode, final String AdminID) {

        /*final ProgressDialog pDialog = new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
        pDialog.setMessage("Loading...");
        pDialog.show();*/

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, medicalDetailsUrl, new Response.Listener<String>() {
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
                                Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            BloodGroup = jsonObject.getString("BloodGroup");
                            String FamilyDoctorName = jsonObject.getString("FamilyDoctorName");
                            String FamilyDoctorNo = jsonObject.getString("FamilyDoctorNo");
                            String Allergies = jsonObject.getString("Allergies");
                            String Illness = jsonObject.getString("Illness");

                            illnessTxt.setText(Illness);
                            allergiesTxt.setText(Allergies);
                            familyDrNameTxt.setText(FamilyDoctorName);
                            familyDrNumberTxt.setText(FamilyDoctorNo);


                            // select by default spinner
                            for (int k = 0; k < bloodList.size(); k++) {
                                if (bloodList.get(k).getBloodGroupId().equalsIgnoreCase(BloodGroup)) {
                                    bloodGroupSpinner.setSelection(k);
                                }
                            }

                            if (FamilyDoctorName.equalsIgnoreCase("")) {
                                editAddBtn.setText("Add Medical Details");
                            } else {
                                editAddBtn.setText("Edit Medical Details");
                            }

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

                Log.e("Parms", params.toString());
                return params;
            }

        };
        historyInquiry.setRetryPolicy(new DefaultRetryPolicy(SettingConstant.Retry_Time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(historyInquiry, "Login");

    }

    //bind all spiiner data
    public void personalDdlDetails() {


        pDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, personalDdlDetailsUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.e("Login", response);
                    JSONObject jsonObject = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));

                    //bind material List
                    if (bloodList.size() > 0) {
                        bloodList.clear();
                    }
                    bloodList.add(new BloodGroupModel("", "Please Select Blood Group"));
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
                        JSONArray bloodGroupObj = jsonObject.getJSONArray("BloodGroupMaster");
                        for (int i = 0; i < bloodGroupObj.length(); i++) {
                            JSONObject object = bloodGroupObj.getJSONObject(i);

                            String BloodGroupID = object.getString("BloodGroupID");
                            String BloodGroupName = object.getString("BloodGroupName");

                            bloodList.add(new BloodGroupModel(BloodGroupID, BloodGroupName));

                        }

                        //Show all Medical Details Data
                        getMedicalDetails(authCode, userId);

                    }


                    adapter.notifyDataSetChanged();


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
        });
        historyInquiry.setRetryPolicy(new DefaultRetryPolicy(SettingConstant.Retry_Time,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(historyInquiry, "Login");

    }

    // Update Medical Details
    public void updateMedicalDetails(final String AdminID, final String BloodGroup, final String FamilyDoctorName,
                                     final String FamilyDoctorNo, final String Allergies, final String Illness
            , final String AuthCode) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
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
                            Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                        } else if (LoginStatus.equalsIgnoreCase("success")) {
                            getMedicalDetails(AuthCode, AdminID);

                            // disable all widget
                            bloodGroupSpinner.setEnabled(false);
                            illnessTxt.setEnabled(false);
                            allergiesTxt.setEnabled(false);
                            familyDrNumberTxt.setEnabled(false);
                            familyDrNameTxt.setEnabled(false);

                            flag = true;
                            editAddBtn.setText("Edit Medical Details");


                        } else {

                            Toast.makeText(getContext(), msgstatus, Toast.LENGTH_LONG).show();
                        }
                    }


//                    if (jsonObject.has("status"))
//                    {
//                        String status = jsonObject.getString("status");
//
//                        if (status.equalsIgnoreCase("success"))
//                        {
//                           // String message =
//
//                            getMedicalDetails(AuthCode,AdminID);
//
//                            // disable all widget
//                            bloodGroupSpinner.setEnabled(false);
//                            illnessTxt.setEnabled(false);
//                            allergiesTxt.setEnabled(false);
//                            familyDrNumberTxt.setEnabled(false);
//                            familyDrNameTxt.setEnabled(false);
//
//                            flag = true;
//                            editAddBtn.setText("Edit Medical Details");
//
//
//
//                            Toast.makeText(getActivity(), "Medical Details Update successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    }


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
                params.put("AdminID", AdminID);
                params.put("BloodGroup", BloodGroup);
                params.put("FamilyDoctorName", FamilyDoctorName);
                params.put("FamilyDoctorNo", FamilyDoctorNo);
                params.put("Allergies", Allergies);
                params.put("Illness", Illness);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
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

    private void Logout() {


        getActivity().finishAffinity();
        startActivity(new Intent(getContext(), LoginActivity.class));

//        Intent ik = new Intent(ManagerRequestToApproveActivity.this, LoginActivity.class);
//        startActivity(ik);


        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setStatus(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAdminId(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setAuthCode(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmailId(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setUserName(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpId(getContext(),
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setEmpPhoto(getContext(),
                "")));

        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setDesignation(getContext(),
                "")));
        UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.setCompanyLogo(getContext(),
                "")));


    }
}
