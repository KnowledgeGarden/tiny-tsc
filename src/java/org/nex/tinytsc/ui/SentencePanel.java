/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import org.nex.tinytsc.api.ISentenceListener;
import org.nex.tinytsc.engine.Sentence;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class SentencePanel extends JPanel {
	/**
	 * <code>SentenceEditorPanel</code>
	 * <code>RuleEditorPanel</code>
	 */
	ISentenceListener host;
	int sentenceType = 0;
	boolean isEditable = true;
	java.util.List sentences = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton newButton = new JButton();
  JButton deleteButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList sentenceList = new JList();
  DefaultListModel model = new DefaultListModel();
  
  public SentencePanel() {
    try {
      jbInit();
      sentenceList.setModel(model);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  
  public void setHost(ISentenceListener h) {
  	host = h;
  }
  
  public void setSentenceType(int type) {
  	this.sentenceType = type;
  }
  private void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(20);
    newButton.setBorder(BorderFactory.createRaisedBevelBorder());
    newButton.setText("  New  ");
    newButton.addActionListener(new SentencePanel_newButton_actionAdapter(this));
    deleteButton.setEnabled(false);
    deleteButton.setBorder(BorderFactory.createRaisedBevelBorder());
    deleteButton.setText(" Delete ");
    deleteButton.addActionListener(new SentencePanel_deleteButton_actionAdapter(this));
    sentenceList.addMouseListener(new SentencePanel_sentenceList_mouseAdapter(this));
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(newButton, null);
    jPanel1.add(deleteButton, null);
    this.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(sentenceList, null);
  }

  public void addSentence(Sentence s) {
  	System.out.println("Adding Sentence "+s);
  	if (sentences == null) sentences = new ArrayList();
  	sentences.add(s);
  	model.addElement(s.toString());
  }
  
  /**
   * 
   * @return can return <code>null</code>
   */
  public java.util.List getSentences() {
  	return sentences;
  }
  
  public void setIsEditable(boolean tf) {
  	newButton.setEnabled(tf);
  	isEditable = tf;
  }
  
  public void clearDisplay() {
  	model.clear();
  	deleteButton.setEnabled(false);
  }
  
  void deleteButton_actionPerformed(ActionEvent e) {
  	int where = sentenceList.getSelectedIndex();
  	Sentence s = (Sentence)sentences.remove(where);
  	model.remove(where);
  	host.removeSentence(s);
  	
  }

  void newButton_actionPerformed(ActionEvent e) {
//  	System.out.println("New ProcessRule Button "+host);
  	host.newSentence(sentenceType);
  }

  void sentenceList_mouseClicked(MouseEvent e) {
  	if (isEditable)
  		deleteButton.setEnabled(true);
  	if (e.getClickCount() > 1) {
  		int where = sentenceList.getSelectedIndex();
  		Sentence s = (Sentence)sentences.get(where);
  		host.displaySentence(s);
  	}
  }



}

class SentencePanel_deleteButton_actionAdapter implements java.awt.event.ActionListener {
  SentencePanel adaptee;

  SentencePanel_deleteButton_actionAdapter(SentencePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.deleteButton_actionPerformed(e);
  }
}

class SentencePanel_newButton_actionAdapter implements java.awt.event.ActionListener {
  SentencePanel adaptee;

  SentencePanel_newButton_actionAdapter(SentencePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newButton_actionPerformed(e);
  }
}

class SentencePanel_sentenceList_mouseAdapter extends java.awt.event.MouseAdapter {
  SentencePanel adaptee;

  SentencePanel_sentenceList_mouseAdapter(SentencePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.sentenceList_mouseClicked(e);
  }
}