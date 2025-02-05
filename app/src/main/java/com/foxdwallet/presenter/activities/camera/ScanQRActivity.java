package com.foxdwallet.presenter.activities.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.legacy.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxdwallet.R;
import com.foxdwallet.presenter.activities.util.BRActivity;
import com.foxdwallet.tools.animation.SpringAnimator;
import com.foxdwallet.tools.qrcode.QRCodeReaderView;
import com.foxdwallet.wallet.WalletsMaster;
import com.foxdwallet.wallet.util.CryptoUriParser;


/**
 * RavenWallet
 * <p/>
 * Created by Mihail Gutan on <mihail@breadwallet.com> 3/29/17.
 * Copyright (c) 2017 breadwallet LLC
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
public class ScanQRActivity extends BRActivity implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {
    private static final String TAG = ScanQRActivity.class.getName();
    private ImageView cameraGuide;
    private TextView descriptionText;
    private long lastUpdated;
    private UIUpdateTask task;
    private boolean handlingCode;
    public static boolean appVisible = false;
    private static ScanQRActivity app;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 56432;

    private ViewGroup mainLayout;

    private QRCodeReaderView qrCodeReaderView;

    public final static String SCANNING_IPFS_HASH_EXTRA_KEY = "scanning.ipfs.hash.extra.key";
    private boolean isScanningIPFSHash;

    public static ScanQRActivity getApp() {
        return app;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        if (getIntent().getExtras() != null) {
            isScanningIPFSHash = getIntent().getExtras().getBoolean(SCANNING_IPFS_HASH_EXTRA_KEY, false);
        }

        cameraGuide = findViewById(R.id.scan_guide);
        descriptionText = findViewById(R.id.description_text);

        task = new UIUpdateTask();
        task.start();

        cameraGuide.setImageResource(R.drawable.cameraguide);
        cameraGuide.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
//            requestCameraPermission();
            Log.e(TAG, "onCreate: Permissions needed? HUH?");
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cameraGuide.setVisibility(View.VISIBLE);
                SpringAnimator.showExpandCameraGuide(cameraGuide);
            }
        }, 400);

    }


    @Override
    protected void onResume() {
        super.onResume();
        appVisible = true;
        app = this;
        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        appVisible = false;
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
        task.stopTask();
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.fade_down, 0);
        super.onBackPressed();
    }

    private class UIUpdateTask extends Thread {
        public boolean running = true;

        @Override
        public void run() {
            super.run();
            while (running) {
                if (System.currentTimeMillis() - lastUpdated > 300) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cameraGuide.setImageResource(R.drawable.cameraguide);
                            descriptionText.setText("");
                        }
                    });
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stopTask() {
            running = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
//            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
//                    .show();
        }
    }

    @Override
    public void onQRCodeRead(final String text, PointF[] points) {
        lastUpdated = System.currentTimeMillis();
        if (handlingCode) return;
        handlingCode = true;
        if (CryptoUriParser.isCryptoUrl(this, text) /*|| BRBitId.isBitId(text)*/) {
            Log.e(TAG, "onQRCodeRead: isCrypto");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cameraGuide.setImageResource(R.drawable.cameraguide);
                        descriptionText.setText("");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", text);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } finally {
                        handlingCode = false;
                    }

                }
            });
        } else if (isScanningIPFSHash) {
            Log.e(TAG, "onQRCodeRead: IPFS Hash");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cameraGuide.setImageResource(R.drawable.cameraguide);
                        descriptionText.setText("");
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", text);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    } finally {
                        handlingCode = false;
                    }

                }
            });
        } else {
            Log.e(TAG, "onQRCodeRead: not a crypto url");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        cameraGuide.setImageResource(R.drawable.cameraguide_red);
                        lastUpdated = System.currentTimeMillis();
                        descriptionText.setText("Not a valid " + WalletsMaster.getInstance(app).getCurrentWallet(app).getName(app) + " address");
                    } finally {
                        handlingCode = false;
                    }
                }
            });

        }

    }

    private void initQRCodeReaderView() {
        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setAutofocusInterval(500L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }
}