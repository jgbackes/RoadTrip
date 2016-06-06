package com.backesfamily.roadtrip;

import java.util.HashMap;

/**
 * User: jbackes
 * Date: 4/15/15
 * Time: 4:45 PM
 */

public class BMWNavigationTemplate extends BMWBaseTemplate implements BMWTemplate {

  public BMWNavigationTemplate(HashMap<String, Object> variables) {
    _variables = variables;
  }

  @SuppressWarnings("HardcodedFileSeparator")
  public StringBuilder renderHeadingToXML() {
    StringBuilder result = new StringBuilder();

    String IdName = (String) _variables.get(ParseConstants.ID_NAME.getName());
    String dateStamp = (String) _variables.get(ParseConstants.DT_STAMP.getName());
    String startAddress = (String) _variables.get(ParseConstants.START_ADDRESS.getName());
    String unitOfMeasure = (String) _variables.get(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName());
    String country = (String) _variables.get(ParseConstants.COUNTRY.getName());
    String language = (String) _variables.get(ParseConstants.LANGUAGE.getName());
    String area = startAddress.substring(startAddress.lastIndexOf(",") + 2);

    String routeName = (String) _variables.get(ParseConstants.ROUTE_NAME.getName());

    String description = (String) _variables.get(ParseConstants.DESCRIPTION.getName());
    if (description == null || description.length() == 0) {
      description = (String) _variables.get(ParseConstants.USER_DESCRIPTION.getName());
    }

    int endIndex = description.indexOf(".");
    if (endIndex > 0) {
      description = description.substring(0, endIndex);
    }

    Double totalDistance = (Double) _variables.get(ParseConstants.TOTAL_DISTANCE.getName());
    Double travelTime = (Double) _variables.get((ParseConstants.TRAVEL_TIME.getName()));

    result.append("<DeliveryPackage VersionNo=\"0.0\" CreationTime=\"")
        .append(dateStamp)
        .append("\" MapVersion=\"0.0\" " +
            "Language_Code_Desc=\"../definitions/language.xml\" " +
            "Country_Code_Desc=\"../definitions/country.xml\" " +
            "Supplier_Code_Desc=\"../definitions/supplier.xml\" " +
            "XY_Type=\"WGS84\" " +
            "Category_Code_Desc=\"../definitions/category.xml\" " +
            "Char_Set=\"UTF-8\" " +
            "UpdateType=\"BulkUpdate\" " +
            "Coverage=\"0\" " +
            "Category=\"4096\" " +
            "MajorVersion=\"0\" " +
            "MinorVersion=\"0\">\n");
    result.append(" <GuidedTour access=\"WEEKDAYS\" use=\"ONFOOT\">\n");
    result.append("   <Id>").append(IdName).append("</Id>\n");
    result.append("   <TripType>6</TripType>\n");
    result.append("   <Countries>\n");
    result.append("     <Country>\n");
    result.append("       <CountryCode>3</CountryCode>\n");
    result.append("       <Name Language_Code=\"").append(language).append("\">").append(country).append("</Name>\n");
    result.append("     </Country>\n");
    result.append("   </Countries>\n");
    result.append("   <Names>\n");
    result.append("     <Name Language_Code=\"").append(language).append("\">\n");
    result.append("       <Text>").append(routeName).append("</Text>\n");
    result.append("     </Name>\n");
    result.append("   </Names>\n");
    result.append("   <Length Unit=\"").append(unitOfMeasure).append("\">").append(String.format(floatFormat, totalDistance)).append("</Length>\n");
    result.append("   <Duration Unit=\"h\">").append(String.format(floatFormat, travelTime)).append("</Duration>\n");
    result.append("   <Introductions>\n");
    result.append("     <Introduction Language_Code=\"").append(language).append("\">\n");
    result.append("       <Text>").append(description).append("</Text>\n");
    result.append("     </Introduction>\n");
    result.append("   </Introductions>\n");
    result.append("   <Descriptions>\n");
    result.append("     <Description Language_Code=\"").append(language).append("\">\n");
    result.append("       <Text>").append("RoadTrip by jgb").append("</Text>\n");
    result.append("     </Description>\n");
    result.append("   </Descriptions>\n");
    result.append("   <Pictures>\n");
    result.append("     <Picture>\n");
    result.append("       <Reference>routepicture_").append(IdName).append(".jpg</Reference>\n");
    result.append("       <Encoding>JPEG</Encoding>\n");
    result.append("       <Width>252</Width>\n");
    result.append("       <Height>172</Height>\n");
    result.append("     </Picture>\n");
    result.append("   </Pictures>\n");
    result.append(renderEntryPoints());
    result.append("   <Routes>\n");
    result.append("     <Route>\n");
    result.append("       <RouteID>").append(IdName).append("</RouteID>\n");
    result.append("       <WayPoint>\n");
    result.append("         <Id>0_0</Id>\n");
    result.append("         <Locations>\n");
    result.append("           <Location>\n");
    result.append("             <Address>\n");
    result.append("               <ParsedAddress>\n");
    result.append("                 <ParsedStreetAddress>\n");
    result.append("                   <ParsedStreetName>\n");
    result.append("                     <StreetName>").append(startAddress).append("</StreetName>\n");
    result.append("                   </ParsedStreetName>\n");
    result.append("                 </ParsedStreetAddress>\n");
    result.append("                 <ParsedPlace>\n");
    result.append("                   <PlaceLevel4>").append(area).append("</PlaceLevel4>\n");
    result.append("                 </ParsedPlace>\n");
    result.append("               </ParsedAddress>\n");
    result.append("             </Address>\n");
    result.append(renderGeoPosition(0));
    result.append("           </Location>\n");
    result.append("         </Locations>\n");
    result.append("         <Importance>always</Importance>\n");
    result.append("         <Descriptions>\n");
    result.append("           <Description Language_Code=\"").append(language).append("\">\n");
    result.append("             <Text>").append(IdName).append("</Text>\n");
    result.append("           </Description>\n");
    result.append("         </Descriptions>\n");
    result.append("       </WayPoint>\n");

    return result;
  }

  public StringBuilder renderBodyToXML() {
    StringBuilder result = new StringBuilder();

    Integer BMW_Points = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());

    for (int i = 1; i < (BMW_Points - 1); i++) {

      result.append("       <WayPoint>\n");
      result.append("         <Id>0_").append(i).append("</Id>\n");
      result.append("         <Locations>\n");
      result.append("           <Location>\n");
      result.append(renderAddress(i));
      result.append(renderGeoPosition(i));
      result.append("           </Location>\n");
      result.append("         </Locations>\n");
      result.append("         <Importance>always</Importance>\n");
      result.append("       </WayPoint>\n");
    }
    return result;
  }

  public StringBuilder renderFootingToXML() {
    StringBuilder result = new StringBuilder();

    String unitOfMeasure = (String) _variables.get(ParseConstants.DISTANCE_UNIT_OF_MEASURE.getName());
    Integer pointCount = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());
    Double totalDistance = (Double) _variables.get(ParseConstants.TOTAL_DISTANCE.getName());
    Double travelTime = (Double) _variables.get((ParseConstants.TRAVEL_TIME.getName()));

    // 'then finish off the destination details
    result.append("       <WayPoint>\n");
    result.append("         <Id>0_").append(pointCount - 1).append("</Id>\n");
    result.append("         <Locations>\n");
    result.append("           <Location>\n");
    result.append("             <Address>\n");
    result.append("               <ParsedAddress>\n");
    result.append(renderParsedStreetAddress());
    result.append("                 <ParsedPlace>\n");
    result.append("                   <PlaceLevel4>End</PlaceLevel4>\n");
    result.append("                 </ParsedPlace>\n");
    result.append("               </ParsedAddress>\n");
    result.append("             </Address>\n");
    result.append(renderGeoPosition(pointCount - 1));
    result.append("           </Location>\n");
    result.append("         </Locations>\n");
    result.append("         <Importance>always</Importance>\n");
    result.append(renderDescriptionLanguageCode());
    result.append("       </WayPoint>\n");
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

  public StringBuilder renderAddress(int i) {
    StringBuilder result = new StringBuilder();
    String placeName = (String) (_variables.get(ParseConstants.PLACE_NAME.getName() + i));

    if (placeName != null) {
      result.append("             <Address>\n");
      result.append("               <ParsedAddress>\n");
      result.append("                 <ParsedStreetAddress>\n");
      result.append("                   <ParsedStreetName>\n");
      result.append("                     <StreetName>").append(placeName).append("</StreetName>\n");
      result.append("                   </ParsedStreetName>\n");
      result.append("                 </ParsedStreetAddress>\n");
      result.append("               </ParsedAddress>\n");
      result.append("             </Address>\n");
    }

    return result;
  }

  public StringBuilder renderGeoPosition(int pointCount) {
    StringBuilder result = new StringBuilder();
    Double latitude = (Double) _variables.get(ParseConstants.LATITUDE.getName() + pointCount);
    Double longitude = (Double) _variables.get(ParseConstants.LONGITUDE.getName() + pointCount);

    result.append("             <GeoPosition>\n");
    result.append("               <Latitude>").append(String.format(floatFormat, latitude)).append("</Latitude>\n");
    result.append("               <Longitude>").append(String.format(floatFormat, longitude)).append("</Longitude>\n");
    result.append("             </GeoPosition>\n");

    return result;
  }

  public StringBuilder renderEntryPoints() {
    StringBuilder result = new StringBuilder();
    int streetNameCount = (Integer) _variables.get(ParseConstants.POINT_COUNT.getName());

    result.append("    <EntryPoints>\n");
    for (int i = 1; i <= streetNameCount; i++) {
      result.append("      <EntryPoint Route=\"1\">0_").append(i - 1).append("</EntryPoint>\n");
    }
    result.append("    </EntryPoints>\n");

    return result;
  }

  public StringBuilder renderParsedStreetAddress() {
    StringBuilder result = new StringBuilder();
    String endAddress = (String) _variables.get(ParseConstants.END_ADDRESS.getName());

    if (endAddress != null) {
      result.append("                 <ParsedStreetAddress>\n");
      result.append("                   <ParsedStreetName>\n");
      result.append("                     <StreetName>").append(endAddress).append("</StreetName>\n");
      result.append("                   </ParsedStreetName>\n");
      result.append("                 </ParsedStreetAddress>\n");
    }

    return result;
  }

  public StringBuilder renderDescriptionLanguageCode() {
    StringBuilder result = new StringBuilder();
    String endAddress = (String) _variables.get(ParseConstants.END_ADDRESS.getName());
    String language = (String) _variables.get(ParseConstants.LANGUAGE.getName());

    result.append("         <Descriptions>\n");
    result.append("           <Description Language_Code=\"").append(language).append("\">\n");
    result.append("             <Text>").append(endAddress).append("</Text>\n");
    result.append("           </Description>\n");
    result.append("         </Descriptions>\n");

    return result;
  }
}
