package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class UserMatched {

    private boolean isChat=false;
    private Map<String, String> date;


    public UserMatched() {

    }

    public UserMatched(boolean isChat, Map<String, String> date) {
        this.isChat = isChat;
        this.date = date;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("isChat", isChat);


        return result;
    }
}
