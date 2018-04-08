package com.example.ghost;
/*
 * Citations:
 *
 * Android Developers API Guides - Dialogs
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * */

import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class BuyDialogFragment extends DialogFragment {
  public interface BuyDialogListener {
    public void onBuyClick(DialogFragment dialog);
  }

  BuyDialogListener mListener;
  Marker itemMarker;

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setMessage(R.string.dialog_buy_item)
        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            MainScreen.setDialogShown(false);
            BuyDialogFragment.this.getDialog().cancel();
          }
        })
        .setNegativeButton(R.string.buy, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
            mListener.onBuyClick(BuyDialogFragment.this);
          }
        });
    return builder.create();
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      mListener = (BuyDialogListener) activity;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString()
          + " must implement BuyDialogListener");
    }
  }

  public Marker getItemMarker() {
    return itemMarker;
  }

  public void setItemMarker(Marker itemMarker) {
    this.itemMarker = itemMarker;
  }
}
