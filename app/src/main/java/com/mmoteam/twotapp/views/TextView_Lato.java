package com.mmoteam.twotapp.views;

import android.content.Context;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by DroidOXY
 */

public class TextView_Lato extends AppCompatTextView {

    public TextView_Lato(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextView_Lato(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextView_Lato(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Lato-Medium.ttf");
            setTypeface(tf);
        }
    }

}