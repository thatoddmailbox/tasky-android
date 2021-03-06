package io.github.thatoddmailbox.tasky.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.thatoddmailbox.tasky.R;
import io.github.thatoddmailbox.tasky.activities.MainActivity;
import io.github.thatoddmailbox.tasky.api.APICallback;
import io.github.thatoddmailbox.tasky.api.APIClient;
import io.github.thatoddmailbox.tasky.data.Homework;
import io.github.thatoddmailbox.tasky.misc.ItemOptionsDialog;
import okhttp3.Call;

public class TodoAdapter extends ArrayAdapter<Homework> implements Filterable {
    private String _token;
    private final List<Homework> _items;
    private final MainActivity _mainActivity;

    public TodoAdapter(MainActivity mainActivity, String token, ArrayList<Homework> homework) {
        super(mainActivity, 0, homework);
        _token = token;
        _items = (List<Homework>)homework.clone();
        _mainActivity = mainActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Homework homeworkObj = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemView) {
                CheckBox checkbox = (CheckBox) ((ViewGroup) itemView).getChildAt(0);

                Homework itemHomework = (Homework) itemView.getTag();

                itemHomework.Complete = !itemHomework.Complete;;

                itemView.setTag(itemHomework);
                checkbox.setChecked(itemHomework.Complete);

                setCheckedMode(itemView, checkbox.isChecked());

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
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Homework homework = (Homework) view.getTag();

                final AlertDialog dialog = ItemOptionsDialog.build(_token, getContext(), homework, _mainActivity);
                dialog.show();

                return true;
            }
        });

        convertView.setTag(homeworkObj);

        CheckBox done = (CheckBox) convertView.findViewById(R.id.item_todo_done);

        done.setText(homeworkObj.Name);
        setCheckedMode(convertView, homeworkObj.Complete);

        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    clear();
                    addAll((ArrayList<Homework>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<Homework> filteredHomeworkItems = new ArrayList<Homework>();

                boolean showAll = (constraint.length() == 0);

                for (int i = 0; i < _items.size(); i++) {
                    Homework homeworkObj = _items.get(i);
                    if (showAll) {
                        filteredHomeworkItems.add(homeworkObj);
                    } else {
                        if (!homeworkObj.Complete) {
                            filteredHomeworkItems.add(homeworkObj);
                        }
                    }
                }

                results.count = filteredHomeworkItems.size();
                results.values = filteredHomeworkItems;

                return results;
            }
        };

        return filter;
    }

    public void setCheckedMode(View itemView, boolean checked) {
        CheckBox done = (CheckBox) itemView.findViewById(R.id.item_todo_done);

        done.setChecked(checked);

        if (checked) {
            done.setTypeface(null, Typeface.ITALIC);
            done.setPaintFlags(done.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            done.setTypeface(null, Typeface.NORMAL);
            done.setPaintFlags(done.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
