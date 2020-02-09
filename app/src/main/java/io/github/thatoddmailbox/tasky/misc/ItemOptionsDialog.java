package io.github.thatoddmailbox.tasky.misc;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

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
        String[] options = {
                "Edit",
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(item.Name);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Edit
                    ItemOptionsDialog.editItem(token, ctx, item, mainActivity);
                }
            }
        });
        return builder.create();
    }
}
