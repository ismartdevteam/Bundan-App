package ismartdev.mn.bundan.models;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class SearchParams {
    private String uid;
    private String age_range;
    private String limit;
    private String gender;

    public SearchParams(String age_range, String gender, String limit, String uid) {
        this.age_range = age_range;
        this.gender = gender;
        this.limit = limit;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAge_range() {
        return age_range;
    }

    public void setAge_range(String age_range) {
        this.age_range = age_range;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
