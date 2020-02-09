package io.github.thatoddmailbox.tasky.misc;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import io.github.thatoddmailbox.tasky.R;

public class TextPromptDialog {
    public static AlertDialog build(Context ctx, String title, String description, String placeholder, String currentText, final TextPromptOnEnterListener onEnter, final DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View dialogView = inflater.inflate(R.layout.dialog_text_prompt, null);

        final TextView infoView = (TextView)dialogView.findViewById(R.id.text_prompt_info);
        final EditText inputText = (EditText)dialogView.findViewById(R.id.text_prompt_input);

        if (description.isEmpty()) {
            infoView.setVisibility(View.GONE);
        } else {
            infoView.setVisibility(View.VISIBLE);
        }

        infoView.setText(description);
        inputText.setHint(placeholder);
        if (!currentText.isEmpty()) {
            inputText.setText(currentText);
        }
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