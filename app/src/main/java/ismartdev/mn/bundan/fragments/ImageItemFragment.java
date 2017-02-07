package ismartdev.mn.bundan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ismartdev.mn.bundan.R;

/**
 * Created by Ulziiburen on 1/2/16.
 */
public class ImageItemFragment extends Fragment {
    private static final String ARG_PARAM1 = "image";
    private String mParam1;

    public static ImageItemFragment newInstance(String param1) {
        ImageItemFragment fragment = new ImageItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);

        fragment.setArguments(args);
        return fragment;
    }
    public ImageItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.tinder_card_image_item,container,false);
        ImageView imageView= (ImageView) view.findViewById(R.id.image_item);
        Picasso.with(getActivity()).load(mParam1).into(imageView);
        return view;
    }
}
