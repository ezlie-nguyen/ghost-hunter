package com.example.ghost;

import java.util.Random;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import android.location.Location;

public class Item {

  protected LatLng itemLocation;
  protected int coinValue;
  protected int pickable;
  protected String itemType;
  protected String boneColor;
  protected int startingTime;

  public Item(Location l, int time) {
    double latitude = l.getLatitude();
    double longitude = l.getLongitude();

    double itemLat = (Math.random() - 0.5)/600.0 + latitude;
    double itemLong = (Math.random() - 0.5)/600.0 + longitude;

    startingTime = time;
    itemLocation = new LatLng(itemLat, itemLong);
    pickable = 0;
    coinValue = 0;
    randomizeInitialItems();
    initializeCoinValues();
  }

  public Item(LatLng l, int lives, int time) {
    // Spawn random items when ghosts die
    startingTime = time;
    itemLocation = l;
    pickable = 0;
    coinValue = 0;
    randomizeItemType(lives);
    initializeCoinValues();
  }

  public Item(LatLng l, String item, int time) {
    // Spawn one item of a specific type when ghosts die
    startingTime = time;
    itemLocation = l;
    pickable = 1;
    Random generator = new Random();

    itemType = item;
    if (itemType.equals("Coins")) {
      coinValue = generator.nextInt(80);
    }
  }

  public Item(Location l, String item, int time) {
    // Spawn one item of a specific type at random locations
    double latitude = l.getLatitude();
    double longitude = l.getLongitude();

    double itemLat = (Math.random() - 0.5)/600.0 + latitude;
    double itemLong = (Math.random() - 0.5)/600.0 + longitude;

    startingTime = time;
    itemLocation = new LatLng(itemLat, itemLong);
    itemType = item;
    initializeCoinValues();
  }

  public void randomizeItemType(int lives) {
    Random generator = new Random();
    int itemType;
    if (lives < 5) {
      itemType = generator.nextInt(6);
    }
    else {
      itemType = generator.nextInt(5);
    }

    String itemName = "";

    switch(itemType) {
    case 0:
      itemName = "Bones";
      break;
    case 1:
      itemName = "Coins";
      break;
    case 2:
      itemName = "Shovel";
      break;
    case 3:
      itemName = "Friendship Bracelet";
      break;
    case 4:
      itemName = "Bomb";
      break;
    case 5:
      itemName = "Healing Potion";
      break;
    }
    setItemType(itemName);
  }

  public void randomizeInitialItems() {
    Random generator = new Random();
    int itemType = generator.nextInt(5);
    String itemName = "";

    switch(itemType) {
    case 0:
      itemName = "Bones";
      break;
    case 1:
      itemName = "Coins";
      break;
    case 2:
      itemName = "Shovel";
      break;
    case 3:
      itemName = "Friendship Bracelet";
      break;
    case 4:
      itemName = "Bomb";
      break;
    }
    setItemType(itemName);
  }

  public void initializeCoinValues() {
    Random generator = new Random();
    if (itemType.equals("Shovel")) {
      coinValue = 25;
    }
    if (itemType.equals("Bomb")) {
      coinValue = 100;
    }
    if (itemType.equals("Bones")) {
      coinValue = 0;
    }
    if (itemType.equals("Friendship Bracelet")) {
      coinValue = 50;
    }
    if (itemType.equals("Healing Potion")) {
      coinValue = 75;
    }
    if (itemType.equals("Coins")) {
      coinValue = generator.nextInt(80);
    }
    setCoinValue(coinValue);
  }

  public int isPickable(int k, Location l) {
    if (itemType.equals("Coins")) {
      pickable = 1;
    }
    else {
      if (isInProximity(l)) {
        pickable = 3;
      }
      else {
        if (itemType.equals("Bones")) {
          if (MainScreen.getShovels() == 0) {
            pickable = -2;
          }
          else {
            if (k >= coinValue) {
              pickable = 2;
            }
          }
        }
        else {
          if (k >= coinValue) {
            pickable = 2;
          }
          else if (k < coinValue) {
            pickable = -1;
          }
          else {
            pickable = 0;
          }
        }
      }
    }
    return pickable;
  }

  public boolean isInProximity(Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double itemLat = itemLocation.latitude;
    double itemLng = itemLocation.longitude;

    double distance = Math.sqrt(Math.pow(itemLat - playerLat, 2) + Math.pow(itemLng - playerLng, 2));
    if (distance <= 0.0002) {
      return true;
    }
    return false;
  }

  public void setPickable(int pickable) {
    this.pickable = pickable;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public LatLng getItemLocation() {
    return itemLocation;
  }

  public void setItemLocation(LatLng itemLocation) {
    this.itemLocation = itemLocation;
  }

  public int getCoinValue() {
    return coinValue;
  }

  public void setCoinValue(int coin) {
    coinValue = coin;
  }

  public void setStartingTime(int t) {
    startingTime = t;
  }

  public int getStartingTime() {
    return startingTime;
  }
}
