package io.github.thatoddmailbox.tasky.data;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public int ID;
    public String Name;
    public String Username;
    public String Email;
    public String Type;

    public static User fromJSON(JSONObject o) throws JSONException {
        User u = new User();

        u.ID = o.getInt("id");
        u.Name = o.getString("name");
        u.Username = o.getString("username");
        u.Email = o.getString("email");
        u.Type = o.getString("type");

        return u;
    }
}