package ismartdev.mn.bundan.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.util.CircleImageView;

/**
 * Created by Ulzii on 11/13/2016.
 */

public class MatchViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView userImage;


    public MatchViewHolder(View itemView) {
        super(itemView);

        userImage = (CircleImageView) itemView.findViewById(R.id.match_image);



    }


}
