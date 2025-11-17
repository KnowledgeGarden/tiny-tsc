/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import javax.swing.border.*;
import java.awt.event.*;

//import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Environment;
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class SlotEditorPanel extends JPanel {
	final String newTipConcept = "Select Slot Value from Tree";
	final String newTipRule = "Create a new QP Sentence";
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  TreePanel treePanel1;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  JButton selSlotTypeButton = new JButton();
  JPanel jPanel3 = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  Border border1;
  TitledBorder titledBorder1;
  JTextField slotTypeField = new JTextField();
  JPanel jPanel4 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JButton newButton = new JButton();
  JButton deleteButton = new JButton();
  Border border2;
  TitledBorder titledBorder2;
  JList valuesList = new JList();
  DefaultListModel valuesModel = new DefaultListModel();

  public SlotEditorPanel(Environment env) {
    try {
    	treePanel1 = new TreePanel(env);
      jbInit();
      treePanel1.setIsSlaveTree(true);
      valuesList.setModel(valuesModel);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  public void setListener(TreeListener e) {
    treePanel1.setListener(e);
  }

  public void setRootConcept(Concept c) {
	  try {
    treePanel1.setRootConcept(c);
	  } catch (Exception e) {
		  throw new RuntimeException(e);
	  }
  }
  
  /**
   * Different behaviors between
   * slots for <code>Concept</code>s and
   * slots for <code>ProcessRule</code>s and <code>Model</code>s
   */
  public void setIsQP() {
  	newButton.setToolTipText(newTipRule);
  	this.treePanel1.setEnabled(false);
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder1 = new TitledBorder(border1,"Slot Type");
    border2 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder2 = new TitledBorder(border2,"Slot Values");
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jPanel2.setLayout(borderLayout3);
    selSlotTypeButton.setBorder(BorderFactory.createRaisedBevelBorder());
    selSlotTypeButton.setToolTipText("Select Slot Value from Tree");
    selSlotTypeButton.setText("Select");
    selSlotTypeButton.addActionListener(new SlotEditorPanel_selSlotTypeButton_actionAdapter(this));
    jPanel3.setLayout(borderLayout4);
    jPanel2.setBorder(titledBorder1);
    slotTypeField.setEditable(false);
    slotTypeField.setText("");
    jPanel4.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(10);
    newButton.setBorder(BorderFactory.createRaisedBevelBorder());
    newButton.setToolTipText(newTipConcept);
    newButton.setText("  New  ");
    newButton.addActionListener(new SlotEditorPanel_newButton_actionAdapter(this));
    deleteButton.setEnabled(false);
    deleteButton.setBorder(BorderFactory.createRaisedBevelBorder());
    deleteButton.setToolTipText("Delete Selected Slot");
    deleteButton.setText("Delete");
    deleteButton.addActionListener(new SlotEditorPanel_deleteButton_actionAdapter(this));
    jPanel3.setBorder(titledBorder2);
    valuesList.addMouseListener(new SlotEditorPanel_valuesList_mouseAdapter(this));
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(treePanel1, JSplitPane.LEFT);
    jSplitPane1.add(jPanel1, JSplitPane.RIGHT);
    jPanel1.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(selSlotTypeButton,  BorderLayout.EAST);
    jPanel1.add(jPanel3,  BorderLayout.CENTER);
    jPanel2.add(slotTypeField, BorderLayout.NORTH);
    jPanel3.add(jPanel4, BorderLayout.NORTH);
    jPanel3.add(jScrollPane1,  BorderLayout.CENTER);
    jPanel4.add(newButton, null);
    jPanel4.add(deleteButton, null);
    jScrollPane1.getViewport().add(valuesList, null);
  }

  public Enumeration getValues() {
    return valuesModel.elements();
  }

  public String getSlotType() {
    return this.slotTypeField.getText();
  }

  public void clearValues() {
    valuesModel.clear();
    deleteButton.setEnabled(false);
  }

  public void addValue(String val) {
    valuesModel.addElement(val);
  }

  public void setSlotType(String id) {
    this.slotTypeField.setText(id);
    this.selSlotTypeButton.setEnabled(false);
  }

  public void enableSlotTypeButton() {
    this.selSlotTypeButton.setEnabled(true);
  }

  void valuesList_mouseClicked(MouseEvent e) {
    deleteButton.setEnabled(true);
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    //TODO
  }

  void selSlotTypeButton_actionPerformed(ActionEvent e) {
    String id = treePanel1.getSelectedNodeId();
    if (id != null)
      this.slotTypeField.setText(id);
  }

  void newButton_actionPerformed(ActionEvent e) {
    String id = treePanel1.getSelectedNodeId();
    if (id != null)
      addValue(id);
  }


}

class SlotEditorPanel_valuesList_mouseAdapter extends java.awt.event.MouseAdapter {
  SlotEditorPanel adaptee;

  SlotEditorPanel_valuesList_mouseAdapter(SlotEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.valuesList_mouseClicked(e);
  }
}

class SlotEditorPanel_deleteButton_actionAdapter implements java.awt.event.ActionListener {
  SlotEditorPanel adaptee;

  SlotEditorPanel_deleteButton_actionAdapter(SlotEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.deleteButton_actionPerformed(e);
  }
}

class SlotEditorPanel_selSlotTypeButton_actionAdapter implements java.awt.event.ActionListener {
  SlotEditorPanel adaptee;

  SlotEditorPanel_selSlotTypeButton_actionAdapter(SlotEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.selSlotTypeButton_actionPerformed(e);
  }
}

class SlotEditorPanel_newButton_actionAdapter implements java.awt.event.ActionListener {
  SlotEditorPanel adaptee;

  SlotEditorPanel_newButton_actionAdapter(SlotEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newButton_actionPerformed(e);
  }
}

