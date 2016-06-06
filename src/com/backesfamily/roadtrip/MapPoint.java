package com.backesfamily.roadtrip;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * User: jbackes
 * Date: 5/11/15
 * Time: 3:10 PM
 */

public class MapPoint {
  private final double _latitude;
  private final double _longitude;
  private final double _altitude;
  private int _precision = 5;

  public MapPoint(double latitude, double longitude, double altitude) {
    _latitude = latitude;
    _longitude = longitude;
    _altitude = altitude;
  }

  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public double getLatitude() {
    return _latitude;
  }

  public double getLongitude() {
    return _longitude;
  }

  @Override
  public boolean equals(Object o) {
    boolean result = false;
    if (this == o) return true;
    if (o != null && getClass() == o.getClass()) {

      MapPoint mapPoint = (MapPoint) o;

      if (Double.compare(round(mapPoint._altitude, _precision), round(_altitude, _precision)) == 0) {
        if (Double.compare(round(mapPoint._latitude, _precision), round(_latitude, _precision)) == 0) {
          if (Double.compare(round(mapPoint._longitude, _precision), round(_longitude, _precision)) == 0) {
            result = true;
          }
        }
      }
    }

    return result;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(_latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_altitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return "Lat: " + _latitude + ", Lon: " + _longitude + ", Alt: " + _altitude;
  }
}
