package io.github.thatoddmailbox.tasky.api;

import org.json.JSONObject;

import okhttp3.Call;

public interface APICallback {
    void onFailure(Call call, Exception e);
    void onResponse(Call call, JSONObject o);
}