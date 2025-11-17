/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 Licenses
 */
package org.nex.tinytsc.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

import org.nex.tinytsc.api.IConstants;
//import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.Sentence;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class SentenceEditorPanel extends JPanel {
  Sentence mySentence;
  boolean isDirty = false;
  int sentenceType = 0;
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  TreePanel treePanel;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel jPanel2 = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JPanel truthPanel = new JPanel();
  JPanel weightPanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JRadioButton falseRadioButton = new JRadioButton();
  JRadioButton trueRadioButton = new JRadioButton();
  FlowLayout flowLayout2 = new FlowLayout();
  JTextField probabilityField = new JTextField();
  Border border1;
  TitledBorder titledBorder1;
  Border border2;
  TitledBorder titledBorder2;
  JPanel jPanel5 = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  JPanel predPanel = new JPanel();
  JPanel objBPanel = new JPanel();
  JPanel objPanel = new JPanel();
  JPanel subjPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout6 = new BorderLayout();
  JButton predSelButton = new JButton();
  JButton subjSelectButton = new JButton();
  JButton objSelectButton = new JButton();
  JButton objBSelectButton = new JButton();
  Border border3;
  TitledBorder titledBorder3;
  Border border4;
  TitledBorder titledBorder4;
  Border border5;
  TitledBorder titledBorder5;
  Border border6;
  TitledBorder titledBorder6;
  JTextField predicateField = new JTextField();
  JTextField subjectField = new JTextField();
  JTextField objectField = new JTextField();
  JTextField objectBField = new JTextField();
  ButtonGroup truthGroup = new ButtonGroup();
  JPanel messagePanel = new JPanel();
  FlowLayout flowLayout3 = new FlowLayout();
  Border border7;
  TitledBorder titledBorder7;
  JTextField messageField = new JTextField();
  JPanel runPanel = new JPanel();
  FlowLayout flowLayout4 = new FlowLayout();
  Border border8;
  TitledBorder titledBorder8;
  JTextField runField = new JTextField();

  public SentenceEditorPanel(Environment env) {
    try {
    	treePanel = new TreePanel(env);
      jbInit();
      truthGroup.add(this.trueRadioButton);
      truthGroup.add(this.falseRadioButton);
      treePanel.setIsSlaveTree(true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * <p>If this panel is building <code>ProcessRule</code>
   * <code>Sentence</code>s, then the fields:
   * <code>subject</code>, <code>object</code>, and
   * <code>objectB</code> are hand-entered variables.
   * Otherwise, they are selected from the tree.</p>
   *
   */
  public void setIsRule() {
  	this.subjectField.setEditable(true);
  	this.objectField.setEditable(true);
  	this.objectBField.setEditable(true);
  	this.subjSelectButton.setEnabled(false);
  	this.objBSelectButton.setEnabled(false);
  	this.objSelectButton.setEnabled(false);
  }
  
  public void setListener(TreeListener e) {
    treePanel.setListener(e);
  }

  public void setRootConcept(Concept c) {
	  try {
    treePanel.setRootConcept(c);
	  } catch (Exception e) {
		  throw new RuntimeException(e);
	  }

  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder1 = new TitledBorder(border1,"Weight");
    border2 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder2 = new TitledBorder(border2,"Truth");
    border3 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder3 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Predicate");
    border4 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder4 = new TitledBorder(border4,"Subject");
    border5 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder5 = new TitledBorder(border5,"Object");
    border6 = BorderFactory.createEmptyBorder();
    titledBorder6 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Object B");
    border7 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder7 = new TitledBorder(border7,"Message");
    border8 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder8 = new TitledBorder(border8,"Run Method");
    this.setLayout(borderLayout1);
    jPanel1.setLayout(borderLayout2);
    jPanel2.setLayout(gridLayout1);
    gridLayout1.setColumns(2);
    truthPanel.setLayout(flowLayout1);
    falseRadioButton.setText("False");
    trueRadioButton.setSelected(true);
    trueRadioButton.setText("True");
    weightPanel.setLayout(flowLayout2);
    probabilityField.setPreferredSize(new Dimension(80, 21));
    probabilityField.setRequestFocusEnabled(true);
    probabilityField.setText("");
    weightPanel.setBorder(titledBorder1);
    truthPanel.setBorder(titledBorder2);
    jPanel5.setLayout(gridLayout2);
    gridLayout2.setColumns(1);
    gridLayout2.setRows(6);
    gridLayout2.setVgap(2);
    predPanel.setLayout(borderLayout3);
    subjPanel.setLayout(borderLayout4);
    objPanel.setLayout(borderLayout5);
    objBPanel.setLayout(borderLayout6);
    predSelButton.setBorder(BorderFactory.createRaisedBevelBorder());
    predSelButton.setToolTipText("Select Predicate from Tree");
    predSelButton.setText("Select");
    predSelButton.addActionListener(new SentenceEditorPanel_predSelButton_actionAdapter(this));
    subjSelectButton.setBorder(BorderFactory.createRaisedBevelBorder());
    subjSelectButton.setToolTipText("Select  Subject from Tree");
    subjSelectButton.setText("Select");
    subjSelectButton.addActionListener(new SentenceEditorPanel_subjSelectButton_actionAdapter(this));
    objSelectButton.setBorder(BorderFactory.createRaisedBevelBorder());
    objSelectButton.setToolTipText("Select Object from Tree");
    objSelectButton.setText("Select");
    objSelectButton.addActionListener(new SentenceEditorPanel_objSelectButton_actionAdapter(this));
    objBSelectButton.setEnabled(false);
    objBSelectButton.setBorder(BorderFactory.createRaisedBevelBorder());
    objBSelectButton.setToolTipText("Select Object B from Tree");
    objBSelectButton.setText("Select");
    objBSelectButton.addActionListener(new SentenceEditorPanel_objBSelectButton_actionAdapter(this));
    predPanel.setBorder(titledBorder3);
    subjPanel.setBorder(titledBorder4);
    objPanel.setBorder(titledBorder5);
    objBPanel.setBorder(titledBorder6);
    predicateField.setEditable(false);
    predicateField.setText("");
    subjectField.setEditable(false);
    subjectField.setText("");
    objectField.setEditable(false);
    objectField.setText("");
    objectBField.setEditable(false);
    objectBField.setText("");
    messagePanel.setLayout(flowLayout3);
    messagePanel.setBorder(titledBorder7);
    messageField.setPreferredSize(new Dimension(300, 21));
    messageField.setText("");
    runPanel.setLayout(flowLayout4);
    runPanel.setBorder(titledBorder8);
    runField.setPreferredSize(new Dimension(300, 21));
    runField.setText("");
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(treePanel, JSplitPane.LEFT);
    jSplitPane1.add(jPanel1, JSplitPane.RIGHT);
    jPanel1.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(truthPanel, null);
    truthPanel.add(trueRadioButton, null);
    truthPanel.add(falseRadioButton, null);
    jPanel2.add(weightPanel, null);
    weightPanel.add(probabilityField, null);
    jPanel1.add(jPanel5,  BorderLayout.CENTER);
    jPanel5.add(predPanel, null);
    predPanel.add(predSelButton,  BorderLayout.EAST);
    jPanel5.add(subjPanel, null);
    jPanel5.add(objPanel, null);
    jPanel5.add(objBPanel, null);
    subjPanel.add(subjSelectButton,  BorderLayout.EAST);
    objPanel.add(objSelectButton,  BorderLayout.EAST);
    objBPanel.add(objBSelectButton,  BorderLayout.EAST);
    predPanel.add(predicateField, BorderLayout.NORTH);
    subjPanel.add(subjectField, BorderLayout.NORTH);
    objPanel.add(objectField, BorderLayout.NORTH);
    objBPanel.add(objectBField, BorderLayout.CENTER);
    jPanel5.add(messagePanel, null);
    messagePanel.add(messageField, null);
    jPanel5.add(runPanel, null);
    runPanel.add(runField, null);
  }

  /**
   * Callback from <code>SentencePanel</code>
   * @param type
   */
  public void newSentence(int type) {
  	System.out.println("New Sentence "+type);
    mySentence = new Sentence();
    mySentence.type = type;

  }

  public void setSentence(Sentence sentence) {
    mySentence = sentence;
    isDirty = false;
  }

  void checkSentenceType(int type) {
  	sentenceType = type;
  	if (type == IConstants.THEN_RUN ||
  		type == IConstants.IF_RUN ||
		type == IConstants.IF_NOT_RUN)
  		runType();
  	else if (type == IConstants.THEN_SAY)
  		sayType();
  	else
  		qpType();
  }
  
  void runType() {
  	truthPanel.setEnabled(false);
  	weightPanel.setEnabled(false);
  	predPanel.setEnabled(false);
  	subjPanel.setEnabled(false);
  	objPanel.setEnabled(false);
  	objBPanel.setEnabled(false);
  	runPanel.setEnabled(true);
  	messagePanel.setEnabled(false);
  }
  void sayType() {
  	truthPanel.setEnabled(false);
  	weightPanel.setEnabled(false);
  	predPanel.setEnabled(false);
  	subjPanel.setEnabled(false);
  	objPanel.setEnabled(false);
  	objBPanel.setEnabled(false);
  	runPanel.setEnabled(false);
  	messagePanel.setEnabled(true);
  }
  void qpType() {
  	truthPanel.setEnabled(true);
  	weightPanel.setEnabled(true);
  	predPanel.setEnabled(true);
  	subjPanel.setEnabled(true);
  	objPanel.setEnabled(true);
  	objBPanel.setEnabled(true);
  	runPanel.setEnabled(false);
  	messagePanel.setEnabled(false);
  }
  public boolean getIsDirty() {
    return isDirty;
  }

  public Sentence getSentence() {
  	populateSentence();
    return mySentence;
  }
  
  void populateSentence() {
  	if (sentenceType == IConstants.THEN_SAY)
  		mySentence.object = this.messageField.getText();
  	else if (sentenceType == IConstants.THEN_RUN ||
  			sentenceType == IConstants.IF_RUN ||
  			sentenceType == IConstants.IF_NOT_RUN)
  		mySentence.object = this.runField.getText();
  	else {
  		mySentence.truth = getTruth();
  		if (!this.probabilityField.getText().equals(""))
  			mySentence.probability = Double.parseDouble(probabilityField.getText());
  		mySentence.predicate = getPredicate();
  		mySentence.subject = getSubject();
  		mySentence.object = getObject();
  		mySentence.objectB = getObjectB();
  	}
  	System.out.println("Populate Sentence "+mySentence);
  }
  public void setPredicate(String pred) {
    this.predicateField.setText(pred);
  }
  public String getPredicate() {
    return predicateField.getText();
  }
  public void setSubject(String subj) {
    this.subjectField.setText(subj);
  }
  public String getSubject() {
    return subjectField.getText();
  }
  public void setObject(String obj) {
    this.objectField.setText(obj);
  }
  public String getObject() {
    return objectField.getText();
  }
  public void setObjectB(String obj) {
    this.objectBField.setText(obj);
  }
  public String getObjectB() {
    return objectBField.getText();
  }
  public void setTruth(boolean t) {
    if (t)
      this.trueRadioButton.setSelected(true);
    else
      this.falseRadioButton.setSelected(true);
  }
  public boolean getTruth() {
    return (trueRadioButton.isSelected());
  }
  public void setWeight(String w) {
    this.probabilityField.setText(w);
  }
  public String getWeight() {
    return probabilityField.getText();
  }

  void predSelButton_actionPerformed(ActionEvent e) {
    setPredicate(treePanel.getSelectedNodeId());
    isDirty = true;
  }

  void subjSelectButton_actionPerformed(ActionEvent e) {
    setSubject(treePanel.getSelectedNodeId());
    isDirty = true;
  }

  void objSelectButton_actionPerformed(ActionEvent e) {
    setObject(treePanel.getSelectedNodeId());
    isDirty = true;
  }

  void objBSelectButton_actionPerformed(ActionEvent e) {
    setObjectB(treePanel.getSelectedNodeId());
    isDirty = true;
  }

}

class SentenceEditorPanel_predSelButton_actionAdapter implements java.awt.event.ActionListener {
  SentenceEditorPanel adaptee;

  SentenceEditorPanel_predSelButton_actionAdapter(SentenceEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.predSelButton_actionPerformed(e);
  }
}

class SentenceEditorPanel_subjSelectButton_actionAdapter implements java.awt.event.ActionListener {
  SentenceEditorPanel adaptee;

  SentenceEditorPanel_subjSelectButton_actionAdapter(SentenceEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.subjSelectButton_actionPerformed(e);
  }
}

class SentenceEditorPanel_objSelectButton_actionAdapter implements java.awt.event.ActionListener {
  SentenceEditorPanel adaptee;

  SentenceEditorPanel_objSelectButton_actionAdapter(SentenceEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.objSelectButton_actionPerformed(e);
  }
}

class SentenceEditorPanel_objBSelectButton_actionAdapter implements java.awt.event.ActionListener {
  SentenceEditorPanel adaptee;

  SentenceEditorPanel_objBSelectButton_actionAdapter(SentenceEditorPanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.objBSelectButton_actionPerformed(e);
  }
}