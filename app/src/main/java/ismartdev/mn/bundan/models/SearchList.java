package ismartdev.mn.bundan.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ulzii on 1/11/2017.
 */

public class SearchList {
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private List<UserGender> data;

    public List<UserGender> getData() {
        return data;
    }

    public void setData(List<UserGender> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int response) {
        this.code = response;
    }
}
