package com.example.covid_tracker_user.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {

    public static final String active = "active";
    public static final String confirmed = "confirmed";
    public static final String recovered = "recovered";
    public static final String deaths = "deaths";
    public static final String deceased = "deceased";
    public static final String dailyconfirmed = "dailyconfirmed";
    public static final String dailydeceased = "dailydeceased";
    public static final String dailyrecovered = "dailyrecovered";
    public static final String date = "date";
    public static final String totalconfirmed = "totalconfirmed";
    public static final String totaldeceased = "totaldeceased";
    public static final String totalrecovered = "totalrecovered";
    public static final String totalactive = "totalactive";
    public static final String lastupdatedtime = "lastupdatedtime";
    public static final String userName = "userName";
    public static final String userAge = "userAge";
    public static final String userGender = "userGender";
    public static final String userPhone = "userPhone";
    public static final String userBlood = "userBlood";

    public static Map<String, Object> getStateDetails(List<Map<String, Object>> statewise) {
        Map<String, Object> stateDetail = new HashMap<>();
        for (int i = 0; i < statewise.size(); i++) {
            Map<String, Object> stateDetails = statewise.get(i);
            if (stateDetails.containsValue("Tamil Nadu")) {
                stateDetail.put(active, stateDetails.get(active));
                stateDetail.put(confirmed, stateDetails.get(confirmed));
                stateDetail.put(recovered, stateDetails.get(recovered));
                stateDetail.put(deaths, stateDetails.get(deaths));
                stateDetail.put(lastupdatedtime, stateDetails.get(lastupdatedtime));
            }
        }
        return stateDetail;
    }

    public static Map<String, Object> getCountryDetails(List<Map<String, Object>> cases_series) {
        Map<String, Object> stateDetail = new HashMap<>();
        stateDetail = cases_series.get(cases_series.size() - 1);
        return stateDetail;
    }

    //Hides Keyboard
    public static void hideKeyboard(Activity activity){
        try {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Checks for Internet Connectivity
    public static boolean alerter(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            Boolean flag = !(activeNetworkInfo != null && activeNetworkInfo.isConnected());
            if(flag)
                Toast.makeText(context, "Please check your internet connection!", Toast.LENGTH_SHORT).show();
            return flag;
        }
        return false;
    }

}
