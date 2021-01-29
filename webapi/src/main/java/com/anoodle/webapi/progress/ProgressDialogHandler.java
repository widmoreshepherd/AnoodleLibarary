package com.anoodle.webapi.progress;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.anoodle.webapi.view.LoadingDialog;


public class ProgressDialogHandler extends Handler {

    public static final int SHOW_PROGRESS_DIALOG = 1;
    public static final int DISMISS_PROGRESS_DIALOG = 2;
    private ProgressDialog pd;
    private Dialog dialog;
    private Context context;
    private boolean cancelable;
    private String message;
    private boolean isShow;

    /**
     *
     * @param context
     * @param cancelable
     * @param dialogMessage
     * @param isShow
     */
    public ProgressDialogHandler(Context context, boolean cancelable, String dialogMessage, boolean isShow) {
        super();
        this.context = context;
        this.cancelable = cancelable;
        this.message = dialogMessage;
        this.isShow = isShow;
    }

    private void initProgressDialog() {
        if (pd == null) {
            pd = new ProgressDialog(context);

            pd.setCancelable(cancelable);

            if (cancelable) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                });
            }

            if (!pd.isShowing()) {
                pd.show();
            }
        }
    }

    private void dismissProgressDialog() {
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS_DIALOG:
//                initProgressDialog();
                if (isShow) {
                    dialog = LoadingDialog.showDialog(context, true, message);
                    dialog.setCanceledOnTouchOutside(false);//设置点击屏幕加载框不会取消（返回键可以取消）
                    if (!getActivity(dialog.getContext()).isFinishing()){
                        dialog.show();
                    }
                }

                break;
            case DISMISS_PROGRESS_DIALOG:
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
//                dismissProgressDialog();
                break;
        }
    }

    private Activity getActivity(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }else
            return null;
    }

}
