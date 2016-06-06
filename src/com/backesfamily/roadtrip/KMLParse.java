package com.backesfamily.roadtrip;

import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class KMLParse {

  private final static String[] FOUR_COMPASS_POINTS = new String[]{"N", "E", "S", "W"};
  private final static String[] EIGHT_COMPASS_POINTS = new String[]{"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
  private final static String[] SIXTEEN_COMPASS_POINTS = new String[]{"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE"
      , "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
  //  private final com.backesfamily.roadtrip.BMWNavTemplate template;
  private final double radius_of_earth;
  private final double radius_factor;
  private HashMap<String, Object> _variables;
  public KMLParse() {
    //noinspection Convert2Diamond
    _variables = new HashMap<String, Object>();

//    template = new com.backesfamily.roadtrip.BMWNavTemplate(_variables);
    MeasuringSystem measuringSystem = MeasuringSystem.METRIC_SYSTEM;

    switch (measuringSystem) {
      case METRIC_SYSTEM:
        radius_of_earth = 6378100.00;
        radius_factor = 1000;
        _variables.put(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName(), "km");
        break;
      case ENGLISH_SYSTEM:
        radius_of_earth = 20925524.00;
        radius_factor = 5280;
        _variables.put(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName(), "mi");
        break;
      default:
        radius_of_earth = 0.00;
        radius_factor = 0.00;
        _variables.put(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName(), "UNSUPPORTED MEASURING SYSTEM");
    }
  }

  public void putElement(String key, Object value) {
    _variables.put(key, value);
  }

  public Object getElement(String key) {
    return _variables.get(key);
  }

  public HashMap<String, Object> parseFile(String path) {
    Kml kml = null;

    Placemark start;
    Placemark end;

    if (path == null) {
      System.exit(-1);
    }


    if (path.toLowerCase().endsWith(".kmz")) {
      try (ZipFile zipFile = new ZipFile(path)) {
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          String fileName = entry.getName();
          if (fileName.toLowerCase().endsWith(".kml")) {
            try (InputStream stream = zipFile.getInputStream(entry)){
              kml = Kml.unmarshal(stream);
            }
          }
        }
      } catch (IOException e) {
        displayFatalError(e);
      }
    } else {
      kml = Kml.unmarshal(new File(path), false);
    }

    if (kml == null) {
      throw new RuntimeException(".kml files was not found in " + path);
    }

    Object feature = kml.getFeature();

    if (!(feature instanceof Document)) {
      throw new RuntimeException("Parse error #1");
    }

    Document document = (Document) feature;

    _variables.put(ParseConstants.ROUTE_NAME.getName(), document.getName());
    _variables.put(ParseConstants.DESCRIPTION.getName(), document.getDescription());

    feature = document.getFeature();
    if (!(feature instanceof ArrayList)) {
      throw new RuntimeException("Parse error #2");
    }

    for (int i = 0; i < ((ArrayList) feature).size(); i++) {
      Folder folder = (Folder) ((ArrayList) feature).get(i);


      List<Feature> layer = folder.getFeature();
      if (!(layer instanceof ArrayList)) {
        throw new RuntimeException("Parse error #3");
      }

      if (layer.size() > 0) {
        @SuppressWarnings("unchecked") ArrayList<Placemark> head = (ArrayList) layer;

        start = head.get(1);
        end = head.get(head.size() - 1);
        int count = 0;
        int segmentCount = 0;

        for (Placemark place : head) {
          String placeName = place.getName();
          Geometry geometry = place.getGeometry();
          if (geometry instanceof LineString) {
            LineString lineString = (LineString) geometry;
            List<Coordinate> coordinates = lineString.getCoordinates();
            for (Coordinate o1 : coordinates) {
              _variables.put(ParseConstants.LINE_STRING.getName() + ParseConstants.LATITUDE.getName() + segmentCount, o1.getLatitude());
              _variables.put(ParseConstants.LINE_STRING.getName() + ParseConstants.LONGITUDE.getName() + segmentCount, o1.getLongitude());
              _variables.put(ParseConstants.LINE_STRING.getName() + ParseConstants.ALTITUDE.getName() + segmentCount, o1.getAltitude());
              segmentCount++;
            }
            _variables.put(ParseConstants.SEGMENT_COUNT.getName(), segmentCount);
            computeTotalDistance(segmentCount, ParseConstants.LINE_STRING.getName());
          }
          if (geometry instanceof Point) {
            _variables.put(ParseConstants.PLACE_NAME.getName() + count, placeName);
            Point point = (Point) geometry;
            List<Coordinate> coordinates = point.getCoordinates();
            for (Coordinate o1 : coordinates) {
              _variables.put(ParseConstants.LATITUDE.getName() + count, o1.getLatitude());
              _variables.put(ParseConstants.LONGITUDE.getName() + count, o1.getLongitude());
              _variables.put(ParseConstants.ALTITUDE.getName() + count, o1.getAltitude());
              count++;
            }
            _variables.put(ParseConstants.POINT_COUNT.getName(), count);
            _variables.put(ParseConstants.START_ADDRESS.getName(), start.getName());
            _variables.put(ParseConstants.END_ADDRESS.getName(), end.getName());

            computeTotalDistance(count, "");
          }
        }
      }
    }

    return _variables;
  }

  public void computeTotalDistance(int weighPoints, String prefix) {
    double sum = 0.00;

    for (int i = 1; i < (weighPoints - 1); i++) {
      double lat1 = (Double) (_variables.get(prefix + ParseConstants.LATITUDE.getName() + i));
      double lon1 = (Double) (_variables.get(prefix + ParseConstants.LONGITUDE.getName() + i));
      double lat2 = (Double) (_variables.get(prefix + ParseConstants.LATITUDE.getName() + (i + 1)));
      double lon2 = (Double) (_variables.get(prefix + ParseConstants.LONGITUDE.getName() + (i + 1)));

      //System.out.println("Lat: " + lat1 + ", Lng: " + lon1);

      double φ1 = Math.toRadians(lat1);
      double φ2 = Math.toRadians(lat2);
      double λ1 = Math.toRadians(lon1);
      double λ2 = Math.toRadians(lon2);

      double Δφ = Math.toRadians(lat2 - lat1);
      double Δλ = Math.toRadians(lon2 - lon1);

      double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
          Math.cos(φ1) * Math.cos(φ2) *
              Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

      double d = radius_of_earth * c;

      double y = Math.sin(λ2 - λ1) * Math.cos(φ2);
      double x = Math.cos(φ1) * Math.sin(φ2) -
          Math.sin(φ1) * Math.cos(φ2) * Math.cos(λ2 - λ1);
      double bearing = Math.toDegrees(Math.atan2(y, x));
      String compassPoint = getCompassPoint(bearing, 3);

      _variables.put(prefix + ParseConstants.BEARING.getName() + i, bearing);
      _variables.put(prefix + ParseConstants.COMPASS_POINT.getName() + i, compassPoint);

      sum += d;
    }

    double totalDistance = sum / radius_factor;
    double travelTime = totalDistance / 100;

    _variables.put(prefix + ParseConstants.TRAVEL_TIME.getName(), travelTime);
    _variables.put(prefix + ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName(), "km");
    _variables.put(prefix + ParseConstants.TOTAL_DISTANCE.getName(), totalDistance);
  }

  /**
   * Returns compass point (to given precision) for supplied bearing.
   * <p>
   * var point = Dms.compassPoint(24, 1); // point = "N'
   */
  public String getCompassPoint(double bearing, int precision) {
    // note precision = max length of compass point; it could be extended to 4 for quarter-winds
    // (eg NEbN), but I think they are little used

    bearing = ((bearing % 360) + 360) % 360; // normalise to 0..360

    String point;

    switch (precision) {
      case 1: // 4 compass points
        point = FOUR_COMPASS_POINTS[((int) (Math.round(bearing * 4 / 360) % 4))];
        break;
      case 2: // 8 compass points
        point = EIGHT_COMPASS_POINTS[((int) Math.round(bearing * 8 / 360) % 8)];
        break;
      case 3: // 16 compass points
        point = SIXTEEN_COMPASS_POINTS[((int) Math.round(bearing * 16 / 360) % 16)];
        break;
      default:
        throw new RuntimeException("Precision must be between 1 and 3");
    }

    return point;
  }

  public void displayFatalError(Exception e) {
    Error dialog = new Error(e);
    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }

  enum MeasuringSystem {
    METRIC_SYSTEM, ENGLISH_SYSTEM
  }
}
