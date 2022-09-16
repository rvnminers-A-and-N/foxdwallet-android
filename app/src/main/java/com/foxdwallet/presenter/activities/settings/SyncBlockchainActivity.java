package com.foxdwallet.presenter.activities.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.foxdwallet.R;
import com.foxdwallet.presenter.activities.util.BRActivity;
import com.foxdwallet.presenter.customviews.BRDialogView;
import com.foxdwallet.tools.animation.BRAnimator;
import com.foxdwallet.tools.animation.BRDialog;
import com.foxdwallet.tools.manager.BRSharedPrefs;
import com.foxdwallet.tools.threads.executor.BRExecutor;
import com.foxdwallet.tools.util.BRConstants;
import com.foxdwallet.wallet.WalletsMaster;


public class SyncBlockchainActivity extends BRActivity {
    private static final String TAG = SyncBlockchainActivity.class.getName();
    private Button mRescanButton;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_blockchain);

        ImageButton faq = (ImageButton) findViewById(R.id.faq_button);

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRAnimator.showSupportFragment(SyncBlockchainActivity.this, BRConstants.reScan);
            }
        });

        mRescanButton = (Button) findViewById(R.id.button_scan);
        mRescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                BRDialog.showCustomDialog(SyncBlockchainActivity.this, getString(R.string.ReScan_alertTitle),
                        getString(R.string.ReScan_footer), getString(R.string.ReScan_alertAction), getString(R.string.Button_cancel),
                        new BRDialogView.BROnClickListener() {
                            @Override
                            public void onClick(BRDialogView brDialogView) {
                                brDialogView.dismissWithAnimation();
                                rescanClicked();
                            }
                        }, new BRDialogView.BROnClickListener() {
                            @Override
                            public void onClick(BRDialogView brDialogView) {
                                brDialogView.dismissWithAnimation();
                            }
                        }, null, 0);


            }
        });

    }

    private void rescanClicked() {
        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
            @Override
            public void run() {
                Activity app = SyncBlockchainActivity.this;
                BRSharedPrefs.putStartHeight(app, BRSharedPrefs.getCurrentWalletIso(app), 0);
                BRSharedPrefs.putAllowSpend(app, BRSharedPrefs.getCurrentWalletIso(app), false);
                WalletsMaster.getInstance(app).getCurrentWallet(app).rescan(app);
                BRAnimator.startfoxdActivity(app, false);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
