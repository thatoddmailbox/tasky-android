package io.github.thatoddmailbox.tasky.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MHSClass {
    public int ID;
    public String Name;
    public String Teacher;
    public String Color;
    public int UserID;

    private final Pattern todoPattern = Pattern.compile("To-do \\((.*)\\)");

    public boolean isTodoClass() {
        Matcher m = todoPattern.matcher(this.Name);
        return m.find();
    }

    public String getTodoName() {
        Matcher m = todoPattern.matcher(this.Name);
        if (!m.find()) {
            return null;
        }
        return m.group(1);
    }

    public static MHSClass fromJSON(JSONObject o) throws JSONException {
        MHSClass c = new MHSClass();

        c.ID = o.getInt("id");
        c.Name = o.getString("name");
        c.Teacher = o.getString("teacher");
        c.Color = o.getString("color");
        c.UserID = o.getInt("userId");

        return c;
    }
}
