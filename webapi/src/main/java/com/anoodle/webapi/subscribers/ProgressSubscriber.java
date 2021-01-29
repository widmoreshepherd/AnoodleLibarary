package com.anoodle.webapi.subscribers;

import android.content.Context;

import com.anoodle.webapi.progress.ProgressDialogHandler;
import com.anoodle.webapi.utils.LogUtils;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ProgressSubscriber<T> implements Observer<T> {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;
    private SubscriberOnErrorListener mSubscriberOnErrorListener;

    public ProgressSubscriber(Context mContext, SubscriberOnNextListener mSubscriberOnNextListener, SubscriberOnErrorListener subscriberOnErrorListener, boolean isShow, String dialogMessage) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mSubscriberOnErrorListener = subscriberOnErrorListener;
        this.mProgressDialogHandler = new ProgressDialogHandler(mContext, true, dialogMessage, isShow);
        showProgressDialog();
    }

    public ProgressSubscriber(Context mContext, SubscriberOnNextListener mSubscriberOnNextListener, boolean isShow, String dialogMessage) {
        SubscriberOnErrorListener subscriberOnErrorListener = new SubscriberOnErrorListener() {
            @Override
            public void OnError(Throwable e) {
                LogUtils.e(e.toString());
            }
        };
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mSubscriberOnErrorListener = subscriberOnErrorListener;
        this.mProgressDialogHandler = new ProgressDialogHandler(mContext, true, dialogMessage, isShow);
        showProgressDialog();
    }

    /*public ProgressSubscriber(Context mContext, SubscriberOnNextListener mSubscriberOnNextListener, boolean isShow, String dialogMessage) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mProgressDialogHandler = new ProgressDialogHandler(mContext, true, dialogMessage, isShow);
    }*/

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
        if (mSubscriberOnErrorListener != null) {
            mSubscriberOnErrorListener.OnError(e);
        }
    }

    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        try {
            if (mSubscriberOnNextListener != null) {
                mSubscriberOnNextListener.onNext(t);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
