package com.foxdwallet.presenter.customviews;

import android.content.Context;
import android.util.AttributeSet;

import com.foxdwallet.R;

public class ScanButton extends IconButton {


    public ScanButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getIconId() {
        return R.drawable.ic_scan;
    }
}