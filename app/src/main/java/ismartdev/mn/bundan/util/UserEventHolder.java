package ismartdev.mn.bundan.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ismartdev.mn.bundan.R;


/**
 * Created by Ulzii on 11/13/2016.
 */

public class UserEventHolder extends RecyclerView.ViewHolder {
    public TextView nameText;

    public ImageView userImage;
    public View mView;

    public UserEventHolder(View itemView) {
        super(itemView);
        this.mView = itemView;

        nameText = (TextView) itemView.findViewById(R.id.user_name);

        userImage = (ImageView) itemView.findViewById(R.id.user_image);



    }


}
