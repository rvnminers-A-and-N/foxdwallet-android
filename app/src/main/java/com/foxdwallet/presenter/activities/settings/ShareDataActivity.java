package com.foxdwallet.presenter.activities.settings;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.foxdwallet.R;
import com.foxdwallet.presenter.activities.util.BRActivity;
import com.foxdwallet.tools.manager.BRSharedPrefs;

public class ShareDataActivity extends BRActivity {
    private static final String TAG = ShareDataActivity.class.getName();
    private ToggleButton toggleButton;
    public static boolean appVisible = false;
    private static ShareDataActivity app;

    public static ShareDataActivity getApp() {
        return app;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setChecked(BRSharedPrefs.getShareData(this));
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BRSharedPrefs.putShareData(ShareDataActivity.this, isChecked);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

}
