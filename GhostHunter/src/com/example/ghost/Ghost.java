package com.example.ghost;

import java.util.Random;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import android.graphics.Color;
import android.location.Location;

public class Ghost {

  private String color;
  private LatLng ghostLatLng;
  private Marker ghostMarker;
  private boolean dangerZone;

  public Ghost(Location l) {
    double latitude = l.getLatitude();
    double longitude = l.getLongitude();

    double ghostLat = latitude;
    double ghostLong = longitude;

    ghostLat = (Math.random() - 0.5)/600.0 + latitude;
    ghostLong = (Math.random() - 0.5)/600.0 + longitude;

    ghostLatLng = new LatLng(ghostLat, ghostLong);
    dangerZone = false;
  }

  public LatLng getGhostLatLng() {
    return ghostLatLng;
  }

  public void setGhostLatLng(LatLng l) {
    ghostLatLng = l;
  }

  public Marker getMarker() {
    return ghostMarker;
  }

  public void setMarker(Marker m){
    ghostMarker = m;
  }

  public boolean closeProximity(Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double ghostLat = ghostLatLng.latitude;
    double ghostLng = ghostLatLng.longitude;

    double distance = Math.sqrt(Math.pow(playerLat - ghostLat, 2) + Math.pow(playerLng - ghostLng, 2));
    if (distance <= 0.0002) {
      return true;
    }
    return false;
  }

  public double distance(Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double ghostLat = ghostLatLng.latitude;
    double ghostLng = ghostLatLng.longitude;

    double distance = Math.sqrt(Math.pow(playerLat - ghostLat, 2) + Math.pow(playerLng - ghostLng, 2));
    return distance;
  }

  public boolean tooFar(Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double ghostLat = ghostLatLng.latitude;
    double ghostLng = ghostLatLng.longitude;

    double distance = Math.sqrt(Math.pow(playerLat - ghostLat, 2) + Math.pow(playerLng - ghostLng, 2));
    if (distance >= (0.0010)) {
      return true;
    }
    return false;
  }

  public LatLng ghostMove(double timeElapsed, Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double ghostLat = ghostLatLng.latitude;
    double ghostLng = ghostLatLng.longitude;
    double newGhostLat = (Math.random() - 0.5)/12000.0 + ghostLat;
    double newGhostLng = (Math.random() - 0.5)/12000.0 + ghostLng;

    ghostLatLng = new LatLng(newGhostLat, newGhostLng);
    return ghostLatLng;
  }

  public LatLng ghostMoveConverge(double timeElapsed, Location l) {
    double playerLat = l.getLatitude();
    double playerLng = l.getLongitude();

    double ghostLat = ghostLatLng.latitude;
    double ghostLng = ghostLatLng.longitude;

    double newGhostLat = ghostLat;
    double newGhostLng = ghostLng;

    if (playerLat > ghostLat) {
      newGhostLat = (Math.random()*0.5)/12000.0 + ghostLat;
    }
    else if (playerLat < ghostLat) {
      newGhostLat = ghostLat - (Math.random()*0.5)/12000.0;
    }

    if (playerLng > ghostLng) {
      newGhostLng = (Math.random()*0.5)/12000.0 + ghostLng;
    }

    else if (playerLng < ghostLng) {
      newGhostLng = ghostLng - (Math.random()*0.5)/12000.0;
    }

    ghostLatLng = new LatLng(newGhostLat, newGhostLng);
    return ghostLatLng;
  }

  public void setColor(String c) {
    color = c;
  }

  public String getColor() {
    return color;
  }

  public void setDangerZone(Boolean b) {
    dangerZone = b;
  }

  public boolean getDangerZone() {
    return dangerZone;
  }
}
