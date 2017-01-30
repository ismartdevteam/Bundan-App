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

    private Map<String, String> createDate;

    private long date;
    private String uid;


    public UserMatched() {

    }

    public UserMatched(boolean isChat, Map<String, String> createDate,String uid) {
        this.isChat = isChat;
        this.uid = uid;
        this.createDate = createDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("date", createDate);

//        result.put("isChat", isChat);


        return result;
    }
    public boolean isChat() {
        return isChat;
    }

    public void setChat(boolean chat) {
        isChat = chat;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
