package com.gpridesolutions.buber.common;

import com.google.android.gms.common.api.GoogleApi;
import com.gpridesolutions.buber.remote.RetrofitClient;

public class Common {
    public static final String baseURL = "https://maps.google.com";
    public static GoogleApi iGoogleAPI(){
        return RetrofitClient.getClient(baseURL).create(GoogleApi.class);
    }
}
