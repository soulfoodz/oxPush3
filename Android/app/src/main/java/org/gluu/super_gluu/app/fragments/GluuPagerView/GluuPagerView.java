package org.gluu.super_gluu.app.fragments.GluuPagerView;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by nazaryavornytskyy on 5/11/16.
 */
public class GluuPagerView extends ViewPager {

    private boolean swipeLocked;

    public GluuPagerView(Context context) {
        super(context);
    }

    public GluuPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean getSwipeLocked() {
        return swipeLocked;
    }

    public void setSwipeLocked(boolean swipeLocked) {
        this.swipeLocked = swipeLocked;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !swipeLocked && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !swipeLocked && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int direction) {
        return !swipeLocked && super.canScrollHorizontally(direction);
    }

}
