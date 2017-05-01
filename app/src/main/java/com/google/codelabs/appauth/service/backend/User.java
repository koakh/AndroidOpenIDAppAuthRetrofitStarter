package com.google.codelabs.appauth.service.backend;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {

  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("email")
  @Expose
  private String email;
  @SerializedName("firstName")
  @Expose
  private String firstName;
  @SerializedName("lastName")
  @Expose
  private String lastName;
  @SerializedName("roles")
  @Expose
  private List<String> roles = null;
  @SerializedName("fullName")
  @Expose
  private String fullName;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

}
