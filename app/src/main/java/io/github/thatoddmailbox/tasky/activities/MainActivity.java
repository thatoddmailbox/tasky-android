package io.github.thatoddmailbox.tasky.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.github.thatoddmailbox.tasky.AuthManager;
import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.User;
import io.github.thatoddmailbox.tasky.misc.AlertUtils;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 1;

    User user;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        handleActivityStart(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN) {
            handleActivityStart(true);
        }
    }

    void handleActivityStart(final boolean showSigninToast) {
        if (AuthManager.haveToken(this)) {
            // get information about the user
            token = AuthManager.getToken(this);
            APIClient.get(token, "auth/me", new HashMap<String, String>(), new APICallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
                }

                @Override
                public void onResponse(Call call, JSONObject o) {
                    try {
                        user = User.fromJSON(o.getJSONObject("user"));

                        if (showSigninToast) {
                            Toast.makeText(MainActivity.this, "Signed in as " + user.Name, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
                    }
                }
            });
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, REQUEST_CODE_LOGIN);
        }
    }
}
