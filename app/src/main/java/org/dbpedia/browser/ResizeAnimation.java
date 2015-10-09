package org.dbpedia.browser;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation which resizes a view to a given height.
 */
public class ResizeAnimation extends Animation {
    final int startHeight;
    final int targetHeigth;
    View view;

    public ResizeAnimation(View view, int targetHeigth) {
        this.view = view;
        this.targetHeigth = targetHeigth;
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = (int) (startHeight + (targetHeigth - startHeight) * interpolatedTime);
        view.getLayoutParams().height = newWidth;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
