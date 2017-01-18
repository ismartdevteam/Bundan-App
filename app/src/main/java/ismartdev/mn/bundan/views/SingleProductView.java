package ismartdev.mn.bundan.views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import ismartdev.mn.bundan.R;


public class SingleProductView extends RelativeLayout implements ProductStackView.ProductStackListener {

    public SingleProductView(Context context) {
        super(context);
    }

    public SingleProductView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SingleProductView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getRootView().findViewById(R.id.yesicon).setAlpha((float) 0);
        getRootView().findViewById(R.id.noicon).setAlpha((float) 0);
    }

    @Override
    public void onUpdateProgress(boolean positif, float percent, View view) {
        if (positif) {
            view.findViewById(R.id.yes).setAlpha(percent);
            view.findViewById(R.id.yesicon).setAlpha(percent);
        } else {
            view.findViewById(R.id.no).setAlpha(percent);
            view.findViewById(R.id.noicon).setAlpha(percent);
        }

    }

    @Override
    public void onCancelled(View view) {
        view.findViewById(R.id.yes).setAlpha(0);
        view.findViewById(R.id.no).setAlpha(0);
        view.findViewById(R.id.yesicon).setAlpha((float) 0);
        view.findViewById(R.id.noicon).setAlpha((float) 0);
    }

    @Override
    public void onChoiceMade(boolean choice, View view) {

        view.findViewById(R.id.yes).setAlpha(0);
        view.findViewById(R.id.no).setAlpha(0);
        view.findViewById(R.id.yesicon).setAlpha((float) 0);
        view.findViewById(R.id.noicon).setAlpha((float) 0);
    }
}