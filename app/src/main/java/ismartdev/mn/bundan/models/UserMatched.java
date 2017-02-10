package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class UserMatched  {

    private Map<String, String> createDate;

    private String match_id;


    public UserMatched() {

    }

    public UserMatched( Map<String, String> createDate,String match_id) {

        this.match_id = match_id;
        this.createDate = createDate;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("match_id", match_id);
        result.put("date", createDate);



        return result;
    }

}
