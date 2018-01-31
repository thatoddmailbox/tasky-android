package io.github.thatoddmailbox.tasky.misc;

import android.content.DialogInterface;

public interface TextPromptOnEnterListener {
    abstract void onEnter(String text, DialogInterface dialog, int id);
}