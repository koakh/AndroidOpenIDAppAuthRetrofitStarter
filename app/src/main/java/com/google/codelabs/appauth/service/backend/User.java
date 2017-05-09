package com.google.codelabs.appauth.service.backend;

import com.google.codelabs.appauth.service.BaseModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Set;

public class User implements BaseModel {

  @SerializedName("id")
  @Expose
  private String id;

  @SerializedName("firstName")
  @Expose
  private String firstName;

  @SerializedName("lastName")
  @Expose
  private String lastName;

  @SerializedName("fullName")
  @Expose
  private String fullName;

  @SerializedName("gender")
  @Expose
  private String gender;

  @SerializedName("email")
  @Expose
  private String email;

  @SerializedName("email_verified")
  @Expose
  private boolean emailVerified;

  @SerializedName("picture")
  @Expose
  private String picture;

  @SerializedName("profile")
  @Expose
  private String profile;

  @SerializedName("locale")
  @Expose
  private String locale;

  @SerializedName("roles")
  @Expose
  private List<String> roles = null;

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFullName() {
    return fullName;
  }

  public String getGender() {
    return gender;
  }

  public String getEmail() {
    return email;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  public String getPicture() {
    return picture;
  }

  public String getProfile() {
    return profile;
  }

  public String getLocale() {
    return locale;
  }

  public List<String> getRoles() {
    return roles;
  }
}
