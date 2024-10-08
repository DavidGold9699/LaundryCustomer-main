/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source categoryName is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package nsoft.laundry.customer.utils.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import nsoft.laundry.customer.R;

public class ECProgressDialog extends Dialog {

    private TextView mTextView;
    private View mImageView;
    AsyncTask mAsyncTask;

    private final OnCancelListener mCancelListener
            = new OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            if(mAsyncTask != null) {
                mAsyncTask.cancel(true);
            }
        }
    };

    public ECProgressDialog(Context context) {
        super(context , R.style.Theme_Light_CustomDialog_Blue);
        mAsyncTask = null;
        setCancelable(true);
        setContentView(R.layout.common_loading_diloag);
        mTextView = (TextView)findViewById(R.id.textview);
        mTextView.setText(R.string.loading_process);
        mImageView = findViewById(R.id.imageview);
        setOnCancelListener(mCancelListener);
    }

    public ECProgressDialog(Context context , int resid) {
        this(context);
        mTextView.setText(resid);
    }

    public ECProgressDialog(Context context , CharSequence text) {
        this(context);
        mTextView.setText(text);
    }

    public ECProgressDialog(Context context , AsyncTask asyncTask) {
        this(context);
        mAsyncTask = asyncTask;
    }

    public ECProgressDialog(Context context , CharSequence text , AsyncTask asyncTask) {
        this(context , text);
        mAsyncTask = asyncTask;
    }

    public final void setPressText(CharSequence text) {
        mTextView.setText(text);
    }

    public final void dismiss() {
        super.dismiss();
        mImageView.clearAnimation();
    }

    public final void show() {
        super.show();
        Animation loadAnimation = AnimationUtils.loadAnimation(getContext() ,R.anim.loading);
        mImageView.startAnimation(loadAnimation);
    }
}
