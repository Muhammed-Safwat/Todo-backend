package com.app.todo.utils;

public class AppConstants {
    public static final String AUTH_HEADER = "Authentication";
    public static final int ACCESS_EXPATRIATION_TIME = 5 * 60 * 1000;
    public static final int REFRESH_EXPATRIATION_TIME = 7 * ACCESS_EXPATRIATION_TIME;

    public static final String CLIENT_URL = "http://localhost:4200";

    public static final String EMAIL_CONFORMATION_PREFIX = CLIENT_URL + "/auth/verfiy/";

    public static final String LOCAL_PROVIDER_ID = "412429";
}
