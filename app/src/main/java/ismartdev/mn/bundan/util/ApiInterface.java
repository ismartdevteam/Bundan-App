package ismartdev.mn.bundan.util;

import ismartdev.mn.bundan.models.MatchPost;
import ismartdev.mn.bundan.models.SearchList;
import ismartdev.mn.bundan.models.SearchParams;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Ulzii on 1/11/2017.
 */

public interface ApiInterface {

    @POST("getList")
    Call<SearchList> searchPeople(@Body SearchParams params);
    @POST("match")
    Call<MatchPost> matchPush(@Body MatchPost params);


}
