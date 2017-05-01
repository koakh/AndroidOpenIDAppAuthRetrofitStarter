package com.google.codelabs.appauth.service.keycloak;

import com.google.gson.annotations.SerializedName;

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

  public String getSub() {
    return sub;
  }

  public void setSub(String sub) {
    this.sub = sub;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPreferredUsername() {
    return preferredUsername;
  }

  public void setPreferredUsername(String preferredUsername) {
    this.preferredUsername = preferredUsername;
  }

  public String getGivenName() {
    return givenName;
  }

  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}
