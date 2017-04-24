package ismartdev.mn.bundan.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class MatchPost {


    private String uid;
    @SerializedName("code")
    private int code =0;
    private String inter_uid;


    public MatchPost(String uid, String inter_uid) {

        this.uid = uid;
        this.inter_uid = inter_uid;
    }


    public int getCode() {

            return code;

    }

    public void setCode(int code) {
        this.code = code;
    }
}

