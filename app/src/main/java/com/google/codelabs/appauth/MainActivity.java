package com.google.codelabs.appauth;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.codelabs.appauth.service.BaseModel;
import com.google.codelabs.appauth.service.ServiceGenerator;
import com.google.codelabs.appauth.service.backend.BackendApi;
import com.google.codelabs.appauth.service.backend.User;
import com.google.codelabs.appauth.service.keycloak.KeycloakApi;
import com.google.codelabs.appauth.service.keycloak.UserInfo;
import com.squareup.picasso.Picasso;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.TokenResponse;

import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.codelabs.appauth.MainApplication.ACCESS_TOKEN;
import static com.google.codelabs.appauth.MainApplication.AUTH_STATE;
import static com.google.codelabs.appauth.MainApplication.SHARED_PREFERENCES_NAME;
import static com.google.codelabs.appauth.objects.Utils.getHeaderResponseError;

public class MainActivity extends AppCompatActivity {

  private static final String USED_INTENT = "USED_INTENT";

  MainApplication mMainApplication;
  Context mContext;

  // State
  AuthState mAuthState;

  // AuthUser
  UserInfo mUserInfo;

  // Views : Buttons
  AppCompatButton mAuthorize;
  //AppCompatButton mMakeApiCall;
  AppCompatButton mSignOut;
  AppCompatButton mButtonCall1;
  AppCompatButton mButtonCall2;
  // Views : TextViews
  AppCompatTextView mGivenName;
  AppCompatTextView mFamilyName;
  AppCompatTextView mFullName;
  ImageView mProfileView;

  // Services
  KeycloakApi mKeycloakApi;
  BackendApi mBackendApi;

//// Service Calls
//// Used to pass has parameter to mAuthState.performActionWithFreshTokens
//private Call<UserInfo> mKeycloakApiUserInfoCall;
//private Call<User> mBackendApiUserCall;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mMainApplication = (MainApplication) getApplication();
    mContext = getApplicationContext();

    // Buttons
    mAuthorize = (AppCompatButton) findViewById(R.id.buttonAuthorize);
    //mMakeApiCall = (AppCompatButton) findViewById(R.id.buttonApiCall);
    mSignOut = (AppCompatButton) findViewById(R.id.buttonSignOut);
    mButtonCall1 = (AppCompatButton) findViewById(R.id.buttonCall1);
    mButtonCall2 = (AppCompatButton) findViewById(R.id.buttonCall2);
    // TextView
    mGivenName = (AppCompatTextView) findViewById(R.id.givenName);
    mFamilyName = (AppCompatTextView) findViewById(R.id.familyName);
    mFullName = (AppCompatTextView) findViewById(R.id.fullName);
    mProfileView = (ImageView) findViewById(R.id.profileImage);

    // EnablePostAuthorizationFlows
    enablePostAuthorizationFlows();

    // Wire click listeners
    mAuthorize.setOnClickListener(new AuthorizeListener());
    mButtonCall1.setOnClickListener(new Call1Listener());
    mButtonCall2.setOnClickListener(new Call2Listener());
    mSignOut.setOnClickListener(new SignOutListener());
  }

  @Override
  protected void onResume() {
    super.onResume();
    //TODO
    // Call userInfo to check if session is valid
  }

  /**
   * Disabled go back button action
   */
  @Override
  public void onBackPressed() {
  }

  /**
   * Handle the intents from RedirectUriReceiverActivity
   */
  @Override
  protected void onStart() {
    super.onStart();
    checkIntent(getIntent());
  }

  /**
   * Handle the intents from RedirectUriReceiverActivity
   */
  @Override
  protected void onNewIntent(Intent intent) {
    checkIntent(intent);
  }

  private void checkIntent(@Nullable Intent intent) {
    if (intent != null) {
      String action = intent.getAction();
      switch (action) {
        case "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE":
          if (!intent.hasExtra(USED_INTENT)) {
            handleAuthorizationResponse(intent);
            intent.putExtra(USED_INTENT, true);
          }
          break;
        default:
          // do nothing
      }
    }
  }

  private void enablePostAuthorizationFlows() {
    mAuthState = restoreAuthState();
    // Authorized
    if (mAuthState != null && mAuthState.isAuthorized()) {
      mAuthorize.setVisibility(View.GONE);
      //if (mMakeApiCall.getVisibility() == View.GONE) {
      //  mMakeApiCall.setVisibility(View.VISIBLE);
      //  mMakeApiCall.setOnClickListener(new MakeApiCallListener(this, mAuthState, new AuthorizationService(this)));
      //}
      if (mButtonCall1.getVisibility() == View.GONE) {
        mButtonCall1.setVisibility(View.VISIBLE);
      }
      if (mButtonCall2.getVisibility() == View.GONE) {
        mButtonCall2.setVisibility(View.VISIBLE);
      }
      if (mSignOut.getVisibility() == View.GONE) {
        mSignOut.setVisibility(View.VISIBLE);
      }
      // initApiServices
      initApiServices();
    // Not Authorized
    } else {
      mAuthorize.setVisibility(View.VISIBLE);
      //mMakeApiCall.setVisibility(View.GONE);
      mButtonCall1.setVisibility(View.GONE);
      mButtonCall2.setVisibility(View.GONE);
      mSignOut.setVisibility(View.GONE);
    }
  }

  private void initApiServices() {
    // Init Services
    if (mKeycloakApi == null) mKeycloakApi = ServiceGenerator.createService(KeycloakApi.class, mContext);
    if (mBackendApi == null) mBackendApi = ServiceGenerator.createService(BackendApi.class, mContext);
  }

  /**
   * Processing the Authorization Response
   * Exchanges the code, for the {@link TokenResponse}.
   *
   * @param intent represents the {@link Intent} from the Custom Tabs or the System Browser.
   */
  private void handleAuthorizationResponse(@NonNull Intent intent) {
    // code from the step 'Handle the Authorization Response' goes here.
    AuthorizationResponse response = AuthorizationResponse.fromIntent(intent);
    AuthorizationException error = AuthorizationException.fromIntent(intent);
    // The AuthState object created here is a convenient way to store details from the authorization session.
    // You can update it with the results of new OAuth responses, and persist it to store the authorization session between
    // app starts.
    final AuthState authState = new AuthState(response, error);
    // Exchange authorization code for the refresh and access tokens, and update the AuthState instance with that response.
    // Add the following code right below the last block.
    if (response != null) {
      Log.i(MainApplication.TAG, String.format("Handled Authorization Response %s ", authState.toJsonString()));
      AuthorizationService service = new AuthorizationService(this);
      service.performTokenRequest(response.createTokenExchangeRequest(), new AuthorizationService.TokenResponseCallback() {
        @Override
        public void onTokenRequestCompleted(@Nullable TokenResponse tokenResponse, @Nullable AuthorizationException exception) {
          if (exception != null) {
            Log.w(MainApplication.TAG, "Token Exchange failed", exception);
          } else {
            if (tokenResponse != null) {
              authState.update(tokenResponse, exception);
              persistAuthState(authState);
              Log.i(MainApplication.TAG, String.format("Token Response [ Access Token: %s, ID Token: %s ]", tokenResponse.accessToken, tokenResponse.idToken));
            }
          }
        }
      });
    }
  }

  /**
   * Save AuthState object
   */
  private void persistAuthState(@NonNull AuthState authState) {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
      .putString(AUTH_STATE, authState.toJsonString())
      .apply();
    enablePostAuthorizationFlows();
  }

  /**
   * Clear AuthState object
   */
  private void clearAuthState() {
    getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
      .edit()
      .remove(AUTH_STATE)
      .apply();
  }

  /**
   * Load the AuthState object
   */
  @Nullable
  private AuthState restoreAuthState() {
    String jsonString = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
      .getString(AUTH_STATE, null);
    if (!TextUtils.isEmpty(jsonString)) {
      try {
        return AuthState.fromJson(jsonString);
      } catch (JSONException jsonException) {
        // should never happen
      }
    }
    return null;
  }

  /**
   * Kicks off the authorization flow.
   */
  public static class AuthorizeListener implements Button.OnClickListener {
    @Override
    public void onClick(View view) {

      // Create the ServiceConfiguration
      // Create the AuthorizationServiceConfiguration object in the AuthorizeListener::onClick method
      // which declares the authorization and token endpoints of the OAuth server you wish to authorize with.
      // In our example, we will use Google, but this will work with any compliant OAuth server.
      AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
        Uri.parse(KeycloakApi.AUTHORIZATION_ENDPOINT) /* auth endpoint */,
        Uri.parse(KeycloakApi.TOKEN_ENDPOINT) /* token endpoint */
      );

      // If your server supports dynamic discovery, you can also fetch this configuration dynamically with AuthorizationServiceConfiguration.fetchFromIssuer.
      // We'll stick with the static configuration for simplicity.
      // https://openid.github.io/AppAuth-Android/docs/latest/net/openid/appauth/AuthorizationServiceConfiguration.html#fetchFromIssuer-android.net.Uri-net.openid.appauth.AuthorizationServiceConfiguration.RetrieveConfigurationCallback-

      // Build the AuthorizationRequest
      // Once you have an instance of AuthorizationServiceConfiguration, you can now build an instance of AuthorizationRequest
      // which describes actual authorization request, including your OAuth client id, and the scopes you are requesting.
      // Add the following code right below the previous block.
      String clientId = KeycloakApi.CLIENT_ID;
      Uri redirectUri = Uri.parse(KeycloakApi.REDIRECT_URL);
      AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(
        serviceConfiguration,
        clientId,
        AuthorizationRequest.RESPONSE_TYPE_CODE,
        redirectUri
      );
      builder.setScopes("profile");
      AuthorizationRequest request = builder.build();

      // Perform the Authorization Request
      // Create an instance of the AuthorizationService. Ideally, there is one instance of AuthorizationService per Activity
      AuthorizationService authorizationService = new AuthorizationService(view.getContext());

      // Create the PendingIntent to handle the authorization response, then perform the authorization request with performAuthorizationRequest.
      // This will open the authorization request you configured previously in a Custom Tab (or the default browser if no browsers support Custom Tabs).
      String action = "com.google.codelabs.appauth.HANDLE_AUTHORIZATION_RESPONSE";
      Intent postAuthorizationIntent = new Intent(action);

      PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), request.hashCode(), postAuthorizationIntent, 0);
      authorizationService.performAuthorizationRequest(request, pendingIntent);
    }
  }

  public class Call1Listener implements Button.OnClickListener {
    @Override
    public void onClick(View view) {
      // Used to pass call<T extends BaseModel> has a parameter of mAuthState.performActionWithFreshTokens
      Call<UserInfo> call = mKeycloakApi.getUserInfo(
        KeycloakApi.REALM,
        mAuthState.getAccessToken()
      );
      performCallAction(new AuthorizationService(mContext), call, keycloakApiUserInfoCallback);
    }
  }

  public class Call2Listener implements Button.OnClickListener {
    @Override
    public void onClick(View view) {
      // Used to pass call<T extends BaseModel> has a parameter of mAuthState.performActionWithFreshTokens
      Call<User> call = mBackendApi.getUser(
      );
      performCallAction(new AuthorizationService(mContext), call, backendApiUserCallback);
    }
  }

  private class SignOutListener implements Button.OnClickListener {
    @Override
    public void onClick(View view) {
      signOut();
    }
  }

  private Callback<ResponseBody> logoutCallback = new Callback<ResponseBody>() {
    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
    }
  };

  private void signOut() {
    // Logout Keycloak before cleaning phase, this force keycloak to be logout, else current active tokens be valid
    mKeycloakApi.logoutUser(
      KeycloakApi.REALM,
      KeycloakApi.CLIENT_ID,
      mAuthState.getAccessToken()
    ).enqueue(logoutCallback);

    // Cleaning Phase
    this.mAuthState = null;
    this.clearAuthState();
    this.enablePostAuthorizationFlows();

    // Update UI
    updateLabels();
  }

  private Callback<UserInfo> keycloakApiUserInfoCallback = new Callback<UserInfo>() {
    @Override
    public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
      if (response.isSuccessful()) {
        mUserInfo = response.body();
        String msg = String.format("keycloakApiUserInfoCallback: User Sub: %s, Name: %s", mUserInfo.getSub(), mUserInfo.getName());
        Log.d(MainApplication.TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        // Update UI
        updateLabels(mUserInfo);
      } else {
        // Code: 400 Message: Bad Request
        String msg = String.format("keycloakApiUserInfoCallback: Code: %s Message: %s", response.code(), response.message());
        Log.d(MainApplication.TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
      }
    }

    @Override
    public void onFailure(Call<UserInfo> call, Throwable t) {
      Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
      t.printStackTrace();
    }
  };

  private Callback<User> backendApiUserCallback = new Callback<User>() {
    @Override
    public void onResponse(Call<User> call, Response<User> response) {
      if (response.isSuccessful()) {
        User user = response.body();
        String msg = String.format("backendApiUserCallback: User Id: %s, FullName: %s", user.getId(), user.getFullName());
        Log.d(MainApplication.TAG, msg);
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
      } else {
        //Default Message
        String errorMessage;
        switch (response.code()) {
          case 401:
            //Require to extract error message from headers, response.message() comes empty/null
            String headerError = (response.headers()).toMultimap().values().toArray()[2].toString();
            errorMessage = getHeaderResponseError(headerError, response.message());
            break;
          case 403:
            errorMessage = getResources().getString(R.string.error_message_http_error_code_403);
            break;
          default:
            errorMessage = String.format("Error Code: %s -  %s", response.code(), response.message());
            break;
        }

        Log.d(MainApplication.TAG, errorMessage);
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
      }
    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
      Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
      t.printStackTrace();
    }
  };

  //private class MakeApiCallListener implements Button.OnClickListener {
  //
  //  private final MainActivity mMainActivity;
  //  private AuthState mAuthState;
  //  private AuthorizationService mAuthorizationService;
  //
  //  public MakeApiCallListener(@NonNull MainActivity mainActivity, @NonNull AuthState authState, @NonNull AuthorizationService authorizationService) {
  //    mMainActivity = mainActivity;
  //    mAuthState = authState;
  //    mAuthorizationService = authorizationService;
  //  }
  //
  //  /**
  //   * While you can get the tokens directly from the token response, those tokens expire and must be refreshed occasionally.
  //   * Using AuthState and making your REST API calls inside authState.performActionWithFreshTokens is recommended,
  //   * as it will automatically ensure that the tokens are fresh (refreshing them when needed) before executing your code.
  //   */
  //  @Override
  //  public void onClick(View view) {
  //    /**
  //     * Starter to use performActionWithFreshTokens, just work with AccessToken and refreshToken
  //     */
  //    // code from the section 'Making API Calls' goes here
  //    mAuthState.performActionWithFreshTokens(mAuthorizationService, new AuthState.AuthStateAction() {
  //      @Override
  //      public void execute(
  //        String accessToken,
  //        String idToken,
  //        AuthorizationException ex) {
  //        if (ex != null) {
  //          boolean sessionExpired = (ex.getCause().toString().contains(KeycloakApi.TOKEN_ENDPOINT));
  //          // Default errorMessage
  //          String errorMessage = ex.getCause().getLocalizedMessage().toString();
  //          if (sessionExpired) {
  //            // Occurs when we lost keycloak auth, ex logout session in BO, or by time pass
  //            // negotiation for fresh tokens failed, check ex for more details
  //            errorMessage = String.format("%s : %s", ex.getLocalizedMessage(), getResources().getString(R.string.error_message_session_expired));
  //            // Force signOut, Tokens Expired : Update UI
  //            mAuthorize.setVisibility(View.VISIBLE);
  //            mMakeApiCall.setVisibility(View.GONE);
  //            mSignOut.setVisibility(View.GONE);
  //            //Shared Prefs save CLEAR and SAVE
  //            if (mAuthState != null) {
  //              signOut();
  //            }
  //          }
  //
  //          Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
  //          Log.e(MainApplication.TAG, ex.getCause().toString());
  //          return;
  //        }
  //
  //        // Store AccessToken to use in Interceptor, before call Api, this way we can call backendApi without send AccessToken, and can Call it after close App
  //        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
  //          .putString(ACCESS_TOKEN, accessToken)
  //          .apply();
  //
  //        // Use the access token to do something ...
  //        Log.i(MainApplication.TAG, String.format("TODO: make an API call with [Access Token: %s, ID Token: %s]", accessToken, idToken));
  //
  //        // Call KeycloakApi
  //        mKeycloakApi.getUserInfo(
  //          KeycloakApi.REALM,
  //          accessToken
  //        ).enqueue(keycloakApiUserInfoCallback);
  //
  //        // Call BackendApi
  //        mBackendApi.getUser()
  //          .enqueue(backendApiUserCallback);
  //      }
  //    });
  //  }
  //}

  /**
   * Generic method with calls and callbacks of BaseModels
   * Starter to use performActionWithFreshTokens, just work with AccessToken and refreshToken
   * While you can get the tokens directly from the token response, those tokens expire and must be refreshed occasionally.
   * Using AuthState and making your REST API calls inside authState.performActionWithFreshTokens is recommended,
   * as it will automatically ensure that the tokens are fresh (refreshing them when needed) before executing your code.
   * @param authorizationService
   * @param call<T extends BaseModel>
   * @param callback<T extends BaseModel>
   */
  private <T extends BaseModel> void performCallAction(@NonNull AuthorizationService authorizationService, @NonNull final Call<T> call, @NonNull final Callback<T> callback) {
    // code from the section 'Making API Calls' goes here
    mAuthState.performActionWithFreshTokens(authorizationService, new AuthState.AuthStateAction() {
      @Override
      public void execute(
        String accessToken,
        String idToken,
        AuthorizationException ex) {
        if (ex != null) {
          boolean sessionExpired = (ex.getCause().toString().contains(KeycloakApi.TOKEN_ENDPOINT));
          // Default errorMessage
          String errorMessage = ex.getCause().getLocalizedMessage().toString();
          if (sessionExpired) {
            // Occurs when we lost keycloak auth, ex logout session in BO, or by time pass
            // negotiation for fresh tokens failed, check ex for more details
            errorMessage = String.format("%s : %s", ex.getLocalizedMessage(), getResources().getString(R.string.error_message_session_expired));
            // Force signOut, Tokens Expired : Update UI
//mAuthorize.setVisibility(View.VISIBLE);
//mMakeApiCall.setVisibility(View.GONE);
//mSignOut.setVisibility(View.GONE);
            //Shared Prefs save CLEAR and SAVE
            if (mAuthState != null) {
              signOut();
            }
          }

          Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
          Log.e(MainApplication.TAG, ex.getCause().toString());
          return;
        }

        // Store AccessToken to use in Interceptor, before call Api, this way we can call backendApi without send AccessToken, and can Call it after close App
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
          .putString(ACCESS_TOKEN, accessToken)
          .apply();

        // Use the access token to do something ...
        // Log.i(MainApplication.TAG, String.format("Call Api with [Access Token: %s]", accessToken));

        // Call call with callback
        call.enqueue(callback);
      }
    });
  }

  private void updateLabels() {
    mProfileView.setVisibility(View.GONE);
    mFullName.setVisibility(View.GONE);
    mGivenName.setVisibility(View.GONE);
    mFamilyName.setVisibility(View.GONE);
  }

  private void updateLabels(UserInfo userInfo) {
    String imageUrl = (userInfo.getPicture() != null)
      ? userInfo.getPicture()
      : "http://lorempixel.com/600/600/people/";

    if (!TextUtils.isEmpty(imageUrl)) {
      Picasso.with(this)
        .load(imageUrl)
        .placeholder(R.drawable.ic_account_circle_black_48dp)
        .into(this.mProfileView);
    }
    if (!TextUtils.isEmpty(userInfo.getName())) {
      mFullName.setVisibility(View.VISIBLE);
      mFullName.setText(userInfo.getName());
    }
    if (!TextUtils.isEmpty(userInfo.getGivenName())) {
      mGivenName.setVisibility(View.VISIBLE);
      mGivenName.setText(userInfo.getGivenName());
    }
    if (!TextUtils.isEmpty(userInfo.getFamilyName())) {
      mFamilyName.setVisibility(View.VISIBLE);
      mFamilyName.setText(userInfo.getFamilyName());
    }
  }

}
