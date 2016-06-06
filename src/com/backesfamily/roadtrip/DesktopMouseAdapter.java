package com.backesfamily.roadtrip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

/**
 * User: jbackes
 * Date: 5/11/15
 * Time: 2:22 PM
 */
public class DesktopMouseAdapter extends MouseAdapter {

  private final JTextField _textField;
  private final boolean _isFolder;

  public DesktopMouseAdapter(JTextField textField, boolean isFolder) {
    _textField = textField;
    _isFolder = isFolder;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    openOnDesktop();
  }

  private void openOnDesktop() {
    final String text = _textField.getText();
    if (Desktop.isDesktopSupported()) {
      SwingUtilities.invokeLater(() -> {
        try {
          String pathName;
          if (!_isFolder) {
            pathName = extractHostFolder(text);
          } else {
            pathName = text;
          }
          Desktop.getDesktop().open(new File(pathName));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      });
    }
  }

  private String extractHostFolder(String fullPath) {
    String folder = "";
    int directoryPathPart = fullPath.lastIndexOf(File.separator);
    if (directoryPathPart > 0) {
      folder = fullPath.substring(0, directoryPathPart);
    }
    return folder;
  }
}
