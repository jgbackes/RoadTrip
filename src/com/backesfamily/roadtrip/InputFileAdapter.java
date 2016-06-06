package com.backesfamily.roadtrip;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 5/10/15
 * Time: 9:42 PM
 */
public class InputFileAdapter extends FileSelectionAdapter {
  JTextField _field;
  String _key;
  String _description;
  String[] _fileTypes;

  public InputFileAdapter(JFrame frame, JTextField field, String key, String description, final String... fileTypes) {
    super(frame, field, key);
    _field = field;
    _key = key;
    _description = description;
    _fileTypes = fileTypes;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    getInputFilePath(_description, _fileTypes);
  }
}
