package io.github.thatoddmailbox.tasky.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Homework {
    public int ID;
    public String Name;
    public String Due;
    public String Desc;
    public boolean Complete;
    public int ClassID;

    public static Homework fromJSON(JSONObject o) throws JSONException {
        Homework u = new Homework();

        u.ID = o.getInt("id");
        u.Name = o.getString("name");
        u.Due = o.getString("due");
        u.Desc = o.getString("desc");
        u.Complete = (o.getInt("complete") == 1);
        u.ClassID = o.getInt("classId");

        return u;
    }
}