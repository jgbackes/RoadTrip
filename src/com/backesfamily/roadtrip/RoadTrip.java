package com.backesfamily.roadtrip;

/**
 * User: jbackes
 * Date: 4/20/15
 * Time: 1:03 PM
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class RoadTrip {

  //  private final static DateTimeFormatter CREATION_DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
//  private final static DateTimeFormatter ID_DATE_TIME_FORMAT = DateTimeFormat.forPattern("HHmmss");
  private final static String HOME_FOLDER = System.getProperty("user.home");
  private final static String LAST_PICTURE_FILE = "LAST_PICTURE_FILE";
  private final static String LAST_OUTPUT_DIR = "LAST_OUTPUT_DIR";
  private final static String LAST_KMZ_KML_FILE = "LAST_KMZ_KML_INPUT";
  private final static String ROUTE_FRAME_TOP = "ROUTE_FRAME_TOP";
  private final static String ROUTE_FRAME_LEFT = "ROUTE_FRAME_LEFT";
  private final static String ROUTE_PANEL_WIDTH = "ROUTE_PANEL_WIDTH";
  private final static String ROUTE_PANEL_HEIGHT = "ROUTE_PANEL_HEIGHT";
  private final static ResourceBundle resourceBundle = ResourceBundle.getBundle("com.backesfamily.roadtrip.roadtrip");

  private final String DESKTOP = "Desktop";
  private final String DOCUMENTS = "Documents";


  private KMLParse _kmlParse;
  private Preferences _preferences;

  private JFrame _frame;
  private JPanel _routePanel;
  private JButton _helpButton;
  private JButton _cancelButton;
  private JButton _parseButton;
  //private JButton _displayMap;
  private JTextArea _routeDescription;
  private JTextField _sourceKMLPath;
  private JTextField _sourceJPGPath;
  private JTextField _outputPath;
  private JLabel _statusArea;
  private JLabel _selectKML;
  private JLabel _selectJPG;
  private JLabel _selectOutput;
  private JLabel _openKMLPath;
  private JLabel _openPicturePath;
  private JLabel _openOutputPath;


  public RoadTrip() {
    _kmlParse = new KMLParse();

    // On OSX /Users/<user>/Library/Preferences/com.backesfamily.roadtrip
    _preferences = Preferences.userNodeForPackage(this.getClass());

    String routePanelWidth = _preferences.get(ROUTE_PANEL_WIDTH, "550");
    String routePanelHeight = _preferences.get(ROUTE_PANEL_HEIGHT, "300");
    Dimension routePanelSize = new Dimension(Integer.parseInt(routePanelWidth), Integer.parseInt(routePanelHeight));
    _routePanel.setPreferredSize(routePanelSize);

    final String defaultLastPicturePath = HOME_FOLDER + File.separator + DESKTOP + File.separator + resourceBundle.getString("FILENAME_EXAMPLE_PICTURE");
    String lastPictureFile = _preferences.get(LAST_PICTURE_FILE, defaultLastPicturePath);
    _sourceJPGPath.setText(lastPictureFile);

    final String defaultLastKMLPath = HOME_FOLDER + File.separator + DOCUMENTS + File.separator + resourceBundle.getString("FILENAME_EXAMPLE_KML");
    String lastKMLInput = _preferences.get(LAST_KMZ_KML_FILE, defaultLastKMLPath);
    _sourceKMLPath.setText(lastKMLInput);

    String defaultOutputDirectoryPath = HOME_FOLDER + File.separator + DESKTOP + File.separator;
    String lastOutputDirectory = _preferences.get(LAST_OUTPUT_DIR, defaultOutputDirectoryPath);
    _outputPath.setText(lastOutputDirectory);

    _helpButton.addActionListener(e -> {
      Help help = new Help(resourceBundle.getString("MESSAGE_HELP_TEXT"));
      help.pack();
      help.setVisible(true);
    });

    _cancelButton.addActionListener(e -> {
      //noinspection CallToSystemExit
      System.exit(1);
    });

    _parseButton.addActionListener(e -> {
      BMWBundleBuilder builder = new BMWBundleBuilder()
          .withKmlParse(_kmlParse)
          .withOutputPath(_outputPath)
          .withParseButton(_parseButton)
          .withRouteDescription(_routeDescription)
          .withSourceJPGPath(_sourceJPGPath)
          .withSourceKMLPath(_sourceKMLPath)
          .withStatusArea(_statusArea);

      builder.handleParseButton();
    });

    _routePanel.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent evt) {
        Component c = (Component) evt.getSource();
        String width = c.getWidth() + "";
        String height = c.getHeight() + "";
        _preferences.put(ROUTE_PANEL_WIDTH, width);
        _preferences.put(ROUTE_PANEL_HEIGHT, height);
      }
    });

    addMouseListeners();
//    _displayMap.addActionListener(e -> {
//
//      JFrame preview = new MapFrame(_sourceKMLPath.getText());
//      preview.pack();
//      preview.setVisible(true);
//    });
  }

  public static void main(String[] args) {
    RoadTrip roadTrip = new RoadTrip();
    System.setProperty("awt.useSystemAAFontSettings", "on");
    System.setProperty("swing.aatext", "true");
    roadTrip._frame = new JFrame(resourceBundle.getString("MESSAGE_ABOUT"));
    roadTrip._frame.setContentPane(roadTrip._routePanel);
    roadTrip._frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    roadTrip._frame.pack();

    roadTrip.restoreLocation();
    roadTrip.addMoveListener();

    roadTrip._frame.setIconImages(roadTrip.getApplicationIcons());
    roadTrip._frame.setVisible(true);
  }

  private void addMouseListeners() {
    _selectKML.addMouseListener(new InputFileAdapter(_frame, _sourceKMLPath, LAST_KMZ_KML_FILE, resourceBundle.getString("MESSAGE_KML_INPUT_FILE"), "kml", "kmz"));
    _selectJPG.addMouseListener(new InputFileAdapter(_frame, _sourceJPGPath, LAST_PICTURE_FILE, resourceBundle.getString("MESSAGE_SELECT_ROUTE_PICTURE"), "jpg", "gif", "png"));
    _selectOutput.addMouseListener(new OutputDirectoryAdapter(_frame, _outputPath, resourceBundle.getString("MESSAGE_DESTINATION_FOLDER")));

    _openKMLPath.addMouseListener(new DesktopMouseAdapter(_sourceKMLPath, false));
    _openPicturePath.addMouseListener(new DesktopMouseAdapter(_sourceJPGPath, false));
    _openOutputPath.addMouseListener(new DesktopMouseAdapter(_outputPath, true));
  }

  @SuppressWarnings("HardcodedFileSeparator")
  private ArrayList<Image> getApplicationIcons() {
    String[] imagesNames = new String[]{
        "images/20x20_app_icon.png"
        , "images/32x32_app_icon.png"
        , "images/128x128_app_icon.png"
        , "images/400x400_app_icon.png"};

    ArrayList<Image> images = new ArrayList<>();
    for (String name : imagesNames) {
      URL resource = this.getClass().getResource(name);
      Image im = new ImageIcon(resource).getImage();
      images.add(im);
    }

    return images;
  }

  private void restoreLocation() {
    String left = _preferences.get(ROUTE_FRAME_LEFT, "0");
    String top = _preferences.get(ROUTE_FRAME_TOP, "0");
    if (left.equals("0") && top.equals("0")) {
      _frame.setLocationRelativeTo(null);
    } else {
      _frame.setLocation((int) Double.parseDouble(left), (int) Double.parseDouble(top));
    }
  }

  private void addMoveListener() {
    _frame.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentMoved(ComponentEvent evt) {
        Component c = (Component) evt.getSource();
        String left = ((int) c.getLocation().getX()) + "";
        String top = ((int) c.getLocation().getY()) + "";
        _preferences.put(ROUTE_FRAME_TOP, top);
        _preferences.put(ROUTE_FRAME_LEFT, left);
      }
    });
  }

  private void createUIComponents() {
    // No custom components in this UI
  }
}
