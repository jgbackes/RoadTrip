package com.backesfamily.roadtrip;

import java.util.HashMap;

/**
 * Created by jbackes on 11/20/15
 */
public class BMWBaseTemplate implements BMWTemplate {
  protected HashMap<String, Object> _variables;
  protected String floatFormat = "%.16f";


  @Override
  public StringBuilder renderHeadingToXML() {
    return null;
  }

  @Override
  public StringBuilder renderBodyToXML() {
    return null;
  }

  @Override
  public StringBuilder renderFootingToXML() {
    return null;
  }

}
