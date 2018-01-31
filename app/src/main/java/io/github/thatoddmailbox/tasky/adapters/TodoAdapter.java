package io.github.thatoddmailbox.tasky.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.Homework;
import io.github.thatoddmailbox.tasky.data.MHSClass;
import io.github.thatoddmailbox.tasky.misc.AlertUtils;
import okhttp3.Call;

public class TodoAdapter extends ArrayAdapter<Homework> {
    private String _token;

    public TodoAdapter(Context context, String token, ArrayList<Homework> homework) {
        super(context, 0, homework);
        _token = token;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Homework homeworkObj = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.item_todo_name);
        CheckBox done = (CheckBox) convertView.findViewById(R.id.item_todo_done);

        name.setText(homeworkObj.Name);
        setCheckedMode(convertView, homeworkObj.Complete);

        convertView.setTag(homeworkObj);

        done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                View itemView = (View) compoundButton.getParent().getParent();
                Homework itemHomework = (Homework) itemView.getTag();

                itemHomework.Complete = checked;

                itemView.setTag(itemHomework);

                setCheckedMode(itemView, checked);

                HashMap<String, String> homeworkParams = new HashMap<String, String>();

                homeworkParams.put("id", Integer.toString(itemHomework.ID));
                homeworkParams.put("name", itemHomework.Name);
                homeworkParams.put("due", itemHomework.Due);
                homeworkParams.put("desc", itemHomework.Desc);
                homeworkParams.put("complete", (itemHomework.Complete ? "1" : "0"));
                homeworkParams.put("classId", Integer.toString(itemHomework.ClassID));

                APIClient.post(_token, "homework/edit", homeworkParams, new APICallback() {
                    @Override
                    public void onFailure(Call call, Exception e) {
                        Toast.makeText(getContext(), R.string.error_generic_title, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, JSONObject o) {

                    }
                });
            }
        });

        return convertView;
    }

    public void setCheckedMode(View itemView, boolean checked) {
        TextView name = (TextView) itemView.findViewById(R.id.item_todo_name);
        CheckBox done = (CheckBox) itemView.findViewById(R.id.item_todo_done);

        done.setChecked(checked);

        if (checked) {
            name.setTypeface(null, Typeface.ITALIC);
            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            name.setTypeface(null, Typeface.NORMAL);
            name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
