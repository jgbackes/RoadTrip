package com.backesfamily.roadtrip;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 4/16/15
 * Time: 1:21 PM
 */
public enum ParseConstants {

  LATITUDE("latitude"), LONGITUDE("longitude"), ALTITUDE("altitude"), BEARING("bearing"), COMPASS_POINT("compass_point"), ID_NAME("IdName"), DT_STAMP("dtstamp"), POINT_COUNT("BMW_Points"), START_ADDRESS("start_address"), END_ADDRESS("end_address"), TOTAL_DISTANCE("total_distance"), DISTANCE_UNIT_OF_MEASURE("distance_unit_of_measure"), TRAVEL_TIME("travel_time"), ROUTE_NAME("route_name"), DESCRIPTION("description"), USER_DESCRIPTION("user_description"), COUNTRY("country"), LANGUAGE("language"), PLACE_NAME("placeName"), SEGMENT_COUNT("segment_count"), LINE_STRING("line_string");

  private String _name;

  private ParseConstants(String name) {
    _name = name;
  }

  public String getName() {
    return _name;
  }
}
