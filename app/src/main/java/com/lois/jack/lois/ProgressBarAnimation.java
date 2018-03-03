package com.lois.jack.lois;

import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.github.lzyzsd.circleprogress.DonutProgress;

/**
 * Created by Jack on 03/03/2018.
 */

public class ProgressBarAnimation extends Animation {
    private DonutProgress progressBar;
    private float from;
    private float  to;

    public ProgressBarAnimation(DonutProgress progressBar, float from, float to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }

}
