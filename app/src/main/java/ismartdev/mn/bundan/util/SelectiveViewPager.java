package ismartdev.mn.bundan.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Ulzii on 1/16/2017.
 */

public class SelectiveViewPager extends ViewPager {
    private boolean paging = true;

    public SelectiveViewPager(Context context) {
        super(context);
    }

    public SelectiveViewPager(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (paging) {
            return super.onInterceptTouchEvent(e);
        }

        return false;
    }

    public void setPaging(boolean p){ paging = p; }

}