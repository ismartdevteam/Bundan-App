package ismartdev.mn.bundan.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ismartdev.mn.bundan.R;
import ismartdev.mn.bundan.models.UserGender;
import ismartdev.mn.bundan.views.SingleProductView;
import ismartdev.mn.bundan.views.UserHolder;


public class UserAdapter extends BaseAdapter {
    private final Context mContext;
    private final  List<UserGender> mListItems;

    public UserAdapter(Context context, List<UserGender> mListItems) {
        this.mContext = context;
        this.mListItems=mListItems;
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mListItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        // TODO Auto-generated method stub

        final UserGender item = (UserGender) getItem(position);
        UserHolder hol = null;
        if (v == null) {
            hol = new UserHolder();
            v = ((Activity) mContext).getLayoutInflater().inflate(
                    R.layout.user_item, null);
            hol.userGender=item;
            hol.image=(ImageView)v.findViewById(R.id.user_image);
            hol.singleProductView=(SingleProductView)v.findViewById(R.id.user_main_layout);

            hol.nameAndAge=(TextView) v.findViewById(R.id.user_name);
            v.setTag(hol);
        } else{

            hol = (UserHolder) v.getTag();
        }

        hol.nameAndAge.setText(item.getName() + ","+item.getAge());
        Picasso.with(mContext).load("https://fb-s-c-a.akamaihd.net/h-ak-xfl1/v/t1.0-1/p200x200/15401145_1281606031862284_1941323810479010211_n.jpg?oh=a6f82189e71ea0283336f47075543967&oe=58D6C7AF&__gda__=1495177283_29a4a45ba65d964173cb888870283f9e").into(hol.image);

        return v;
    }


}
