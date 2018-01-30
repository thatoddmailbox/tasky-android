package io.github.thatoddmailbox.tasky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.data.Homework;
import io.github.thatoddmailbox.tasky.data.MHSClass;

public class TodoAdapter extends ArrayAdapter<Homework> {
    public TodoAdapter(Context context, ArrayList<Homework> homework) {
        super(context, 0, homework);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Homework homeworkObj = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.item_todo_name);
        CheckBox done = (CheckBox) convertView.findViewById(R.id.item_todo_done);

        name.setText(homeworkObj.Name);
        done.setChecked(homeworkObj.Complete);

        return convertView;
    }
}
