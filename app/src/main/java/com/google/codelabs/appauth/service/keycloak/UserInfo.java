package com.google.codelabs.appauth.service.keycloak;

import com.google.gson.annotations.SerializedName;

import java.util.Set;

//PayLoad
//{
//  "sub": "81c92e87-1792-480a-98d4-72add2c033d9",
//  "name": "Mário Monteiro",
//  "preferred_username": "marioammonteiro@gmail.com",
//  "given_name": "Mário",
//  "family_name": "Monteiro",
//  "email": "marioammonteiro@gmail.com"
//}

public class UserInfo {
  @SerializedName("sub")
  private String sub;

  @SerializedName("name")
  private String name;

  @SerializedName("preferred_username")
  private String preferredUsername;

  @SerializedName("given_name")
  private String givenName;

  @SerializedName("family_name")
  private String familyName;

  @SerializedName("email")
  private String email;

  @SerializedName("picture")
  private String picture;

  @SerializedName("profile")
  private String profile;

  @SerializedName("gender")
  private String gender;

  @SerializedName("locale")
  private String locale;

  @SerializedName("email_verified")
  private boolean emailVerified;

  //@SerializedName("roles")
  //private Set<String> roles;

  public String getSub() {
    return sub;
  }

  public String getName() {
    return name;
  }

  public String getPreferredUsername() {
    return preferredUsername;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public String getEmail() {
    return email;
  }

  public String getPicture() {
    return picture;
  }

  public String getProfile() {
    return profile;
  }

  public String getGender() {
    return gender;
  }

  public String getLocale() {
    return locale;
  }

  public boolean isEmailVerified() {
    return emailVerified;
  }

  //public Set<String> getRoles() {
  //  return roles;
  //}
}
