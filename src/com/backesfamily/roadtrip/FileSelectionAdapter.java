package com.backesfamily.roadtrip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.util.prefs.Preferences;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 5/10/15
 * Time: 10:49 PM
 */
public class FileSelectionAdapter extends MouseAdapter {
  protected JTextField _textField;
  protected String _key;
  protected Preferences _preferences;
  private JFrame _frame;

  public FileSelectionAdapter(JFrame frame, JTextField textField, String key) {
    _frame = frame;
    _textField = textField;
    _key = key;

    _preferences = Preferences.userNodeForPackage(this.getClass());
  }

  protected String extractHostFolder(String fullPath) {
    String folder = "";
    int directoryPathPart = fullPath.lastIndexOf(File.separator);
    if (directoryPathPart > 0) {
      folder = fullPath.substring(0, directoryPathPart);
    }
    return folder;
  }

  protected void getInputFilePath(String description, final String[] fileTypes) {

    FileDialog d = new FileDialog(_frame);
    d.setDirectory(extractHostFolder(_textField.getText()));
    d.setTitle(description);
    d.setFilenameFilter((dir, name) -> {
      boolean result = false;
      for (String fileType : fileTypes) {
        if (name.endsWith(fileType)) {
          result = true;
          break;
        }
      }
      return result;
    });

    d.setVisible(true);

    String directory = d.getDirectory();
    String file = d.getFile();

    if ((directory != null) && (file != null)) {
      String path = directory + file;
      _textField.setText(path);
      _preferences.put(_key, path);
    }
  }

  protected void getOutputFilePath(String title) {
    System.setProperty("apple.awt.fileDialogForDirectories", "true");

    FileDialog d = new FileDialog(_frame);
    d.setDirectory(_textField.getText());
    d.setTitle(title);

    d.setVisible(true);

    String fileName = d.getFile();
    System.setProperty("apple.awt.fileDialogForDirectories", "false");

    if (fileName != null) {
      _textField.setText(d.getDirectory() + fileName);
      _preferences.put(_key, fileName);
    }
  }
}
