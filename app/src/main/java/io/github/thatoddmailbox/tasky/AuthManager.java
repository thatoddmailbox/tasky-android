package io.github.thatoddmailbox.tasky;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthManager {
    public static final String AUTH_PREFS_FILE = "auth";

    public static boolean haveToken(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(AUTH_PREFS_FILE, 0);
        return settings.contains("mhsToken");
    }

    public static String getToken(Context ctx) {
        SharedPreferences settings = ctx.getSharedPreferences(AUTH_PREFS_FILE, 0);
        return settings.getString("mhsToken", null);
    }

    public static void setToken(Context ctx, String token) {
        SharedPreferences settings = ctx.getSharedPreferences(AUTH_PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("mhsToken", token);
        editor.commit();
    }
}
