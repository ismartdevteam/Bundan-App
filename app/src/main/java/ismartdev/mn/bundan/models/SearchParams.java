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
}
