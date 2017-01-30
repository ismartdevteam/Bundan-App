package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class User implements java.io.Serializable {
    public String fb_id = "";
    public String name = "";
    public String birthday = "";
    public String gender = "";
    public String work = "";
    public String education = "";

    public String email = "";
    public List<String> picture = new ArrayList<>();
    public String user_friends;
    public String user_likes;

    public User() {

    }

    public User(String birthday, String education, String email, String gender, String fb_id,
                String name, List<String> picture, String work, String user_friends, String user_likes) {

        this.birthday = birthday;
        this.education = education;
        this.email = email;
        this.gender = gender;
        this.fb_id = fb_id;
        this.name = name;
        this.picture = picture;
        this.work = work;
        this.user_friends = user_friends;
        this.user_likes = user_likes;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fb_id", fb_id);
        result.put("name", name);
        result.put("birthday", birthday);
        result.put("email", email);
        result.put("gender", gender);
//        result.put("picture", picture);
        result.put("education", education);
        result.put("work", work);
        result.put("user_friends", user_friends);
        result.put("user_likes", user_likes);

        return result;
    }
}
