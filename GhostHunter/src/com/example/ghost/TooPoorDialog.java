package com.example.ghost;

/*
 * Citations:
 *
 * Android Developers API Guides - Dialogs
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class TooPoorDialog extends DialogFragment {

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new AlertDialog.Builder(getActivity())
        .setTitle("Not enough coins to buy!")
        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            MainScreen.setDialogShown(false);
            TooPoorDialog.this.getDialog().cancel();
          }
        })
        .create();
  }
}
