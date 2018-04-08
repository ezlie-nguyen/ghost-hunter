package com.example.ghost;

/*
 * Citations:
 *
 * Android Developers API Guides - Dialogs
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class FriendlyGhostDialog extends DialogFragment {

  public interface FriendlyGhostDialogListener {
    public void onBefriendClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
  }

  FriendlyGhostDialogListener mListener;
  private int marker;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog_friendly_ghost)
        .setPositiveButton(R.string.kill_ghost, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mListener.onDialogNegativeClick(FriendlyGhostDialog.this);
          }
        })
        .setNegativeButton(R.string.befriend, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mListener.onBefriendClick(FriendlyGhostDialog.this);
          }
        })
        .setNeutralButton(R.string.nothing, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            MainScreen.setDialogShown(false);
            FriendlyGhostDialog.this.getDialog().cancel();
          }
        });
    return builder.create();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      mListener = (FriendlyGhostDialogListener) activity;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString()
          + " must implement NoticeDialogListener");
    }
  }

  public void setMarker(int k) {
    marker = k;
  }

  public int getMarker() {
    return marker;
  }
}
