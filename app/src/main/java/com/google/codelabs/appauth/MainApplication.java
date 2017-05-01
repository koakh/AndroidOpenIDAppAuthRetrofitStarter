package com.google.codelabs.appauth;

import android.app.Application;

public class MainApplication extends Application {

  public static final String TAG = "AppAuthSample";

  // SharedPrefs
  public static final String SHARED_PREFERENCES_NAME = "AuthStatePreference";
  public static final String AUTH_STATE = "AUTH_STATE";
  public static final String ACCESS_TOKEN = "ACCESS_TOKEN";

  @Override
  public void onCreate() {
    super.onCreate();
  }
}
