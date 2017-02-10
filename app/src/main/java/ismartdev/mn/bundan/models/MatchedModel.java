package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class MatchedModel {

    public Messages last_message;
    public long matched_date;
    public Map<String, String> createDate;
    public List<String> uids;
    public MatchedModel() {

    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("matched_date", createDate);
        result.put("uids", uids);



        return result;
    }


}
