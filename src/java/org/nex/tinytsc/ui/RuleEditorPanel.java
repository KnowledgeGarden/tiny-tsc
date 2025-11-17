/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;

import java.awt.*;
//import java.util.*;

import javax.swing.*;
import javax.swing.border.*;

import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.api.ISentenceListener;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.ProcessRule;
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

public class RuleEditorPanel extends JPanel implements ISentenceListener {
	RuleEditorDialog host;
  Environment environment;
  SentenceEditorDialog sentenceEditor;
  ProcessRule myRule = null;
  boolean isDirty = false;
  GridLayout gridLayout1 = new GridLayout();
  JPanel ifPanel = new JPanel();
  JPanel thenPanel = new JPanel();
  GridLayout gridLayout2 = new GridLayout();
  Border border1;
  TitledBorder titledBorder1;
  Border border2;
  TitledBorder titledBorder2;
  GridLayout gridLayout3 = new GridLayout();
  Border border3;
  TitledBorder titledBorder3;
  SentencePanel thenConjectureSentence = new SentencePanel();
  SentencePanel thenRunSentence = new SentencePanel();
  SentencePanel thenStateSentence = new SentencePanel();
  SentencePanel thenRelationSentence = new SentencePanel();
  SentencePanel thenActorSentence = new SentencePanel();
  SentencePanel thenCreateSentence = new SentencePanel();
  SentencePanel ifNotRunSentence = new SentencePanel();
  SentencePanel ifRunSentence = new SentencePanel();
  SentencePanel ifNotStateSentence = new SentencePanel();
  SentencePanel ifStateSentence = new SentencePanel();
  SentencePanel ifNotRelateSentence = new SentencePanel();
  SentencePanel ifRelateSentence = new SentencePanel();
  SentencePanel ifNotActorSentence = new SentencePanel();
  SentencePanel ifActorSentence = new SentencePanel();
  JPanel terminatePanel = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JCheckBox terminateCheckBox = new JCheckBox();
  Border border4;
  TitledBorder titledBorder4;
  Border border5;
  TitledBorder titledBorder5;
  Border border6;
  TitledBorder titledBorder6;
  Border border7;
  TitledBorder titledBorder7;
  Border border8;
  TitledBorder titledBorder8;
  Border border9;
  TitledBorder titledBorder9;
  Border border10;
  TitledBorder titledBorder10;
  Border border11;
  TitledBorder titledBorder11;
  Border border12;
  TitledBorder titledBorder12;
  Border border13;
  TitledBorder titledBorder13;
  Border border14;
  TitledBorder titledBorder14;
  Border border15;
  TitledBorder titledBorder15;
  Border border16;
  TitledBorder titledBorder16;
  Border border17;
  TitledBorder titledBorder17;
  Border border18;
  TitledBorder titledBorder18;
  Border border19;
  TitledBorder titledBorder19;
  /**
   * TODO
   * This should be a textField for a String, not a Sentence
   */
  SentencePanel thenSaySentence = new SentencePanel();
  Border border20;
  TitledBorder titledBorder20;

  public RuleEditorPanel(Environment env) {
	  environment = env;
    try {
    	sentenceEditor = new SentenceEditorDialog(environment);
      jbInit();
      this.ifRunSentence.setEnabled(false);
      this.ifNotRunSentence.setEnabled(false);
      this.thenRunSentence.setEnabled(false);
      this.thenConjectureSentence.setEnabled(false);
      sentenceEditor.setHost(this, true);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setHost(RuleEditorDialog h) {
  	host = h;
  }
  public void setListener(TreeListener e) {
    sentenceEditor.setListener(e);
  }

  public void setRootConcept(Concept c) {
    sentenceEditor.setRootConcept(c);
  }

  private void jbInit() throws Exception {
    border1 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder1 = new TitledBorder(border1,"Antecedents");
    border2 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder2 = new TitledBorder(border2,"Consequents");
    border3 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder3 = new TitledBorder(border3,"Create");
    border4 = BorderFactory.createLineBorder(Color.white,1);
    titledBorder4 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Terminate");
    border5 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder5 = new TitledBorder(border5,"Create Actor");
    border6 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder6 = new TitledBorder(border6,"New Actors");
    border7 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder7 = new TitledBorder(border7,"New Relations");
    border8 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder8 = new TitledBorder(border8,"New States");
    border9 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder9 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Execute Methods");
    border10 = BorderFactory.createEmptyBorder();
    titledBorder10 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"New Conjectures");
    border11 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder11 = new TitledBorder(border11,"Visible Message");
    border12 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder12 = new TitledBorder(border12,"Actors");
    border13 = BorderFactory.createEmptyBorder();
    titledBorder13 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Not Actors");
    border14 = BorderFactory.createEmptyBorder();
    titledBorder14 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Relations");
    border15 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder15 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Not Relations");
    border16 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder16 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"States");
    border17 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder17 = new TitledBorder(border17,"Not States");
    border18 = BorderFactory.createEmptyBorder();
    titledBorder18 = new TitledBorder(BorderFactory.createLineBorder(SystemColor.controlText,1),"Execute Method");
    border19 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder19 = new TitledBorder(border19,"Not Execute Method");
    border20 = BorderFactory.createLineBorder(SystemColor.controlText,1);
    titledBorder20 = new TitledBorder(border20,"New Messages");
    gridLayout1.setColumns(2);
    gridLayout1.setHgap(2);
    gridLayout1.setRows(1);
    this.setLayout(gridLayout1);
    ifPanel.setLayout(gridLayout2);
    gridLayout2.setColumns(1);
    gridLayout2.setRows(8);
    gridLayout2.setVgap(2);
    ifPanel.setBorder(titledBorder1);
    thenPanel.setBorder(titledBorder2);
    thenPanel.setLayout(gridLayout3);
    gridLayout3.setColumns(1);
    gridLayout3.setHgap(0);
    gridLayout3.setRows(8);
    gridLayout3.setVgap(2);
    terminatePanel.setLayout(flowLayout1);
    terminateCheckBox.setBorder(null);
    terminateCheckBox.setText("Terminate Expansion");
    terminatePanel.setBorder(titledBorder4);
    thenCreateSentence.setBorder(titledBorder5);
    thenCreateSentence.setSentenceType(IConstants.THEN_CREATE);
    thenCreateSentence.setHost(this);
    thenActorSentence.setBorder(titledBorder6);
    thenActorSentence.setSentenceType(IConstants.THEN_ACTORS);
    thenActorSentence.setHost(this);
    thenRelationSentence.setBorder(titledBorder7);
    thenRelationSentence.setSentenceType(IConstants.THEN_RELATIONS);
    thenRelationSentence.setHost(this);
    thenStateSentence.setBorder(titledBorder8);
    thenStateSentence.setSentenceType(IConstants.THEN_STATES);
    thenStateSentence.setHost(this);
    thenRunSentence.setBorder(titledBorder9);
    thenRunSentence.setSentenceType(IConstants.THEN_RUN);
    thenConjectureSentence.setBorder(titledBorder10);
    thenConjectureSentence.setSentenceType(IConstants.THEN_CONJECTURE);
    thenConjectureSentence.setHost(this);
    thenSaySentence.setBorder(titledBorder20);
    thenSaySentence.setSentenceType(IConstants.THEN_SAY);
    thenSaySentence.setHost(this);
    ifActorSentence.setBorder(titledBorder12);
    ifActorSentence.setSentenceType(IConstants.IF_ACTORS);
    ifActorSentence.setHost(this);
    ifNotActorSentence.setBorder(titledBorder13);
    ifNotActorSentence.setSentenceType(IConstants.IF_NOT_ACTORS);
    ifNotActorSentence.setHost(this);
    ifRelateSentence.setBorder(titledBorder14);
    ifRelateSentence.setSentenceType(IConstants.IF_NOT_RELATIONS);
    ifRelateSentence.setHost(this);
    ifNotRelateSentence.setBorder(titledBorder15);
    ifNotRelateSentence.setSentenceType(IConstants.IF_NOT_RELATIONS);
    ifNotRelateSentence.setHost(this);
    ifStateSentence.setBorder(titledBorder16);
    ifStateSentence.setSentenceType(IConstants.IF_STATES);
    ifStateSentence.setHost(this);
    ifNotStateSentence.setBorder(titledBorder17);
    ifNotStateSentence.setSentenceType(IConstants.IF_NOT_STATES);
    ifNotStateSentence.setHost(this);
    ifRunSentence.setBorder(titledBorder18);
    ifNotRunSentence.setBorder(titledBorder19);
    this.add(ifPanel, null);
    thenPanel.add(terminatePanel, null);
    terminatePanel.add(terminateCheckBox, null);
    thenPanel.add(thenCreateSentence, null);
    thenPanel.add(thenActorSentence, null);
    thenPanel.add(thenRelationSentence, null);
    thenPanel.add(thenStateSentence, null);
    thenPanel.add(thenRunSentence, null);
    thenPanel.add(thenConjectureSentence, null);
    thenPanel.add(thenSaySentence, null);
    ifPanel.add(ifActorSentence, null);
    ifPanel.add(ifNotActorSentence, null);
    ifPanel.add(ifRelateSentence, null);
    ifPanel.add(ifNotRelateSentence, null);
    ifPanel.add(ifStateSentence, null);
    ifPanel.add(ifNotStateSentence, null);
    ifPanel.add(ifRunSentence, null);
    ifPanel.add(ifNotRunSentence, null);
    this.add(thenPanel, null);

  }

  public void setRuleId(String id) {
    myRule = environment.getRule(id);
    if (myRule == null)
    	throw new RuntimeException("Cannot find ProcessRule: "+id);
    host.setRuleId(id);
    isDirty = false;
    //fillin display
    refreshDisplay();
  }

  void refreshDisplay() {
  	java.util.List l = myRule.getIfActors();
  	int len = 0;
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifActorSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getIfNotActors();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifNotActorSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getIfRelations();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifRelateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getIfNotRelations();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifNotRelateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getIfStates();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifStateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getIfNotStates();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			ifNotStateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenCreates();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenCreateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenConjectures();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenConjectureSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenActors();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenActorSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenRelations();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenRelationSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenStates();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenStateSentence.addSentence((Sentence)l.get(i));
  	}
  	l = myRule.getThenSays();
  	if (l != null) {
  		len = l.size();
  		for (int i=0;i<len;i++)
  			thenSaySentence.addSentence((Sentence)l.get(i));
  	}

  }
  public void newInstance(String parentId) {
  	System.out.println("ProcessRule Editor Panel New ProcessRule "+parentId);
  	clearDisplay();
  	myRule = new ProcessRule();
  	isDirty = true;
  	myRule.setInstanceOf(parentId);
  }

  public void removeSentence(Sentence sentence) {
  	if (sentence.type == IConstants.IF_ACTORS)
  		myRule.removeIfActor(sentence);
  	else if (sentence.type == IConstants.IF_NOT_ACTORS)
  		myRule.removeIfNotActor(sentence);
  	else if (sentence.type == IConstants.IF_RELATIONS)
  		myRule.removeIfRelation(sentence);
  	else if (sentence.type == IConstants.IF_NOT_RELATIONS)
  		myRule.removeIfNotRelation(sentence);
  	else if (sentence.type == IConstants.IF_STATES)
  		myRule.removeIfState(sentence);
  	else if (sentence.type == IConstants.IF_NOT_STATES)
  		myRule.removeIfNotState(sentence);
  	else if (sentence.type == IConstants.THEN_ACTORS)
  		myRule.removeThenActor(sentence);
  	else if (sentence.type == IConstants.THEN_RELATIONS)
  		myRule.removeThenRelation(sentence);
  	else if (sentence.type == IConstants.THEN_STATES)
  		myRule.removeThenState(sentence);
  	else if (sentence.type == IConstants.THEN_CONJECTURE)
  		myRule.removeThenConjecture(sentence);
    else if (sentence.type == IConstants.THEN_SAY)
        myRule.removeThenSay(sentence.object);
  	else {
  		System.out.println("Can't remove ProcessRule sentence "+sentence.toString());
  		return;
  	}
  	isDirty = true;
  }

  public void displaySentence(Sentence sentence) {
  	sentenceEditor.showSelf(sentence);
  }

  public void clearDisplay() {
    this.ifActorSentence.clearDisplay();
    this.ifNotActorSentence.clearDisplay();
    this.ifRelateSentence.clearDisplay();
    this.ifNotRelateSentence.clearDisplay();
    this.ifStateSentence.clearDisplay();
    this.ifNotStateSentence.clearDisplay();
    this.ifRunSentence.clearDisplay();
    this.ifNotRunSentence.clearDisplay();
    this.thenActorSentence.clearDisplay();
    this.thenConjectureSentence.clearDisplay();
    this.thenCreateSentence.clearDisplay();
    this.thenRelationSentence.clearDisplay();
    this.thenStateSentence.clearDisplay();
    this.thenSaySentence.clearDisplay();
  }

  public void newSentence(int type) {
  	System.out.println("ProcessRule New Sentence "+type);
    sentenceEditor.newSentence(type);
  }


  public void acceptSentence(boolean isDirty, Sentence sentence) {
  	System.out.println("ProcessRule Accept Sentence "+isDirty+" "+sentence.type+" "+sentence);
    if (isDirty) {
    	switch(sentence.type) {
    		case IConstants.IF_ACTORS:
    			ifActorSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_NOT_ACTORS:
    			ifNotActorSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_RELATIONS:
    			ifRelateSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_NOT_RELATIONS:
    			ifNotRelateSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_STATES:
    			ifStateSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_NOT_STATES:
    			ifNotStateSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_RUN:
    			ifRunSentence.addSentence(sentence);
    			break;
    		case IConstants.IF_NOT_RUN:
    			ifNotRunSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_CREATE:
    			thenCreateSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_ACTORS:
    			thenActorSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_RELATIONS:
    			thenRelationSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_STATES:
    			thenStateSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_SAY:
    			thenSaySentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_CONJECTURE:
    			thenConjectureSentence.addSentence(sentence);
    			break;
    		case IConstants.THEN_RUN:
    			thenRunSentence.addSentence(sentence);
    			break;
    	}
    }
  }

  void populateRule() {
  	myRule.setIfActors(ifActorSentence.getSentences());
  	myRule.setIfNotActors(ifNotActorSentence.getSentences());
  	myRule.setIfRelation(ifRelateSentence.getSentences());
  	myRule.setIfNotRelation(ifNotRelateSentence.getSentences());
  	myRule.setIfState(ifStateSentence.getSentences());
  	myRule.setIfNotState(ifNotStateSentence.getSentences());
  	myRule.setThenCreates(thenCreateSentence.getSentences());
  	myRule.setThenActors(thenActorSentence.getSentences());
  	myRule.setThenRelation(thenRelationSentence.getSentences());
  	myRule.setThenState(thenStateSentence.getSentences());
  	myRule.setThenConjecture(thenConjectureSentence.getSentences());
  	myRule.setThenSay(thenSaySentence.getSentences());
  	//TODO add ifRun, ifNotRun, thenRun
  	myRule.setIsTerminate(terminateCheckBox.isSelected());
  }
  public ProcessRule getRule() {
  	populateRule();
  	System.out.println("ProcessRule Editor Panel getRule "+myRule);
  	return myRule;
  }

  public boolean getIsDirty() {
  	return isDirty;
  }

}
