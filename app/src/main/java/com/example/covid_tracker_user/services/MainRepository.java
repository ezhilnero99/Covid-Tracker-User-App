package com.example.covid_tracker_user.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainRepository {

    public static MainService getService(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(MainService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return (MainService) retrofit.create(MainService.class);
    }
}
