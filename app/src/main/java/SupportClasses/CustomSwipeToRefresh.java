package SupportClasses;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.webkit.WebView;

import com.liveplatform.qalivestuff.R;

/**
 * Created by devanshu.kanik on 7/4/2016.
 */
public class CustomSwipeToRefresh extends SwipeRefreshLayout {

    private int mTouchSlop;
    WebView view;
    private Boolean touchedonce=false;
    private float mPrevX;

    public CustomSwipeToRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setSize(SwipeRefreshLayout.DEFAULT);
        setProgressBackgroundColorSchemeResource(R.color.LScolor);
        setColorSchemeColors(Color.WHITE);
    }

    public void setView(WebView view) {
        this.view = view;
    }


    @Override
    public boolean canChildScrollUp() {
        if(view!=null)
            return view.getScrollY() > 0;
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(touchedonce){
                    touchedonce=false;
                }
                else {
                    touchedonce=true;
                }
                mPrevX = MotionEvent.obtain(event).getX();
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                if(view.getScrollY()==0){
                    if (xDiff > mTouchSlop) {
                        return false;
                    }
                    return super.onInterceptTouchEvent(event);
                }
                if(!touchedonce){
                    if (xDiff > mTouchSlop) {
                        return false;
                    }
                    return super.onInterceptTouchEvent(event);
                }else{
                    return false;
                }


        }

        return super.onInterceptTouchEvent(event);
    }
}
