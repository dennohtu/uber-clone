package com.gpridesolutions.buber.common;


import com.gpridesolutions.buber.remote.IGoogleAPI;
import com.gpridesolutions.buber.remote.RetrofitClient;

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
