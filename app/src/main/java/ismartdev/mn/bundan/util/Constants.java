package ismartdev.mn.bundan.util;

/**
 * Created by Ulzii on 7/18/2016.
 */
public class Constants {
    public static final String user = "/user";
    public static final String url = "https://bundan-e28d3.appspot-preview.com/";
    public static final String fcm = "fcm";
    public static final String matches = "user_info/matches/";
    public static final String user_matches = "user-matches";
    public static final String sp_search = "sp_search";
    public static final String sp_app = "sp_app";
    public static final String app_run = "app_run";
    public static final String female = "female";
    public static final String user_settings = "user_settings";
    public static final String user_info = "user_info";
    public static final String male = "male";
    public static final String search_ranges = "/search-ranges-";

    public static String getInteractName(boolean isLiked) {
        if (isLiked)
            return "-likes/";
        else
            return "-dislikes/";

    }
    public static String getGenderEng(String gender) {
        if (gender.equals("Эмэгтэй"))
            return "female";
        else
            return "male";

    }
    public static String getGenderMN(String gender) {
        if (gender.equals("female"))
            return "Эмэгтэй";
        else
            return "Эрэгтэй";

    }

}
