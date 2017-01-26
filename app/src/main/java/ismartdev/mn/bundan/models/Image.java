package ismartdev.mn.bundan.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class Image {


    public String url;


    public Image() {

    }

    public Image(String url) {
        this.url = url;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("url", url);


        return result;
    }
}

