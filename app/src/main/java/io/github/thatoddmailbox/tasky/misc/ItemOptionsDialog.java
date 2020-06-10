package io.github.thatoddmailbox.tasky.misc;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.activities.MainActivity;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.Homework;
import okhttp3.Call;

public class ItemOptionsDialog {
    private static void editItem(final String token, final Context ctx, final Homework item, final MainActivity mainActivity) {
        final AlertDialog dialog = TextPromptDialog.build(ctx, "Edit item", "", "Something to do", item.Name, new TextPromptOnEnterListener() {
            @Override
            public void onEnter(String text, DialogInterface dialog, int id) {
                final ProgressDialog progressDialog = ProgressDialog.show(ctx, "", ctx.getString(R.string.loading));

                HashMap<String, String> homeworkParams = new HashMap<String, String>();

                homeworkParams.put("id", Integer.toString(item.ID));
                homeworkParams.put("name", text);
                homeworkParams.put("due", item.Due);
                homeworkParams.put("desc", item.Desc);
                homeworkParams.put("complete", (item.Complete ? "1" : "0"));
                homeworkParams.put("classId", Integer.toString(item.ClassID));

                APIClient.post(token, "homework/edit", homeworkParams, new APICallback() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ctx, R.string.error_generic_title, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, JSONObject o) {
                        progressDialog.dismiss();
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainActivity.loadCurrentList();
                            }
                        });
                    }
                });
            }
        }, null);
        dialog.show();
    }

    public static AlertDialog build(final String token, final Context ctx, final Homework item, final MainActivity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(item.Name);

        View content = LayoutInflater.from(ctx).inflate(R.layout.dialog_item_options, null);

        // details
        TextView details = content.findViewById(R.id.item_details);
        final DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final DateFormat localFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        String dueText = "(could not parse)";
        try {
            Date parsedDate = isoFormat.parse(item.Due);
            if (parsedDate != null) {
                dueText = localFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        details.setText(String.format(ctx.getString(R.string.added_on), dueText));

        // options
        final ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(ctx, R.layout.item_option, new String[] {
            "Edit"
        });
        final ListView options = content.findViewById(R.id.item_options);
        options.setAdapter(optionAdapter);
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int which, long id) {
                if (which == 0) {
                    // Edit
                    ItemOptionsDialog.editItem(token, ctx, item, mainActivity);
                }
            }
        });

        builder.setView(content);

        return builder.create();
    }
}
