package com.google.codelabs.appauth.service;

import android.content.Context;

import com.google.codelabs.appauth.service.backend.BackendApi;
import com.google.codelabs.appauth.service.keycloak.KeycloakApi;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.codelabs.appauth.MainApplication.ACCESS_TOKEN;
import static com.google.codelabs.appauth.MainApplication.SHARED_PREFERENCES_NAME;

public class ServiceGenerator {

  private static OkHttpClient.Builder mHttpClient;
  private static Retrofit.Builder mBuilder;
  private static Context mContext;
  private static HttpLoggingInterceptor.Level mLoggingLevel = HttpLoggingInterceptor.Level.BODY;

  public static <S> S createService(Class<S> serviceClass, Context context) {
    // Get BASE_URL for Both Apis
    String baseUrl = (serviceClass == KeycloakApi.class) ? KeycloakApi.BASE_URL : BackendApi.BASE_URL;
    mContext = context;
    mHttpClient = new OkHttpClient.Builder();
    mBuilder = new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
    ;

    // Interceptor : Useful for Backend Api to send Bearer Authorization, without adding it to Retrofit Methods
    mHttpClient.addInterceptor(new Interceptor() {
      @Override
      public Response intercept(Chain chain) throws IOException {

        // Get accessToken From SharedPrefs
        String accessToken = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
          .getString(ACCESS_TOKEN, null);
        //if (authStateJson != null) {
        //  JsonObject authState = new JsonParser().parse(authStateJson).getAsJsonObject();
        //  accessToken = authState.get("accessToken").getAsString();
        //}

        Request original = chain.request();

        Request.Builder requestBuilder = original.newBuilder()
          .header("Accept", "application/json")
          .header("Content-type", "application/json")
          .header("Authorization", "Bearer " + accessToken)
          .method(original.method(), original.body()
          );

        Request request = requestBuilder.build();

        return chain.proceed(request);
      }
    });

    // Logging : add logging as the last interceptor, and after inited OkHttpClient
    // because this will also log the information which you added with previous interceptors to your request.

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    logging.setLevel(mLoggingLevel);
    // add logging as last interceptor
    mHttpClient.addInterceptor(logging);// <-- this is the important line!

    // Init Retrofit
    Retrofit retrofit = mBuilder.client(
      mHttpClient.build()
    ).build();

    return retrofit.create(serviceClass);
  }
}
