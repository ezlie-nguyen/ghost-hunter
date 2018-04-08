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
 * Splash Screen
 * http://www.onlymobilepro.com/2013/01/16/android-beginner-creating-splash-screen/
 *
 * Android API
 * http://developer.android.com/training/index.html
 *
 * Google Maps API
 * http://developer.android.com/google/index.html
 * */

import java.util.ArrayList;
import java.util.Random;
import android.app.DialogFragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainScreen extends FragmentActivity implements
    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener, LocationListener, OnMarkerClickListener,
    CloseProximityDialogFragment.CloseProximityDialogListener, MarkerClickDialogFragment.MarkerClickDialogListener,
    OnInfoWindowClickListener, BuyDialogFragment.BuyDialogListener, FriendlyGhostDialog.FriendlyGhostDialogListener
{

  // Google Map
  private GoogleMap googleMap;
  private LocationClient mLocationClient;
  private LocationRequest mLocationRequest;
  private Location lastLocation = null;
  private ArrayList<Pair<Ghost, Marker>> ghosts = new ArrayList<Pair<Ghost, Marker>>();
  private ArrayList<Pair<Item, Marker>> items = new ArrayList<Pair<Item, Marker>>();
  private boolean initialZoom = false;
  private int timeElapsed = 0;
  private boolean itemsInitialized = false;
  private int kills = 0;
  private static boolean dialogShown = false;
  private int money = 100;
  private int lives = 5;
  private static int shovels = 0;
  private static int bracelets = 0;
  private static int bones = 0;
  private TextView statsText;
  private TextView itemsText;
  private ArrayList<Item> shovelList = new ArrayList<Item>();
  private ArrayList<Item> bonesList = new ArrayList<Item>();
  private boolean gameOver = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mLocationRequest = LocationRequest.create();
    // Use high accuracy
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    // updates every 10 seconds
    mLocationRequest.setInterval(5);
    // fastest update interval set to 2 seconds
    mLocationRequest.setFastestInterval(1);

  }

  protected void onStart() {
    super.onStart();
    statsText = (TextView)findViewById(R.id.stat);
    itemsText = (TextView)findViewById(R.id.item_stat);
    // Connect the client.
    try {
      // Loading map
      initializeMap();
      timeElapsed = 0;

      googleMap.setMyLocationEnabled(true);
      mLocationClient = new LocationClient(this, this, this);
      mLocationClient.requestLocationUpdates(mLocationRequest, this);
    } catch (Exception e) {
      e.printStackTrace();
    }
    mLocationClient.connect();

    new MoveTask().execute();
  }

  public void onConnected(Bundle dataBundle) {
    // Display the connection status
    Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();
    // If already requested, start periodic updates
    mLocationClient.requestLocationUpdates(mLocationRequest, this);
  }

  public void moveGhost(int time){
    for(int k = 0; k < ghosts.size(); k++){
      LatLng tempPos;
      if(ghosts.get(k).first.getColor().equals("red")) {
        tempPos = ghosts.get(k).first.ghostMoveConverge(time, lastLocation);
      }
      else {
        tempPos = ghosts.get(k).first.ghostMove(time, lastLocation);
      }
      ghosts.get(k).second.setPosition(tempPos);
    }
  }

  private void initializeMarkers(LatLng ltlg){
    Random r = new Random();
    int determine = r.nextInt(4);
    MarkerOptions ghost = new MarkerOptions();

    Location tempLoc = new Location("");
    tempLoc.setLatitude(ltlg.latitude);
    tempLoc.setLongitude(ltlg.longitude);
    Ghost newGhost = new Ghost(tempLoc);

    if(determine == 0){
      // make friendly ghost
      ghost.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost_friend));
      newGhost.setColor("green");
    }

    if(determine == 1){
      // make red ghost
      ghost.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost_red));
      newGhost.setColor("red");
    }

    if(determine == 2){
      // make blue ghost
      ghost.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost_blue));
      newGhost.setColor("blue");
    }

    if(determine == 3){
      // make purple ghost
      newGhost.setColor("purple");
      ghost.icon(BitmapDescriptorFactory.fromResource(R.drawable.ghost_purple));
    }

    LatLng ghostLoc = newGhost.getGhostLatLng();
    ghost.position(ghostLoc);
    Marker ghostMarker = googleMap.addMarker(ghost);
    googleMap.setOnMarkerClickListener(this);

    ghosts.add(new Pair(newGhost, ghostMarker));
    Toast toast = Toast.makeText(this, "A new ghost has spawned!", Toast.LENGTH_SHORT);
    toast.setGravity(Gravity.TOP, 0, 0);
    toast.show();
  }

  /**
   * function to load map. If map is not created it will create it for you
   * */
  private void initializeMap() {
    if (googleMap == null) {
      googleMap = ((MapFragment) getFragmentManager().findFragmentById(
          R.id.map)).getMap();

      // check if map is created successfully or not
      if (googleMap == null) {
        Toast.makeText(getApplicationContext(),
            "Sorry! Unable to create maps", Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  public void initializeItems() {
    int initialShovels = 0;
    int initialBones = 0;

    for(int n = 0; n < 5; n++) {
      Item i = new Item(lastLocation, timeElapsed);
      if (i.getItemType().equals("Shovel")) {
        initialShovels++;
        shovelList.add(i);
      }
      if (i.getItemType().equals("Bones")) {
        initialBones++;
        bonesList.add(i);
      }
      Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
      items.add(new Pair(i, itemMarker));
    }

    if (initialBones < 3) {
      for (int n = 0; n < 3 - initialBones; n++) {
        Item i = new Item(lastLocation, "Bones", timeElapsed);
        Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
        bonesList.add(i);
        items.add(new Pair(i, itemMarker));
      }
    }
    if (initialShovels < 3) {
      for (int n = 0; n < 3 - initialShovels; n++) {
        Item i = new Item(lastLocation, "Shovel", timeElapsed);
        Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
        shovelList.add(i);
        items.add(new Pair(i, itemMarker));
      }
    }
    googleMap.setOnInfoWindowClickListener(this);
    itemsInitialized = true;
  }

  public MarkerOptions initializeItemMarker(Item i) {
    MarkerOptions m = new MarkerOptions();
    String itemName = i.getItemType();

    if(itemName.equals("Bones")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.bones));
    }
    else if(itemName.equals("Coins")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.coins));
    }
    else if(itemName.equals("Shovel")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.shovel));
    }
    else if(itemName.equals("Friendship Bracelet")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.friend_bracelet));
    }
    else if(itemName.equals("Healing Potion")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.potion));
    }
    else if(itemName.equals("Bomb")) {
      m.icon(BitmapDescriptorFactory.fromResource(R.drawable.bomb));
    }
    LatLng itemLoc = i.getItemLocation();
    m.position(itemLoc);
    return m;
  }

  public void spawnItem(LatLng l) {
    Item i = new Item(l, lives, timeElapsed);
    if (i.getItemType().equals("Shovel")) {
      shovelList.add(i);
    }
    if (i.getItemType().equals("Bones")) {
      bonesList.add(i);
    }
    Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
    items.add(new Pair(i, itemMarker));
  }

  public void spawnCoins(LatLng l) {
    Item i = new Item(l, "Coins", timeElapsed);
    Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
    items.add(new Pair(i, itemMarker));
  }

  public void spawnEssentialItems(String itemType) {
    Item i = new Item(lastLocation, itemType, timeElapsed);
    if (i.getItemType().equals("Shovel")) {
      shovelList.add(i);
    }
    if (i.getItemType().equals("Bones")) {
      bonesList.add(i);
    }
    Marker itemMarker = googleMap.addMarker(initializeItemMarker(i));
    items.add(new Pair(i, itemMarker));
  }

  public void clearAllGhosts() {
    for (int i = 0; i < ghosts.size(); i++) {
      ghosts.get(0).second.setVisible(false);
      ghosts.remove(0);
    }
  }

  public void onLocationChanged(Location location) {
    // Report to the UI that the location was updated
    lastLocation = location;
    if(!itemsInitialized) {
      initializeItems();
    }
    //		Toast.makeText(this, "Time Elapsed " + timeElapsed, Toast.LENGTH_SHORT).show();
    LatLng here = new LatLng(location.getLatitude(), location.getLongitude());

    CameraUpdate cUpdate = CameraUpdateFactory.newLatLngZoom(here, 20);
    if(!initialZoom){
      googleMap.animateCamera(cUpdate);
      initialZoom = true;
    }
  }

  protected void onStop() {
    // If the client is connected
    if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener.
			 * The current Activity is the listener, so
			 * the argument is "this".
			 */
      // removeLocationUpdates(this);
    }
		/*
		 * After disconnect() is called, the client is
		 * considered "dead".
		 */
    mLocationClient.disconnect();
    super.onStop();
  }

  @Override
  public void onConnectionFailed(ConnectionResult arg0) {
    Toast.makeText(this, "Sorry! No connection!", Toast.LENGTH_SHORT).show();
    System.exit(0);
  }

  @Override
  public void onDisconnected() {
    System.exit(0);
  }

  private class MoveTask extends AsyncTask<Void, Pair<Integer, ArrayList<Pair<Boolean, Integer>>>, Void> {
    @Override
    protected Void doInBackground(Void... params) {
      while(true) {
        try {
          Thread.sleep(1000);
        }
        catch (InterruptedException e) {
          e.printStackTrace();
        }
        timeElapsed++;
        ArrayList<Pair<Boolean, Integer>> closeProx = new ArrayList<Pair<Boolean, Integer>>();
        for(int k = 0; k < ghosts.size(); k++) {
          closeProx.add(new Pair<Boolean, Integer>(ghosts.get(k).first.closeProximity(lastLocation), k));
        }
        publishProgress(new Pair<Integer, ArrayList<Pair<Boolean, Integer>>>(timeElapsed, closeProx));
      }
    }

    @Override
    protected void onProgressUpdate(Pair<Integer, ArrayList<Pair<Boolean, Integer>>>... progress) {
      for(int m = 0; m < progress.length; m++) {
        statsText.setText("STATS\nLives: " + lives + "\nCoins: " + money + "\nGhosts Killed: " + kills);
        itemsText.setText("ITEMS\nBones: " + bones + "\nShovels: " + shovels + "\nFriendship Bracelets: " + bracelets);
        moveGhost(progress[m].first);

        if (timeElapsed % 5 == 0 && ghosts.size() < 6) {
          initializeMarkers(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()));
        }

        if (timeElapsed % 5 == 0 && items.size() < 10) {
          double itemLat = (Math.random() - 0.5)/600.0 + lastLocation.getLatitude();
          double itemLong = (Math.random() - 0.5)/600.0 + lastLocation.getLongitude();
          spawnItem(new LatLng(itemLat, itemLong));
        }

        if (timeElapsed % 25 == 0) {
          spawnEssentialItems("Coins");
        }

        for (int k = 0; k < items.size(); k++) {
          if (timeElapsed - items.get(k).first.getStartingTime() > 100) {
            if (timeElapsed % 5 == 0) {
              if (items.get(k).first.getItemType().equals("Bones")) {
                bonesList.remove(items.get(k).first);
              }
              if (items.get(k).first.getItemType().equals("Shovel")) {
                shovelList.remove(items.get(k).first);
              }
              items.get(k).second.setVisible(false);
              items.remove(k);
            }
          }
        }

        if (timeElapsed % 5 == 0 && bonesList.size() < 3) {
          spawnEssentialItems("Bones");
        }
        if (timeElapsed % 5 == 0 && shovelList.size() < 3) {
          spawnEssentialItems("Shovel");
        }

        for (int i = 0; i < progress[m].second.size(); i++) {
          if (progress[m].second.get(i).first && !dialogShown) {
            if (!ghosts.get(progress[m].second.get(i).second).first.getDangerZone()) {
              CloseProximityDialogFragment dialog = new CloseProximityDialogFragment();
              dialog.setMarker(progress[m].second.get(i).second);
              dialog.show(getFragmentManager(), "Close Proximity Dialog");
              dialogShown = true;
            }
          }
        }

        if (lives <= 0 && !gameOver) {
          die();
        }
      }
    }
  }

  @Override
  public boolean onMarkerClick(Marker marker) {
    for (int m = 0; m < items.size(); m++) {
      if (items.get(m).second.equals(marker)) {
        String itemName = items.get(m).first.getItemType();
        int coin = items.get(m).first.getCoinValue();
        if (itemName.equals("Bones")) {
          items.get(m).second.setTitle(itemName);
        }
        else if (items.get(m).first.getItemType().equals("Coins")) {
          items.get(m).second.setTitle(coin + " Coins");
        }
        else {
          items.get(m).second.setTitle(itemName + ": " + coin + " Coins");
        }
      }
    }

    for (int k = 0; k < ghosts.size(); k++) {
      if(ghosts.get(k).second.equals(marker)) {
        if (ghosts.get(k).first.getColor().equals("green")) {
          FriendlyGhostDialog dialog = new FriendlyGhostDialog();
          dialog.setMarker(k);
          dialog.show(getFragmentManager(), "Friendly Ghost Dialog");
          dialogShown = true;
        }
        else {
          MarkerClickDialogFragment dialog = new MarkerClickDialogFragment();
          dialog.setMarker(k);
          dialog.show(getFragmentManager(), "Marker Click Fragment");
          dialogShown = true;
        }
      }
    }
    return false;
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog) {
    if(dialog instanceof CloseProximityDialogFragment){
      CloseProximityDialogFragment click = (CloseProximityDialogFragment) dialog;
      int k = click.getMarker();
      ghosts.get(k).first.setDangerZone(true);
      if(money >= 50) {
        money -= 50;
        Toast toast = Toast.makeText(this, "You lose 50 coins for ignoring the ghost.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
      }
      else {
        Toast toast = Toast.makeText(this, "You lose a life.", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        lives--;
      }
      dialogShown = false;
    }
  }

  @Override
  public void onDialogNegativeClick(DialogFragment dialog) {
    LatLng ghostLocation = null;
    if(dialog instanceof CloseProximityDialogFragment) {
      CloseProximityDialogFragment click = (CloseProximityDialogFragment) dialog;
      if (bones > 0) {
        ghosts.get(click.getMarker()).second.setVisible(false);
        ghostLocation = ghosts.get(click.getMarker()).second.getPosition();
        ghosts.remove(click.getMarker());
        kills++;
        bones--;
        if(Math.random() < 0.5) {
          spawnItem(ghostLocation);
        }
        dialogShown = false;
      }
      else {
        NotEnoughBonesDialog nes = new NotEnoughBonesDialog();
        nes.show(getFragmentManager(), "Bones are required to kill ghosts!");
        dialogShown = true;
      }
    }
    else if(dialog instanceof MarkerClickDialogFragment) {
      MarkerClickDialogFragment click = (MarkerClickDialogFragment) dialog;
      if (bones > 0) {
        ghosts.get(click.getMarker()).second.setVisible(false);
        ghostLocation = ghosts.get(click.getMarker()).second.getPosition();
        ghosts.remove(click.getMarker());
        kills++;
        bones--;
        if(Math.random() < 0.5) {
          spawnItem(ghostLocation);
        }
        dialogShown = false;
      }
      else {
        NotEnoughBonesDialog nes = new NotEnoughBonesDialog();
        nes.show(getFragmentManager(), "Bones are required to kill ghosts!");
        dialogShown = true;
      }
    }
    else if (dialog instanceof FriendlyGhostDialog) {
      FriendlyGhostDialog click = (FriendlyGhostDialog) dialog;
      if (bones > 0) {
        ghosts.get(click.getMarker()).second.setVisible(false);
        ghostLocation = ghosts.get(click.getMarker()).second.getPosition();
        ghosts.remove(click.getMarker());
        kills++;
        bones--;
        if(Math.random() < 0.5) {
          spawnItem(ghostLocation);
        }
        dialogShown = false;
      }
      else {
        NotEnoughBonesDialog nes = new NotEnoughBonesDialog();
        nes.show(getFragmentManager(), "Bones are required to kill ghosts!");
        dialogShown = true;
      }
    }
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    for (int m = 0; m < items.size(); m++) {
      if (items.get(m).second.equals(marker)) {
        if (items.get(m).first.isPickable(money, lastLocation) == 1) {
          Toast itemToast = Toast.makeText(this, "Item Collected! +" + items.get(m).first.getCoinValue() + " Coins", Toast.LENGTH_SHORT);
          itemToast.setGravity(Gravity.CENTER, 0, 0);
          itemToast.show();
          money += items.get(m).first.getCoinValue();
          items.get(m).second.setVisible(false);
          items.remove(m);
        }
        else if (items.get(m).first.isPickable(money, lastLocation) == 2) {
          BuyDialogFragment buyItem = new BuyDialogFragment();
          buyItem.setItemMarker(marker);
          buyItem.show(getFragmentManager(), "Buy Dialog Fragment");
          dialogShown = true;
        }
        else if (items.get(m).first.isPickable(money, lastLocation) == 3) {
          Toast itemToast = Toast.makeText(this, "Item picked up for free!", Toast.LENGTH_SHORT);
          itemToast.setGravity(Gravity.CENTER, 0, 0);
          itemToast.show();
          String itemType = items.get(m).first.getItemType();

          if (itemType.equals("Bones")) {
            bones++;
            bonesList.remove(items.get(m).first);
          }
          else if (itemType.equals("Shovel")) {
            shovels++;
            shovelList.remove(items.get(m).first);
          }
          else if (itemType.equals("Friendship Bracelet")) {
            bracelets++;
          }
          else if (itemType.equals("Bomb")) {
            clearAllGhosts();
          }

          items.get(m).second.setVisible(false);
          items.remove(m);
        }
        else if (items.get(m).first.isPickable(money, lastLocation) == -1) {
          TooPoorDialog tpd = new TooPoorDialog();
          tpd.show(getFragmentManager(), "Not enough coins to buy!");
          dialogShown = true;
        }
        else if (items.get(m).first.isPickable(money, lastLocation) == -2) {
          NotEnoughShovelsDialog nes = new NotEnoughShovelsDialog();
          nes.show(getFragmentManager(), "Shovels required to dig up bones!");
          dialogShown = true;
        }
      }
    }
  }

  @Override
  public void onBuyClick(DialogFragment dialog) {
    if (dialog instanceof BuyDialogFragment) {
      BuyDialogFragment itemBuy = (BuyDialogFragment) dialog;
      Marker itemMarker = itemBuy.getItemMarker();
      for (int m = 0; m < items.size(); m++) {
        if (items.get(m).second.equals(itemMarker)) {
          if (items.get(m).first.isPickable(money, lastLocation) == 2) {
            Toast itemToast = null;

            String itemType = items.get(m).first.getItemType();
            if (itemType.equals("Bones")) {
              itemToast = Toast.makeText(this, "You dug up the bones!", Toast.LENGTH_SHORT);
              bones++;
              bonesList.remove(items.get(m).first);
              shovels--;
            }
            else {
              itemToast = Toast.makeText(this, "Item Bought! -" + items.get(m).first.getCoinValue() + " Coins", Toast.LENGTH_SHORT);
              if (itemType.equals("Healing Potion")) {
                lives++;
                Toast toast = Toast.makeText(this, "+1 life!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
              }
              else if (itemType.equals("Shovel")) {
                shovels++;
                shovelList.remove(items.get(m).first);
              }
              else if (itemType.equals("Friendship Bracelet")) {
                bracelets++;
              }
              else if (itemType.equals("Bomb")) {
                clearAllGhosts();
              }
            }
            itemToast.setGravity(Gravity.CENTER, 0, 0);
            itemToast.show();
            money -= items.get(m).first.getCoinValue();
            items.get(m).second.setVisible(false);
            items.remove(m);
            dialogShown = false;
          }
        }
      }
    }
  }

  public static void setDialogShown(boolean b) {
    dialogShown = b;
  }

  public static int getShovels() {
    return shovels;
  }

  public static int getBones() {
    return bones;
  }

  public static int getBracelets() {
    return bracelets;
  }

  @Override
  public void onBefriendClick(DialogFragment dialog) {
    if (dialog instanceof FriendlyGhostDialog) {
      FriendlyGhostDialog click = (FriendlyGhostDialog) dialog;
      LatLng ghostLocation = null;

      if (bracelets > 0) {
        Toast toast = Toast.makeText(this, "You get coins for befriending the ghost!", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        ghosts.get(click.getMarker()).second.setVisible(false);
        ghostLocation = ghosts.get(click.getMarker()).second.getPosition();
        ghosts.remove(click.getMarker());
        bracelets--;
        spawnCoins(ghostLocation);
        dialogShown = false;
      }
      else {
        NotEnoughBraceletsDialog nes = new NotEnoughBraceletsDialog();
        nes.show(getFragmentManager(), "Bracelets are required to befriend ghosts!");
        dialogShown = true;
      }
    }
  }

  public void die() {
    gameOver = true;
    DieDialog dialog = new DieDialog();
    dialog.show(getFragmentManager(), "Game Over");
  }
}
