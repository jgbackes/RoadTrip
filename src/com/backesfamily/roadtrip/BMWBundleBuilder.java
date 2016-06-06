package com.backesfamily.roadtrip;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

/**
 * Copyright Jeffrey G. Backes. All Rights Reserved.
 * User: jbackes
 * Date: 5/11/15
 * Time: 1:13 AM
 */
public class BMWBundleBuilder {
  private final static DateTimeFormatter CREATION_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
  private final static DateTimeFormatter ID_DATE_TIME_FORMAT = DateTimeFormat.forPattern("HHmmss");
  private ResourceBundle resourceBundle = ResourceBundle.getBundle("com.backesfamily.roadtrip.roadtrip");
  private final String WORKING = resourceBundle.getString("MESSAGE_WORKING");
  private final String READY = resourceBundle.getString("MESSAGE_READY");
  private KMLParse _kmlParse;
  private JButton _parseButton;
  private JTextField _sourceJPGPath;
  private JTextField _sourceKMLPath;
  private JTextField _outputPath;
  private JTextArea _routeDescription;
  private JLabel _statusArea;

  public BMWBundleBuilder() {
  }

  public BMWBundleBuilder withKmlParse(KMLParse _kmlParse) {
    this._kmlParse = _kmlParse;
    return this;
  }

  public BMWBundleBuilder withParseButton(JButton _parseButton) {
    this._parseButton = _parseButton;
    return this;
  }

  public BMWBundleBuilder withSourceJPGPath(JTextField _sourceJPGPath) {
    this._sourceJPGPath = _sourceJPGPath;
    return this;
  }

  public BMWBundleBuilder withSourceKMLPath(JTextField _sourceKMLPath) {
    this._sourceKMLPath = _sourceKMLPath;
    return this;
  }

  public BMWBundleBuilder withOutputPath(JTextField _outputPath) {
    this._outputPath = _outputPath;
    return this;
  }

  public BMWBundleBuilder withRouteDescription(JTextArea _routeDescription) {
    this._routeDescription = _routeDescription;
    return this;
  }

  public BMWBundleBuilder withStatusArea(JLabel _statusArea) {
    this._statusArea = _statusArea;
    return this;
  }

  public void handleParseButton() {
    final SwingWorker<HashMap<String, Object>, Integer> swingWorker = new SwingWorker<HashMap<String, Object>, Integer>() {
      @Override
      protected HashMap<String, Object> doInBackground() throws Exception {
        _parseButton.setEnabled(false);
        _statusArea.setText(WORKING);

        LocalDateTime date = LocalDateTime.now();

        String dateTime = date.toString(CREATION_DATE_TIME_FORMAT) + "Z";
        String id = date.toString(ID_DATE_TIME_FORMAT);

        _kmlParse.putElement(ParseConstants.USER_DESCRIPTION.getName(), _routeDescription.getText());

        _kmlParse.putElement(ParseConstants.COUNTRY.getName(), "Germany");
        _kmlParse.putElement(ParseConstants.LANGUAGE.getName(), "ENG");

        _kmlParse.putElement(ParseConstants.DT_STAMP.getName(), dateTime);
        _kmlParse.putElement(ParseConstants.ID_NAME.getName(), id);

        return _kmlParse.parseFile(_sourceKMLPath.getText());
      }

      @Override
      protected void done() {
        try {
          HashMap<String, Object> results = this.get();

          createBMWBundle(results);
        } catch (InterruptedException | ExecutionException | IOException e) {
          displayError(e);
        } finally {
          _statusArea.setText(READY);
          _parseButton.setEnabled(true);
        }
      }
    };
    swingWorker.execute();
  }

  private void addFileToCompression(TarArchiveOutputStream outputStream, File f, String fileName) throws IOException {
    TarArchiveEntry tae = new TarArchiveEntry(f, fileName);
    FileInputStream fis = null;
    outputStream.putArchiveEntry(tae);
    try {
      fis = new FileInputStream(f);
      IOUtils.copy(fis, outputStream);
    } finally {
      if (fis != null) {
        fis.close();
      }
      outputStream.flush();
      outputStream.closeArchiveEntry();
    }
  }

  private void createTarZipWithFiles(java.util.List<File> list, File outFile) throws IOException {
    TarArchiveOutputStream tarOutputStream = null;
    try {
      tarOutputStream = new TarArchiveOutputStream(
          new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outFile))));

      tarOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
      tarOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

      for (File file : list) {
        addFileToCompression(tarOutputStream, file, file.getName());
      }
    } finally {
      if (tarOutputStream != null) {
        tarOutputStream.close();
      }
    }
  }

  private File savePictureFile(String samplePictureName, String destFolderName, String destFileName) throws IOException {
    File pictureDestFile = new File(String.format("%sroutepicture_%s.jpg", destFolderName, destFileName));
    File sourcePictureFile = new File(samplePictureName);

    FileUtils.copyFile(sourcePictureFile, pictureDestFile);

    return pictureDestFile;
  }

  private File saveFile(BMWTemplate template, String folderName, String fileID) throws IOException {
    StringBuilder heading = template.renderHeadingToXML();
    StringBuilder body = template.renderBodyToXML();
    StringBuilder footing = template.renderFootingToXML();

    String xmlOutput = heading.append(body).append(footing).toString();
    //System.out.println(xmlOutput);
    File outputFile = new File(String.format("%s%s.xml", folderName, fileID));
    FileWriter fw = null;

    try {
      fw = new FileWriter(outputFile);
      fw.write(xmlOutput);
    } finally {
      if (fw != null) {
        fw.close();
      }
    }
    return outputFile;
  }

  private void applyTemplateAndTARZ(BMWTemplate template, String folderName, String ID, String fileName, String pictureName) throws IOException {
    File xmlFile = saveFile(template, folderName, ID);
    File pictureFile = savePictureFile(pictureName, folderName, ID);
    File gzOutFile = new File(String.format("%s%s.tar.gz", folderName, fileName));

    int i = 2;
    while (gzOutFile.exists()) {
      gzOutFile = new File(String.format("%s%s%d", folderName, fileName, i++));
    }

    ArrayList<File> files = new ArrayList<>();
    files.add(xmlFile);
    files.add(pictureFile);
    createTarZipWithFiles(files, gzOutFile);

    StringBuilder deleteErrors = new StringBuilder();

    deleteErrors.append(xmlFile.delete() ? "" : String.format(resourceBundle.getString("ERROR_UNABLE_TO_DELETE_XML_FILE"), xmlFile.getAbsolutePath()));
    deleteErrors.append(pictureFile.delete() ? "" : String.format(resourceBundle.getString("ERROR_FILE_DELETE"), deleteErrors.length() > 0 ? "\n" : "", pictureFile.getAbsolutePath()));

    if (deleteErrors.toString().length() > 0) {
      throw new IOException(String.format(resourceBundle.getString("ERROR_TEMP_FILES_NOT_DELETED"), deleteErrors));
    }
  }

  private void createBMWBundle(HashMap<String, Object> variables) throws IOException {

    String ID = (String) _kmlParse.getElement(ParseConstants.ID_NAME.getName());
    String BMWDataFolderName = String.format("%s%sBMWData%s", _outputPath.getText(), File.separator, File.separator);
    String navFolderName = String.format("%sNav%s", BMWDataFolderName, File.separator);
    String navigationFolderName = String.format("%sNavigation%s", BMWDataFolderName, File.separator);
    String routesFolderName = String.format("%sRoutes%s", navigationFolderName, File.separator);

    String[] foldersToCreate = {BMWDataFolderName, navFolderName, navigationFolderName, routesFolderName};

    for (String folder : foldersToCreate) {
      File outputDirectory = new File(folder);

      if (outputDirectory.exists()) {
        if (!outputDirectory.isDirectory()) {
          throw new IOException(String.format(resourceBundle.getString("DUPLICATE_FILE_NAME"), folder));
        }
      } else {
        if (!outputDirectory.mkdirs()) {
          throw new IOException(String.format(resourceBundle.getString("MESSAGE_FOLDER_CREATE_ERROR"), folder));
        }
      }
    }

    String routeName = (String) _kmlParse.getElement(ParseConstants.ROUTE_NAME.getName());
    routeName = scrubFileName(routeName);

    String pictureName = _sourceJPGPath.getText();

    // Create the NAV version of the output file
    applyTemplateAndTARZ(new BMWNavTemplate(variables), navFolderName, ID, routeName, pictureName);

    // Create the NAVIGATION version of the output file
    applyTemplateAndTARZ(new BMWNavigationTemplate(variables), routesFolderName, ID, routeName, pictureName);
  }

  @SuppressWarnings("HardcodedFileSeparator")
  private String scrubFileName(String fileName) {
    fileName = fileName.replace(":", "_");
    fileName = fileName.replace("/", "_");
    fileName = fileName.replace("\\", "_");
    fileName = fileName.replace("*", "_");
    fileName = fileName.replace("?", "_");

    return fileName;
  }

  public void displayError(Exception e) {
    Error dialog = new Error(e);
    dialog.pack();
    dialog.setVisible(true);
  }
}
