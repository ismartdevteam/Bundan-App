package ismartdev.mn.bundan.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ismartdev.mn.bundan.R;

/**
 * Created by Ulziiburen on 1/2/16.
 */
public class ImageTextFragment extends Fragment {
    private static final String ARG_PARAM1 = "text";
    private static final String ARG_PARAM2 = "image";
    private String text;
    private int imgRes;

    public static ImageTextFragment newInstance(String text, int resourse) {
        ImageTextFragment fragment = new ImageTextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, text);
        args.putInt(ARG_PARAM2, resourse);

        fragment.setArguments(args);
        return fragment;
    }

    public ImageTextFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_PARAM1);
            imgRes = getArguments().getInt(ARG_PARAM2);


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.walk_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.walk_item_image);
        imageView.setImageResource(imgRes);
        TextView textView = (TextView) view.findViewById(R.id.walk_item_text);
        textView.setText(text);
        return view;
    }
}
