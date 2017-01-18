package ismartdev.mn.bundan.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class MatchedUser {


    public String name;
    public String picture;


    public MatchedUser() {

    }

    public MatchedUser(String name, String picture) {
        this.name = name;
        this.picture = picture;

    }


}

