package com.example.ghost;

/*
 * Citations:
 *
 * Android Developers API Guides - Dialogs
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * */

import com.example.ghost.CloseProximityDialogFragment.CloseProximityDialogListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MarkerClickDialogFragment extends DialogFragment {

  public interface MarkerClickDialogListener {
    public void onDialogNegativeClick(DialogFragment dialog);
  }

  CloseProximityDialogListener mListener;
  private int marker;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog_marker_click)
        .setPositiveButton(R.string.nothing, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            MainScreen.setDialogShown(false);
            MarkerClickDialogFragment.this.getDialog().cancel();
          }
        })
        .setNegativeButton(R.string.kill_ghost, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mListener.onDialogNegativeClick(MarkerClickDialogFragment.this);
          }
        });
    return builder.create();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      mListener = (CloseProximityDialogListener) activity;
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
