package com.google.codelabs.appauth.service.keycloak;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface KeycloakApi {

  String CLIENT_ID = "tutorial-frontend";
  String REALM = "Demo-Realm";
  String REDIRECT_URL = "com.google.codelabs.appauth:/oauth2callback";
  String BASE_URL = "https://koakh.com:8082";
  String AUTHORIZATION_ENDPOINT = "https://koakh.com:8082/auth/realms/Demo-Realm/protocol/openid-connect/auth";
  String TOKEN_ENDPOINT = "https://koakh.com:8082/auth/realms/Demo-Realm/protocol/openid-connect/token";
  String TOKEN_INTROSPECTION_ENDPOINT = "https://koakh.com:8082/auth/realms/Demo-Realm/protocol/openid-connect/token/introspect";
  String USERINFO_ENDPOINT = "https://koakh.com:8082/auth/realms/Demo-Realm/protocol/openid-connect/userinfo";
  String END_SESSION_ENDPOINT = "https://koakh.com:8082/auth/realms/Demo-Realm/protocol/openid-connect/logout";

  //Logout
  @FormUrlEncoded
  @POST("auth/realms/{realm}/protocol/openid-connect/logout")
  Call<ResponseBody> logoutUser(
    @Path("realm") String realm,
    @Field("client_id") String clientId,
    @Field("refresh_token") String refreshToken
  );

  //Logout
  @FormUrlEncoded
  @POST("auth/realms/{realm}/protocol/openid-connect/userinfo")
  Call<UserInfo> getUserInfo(
    @Path("realm") String realm,
    // Must send access_token, not catched by interceptor, required here
    @Field("access_token") String accessToken
  );

}
