package io.github.thatoddmailbox.tasky.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.thatoddmailbox.tasky.AuthManager;
import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.adapters.TodoClassAdapter;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.MHSClass;
import io.github.thatoddmailbox.tasky.data.User;
import io.github.thatoddmailbox.tasky.misc.AlertUtils;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 1;

    @BindView(R.id.main_spinner)
    AppCompatSpinner mainSpinner;

    User user;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar t = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ButterKnife.bind(this);

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
            final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));
            APIClient.get(token, "auth/me", new HashMap<String, String>(), new APICallback() {
                @Override
                public void onFailure(Call call, Exception e) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            AlertUtils.showConnectionFailureDialog(MainActivity.this,true);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final JSONObject o) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            try {
                                user = User.fromJSON(o.getJSONObject("user"));

                                if (showSigninToast) {
                                    Toast.makeText(MainActivity.this, "Signed in as " + user.Name, Toast.LENGTH_SHORT).show();
                                }

                                fetchLists();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
                            }
                        }
                    });
                }
            });
        } else {
            Intent login = new Intent(this, LoginActivity.class);
            startActivityForResult(login, REQUEST_CODE_LOGIN);
        }
    }

    void fetchLists() {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", getString(R.string.loading));
        APIClient.get(token, "classes/get", new HashMap<String, String>(), new APICallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                progressDialog.dismiss();
                AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
            }

            @Override
            public void onResponse(Call call, JSONObject o) {
                progressDialog.dismiss();

                try {
                    JSONArray classesJSONArray = o.getJSONArray("classes");

                    final ArrayList<MHSClass> classItems = new ArrayList<MHSClass>();

                    for (int i = 0; i < classesJSONArray.length(); i++) {
                        JSONObject classJSON = classesJSONArray.getJSONObject(i);
                        MHSClass classObj = MHSClass.fromJSON(classJSON);
                        if (classObj.isTodoClass()) {
                            classItems.add(classObj);
                        }
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TodoClassAdapter adapter = new TodoClassAdapter(MainActivity.this, classItems);
                            adapter.setDropDownViewResource(R.layout.item_dropdown_class);
                            mainSpinner.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
                }
            }
        });
    }
}
