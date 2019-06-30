package io.github.thatoddmailbox.tasky.misc;

import android.app.Activity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.github.thatoddmailbox.tasky.R;

public class TextPromptDialog {
    public static AlertDialog build(Activity a, String title, String text, String placeholder, final TextPromptOnEnterListener onEnter, final DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(a);

        LayoutInflater inflater = a.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_text_prompt, null);

        final TextView infoView = (TextView)dialogView.findViewById(R.id.text_prompt_info);
        final EditText inputText = (EditText)dialogView.findViewById(R.id.text_prompt_input);

        if (text.equals("")) {
            infoView.setVisibility(View.GONE);
        } else {
            infoView.setVisibility(View.VISIBLE);
        }

        infoView.setText(text);
        inputText.setHint(placeholder);
        inputText.requestFocus();

        builder.setView(dialogView);

        builder.setTitle(title).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (onEnter != null) {
                    onEnter.onEnter(inputText.getText().toString(), dialog, id);
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (onCancel != null) {
                    onCancel.onClick(dialog, id);
                }
            }
        });

        return builder.create();
    }
}