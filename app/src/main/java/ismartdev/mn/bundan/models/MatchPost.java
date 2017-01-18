package ismartdev.mn.bundan.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class MatchPost {


    private String uid;
    @SerializedName("code")
    private int code;
    private String interUid;


    public MatchPost(String uid, String interUid) {

        this.uid = uid;
        this.interUid = interUid;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

