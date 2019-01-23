package in.co.cfcs.ehrmsone.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import in.co.cfcs.ehrmsone.Main.LoginActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.AttendanceRequest;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerEmployeeDataDashboardActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerFilterActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerLeaveMangementActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerProceedRequestActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerProceedTraningListActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerReportDashboardActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerRequestToApproveActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerRequestTraningListActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ManagerSkillsAndCareerDashboard;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ProceedLeaveRequestListActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.ProceedShortLeaveListActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.RequestToApproveLeaveActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.RequestToApproveLeaveCancelActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.RequestToApproveShortLeaveCancelationActivity;
import in.co.cfcs.ehrmsone.Manager.ManagerActivity.RequestToApprovedShortLeaveActivity;
import in.co.cfcs.ehrmsone.R;
import in.co.cfcs.ehrmsone.Source.AppController;
import in.co.cfcs.ehrmsone.Source.ConnectionDetector;
import in.co.cfcs.ehrmsone.Source.SettingConstant;
import in.co.cfcs.ehrmsone.Source.SharedPrefs;
import in.co.cfcs.ehrmsone.Source.UtilsMethods;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManagerDashBoardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManagerDashBoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagerDashBoardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public ConnectionDetector conn;
    public String userId = "", authCode = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
//    public LinearLayout requaestApprovedLay, proceedLay, sixthTilesLay, thirdTilesLay, fourthLay, fivthLay;

    public LinearLayout manag_leave, manag_leave_cancel, manag_short_leave, manag_short_leave_cancel, manag_traning_request, manag_attendance_request,
            manag_team_profile, manag_administrative, manag_medical_insurance, manag_address, manag_emergency_address, manag_skill_career,
            manag_proceed_leave_request, manag_proceed_short_leave_request, manag_proceed_traning_request, manag_team_summary, manag_team_week_off,
            manag_team_leave_history, manag_team_short_leave_history, manag_attendance_report, manag_aatendance_log_report, manag_team_average_report,
            manag_asset_detail;

    public TextView leave_noti, leave_cancel_noti, short_leave_noti, short_leave_cancel_noti, traning_request_noti, attendance_request_noti,
            proceed_leave_noti, proceed_short_noti, proceed_traning_request_noti;

    private OnFragmentInteractionListener mListener;

    String LoginStatus;
    String invalid = "loginfailed";
    String msgstatus;
    String strtext ="0";

    public String countUrl = SettingConstant.BaseUrl + "AppManagerRequestToApproveDashBoard";
    public String countUrl1 = SettingConstant.BaseUrl + "AppManagerProceededRequestDashBoard";

    public ManagerDashBoardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagerDashBoardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagerDashBoardFragment newInstance(String param1, String param2) {
        ManagerDashBoardFragment fragment = new ManagerDashBoardFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manager_dash_board, container, false);

        if (getArguments() != null) {
            strtext = getArguments().getString("Count");
        }
        if(strtext == null){

            strtext ="0";
        }

        mListener.onFragmentInteraction(strtext);

        manag_leave = (LinearLayout) rootView.findViewById(R.id.manag_leave);
        manag_leave_cancel = (LinearLayout) rootView.findViewById(R.id.manag_leave_cancel);
        manag_short_leave = (LinearLayout) rootView.findViewById(R.id.manag_short_leave);
        manag_short_leave_cancel = (LinearLayout) rootView.findViewById(R.id.manag_short_leave_cancel);
        manag_traning_request = (LinearLayout) rootView.findViewById(R.id.manag_traning_request);
        manag_attendance_request = (LinearLayout) rootView.findViewById(R.id.manag_attendance_request);
        manag_team_profile = (LinearLayout) rootView.findViewById(R.id.manag_team_profile);
        manag_administrative = (LinearLayout) rootView.findViewById(R.id.manag_administrative);
        manag_medical_insurance = (LinearLayout) rootView.findViewById(R.id.manag_medical_insurance);
        manag_address = (LinearLayout) rootView.findViewById(R.id.manag_address);
        manag_emergency_address = (LinearLayout) rootView.findViewById(R.id.manag_emergency_address);
        manag_skill_career = (LinearLayout) rootView.findViewById(R.id.manag_skill_career);
        manag_proceed_leave_request = (LinearLayout) rootView.findViewById(R.id.manag_proceed_leave_request);
        manag_proceed_short_leave_request = (LinearLayout) rootView.findViewById(R.id.manag_proceed_short_leave_request);
        manag_proceed_traning_request = (LinearLayout) rootView.findViewById(R.id.manag_proceed_traning_request);
        manag_team_summary = (LinearLayout) rootView.findViewById(R.id.manag_team_summary);
        manag_team_week_off = (LinearLayout) rootView.findViewById(R.id.manag_team_week_off);
        manag_team_leave_history = (LinearLayout) rootView.findViewById(R.id.manag_team_leave_history);
        manag_team_short_leave_history = (LinearLayout) rootView.findViewById(R.id.manag_team_short_leave_history);
        manag_attendance_report = (LinearLayout) rootView.findViewById(R.id.manag_attendance_report);
        manag_aatendance_log_report = (LinearLayout) rootView.findViewById(R.id.manag_aatendance_log_report);
        manag_team_average_report = (LinearLayout) rootView.findViewById(R.id.manag_team_average_report);
        manag_asset_detail = (LinearLayout) rootView.findViewById(R.id.manag_asset_detail);


        leave_noti = (TextView) rootView.findViewById(R.id.leave_noti);
        leave_cancel_noti = (TextView) rootView.findViewById(R.id.leave_cancel_noti);
        short_leave_noti = (TextView) rootView.findViewById(R.id.short_leave_noti);
        short_leave_cancel_noti = (TextView) rootView.findViewById(R.id.short_leave_cancel_noti);
        traning_request_noti = (TextView) rootView.findViewById(R.id.traning_request_noti);
        attendance_request_noti = (TextView) rootView.findViewById(R.id.attendance_request_noti);
        proceed_leave_noti = (TextView) rootView.findViewById(R.id.proceed_leave_noti);
        proceed_short_noti = (TextView) rootView.findViewById(R.id.proceed_short_noti);
        proceed_traning_request_noti = (TextView) rootView.findViewById(R.id.proceed_traning_request_noti);

        leave_noti.setVisibility(View.GONE);
        leave_cancel_noti.setVisibility(View.GONE);
        short_leave_noti.setVisibility(View.GONE);
        short_leave_cancel_noti.setVisibility(View.GONE);
        traning_request_noti.setVisibility(View.GONE);
        attendance_request_noti.setVisibility(View.GONE);
        proceed_leave_noti.setVisibility(View.GONE);
        proceed_short_noti.setVisibility(View.GONE);
        proceed_traning_request_noti.setVisibility(View.GONE);


        conn = new ConnectionDetector(getActivity());
        userId = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAdminId(getActivity())));
        authCode = UtilsMethods.getBlankIfStringNull(String.valueOf(SharedPrefs.getAuthCode(getActivity())));


        manag_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), RequestToApproveLeaveActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_leave_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), RequestToApproveLeaveCancelActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });


        manag_short_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), RequestToApprovedShortLeaveActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_short_leave_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), RequestToApproveShortLeaveCancelationActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });


        manag_traning_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerRequestTraningListActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_attendance_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), AttendanceRequest.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Personal Information");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

            }
        });

        manag_administrative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Administrative Information");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_medical_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Medical And Insurance");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Address And Contact");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_emergency_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Emergency Contact Address");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_skill_career.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerSkillsAndCareerDashboard.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_proceed_leave_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ProceedLeaveRequestListActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_proceed_short_leave_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getContext(), ProceedShortLeaveListActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_proceed_traning_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerProceedTraningListActivity.class);
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Leave Summery");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_week_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Weak Off");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_leave_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Leave History");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_short_leave_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Short Leave History");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_attendance_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Attendance Report");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

            }
        });

        manag_aatendance_log_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Attendance Basic Log");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_team_average_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Team Average Report");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });

        manag_asset_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
                ik.putExtra("CheckingTheActivity", "Asset Details");
                startActivity(ik);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);

            }
        });


//        requaestApprovedLay = (LinearLayout) rootView.findViewById(R.id.requesttoapprovetxt);
//        proceedLay = (LinearLayout) rootView.findViewById(R.id.proceedlay);
//        sixthTilesLay = (LinearLayout) rootView.findViewById(R.id.sixthtiles);
//        thirdTilesLay = (LinearLayout) rootView.findViewById(R.id.thirdTiles);
//        fourthLay = (LinearLayout) rootView.findViewById(R.id.fourthtile);
//        fivthLay = (LinearLayout) rootView.findViewById(R.id.fivthtiles);
//
//        requaestApprovedLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent ik = new Intent(getActivity(), ManagerRequestToApproveActivity.class);
//                ik.putExtra("BackValue", "1");
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//            }
//        });
//
//        proceedLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent ik = new Intent(getActivity(), ManagerProceedRequestActivity.class);
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//
//            }
//        });
//
//        sixthTilesLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent ik = new Intent(getActivity(), ManagerFilterActivity.class);
//                ik.putExtra("CheckingTheActivity", "Asset Details");
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//            }
//        });
//
//        thirdTilesLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                Intent ik = new Intent(getActivity(), ManagerLeaveMangementActivity.class);
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//            }
//        });
//
//        fourthLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent ik = new Intent(getActivity(), ManagerReportDashboardActivity.class);
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//            }
//        });
//
//        fivthLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent ik = new Intent(getActivity(), ManagerEmployeeDataDashboardActivity.class);
//                startActivity(ik);
//                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//
//            }
//        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
   /* public void onButtonPressed(Uri uri) {
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

    @Override
    public void onResume() {
        super.onResume();
        //get count of dashboard
        if (conn.getConnectivityStatus() > 0) {
            getCount(authCode, userId);
            getCountProceedRequest(authCode, userId);
        } else {
            conn.showNoInternetAlret();
        }
    }


    //show dashbaord count api
    public void getCount(final String AuthCode, final String AdminID) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
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
                                Toast.makeText(getActivity(), msgstatus, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), msgstatus, Toast.LENGTH_LONG).show();
                            }
                        } else {

                            String LeaveCount = jsonObject.getString("LeaveCount");
                            String CancelLeaveCount = jsonObject.getString("CancelLeaveCount");
                            String ShortLeaveCount = jsonObject.getString("ShortLeaveCount");
                            String ShortCancelLeaveCount = jsonObject.getString("ShortCancelLeaveCount");
                            String TrainingCount = jsonObject.getString("TrainingCount");
                            String AttendanceRequestCount = jsonObject.getString("AttendanceRequestCount");

//                            leaveCountTxt.setText("(" + LeaveCount + ")");
//                            leaveCancelCpuntTxt.setText("(" + CancelLeaveCount + ")");
//                            shortLeaveCountTxt.setText("(" + ShortLeaveCount + ")");
//                            shortLeaveCancelCountTxt.setText("(" + ShortCancelLeaveCount + ")");
//                            traningCountTxt.setText("(" + TrainingCount + ")");
//                            attendanceApprove.setText("(" + AttendanceRequestCount + ")");

                            BadgeNotificationShowRequestToApprove(LeaveCount, CancelLeaveCount, ShortLeaveCount, ShortCancelLeaveCount, TrainingCount, AttendanceRequestCount);

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

    private void BadgeNotificationShowRequestToApprove(@NonNull String leaveCount, String cancelLeaveCount, String shortLeaveCount, String shortCancelLeaveCount, String trainingCount, String attendanceRequestCount) {

        if (leaveCount.compareToIgnoreCase("0") != 0) {
            leave_noti.setVisibility(View.VISIBLE);
            leave_noti.setText(leaveCount);
        } else {
            leave_noti.setVisibility(View.GONE);
        }
        if (cancelLeaveCount.compareToIgnoreCase("0") != 0) {
            leave_cancel_noti.setVisibility(View.VISIBLE);
            leave_cancel_noti.setText(cancelLeaveCount);
        } else {
            leave_cancel_noti.setVisibility(View.GONE);
        }
        if (shortLeaveCount.compareToIgnoreCase("0") != 0) {
            short_leave_noti.setVisibility(View.VISIBLE);
            short_leave_noti.setText(shortLeaveCount);
        } else {
            short_leave_noti.setVisibility(View.GONE);
        }
        if (shortCancelLeaveCount.compareToIgnoreCase("0") != 0) {
            short_leave_cancel_noti.setVisibility(View.VISIBLE);
            short_leave_cancel_noti.setText(shortCancelLeaveCount);
        } else {
            short_leave_cancel_noti.setVisibility(View.GONE);
        }
        if (trainingCount.compareToIgnoreCase("0") != 0) {
            traning_request_noti.setVisibility(View.VISIBLE);
            traning_request_noti.setText(trainingCount);
        } else {
            traning_request_noti.setVisibility(View.GONE);
        }
        if (attendanceRequestCount.compareToIgnoreCase("0") != 0) {
            attendance_request_noti.setVisibility(View.VISIBLE);
            attendance_request_noti.setText(attendanceRequestCount);
        } else {
            attendance_request_noti.setVisibility(View.GONE);
        }

    }

    private void getCountProceedRequest(String authCode, String userId) {

        final ProgressDialog pDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading...");
        pDialog.show();

        StringRequest historyInquiry = new StringRequest(
                Request.Method.POST, countUrl1, new Response.Listener<String>() {
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
                                Toast.makeText(getActivity(), msgstatus, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), msgstatus, Toast.LENGTH_LONG).show();
                            }
                        } else {

                            String LeaveCount = jsonObject.getString("LeaveCount");
                            String ShortLeaveCount = jsonObject.getString("ShortLeaveCount");
                            String TrainingCount = jsonObject.getString("TrainingCount");

//
//                            leavecountTxt.setText("("+LeaveCount+")");
//                            shortLeaveCountTxt.setText("("+ShortLeaveCount+")");
//                            tranoingCountTxt.setText("("+TrainingCount+")");

                            BadgeNotificationShowProceededRequest(LeaveCount, ShortLeaveCount, TrainingCount);


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
                params.put("AuthCode", authCode);
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

    private void BadgeNotificationShowProceededRequest(String leaveCount, String shortLeaveCount, String trainingCount) {

        if (leaveCount.compareToIgnoreCase("0") != 0) {

            proceed_leave_noti.setVisibility(View.VISIBLE);
            proceed_leave_noti.setText(leaveCount);

        } else {
            proceed_leave_noti.setVisibility(View.GONE);
        }
        if (shortLeaveCount.compareToIgnoreCase("0") != 0) {
            proceed_short_noti.setVisibility(View.VISIBLE);
            proceed_short_noti.setText(shortLeaveCount);
        } else {
            proceed_short_noti.setVisibility(View.GONE);
        }
        if (trainingCount.compareToIgnoreCase("0") != 0) {
            proceed_traning_request_noti.setVisibility(View.VISIBLE);
            proceed_traning_request_noti.setText(trainingCount);
        } else {
            proceed_traning_request_noti.setVisibility(View.GONE);

        }

    }

    private void Logout() {

        getActivity().finishAffinity();
        startActivity(new Intent(getContext(), LoginActivity.class));

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
