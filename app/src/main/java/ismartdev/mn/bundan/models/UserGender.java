package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class UserGender {


    private String uid;
    private String fb_id;

    private String name;
    private String birthday;



    private String age;
    private String picture;



    private String work;

    public UserGender() {

    }
    public UserGender(String uid, String birthday, String fb_id, String name,String work) {
        this.uid = uid;
        this.birthday = birthday;
        this.work = work;
        this.fb_id = fb_id;
        this.name = name;
    }
    public UserGender(String uid, String birthday, String fb_id, String name) {
        this.uid = uid;
        this.birthday = birthday;
        this.fb_id = fb_id;
        this.name = name;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("fb_id", fb_id);
        result.put("name", name);
        result.put("work", work);
        result.put("birthday", birthday);
        result.put("picture", picture);

        return result;
    }
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    public String getPicture() {
        return picture;
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFb_id() {
        return fb_id;
    }

    public void setFb_id(String fb_id) {
        this.fb_id = fb_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
