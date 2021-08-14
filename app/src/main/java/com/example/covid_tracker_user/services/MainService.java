package com.example.covid_tracker_user.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MainService {
    String BASE_URL = "https://api.covid19india.org/";

    @GET("state_district_wise.json")
    Call<Map<String,Object>> getDistrictPosts();

    @GET("data.json")
    Call<Map<String,Object>> getStatesPosts();


}
