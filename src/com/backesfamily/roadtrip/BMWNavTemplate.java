package com.backesfamily.roadtrip;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: jbackes
 * Date: 4/15/15
 * Time: 4:45 PM
 */

public class BMWNavTemplate extends BMWBaseTemplate implements BMWTemplate {

  public BMWNavTemplate(HashMap<String, Object> variables) {
    _variables = variables;
  }

  public StringBuilder renderHeadingToXML() {
    interlacePointArrays();
    StringBuilder result = new StringBuilder();

    String IdName = (String) _variables.get(ParseConstants.ID_NAME.getName());
    String dateStamp = (String) _variables.get(ParseConstants.DT_STAMP.getName());
    String unitOfMeasure = (String) _variables.get(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName());
    String country = (String) _variables.get(ParseConstants.COUNTRY.getName());
    String language = (String) _variables.get(ParseConstants.LANGUAGE.getName());
    String startAddress = (String) _variables.get(ParseConstants.START_ADDRESS.getName());
    String area = startAddress.substring(startAddress.lastIndexOf(",") + 2);
//    String waypointDescription = startAddress;
    Double totalDistance = (Double) _variables.get(ParseConstants.TOTAL_DISTANCE.getName());
    Double travelTime = (Double) _variables.get((ParseConstants.TRAVEL_TIME.getName()));

    String routeName = (String) _variables.get(ParseConstants.ROUTE_NAME.getName());

    String description = (String) _variables.get(ParseConstants.DESCRIPTION.getName());
    if (description == null || description.length() == 0) {
      description = (String) _variables.get(ParseConstants.USER_DESCRIPTION.getName());
    }

    result.append("<DeliveryPackage VersionNo=\"0.0\" CreationTime=\"").append(dateStamp).append("\" MapVersion=\"0.0\" Language_Code_Desc=\"../definitions/language.xml\" Country_Code_Desc=\"../definitions/country.xml\" Supplier_Code_Desc=\"../definitions/supplier.xml\" XY_Type=\"WGS84\" Category_Code_Desc=\"../definitions/category.xml\" Char_Set=\"UTF-8\" UpdateType=\"BulkUpdate\" Coverage=\"0\" Category=\"4096\" MajorVersion=\"0\" MinorVersion=\"0\">\n");
    result.append(" <GuidedTour access=\"WEEKDAYS\" use=\"ONFOOT\">\n");
    result.append("   <Id>").append(IdName).append("</Id>\n");
    result.append("   <TripType>6</TripType>\n");
    result.append(renderCountries(language, country));
    result.append(renderNames(routeName, language));
    result.append("   <Length Unit=\"").append(unitOfMeasure).append("\">").append(String.format(floatFormat, totalDistance)).append("</Length>\n");
    result.append("   <Duration Unit=\"h\">").append(String.format(floatFormat, travelTime)).append("</Duration>\n");
    result.append(renderIntroductions(routeName, language));
    result.append(renderDescriptions(description, language));
    result.append(renderPictures(IdName));
    result.append(renderEntryPoints());
    result.append("   <Routes>\n");
    result.append("     <Route>\n");
    result.append("       <RouteID>").append(IdName).append("</RouteID>\n");
    result.append(renderWaypoint(0, area, startAddress, IdName, language, "always"));

    return result;
  }

  public StringBuilder renderBodyToXML() {
    StringBuilder result = new StringBuilder();

    Integer BMW_Points = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());
    String waypointDescription = null;
    String language = (String) _variables.get(ParseConstants.LANGUAGE.getName());
    String area = null;

    for (int i = 1; i < (BMW_Points - 1); i++) {
      String placeName = (String) (_variables.get(ParseConstants.PLACE_NAME.getName() + i));
      result.append(renderWaypoint(i, area, placeName, waypointDescription, language, "optional"));
    }
    return result;
  }

  @Override
  public StringBuilder renderFootingToXML() {
    StringBuilder result = new StringBuilder();

    String unitOfMeasure = (String) _variables.get(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName());
    Integer pointCount = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());
    Double totalDistance = (Double) _variables.get(ParseConstants.TOTAL_DISTANCE.getName());
    Double travelTime = (Double) _variables.get((ParseConstants.TRAVEL_TIME.getName()));
    String endAddress = (String) _variables.get(ParseConstants.END_ADDRESS.getName());
    String area = "End";
    String waypointDescription = endAddress;
    String language = (String) _variables.get(ParseConstants.LANGUAGE.getName());

    // 'then finish off the destination details
    result.append(renderWaypoint(pointCount - 1, area, endAddress, waypointDescription, language, "always"));
    result.append("       <Length Unit=\"").append(unitOfMeasure).append("\">").append(String.format("%.3f", totalDistance)).append("</Length>\n");
    result.append("       <Duration Unit=\"h\">").append(String.format("%.4f", travelTime)).append("</Duration>\n");
    result.append("       <CostModel>0</CostModel>\n");
    result.append("       <Criteria>0</Criteria>\n");
    result.append("       <AgoraCString>\n");
    result.append("       </AgoraCString>\n");
    result.append("     </Route>\n");
    result.append("   </Routes>\n");
    result.append(" </GuidedTour>\n");
    result.append("</DeliveryPackage>\n");

    return result;
  }

  private StringBuilder renderIntroductions(String introduction, String language) {
    StringBuilder result = new StringBuilder();

    result.append("   <Introductions>\n");
    result.append("     <Introduction Language_Code=\"").append(language).append("\">\n");
    result.append("       <Text>").append(introduction).append("</Text>\n");
    result.append("     </Introduction>\n");
    result.append("   </Introductions>\n");

    return result;
  }

  private StringBuilder renderNames(String routeName, String language) {
    StringBuilder result = new StringBuilder();

    result.append("   <Names>\n");
    result.append("     <Name Language_Code=\"").append(language).append("\">\n");
    result.append("       <Text>").append(routeName).append("</Text>\n");
    result.append("     </Name>\n");
    result.append("   </Names>\n");

    return result;
  }

  private StringBuilder renderCountries(String language, String country) {
    StringBuilder result = new StringBuilder();

    result.append("   <Countries>\n");
    result.append("     <Country>\n");
    result.append("       <CountryCode>3</CountryCode>\n");
    result.append("       <Name Language_Code=\"").append(language).append("\">").append(country).append("</Name>\n");
    result.append("     </Country>\n");
    result.append("   </Countries>\n");

    return result;
  }

  private StringBuilder renderPictures(String IdName) {
    StringBuilder result = new StringBuilder();

    result.append("   <Pictures>\n");
    result.append("     <Picture>\n");
    result.append("       <Reference>routepicture_").append(IdName).append(".jpg</Reference>\n");
    result.append("       <Encoding>JPEG</Encoding>\n");
    result.append("       <Width>252</Width>\n");
    result.append("       <Height>172</Height>\n");
    result.append("     </Picture>\n");
    result.append("   </Pictures>\n");

    return result;
  }

  private StringBuilder renderDescriptions(String description, String language) {
    StringBuilder result = new StringBuilder();

    if ((language != null) && description != null) {
      result.append("   <Descriptions>\n");
      result.append("     <Description Language_Code=\"").append(language).append("\">\n");
      result.append("       <Text>").append(description).append("</Text>\n");
      result.append("     </Description>\n");
      result.append("   </Descriptions>\n");
    }

    return result;
  }

  private StringBuilder renderWaypoint(int locationNumber, String area, String address, String waypointDescription, String language, String importance) {
    StringBuilder result = new StringBuilder();
    result.append("       <WayPoint>\n");
    result.append("         <Id>").append(locationNumber).append("</Id>\n");
    result.append(renderLocations(locationNumber, area, address));
    result.append("         <Importance>").append(importance).append("</Importance>\n");
    result.append(renderDescriptions(waypointDescription, language));
    result.append("       </WayPoint>\n");
    return result;
  }

  private StringBuilder renderLocations(int locationNumber, String area, String address) {
    StringBuilder result = new StringBuilder();
    result.append("         <Locations>\n");
    result.append("           <Location>\n");
    result.append(renderAddress(area, address));
    result.append(renderGeoPosition(locationNumber));
    result.append("           </Location>\n");
    result.append("         </Locations>\n");
    return result;
  }

  private StringBuilder renderAddress(String area, String address) {
    StringBuilder result = new StringBuilder();

    if (address != null) {
      result.append("             <Address>\n");
      result.append("               <ParsedAddress>\n");
      result.append(renderParsedStreetAddress(address));
      if (area != null) {
        result.append("                 <ParsedPlace>\n");
        result.append("                   <PlaceLevel4>").append(area).append("</PlaceLevel4>\n");
        result.append("                 </ParsedPlace>\n");
      }
      result.append("               </ParsedAddress>\n");
      result.append("             </Address>\n");
    }
    return result;
  }

  private StringBuilder renderParsedStreetAddress(String parsedStreetAddress) {
    StringBuilder result = new StringBuilder();

    if (parsedStreetAddress != null) {
      result.append("                 <ParsedStreetAddress>\n");
      result.append("                   <ParsedStreetName>\n");
      result.append("                     <StreetName>").append(parsedStreetAddress).append("</StreetName>\n");
      result.append("                   </ParsedStreetName>\n");
      result.append("                 </ParsedStreetAddress>\n");
    }

    return result;
  }

  private StringBuilder renderGeoPosition(int pointCount) {
    StringBuilder result = new StringBuilder();
    Double latitude = (Double) _variables.get(ParseConstants.LATITUDE.getName() + pointCount);
    Double longitude = (Double) _variables.get(ParseConstants.LONGITUDE.getName() + pointCount);

    result.append("             <GeoPosition>\n");
    result.append("               <Latitude>").append(String.format(floatFormat, latitude)).append("</Latitude>\n");
    result.append("               <Longitude>").append(String.format(floatFormat, longitude)).append("</Longitude>\n");
    result.append("             </GeoPosition>\n");

    return result;
  }

  private StringBuilder renderEntryPoints() {
    StringBuilder result = new StringBuilder();
    int streetNameCount = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());

    result.append("    <EntryPoints>\n");
    for (int i = 1; i <= streetNameCount; i++) {
      result.append("      <EntryPoint Route=\"1\">").append(i).append("</EntryPoint>\n");
    }
    result.append("    </EntryPoints>\n");

    return result;
  }

  private void interlacePointArrays() {
    ArrayList<MapPoint> segments = new ArrayList<>();
    ArrayList<MapPoint> points = new ArrayList<>();

    Integer segmentCount = (Integer) _variables.get(ParseConstants.SEGMENT_COUNT.getName());
    Integer pointCount = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());

    for (int i = 0; i < segmentCount; i++) {
      segments.add(new MapPoint(
          (double) _variables.get(ParseConstants.LINE_STRING.getName() + ParseConstants.LATITUDE.getName() + i)
          , (double) _variables.get(ParseConstants.LINE_STRING.getName() + ParseConstants.LONGITUDE.getName() + i)
          , (double) _variables.get(ParseConstants.LINE_STRING.getName() + ParseConstants.ALTITUDE.getName() + i)

      ));
    }

    for (int i = 0; i < pointCount; i++) {
      points.add(new MapPoint((double) _variables.get(ParseConstants.LATITUDE.getName() + i),
          (double) _variables.get(ParseConstants.LONGITUDE.getName() + i),
          (double) _variables.get(ParseConstants.ALTITUDE.getName() + i)));
    }
  }
}
