package com.mmoteam.twotapp.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mmoteam.twotapp.R;

/**
 * Created by DroidOXY
 */


public class TintOnStateImageView extends AppCompatImageView
{
    private ColorStateList mColorStateList;

    public TintOnStateImageView(Context context)
    {
        super(context);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(context, attrs, 0);
    }

    public TintOnStateImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialise(context, attrs, defStyleAttr);
    }

    /**
     * Create, bind and set up the resources
     *
     * @param context is the context to get the resources from
     * @param attributeSet is the attributeSet
     * @param defStyle is the style
     */
    private void initialise(Context context, AttributeSet attributeSet, int defStyle)
    {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.TintOnStateImageView, defStyle, 0);
        mColorStateList = a.getColorStateList(R.styleable.TintOnStateImageView_colorStateList);
        a.recycle();
    }

    @Override
    protected void drawableStateChanged()
    {
        super.drawableStateChanged();

        if (mColorStateList != null && mColorStateList.isStateful())
        {
            updateTintColor();
        }
    }

    /**
     * Updates the color of the image
     */
    private void updateTintColor()
    {
        int color = mColorStateList.getColorForState(getDrawableState(),
                getResources().getColor(R.color.nav_drawer_item_icon_normal));

        super.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
