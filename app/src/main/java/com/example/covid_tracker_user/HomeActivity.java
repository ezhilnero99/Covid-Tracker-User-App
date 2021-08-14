package com.example.covid_tracker_user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covid_tracker_user.services.MainRepository;
import com.example.covid_tracker_user.utils.CommonUtils;
import com.example.covid_tracker_user.utils.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "home_tag";
    ImageView userAddIV, languageIV;
    TextView dateST_TV, dateCO_TV;
    TextView activeST_TV, activeCO_TV, activeDT_TV;
    TextView recoverST_TV, recoverCO_TV, recoverDT_TV;
    TextView overallST_TV, overallCO_TV, overallDT_TV;
    TextView activenewTV, recoverednewTV, deathnewTV;
    TextView deathST_TV, deathCO_TV, deathDT_TV;
    TextView districtTV;
    AutoCompleteTextView districtET;
    CardView districtCV;
    RelativeLayout progressRL;
    Button searchBT;
    NestedScrollView scrollView;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<String> districtsArr = new ArrayList<>();
    Map<String, Object> districtsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SharedPref.getString(getApplicationContext(), "sp_language") == null || !SharedPref.getString(getApplicationContext(), "sp_language").equals("tam")) {
            setContentView(R.layout.activity_home);
        } else {
            setContentView(R.layout.activity_home_tamil);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        initUI();
        progressRL.setVisibility(View.VISIBLE);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        String currentDate = formatter.format(date);

        MainRepository.getService().

                getStatesPosts().

                enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse
                            (Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        progressRL.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            fillStateDetails(response);
                            fillCountryData(response);
                        } else {
                            setDataFromMemory();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progressRL.setVisibility(View.GONE);
                        setDataFromMemory();
                    }
                });

        MainRepository.getService().

                getDistrictPosts().

                enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse
                            (Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        if (response.isSuccessful()) {
                            Map<String, Object> temp = (Map<String, Object>) response.body().get("Tamil Nadu");
                            districtsMap = (Map<String, Object>) temp.get("districtData");
                            Set<String> districtsSET = districtsMap.keySet();
                            for (int i = 0; i < districtsSET.size(); i++) {
                                districtsArr.add(String.valueOf(districtsSET.toArray()[i]));
                            }
                            spinnerAdapter = new ArrayAdapter<>(HomeActivity.this, R.layout.support_simple_spinner_dropdown_item, districtsArr);
                            districtET.setAdapter(spinnerAdapter);
                        } else {
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {

                    }
                });

        districtET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollView.scrollTo(0, scrollView.getBottom());
                return false;
            }
        });

        userAddIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, UserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.hideKeyboard(HomeActivity.this);
                String district = districtET.getText().toString();
                if (district != null && spinnerAdapter.getPosition(district) >= 0) {
                    Log.i(TAG, "onClick: yessssss");
                    Map<String, Object> districtdetail = (Map<String, Object>) districtsMap.get(district);
                    fillDistrictDetails(districtdetail);

                } else {
                    Toast.makeText(HomeActivity.this, "Invalid District Key", Toast.LENGTH_SHORT).show();
                }
            }
        });

        languageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.alert_language, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView englishTV = dialogView.findViewById(R.id.englishTV);
                TextView tamilTV = dialogView.findViewById(R.id.tamilTV);


                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();


                englishTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPref.putString(getApplicationContext(), "sp_language","en");
                        alertDialog.dismiss();
                    }
                });

                tamilTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharedPref.putString(getApplicationContext(), "sp_language","tam");
                        alertDialog.dismiss();
                    }
                });
            }
        });


    }

    private void initUI() {
        userAddIV = findViewById(R.id.useraddIV);
        dateCO_TV = findViewById(R.id.dateCO_TV);
        dateST_TV = findViewById(R.id.dateST_TV);
        activeCO_TV = findViewById(R.id.activeCO_TV);
        activeDT_TV = findViewById(R.id.activeDT_TV);
        activeST_TV = findViewById(R.id.activeST_TV);
        recoverCO_TV = findViewById(R.id.recoverCO_TV);
        recoverDT_TV = findViewById(R.id.recoverDT_TV);
        recoverST_TV = findViewById(R.id.recoverST_TV);
        overallCO_TV = findViewById(R.id.overallCO_TV);
        overallDT_TV = findViewById(R.id.overallDT_TV);
        overallST_TV = findViewById(R.id.overallST_TV);
        deathST_TV = findViewById(R.id.deathST_TV);
        deathCO_TV = findViewById(R.id.deathCO_TV);
        activenewTV = findViewById(R.id.active_newCO_TV);
        recoverednewTV = findViewById(R.id.recover_newCO_TV);
        deathnewTV = findViewById(R.id.death_newCO_TV);
        districtTV = findViewById(R.id.districtTitle);
        progressRL = findViewById(R.id.progressRL);
        districtET = findViewById(R.id.districtET);
        districtCV = findViewById(R.id.districtCV);
        deathDT_TV = findViewById(R.id.deathDT_TV);
        scrollView = findViewById(R.id.scrollviewHome);
        searchBT = findViewById(R.id.searchBT);
        languageIV = findViewById(R.id.languageTV);
    }

    public void setDataFromMemory() {
        Log.i(TAG, "setDataFromMemory: Triggered");
        //state details
        activeST_TV.setText(getStringSP("ST", CommonUtils.active));
        recoverST_TV.setText(getStringSP("ST", CommonUtils.recovered));
        overallST_TV.setText(getStringSP("ST", CommonUtils.confirmed));
        deathST_TV.setText(getStringSP("ST", CommonUtils.deaths));
        dateST_TV.setText(getStringSP("ST", CommonUtils.lastupdatedtime));

        //country details
        recoverednewTV.setText(getStringSP("CO", CommonUtils.dailyrecovered));
        recoverCO_TV.setText(getStringSP("CO", CommonUtils.totalrecovered));
        deathnewTV.setText(getStringSP("CO", CommonUtils.dailydeceased));
        deathCO_TV.setText(getStringSP("CO", CommonUtils.totaldeceased));
        dateCO_TV.setText(getStringSP("CO", CommonUtils.date));
        overallCO_TV.setText(getStringSP("CO", CommonUtils.totalconfirmed));
        activeCO_TV.setText(getStringSP("CO", CommonUtils.totalactive));
        activenewTV.setText(getStringSP("CO", CommonUtils.dailyconfirmed));
    }

//    public void setDate(String currentDate) {
//        dateST_TV.setText(currentDate);
//        dateDT_TV.setText(currentDate);
//        dateCO_TV.setText(currentDate);
//    }

    public void fillStateDetails(Response<Map<String, Object>> response) {
        //map initialization
        List<Map<String, Object>> statewise = (List<Map<String, Object>>) response.body().get("statewise");
        Map<String, Object> stateDetails = CommonUtils.getStateDetails(statewise);
        //temp variables
        String confirmed = getNumFor(stateDetails.get(CommonUtils.confirmed));
        String recovered = getNumFor(stateDetails.get(CommonUtils.recovered));
        String active = getNumFor(stateDetails.get(CommonUtils.active));
        String death = getNumFor(stateDetails.get(CommonUtils.deaths));
        String lastupdatedtime = String.valueOf(stateDetails.get(CommonUtils.lastupdatedtime)).substring(0, 10);
        //widgets
        activeST_TV.setText(active);
        recoverST_TV.setText(recovered);
        overallST_TV.setText(confirmed);
        deathST_TV.setText(death);
        dateST_TV.setText(lastupdatedtime);
        //local data
        putStringSP("ST", CommonUtils.active, active);
        putStringSP("ST", CommonUtils.recovered, recovered);
        putStringSP("ST", CommonUtils.confirmed, confirmed);
        putStringSP("ST", CommonUtils.deaths, death);
        putStringSP("ST", CommonUtils.lastupdatedtime, lastupdatedtime);
    }

    public void fillDistrictDetails(Map<String, Object> district) {
        //temp variables
        int confirmed = new Double((Double) district.get(CommonUtils.confirmed)).intValue();
        int recovered = new Double((Double) district.get(CommonUtils.recovered)).intValue();
        int active = new Double((Double) district.get(CommonUtils.active)).intValue();
        int death = new Double((Double) district.get(CommonUtils.deceased)).intValue();
        //widgets
        activeDT_TV.setText(String.valueOf(active));
        recoverDT_TV.setText(String.valueOf(recovered));
        overallDT_TV.setText(String.valueOf(confirmed));
        deathDT_TV.setText(String.valueOf(death));
        districtCV.setVisibility(View.VISIBLE);

        //local data
    }

    public void fillCountryData(Response<Map<String, Object>> response) {
        //map initialization
        List<Map<String, Object>> cases_series = (List<Map<String, Object>>) response.body().get("cases_time_series");
        Map<String, Object> countryDetails = CommonUtils.getCountryDetails(cases_series);
        //temp variables
        String dailyconfirmed = getNumFor(countryDetails.get(CommonUtils.dailyconfirmed));
        String dailydeceased = getNumFor(countryDetails.get(CommonUtils.dailydeceased));
        String dailyrecovered = getNumFor(countryDetails.get(CommonUtils.dailyrecovered));
        String date = String.valueOf(countryDetails.get(CommonUtils.date));
        String totalconfirmed = getNumFor(countryDetails.get(CommonUtils.totalconfirmed));
        String totaldeceased = getNumFor(countryDetails.get(CommonUtils.totaldeceased));
        String totalrecovered = getNumFor(countryDetails.get(CommonUtils.totalrecovered));
        String totalactive = "";
        try {
            totalactive = getNumFor(Long.parseLong(String.valueOf(countryDetails.get(CommonUtils.totalconfirmed))) - Long.parseLong(String.valueOf(countryDetails.get(CommonUtils.totalrecovered))));
        } catch (Exception e) {
            Log.i(TAG, "fillCountryData: " + e.toString());
        }
        //widgets
        recoverednewTV.setText(dailyrecovered);
        recoverCO_TV.setText(totalrecovered);
        deathnewTV.setText(dailydeceased);
        deathCO_TV.setText(totaldeceased);
        dateCO_TV.setText(date);
        overallCO_TV.setText(totalconfirmed);
        activeCO_TV.setText(totalactive);
        activenewTV.setText(dailyconfirmed);
        //local data
        putStringSP("CO", CommonUtils.date, date);
        putStringSP("CO", CommonUtils.dailyconfirmed, dailyconfirmed);
        putStringSP("CO", CommonUtils.dailydeceased, dailydeceased);
        putStringSP("CO", CommonUtils.dailyrecovered, dailyrecovered);
        putStringSP("CO", CommonUtils.totalconfirmed, totalconfirmed);
        putStringSP("CO", CommonUtils.totaldeceased, totaldeceased);
        putStringSP("CO", CommonUtils.totalrecovered, totalrecovered);
        putStringSP("CO", CommonUtils.totalactive, totalactive);


    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(startMain);
        finishAffinity();
        finish();
    }

    //Utils functions()

    private void putStringSP(String prefix, String key, String value) {
        SharedPref.putString(getApplicationContext(), "sp_" + prefix + "_" + key, value);

    }

    private String getStringSP(String prefix, String key) {
        return SharedPref.getString(getApplicationContext(), "sp_" + prefix + "_" + key);
    }

    private String getNumFor(Object value) {
        return NumberFormat.getInstance(new Locale("en", "in")).format(Long.parseLong(String.valueOf(value)));
    }

}