package io.github.thatoddmailbox.tasky.misc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import io.github.thatoddmailbox.tasky.R;

public class AlertUtils {
    public static void showFailureDialog(Activity a, int titleId, int messageId, boolean finishOnDismiss) {
        showFailureDialog(a, a.getString(titleId), a.getString(messageId), finishOnDismiss);
    }

    public static void showFailureDialog(final Activity a, final String title, final String message, final boolean finishOnDismiss) {
        a.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = new AlertDialog.Builder(a)
                        .setTitle(title)
                        .setMessage(message)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (finishOnDismiss) {
                                    a.finish();
                                }
                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (finishOnDismiss) {
                                    a.finish();
                                }
                            }
                        })
                        .create();

                dialog.show();
            }
        });
    }

    public static void showConnectionFailureDialog(Activity a, boolean finishOnDismiss) {
        showFailureDialog(a, R.string.error_generic_title, R.string.error_no_connect_msg, finishOnDismiss);
    }
}
