package com.backesfamily.roadtrip;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 5/10/15
 * Time: 10:53 PM
 */
public class OutputDirectoryAdapter extends FileSelectionAdapter {

  public OutputDirectoryAdapter(JFrame frame, JTextField _outputPath, String key) {
    super(frame, _outputPath, key);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    getOutputFilePath("Destination folder");
  }
}
