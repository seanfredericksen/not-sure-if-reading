package com.frederis.notsureifreading.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.frederis.notsureifreading.R;

public class ConfirmationDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.confirm_exit_assessment)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Listener) getActivity()).onConfirmation(false);
                    }
                })
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Listener) getActivity()).onConfirmation(true);
                    }
                })
                .create();
    }

    /**
     * Callback for the user's response.
     */
    public interface Listener {

        /**
         * Called when the PermissoinRequest is allowed or denied by the user.
         *
         * @param allowed True if the user allowed the request.
         */
        void onConfirmation(boolean allowed);
    }

}