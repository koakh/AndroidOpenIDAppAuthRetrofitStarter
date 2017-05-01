package com.google.codelabs.appauth.objects;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mario on 29/04/2017.
 */

public class Utils {

  // [Bearer realm="Demo-Realm", error="invalid_token", error_description="Token is not active"]
  // RegEx to Explode it: (\w+)=\"([^\,]+)\"
  // Regex link to Sample : https://regex101.com/r/uB4sI9/47
  public static String getHeaderResponseError(String headerResponseHeader, String defaultErrorMessage) {

    String result = defaultErrorMessage;
    HashMap<String,String> hashMap = new HashMap<>();
    String key = "", value = "";
    final String regex = "(\\w+)=\\\"([^\\,]+)\\\"";

    if (headerResponseHeader.contains("error_description")) {

      final Pattern pattern = Pattern.compile(regex);
      final Matcher matcher = pattern.matcher(headerResponseHeader);

      while (matcher.find()) {
        System.out.println("Full match: " + matcher.group(0));
        for (int i = 1; i <= matcher.groupCount(); i++) {
          //System.out.println("Group " + i + ": " + matcher.group(i));
          if (i == 1) {
            key = matcher.group(i);
          }
          else if (i == 2) {
            value = matcher.group(i);
            hashMap.put(key, value);
          }
        }
      }

      result = hashMap.get("error_description");
    }

    return result;
  }

}
