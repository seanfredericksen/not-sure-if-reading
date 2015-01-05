package com.frederis.notsureifreading.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.StudentPopupInfo;

import java.util.ArrayList;

import mortar.Popup;
import mortar.PopupPresenter;

public class SelectStudentPopup implements Popup<StudentPopupInfo, Long> {

    private final Context mContext;
    private AlertDialog mDialog;

    public SelectStudentPopup(Context context) {
        mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void show(final StudentPopupInfo info, boolean withFlourish, final PopupPresenter<StudentPopupInfo, Long> presenter) {
        if (mDialog != null) throw new IllegalStateException("Already showing, can't show " + info);

        mDialog = new AlertDialog.Builder(mContext).setTitle(R.string.select_student)
                .setItems(getNamesArray(info.getStudentData()), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onDismissed(info.getStudentData().get(which).studentId);
                    }
                })
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override public void onCancel(DialogInterface d) {
                        mDialog = null;
                        presenter.onDismissed(0L);
                    }
                })
                .show();
    }

    private CharSequence[] getNamesArray(ArrayList<StudentPopupInfo.StudentData> studentData) {
        CharSequence[] names = new CharSequence[studentData.size()];

        for (int i = 0; i < studentData.size(); i++) {
            names[i] = studentData.get(i).studentName;
        }

        return names;
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

}
