package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class Messages  {


    public long date;
    public Map<String, String> sendDate;
    public String message;

    public String senderId;

    public Messages() {

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", sendDate);
        result.put("message", message);
        result.put("senderId", senderId);
        return result;
    }
}
