package io.github.thatoddmailbox.tasky.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.thatoddmailbox.tasky.AuthManager;
import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.adapters.TodoAdapter;
import io.github.thatoddmailbox.tasky.adapters.TodoClassAdapter;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.Homework;
import io.github.thatoddmailbox.tasky.data.MHSClass;
import io.github.thatoddmailbox.tasky.data.User;
import io.github.thatoddmailbox.tasky.misc.AlertUtils;
import io.github.thatoddmailbox.tasky.misc.TextPromptDialog;
import io.github.thatoddmailbox.tasky.misc.TextPromptOnEnterListener;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOGIN = 1;

    @BindView(R.id.main_spinner)
    AppCompatSpinner mainSpinner;

    @BindView(R.id.main_tabs)
    TabLayout mainTabs;

    @BindView(R.id.main_swipe_view)
    SwipeRefreshLayout mainSwipeView;

    @BindView(R.id.main_todo_list)
    ListView mainTodoList;

    @BindView(R.id.main_fab)
    FloatingActionButton mainFab;

    User user;
    String token;
    ArrayList<MHSClass> todoLists;
    MHSClass currentListClass;
    ArrayList<Homework> todoListItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar t = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(t);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        ButterKnife.bind(this);

        mainTabs.setTabTextColors(Color.rgb(200, 200, 200), Color.WHITE);

        mainTabs.addTab(mainTabs.newTab().setTag(false).setText("Uncompleted"));
        mainTabs.addTab(mainTabs.newTab().setTag(true).setText("All"));

        mainTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Boolean showAll = (Boolean) tab.getTag();
                ((TodoAdapter)mainTodoList.getAdapter()).getFilter().filter(showAll ? "" : "uncompleted");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // don't care
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

        mainSwipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCurrentList();
            }
        });

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog itemDialog = TextPromptDialog.build(MainActivity.this, "Add item to list", "", "Something to do", "", new TextPromptOnEnterListener() {
                    @Override
                    public void onEnter(String text, DialogInterface dialog, int id) {
                        Calendar calendar = Calendar.getInstance();
                        String dueString = "";
                        dueString = String.format(Locale.US, "%1$tY-%1$tm-%1$td", calendar);

                        HashMap<String, String> homeworkParams = new HashMap<String, String>();

                        homeworkParams.put("name", text);
                        homeworkParams.put("due", dueString);
                        homeworkParams.put("desc", "");
                        homeworkParams.put("complete", "0");
                        homeworkParams.put("classId", Integer.toString(currentListClass.ID));

                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading));
                        APIClient.post(token, "homework/add", homeworkParams, new APICallback() {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        AlertUtils.showConnectionFailureDialog(MainActivity.this, false);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, JSONObject o) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, R.string.success_added, Toast.LENGTH_SHORT).show();
                                        loadCurrentList();
                                    }
                                });
                            }
                        });
                    }
                }, null);
                itemDialog.show();
            }
        });

        handleActivityStart(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_list:
                TextPromptDialog.build(this, "Add list", "", "New list name", "", new TextPromptOnEnterListener() {
                    @Override
                    public void onEnter(String text, DialogInterface dialog, int id) {
                        HashMap<String, String> classParams = new HashMap<String, String>();

                        classParams.put("name", "To-do (" + text + ")");
                        classParams.put("color", "40ccff");
                        classParams.put("teacher", "");

                        final ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.loading));
                        APIClient.post(token, "classes/add", classParams, new APICallback() {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        AlertUtils.showConnectionFailureDialog(MainActivity.this, false);
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, JSONObject o) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, R.string.success_added_list, Toast.LENGTH_SHORT).show();
                                        fetchLists();
                                    }
                                });
                            }
                        });
                    }
                }, null).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                            AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
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

                    todoLists = new ArrayList<MHSClass>();

                    for (int i = 0; i < classesJSONArray.length(); i++) {
                        JSONObject classJSON = classesJSONArray.getJSONObject(i);
                        MHSClass classObj = MHSClass.fromJSON(classJSON);
                        if (classObj.isTodoClass()) {
                            todoLists.add(classObj);
                        }
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TodoClassAdapter adapter = new TodoClassAdapter(MainActivity.this, todoLists);
                            adapter.setDropDownViewResource(R.layout.item_dropdown_class);
                            mainSpinner.setAdapter(adapter);
                            mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    currentListClass = todoLists.get(i);
                                    if (mainTodoList.getAdapter() != null) {
                                        ((TodoAdapter) mainTodoList.getAdapter()).clear();
                                    }
                                    loadCurrentList();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                            if (todoLists.size() > 0) {
                                currentListClass = todoLists.get(0);
                                loadCurrentList();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
                }
            }
        });
    }

    public void loadCurrentList() {
        mainSwipeView.setRefreshing(true);
        APIClient.get(token, "homework/getForClass/" + currentListClass.ID, new HashMap<String, String>(), new APICallback() {
            @Override
            public void onFailure(Call call, Exception e) {
                AlertUtils.showConnectionFailureDialog(MainActivity.this, true);
            }

            @Override
            public void onResponse(Call call, JSONObject o) {
                try {
                    JSONArray homeworkJSONArray = o.getJSONArray("homework");

                    todoListItems = new ArrayList<Homework>();

                    for (int i = 0; i < homeworkJSONArray.length(); i++) {
                        JSONObject homeworkJSON = homeworkJSONArray.getJSONObject(i);
                        Homework homeworkObj = Homework.fromJSON(homeworkJSON);
                        todoListItems.add(homeworkObj);
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TodoAdapter adapter = new TodoAdapter(MainActivity.this, token, todoListItems);
                            mainTodoList.setAdapter(adapter);
                            mainSwipeView.setRefreshing(false);
                            mainTabs.getTabAt(mainTabs.getSelectedTabPosition()).select();
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
