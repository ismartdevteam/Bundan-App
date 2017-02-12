package ismartdev.mn.bundan.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ulzii on 7/20/2016.
 */
@IgnoreExtraProperties
public class ListMessages {

    public HashMap<String,Messages> messages;
    public long matched_date;

}
