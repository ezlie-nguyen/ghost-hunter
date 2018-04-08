package com.example.ghost;

/*
 * Citations:
 *
 * Google Maps Android API V2 - MapFragments, LocationData, Markers, Info Windows
 * https://developers.google.com/maps/documentation/android/map
 *
 * Other Google Map Tutorials
 * http://umut.tekguc.info/en/content/google-android-map-v2-step-step
	http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
 *
 * Android Developers API Guides - Dialogs, Toasts
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * http://developer.android.com/guide/topics/ui/notifiers/toasts.html
 *
 * Android API
 * http://developer.android.com/training/index.html
 *
 * Google Maps API
 * http://developer.android.com/google/index.html
 * */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    Thread logoTimer = new Thread() {
      public void run(){
        try{
          int logoTimer = 0;
          while(logoTimer < 5000){
            sleep(100);
            logoTimer = logoTimer + 100;
          };
          startActivity(new Intent("com.example.ghost.CLEARSCREEN"));
        }

        catch (InterruptedException e) {
          e.printStackTrace();
        }

        finally{
          finish();
        }
      }
    };

    logoTimer.start();
  }
}

