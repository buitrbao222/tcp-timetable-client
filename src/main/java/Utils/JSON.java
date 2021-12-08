package Utils;

import Socket.Encryption.Crypto;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Key;

public class JSON {
    public static String getString(String jsonString, String key) throws JSONException {
        JSONObject obj = new JSONObject(jsonString);
        return obj.getString(key);
    }

    public static String toJSON(String label, Key key) {
        JSONObject obj = new JSONObject();
        obj.put(label, Crypto.keyToString(key));
        return obj.toString();
    }

    public static String toJSON(String label, String str) {
        JSONObject obj = new JSONObject();
        obj.put(label, str);
        return obj.toString();
    }

    /*public static List<Subject> parseSubjects(String jsonString) throws Exception {
        JSONObject obj = new JSONObject(jsonString);
        if (!obj.isNull("error")) {
            throw new Exception(obj.getString("error").toString());
        }
        return Subject.getSubjects(obj.getJSONObject("data"));
    }

    public static JSONObject toJSON(List<TimeTable> timeTables, String errorMessage) {
        JSONObject obj = new JSONObject();
        JSONArray timeTableArr = new JSONArray();
        for (TimeTable t : timeTables) {
            timeTableArr.put(t.toJSON());
        }
        obj.put("error", errorMessage);
        obj.put("timeTables", timeTableArr);
        return obj;
    }*/
}
