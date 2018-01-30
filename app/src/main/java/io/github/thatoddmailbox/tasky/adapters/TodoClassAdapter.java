package io.github.thatoddmailbox.tasky.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.data.MHSClass;

public class TodoClassAdapter extends ArrayAdapter<MHSClass> {
    public TodoClassAdapter(Context context, ArrayList<MHSClass> users) {
        super(context, 0, users);
    }

    public View handleView(int resource, int position, View convertView, ViewGroup parent) {
        MHSClass classObj = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.item_class_name);

        name.setText(classObj.getTodoName());

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return handleView(R.layout.item_class, position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return handleView(R.layout.item_dropdown_class, position, convertView, parent);
    }
}
