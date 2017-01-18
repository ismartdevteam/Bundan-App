package ismartdev.mn.bundan.models;

import com.google.firebase.database.Exclude;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class AgeRanges {


    public String uid;


    public AgeRanges() {

    }

    public AgeRanges(String uid) {
        this.uid = uid;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);


        return result;
    }
}

