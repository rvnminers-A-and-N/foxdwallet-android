package com.foxdwallet.presenter.activities.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foxdwallet.R;
import com.foxdwallet.core.BRCorePeer;
import com.foxdwallet.presenter.activities.util.BRActivity;
import com.foxdwallet.tools.animation.BRAnimator;
import com.foxdwallet.tools.manager.BRSharedPrefs;
import com.foxdwallet.tools.threads.executor.BRExecutor;
import com.foxdwallet.tools.util.BRConstants;
import com.foxdwallet.tools.util.TrustedNode;
import com.foxdwallet.tools.util.Utils;
import com.foxdwallet.wallet.FoxdWalletManager;
import com.foxdwallet.wallet.WalletsMaster;

import static com.foxdwallet.tools.util.BRConstants.getNodePort;

public class NodesActivity extends BRActivity {
    private static final String TAG = NodesActivity.class.getName();
    private Button switchButton;
    private TextView nodeStatus;
    private TextView trustNode;
    public static boolean appVisible = false;
    AlertDialog mDialog;
    private int mInterval = 3000;
    private Handler mHandler;
    private boolean updatingNode;
//    private TextView nodeLabel;

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                //this function can change value of mInterval.
                updateButtonText();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };
    private static NodesActivity app;


    public static NodesActivity getApp() {
        return app;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nodes);

        ImageButton faq = findViewById(R.id.faq_button);
        faq.setVisibility(View.GONE);

        BRSharedPrefs.putCurrentWalletIso(this, "foxd");

        nodeStatus = findViewById(R.id.node_status);
        trustNode = findViewById(R.id.node_text);

        switchButton = findViewById(R.id.button_switch);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BRAnimator.isClickAllowed()) return;
                final Activity app = NodesActivity.this;
                final FoxdWalletManager wm = FoxdWalletManager.getInstance(NodesActivity.this);

                if (BRSharedPrefs.getTrustNode(app, wm.getIso(app)).isEmpty()) {
                    createDialog();
                } else {
                    if (!updatingNode) {
                        updatingNode = true;
                        BRSharedPrefs.putTrustNode(app, wm.getIso(app), "");
                        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                WalletsMaster.getInstance(app).updateFixedPeer(app, wm);
                                updatingNode = false;
                                BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateButtonText();
                                    }
                                });

                            }
                        });
                    }

                }

            }
        });

        updateButtonText();

    }

    private void updateButtonText() {
        FoxdWalletManager wm = FoxdWalletManager.getInstance(this);
        if (BRSharedPrefs.getTrustNode(this, wm.getIso(this)).isEmpty()) {
            switchButton.setText(getString(R.string.NodeSelector_manualButton));
        } else {
            switchButton.setText(getString(R.string.NodeSelector_automaticButton));
        }
        nodeStatus.setText(wm.getPeerManager().getConnectStatus() == BRCorePeer.ConnectStatus.Connected ? getString(R.string.NodeSelector_connected) : getString(R.string.NodeSelector_notConnected));
        if (trustNode != null)
            trustNode.setText(wm.getPeerManager().getCurrentPeerName());
    }

    private void createDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(app);
        final TextView customTitle = new TextView(this);

        customTitle.setGravity(Gravity.CENTER);
        customTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        int pad32 = Utils.getPixelsFromDps(app, 32);
        int pad16 = Utils.getPixelsFromDps(app, 16);
        customTitle.setPadding(pad16, pad16, pad16, pad16);
        customTitle.setText(getString(R.string.NodeSelector_enterTitle));
        customTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        customTitle.setTypeface(null, Typeface.BOLD);
        alertDialog.setCustomTitle(customTitle);
        alertDialog.setMessage(getString(R.string.NodeSelector_enterBody));

        final EditText input = new EditText(app);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        int pix = Utils.getPixelsFromDps(app, 24);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
//        input.setKeyListener(DigitsKeyListener.getInstance("0123456789."));
        input.setPadding(pix, 0, pix, pix);
        input.setLayoutParams(lp);
        InputFilter ipFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       android.text.Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart)
                            + source.subSequence(start, end)
                            + destTxt.substring(dend);
                    if (!resultingTxt.matches(BRConstants.IP_REGEX)) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i = 0; i < splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }

        };

        input.setFilters(new InputFilter[]{ipFilter});
        alertDialog.setView(input);

        alertDialog.setNegativeButton(getString(R.string.Button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton(getString(R.string.Button_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        mDialog = alertDialog.show();
//
//        //Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = input.getText().toString();
                str += ":" + getNodePort();
                final FoxdWalletManager wm = FoxdWalletManager.getInstance(app);
                if (TrustedNode.isValid(str)) {
                    mDialog.setMessage("");
                    BRSharedPrefs.putTrustNode(app, wm.getIso(app), str);
                    if (!updatingNode) {
                        updatingNode = true;
                        customTitle.setText(getString(R.string.Webview_updating));
                        BRExecutor.getInstance().forLightWeightBackgroundTasks().execute(new Runnable() {
                            @Override
                            public void run() {
                                WalletsMaster.getInstance(app).updateFixedPeer(app, wm);
                                updatingNode = false;
                                BRExecutor.getInstance().forMainThreadTasks().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        customTitle.setText(getString(R.string.RecoverWallet_done));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mDialog.dismiss();
                                                updateButtonText();
                                            }
                                        }, 500);

                                    }
                                });
                            }
                        });
                    }

                } else {
                    customTitle.setText("Invalid Node");
                    customTitle.setTextColor(app.getColor(R.color.warning_color));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            customTitle.setText(getString(R.string.NodeSelector_enterTitle));
                            customTitle.setTextColor(app.getColor(R.color.almost_black));
                        }
                    }, 1000);
                }
                updateButtonText();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                input.requestFocus();
                final InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(input, 0);
            }
        }, 200);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
        mHandler = new Handler();
        startRepeatingTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
        stopRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
