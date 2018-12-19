package com.customer.example.sdk;

import com.customer.example.model.Customer;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vaishnavi on 19/12/18.
 */
public class Customer_Data {

    public static String CUSTOMER_DEV_URL = "https://capi-stg.cust.com";
    public static final String RESPONSE_HTTP_CODE = "RESPONSE_HTTP_CODE";
    public static final String RESPONSE_JSON = "RESPONSE_JSON";

    public static OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static String getUniqToken(String auth_timestamp, String app_secret_key) {
        String uniq_token_string = app_secret_key + auth_timestamp;
        return getHash(uniq_token_string);
    }

    public static String getAuthToken(String app_code, String app_secret_key) {
        String auth_timestamp = "" + (System.currentTimeMillis());
        String string_auth_token = app_code + ";" + auth_timestamp + ";" + getUniqToken(auth_timestamp, app_secret_key);
        String auth_token = new String(Base64.getEncoder().encode(string_auth_token.getBytes()));
        return auth_token;
    }

    public static String getHash(String message) {
        String sha256hex = new String(Hex.encodeHex(DigestUtils.sha256(message)));
        return sha256hex;
    }

    public static String getCustomerJson(Customer customer, String session_id, String token, double dob, String dev_reference) {
        String session_id_row = "";
        if(session_id != null && session_id != ""){
            session_id_row = "\"session_id\": \"" + session_id + "\",";
        }
        return "{" +
                    session_id_row +
                    "\"user\": {" +
                        "\"id\": \"" + customer.getId() + "\"," +
                        "\"email\": \"" + customer.getEmail() + "\"," +
                        "\"ip_address\": \"" + customer.getIpAddress() + "\"" +
                    "}," +
                    "\"get_cust\": {" +
                        "\"code\": \"123\"," +
                        "\"dob\": " + dob + "," +
                        "\"dev_reference\": \"" + dev_reference + "\"," +
                    "}," +
                    "\"customer\": {" +
                        "\"token\": \"" + token + "\"" +
                    "}" +
                "}";
    }


    public static String deleteCustomerJson(String uid, String token) {
        return "{" +
                    "\"customer\": {" +
                        "\"token\": \"" +token + "\"" +
                    "}," +
                    "\"user\": {" +
                        "\"id\": \"" + uid + "\"" +
                    "}" +
                "}";
    }

    public static String verifyCustomerJson(String uid, String customer_id, String type, String value) {
        return "{" +
                    "\"user\": {" +
                        "\"id\": \"" + uid + "\"" +
                    "}," +
                    "\"customer\": {" +
                        "\"id\": \"" + customer_id + "\"" +
                    "}," +
                    "\"type\": \"" + type + "\"," +
                    "\"value\": \"" + value + "\"" +
                    "" +
                "}";
    }

    public static Map<String, String> doPostRequest(String url, String json){
        String jsonResponse = "{}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Auth-Token", Customer_Data.getAuthToken(System.getenv("CUSTOMER_APP_SERVER_CODE"), System.getenv("CUSTOMER_APP_SERVER_KEY")))
                .url(url)
                .post(body)
                .build();

        Response response = null;
        Map<String, String> mapResult = new HashMap<>(2);

        try {
            response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            mapResult.put(RESPONSE_HTTP_CODE, ""+response.code());
            mapResult.put(RESPONSE_JSON, jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapResult;
    }

    public static Map<String, String> doGetRequest(String url){
        String jsonResponse = "{}";
        Request request = new Request.Builder()
                .header("Auth-Token", Customer_Data.getAuthToken(System.getenv("CUSTOMER_APP_SERVER_CODE"), System.getenv("CSUTOMER_APP_SERVER_KEY")))
                .url(url)
                .build();

        Response response = null;
        Map<String, String> mapResult = new HashMap<>(2);

        try {
            response = client.newCall(request).execute();
            jsonResponse = response.body().string();
            mapResult.put(RESPONSE_HTTP_CODE, ""+response.code());
            mapResult.put(RESPONSE_JSON, jsonResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapResult;
    }
}
