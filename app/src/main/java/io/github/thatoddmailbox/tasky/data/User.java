package io.github.thatoddmailbox.tasky.data;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public int ID;
    public String Name;
    public String Email;

    public static User fromJSON(JSONObject o) throws JSONException {
        User u = new User();

        u.ID = o.getInt("id");
        u.Name = o.getString("name");
        u.Email = o.getString("email");

        return u;
    }
}