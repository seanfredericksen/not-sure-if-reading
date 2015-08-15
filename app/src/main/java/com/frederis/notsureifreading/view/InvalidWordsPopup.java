package com.frederis.notsureifreading.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.WordsPopupInfo;

import mortar.Popup;
import mortar.PopupPresenter;

public class InvalidWordsPopup implements Popup<WordsPopupInfo, Void> {

    private final Context mContext;
    private AlertDialog mDialog;

    public InvalidWordsPopup(Context context) {
        mContext = context;
    }

    @Override
    public void show(WordsPopupInfo info, boolean withFlourish, final PopupPresenter<WordsPopupInfo, Void> presenter) {
        if (mDialog != null) throw new IllegalStateException("Already showing, can't show " + info);

        mDialog = new AlertDialog.Builder(mContext).setTitle(R.string.error)
                .setMessage(mContext.getString(R.string.words_must_be_message, info.getMaxWord()))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss(true);
                        presenter.onDismissed(null);
                    }
                })
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override public void onCancel(DialogInterface d) {
                        dismiss(true);
                        presenter.onDismissed(null);
                    }
                })
                .show();
    }

    @Override
    public boolean isShowing() {
        return mDialog != null;
    }

    @Override
    public void dismiss(boolean withFlourish) {
        mDialog.dismiss();
        mDialog = null;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

}
