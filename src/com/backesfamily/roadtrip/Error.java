package com.backesfamily.roadtrip;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Error extends JDialog {
  private JPanel contentPane;
  private JButton buttonOK;
  private JTextArea _errorMessage;

  public Error(Exception e) {
    setContentPane(contentPane);
    StringBuilder stackTrace = new StringBuilder();

    stackTrace.append(e.getLocalizedMessage() + "\n\n");

    for (StackTraceElement element : e.getStackTrace()) {
      stackTrace.append(element.toString()).append("\n");
    }
    _errorMessage.setText(stackTrace.toString());
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);

    buttonOK.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        onOK();
      }
    });
  }

  private void onOK() {
    dispose();
  }
}
