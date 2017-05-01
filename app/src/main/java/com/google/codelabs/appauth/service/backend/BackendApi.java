package com.google.codelabs.appauth.service.backend;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BackendApi {
  String BASE_URL = "https://koakh.com:8084/api/";

  @GET("user")
  Call<User> getUser();
}
