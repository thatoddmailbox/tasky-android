package io.github.thatoddmailbox.tasky.api;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClient {
    public static final String basePath = "https://api-v2.myhomework.space/";
    public static final String clientId = "qtq3P7HrSo13IMuKRb_kctvUKzJ6xzezF_Bii-nJRTnramPLsHMGV0cN";

    private static OkHttpClient _client;

    private static OkHttpClient getClient() {
        if (_client == null) {
            _client = new OkHttpClient.Builder().build();
        }
        return _client;
    }

    public static String getAuthorizationHeader(String authorizationToken) {
        return "Bearer " + authorizationToken;
    }

    public static String getRequestAuthURL() {
        return basePath + "application/requestAuth/" + clientId;
    }

    public static void makeRequest(Request r, final APICallback callback) {
        getClient().newCall(r).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject o = new JSONObject(response.body().string());
                    callback.onResponse(call, o);
                } catch (JSONException e) {
                    callback.onFailure(call, e);
                }
            }
        });
    }

    public static void get(String token, String path, HashMap<String, String> params, final APICallback callback) {
        String paramStr = "";

        if (params.size() > 0) {
            paramStr = "?";
            boolean first = true;
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    paramStr += "&";
                }

                paramStr += param.getKey();
                paramStr += "=";
                try {
                    paramStr += URLEncoder.encode(param.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    callback.onFailure(null, e);
                }
            }
        }

        Request r = new Request.Builder()
                .url(basePath + path + paramStr)
                .get()
                .addHeader("Authorization", getAuthorizationHeader(token))
                .build();

        makeRequest(r, callback);
    }

    public static void post(String token, String path, HashMap<String, String> params, final APICallback callback) {
        FormBody.Builder bodyBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> param : params.entrySet()) {
            bodyBuilder.add(param.getKey(), param.getValue());
        }

        RequestBody body = bodyBuilder.build();

        Request r = new Request.Builder()
                .url(basePath + path)
                .post(body)
                .addHeader("Authorization", getAuthorizationHeader(token))
                .build();

        makeRequest(r, callback);
    }
}
