package Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSON {
    public static String getString(String jsonString, String key) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        return obj.getString(key);
    }

    public static String toJSON(String label, String str) {
        JSONObject obj = new JSONObject();
        obj.put(label, str);
        return obj.toString();
    }
}
