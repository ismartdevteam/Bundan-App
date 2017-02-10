package ismartdev.mn.bundan.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class MessagePost {


    private String uid;
    @SerializedName("code")
    private int code;
    private String matchId;
    private String message;
    private String name;


    public MessagePost(String uid, String matchId, String message, String name) {

        this.uid = uid;
        this.matchId = matchId;
        this.message = message;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

