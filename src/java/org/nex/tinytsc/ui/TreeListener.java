/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;

//import org.nex.tinytsc.engine.Model;
//import org.nex.tinytsc.engine.Episode;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.engine.Concept;
//import org.nex.tinytsc.engine.ProcessRule;
//import org.nex.tinytsc.engine.Task;
//import org.nex.tinytsc.engine.Sentence;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class TreeListener {
  
  private Environment environment;
  private ConceptEditorDialog conceptEditor;
  private RuleEditorDialog ruleEditor;
  private EpisodeEditorDialog episodeEditor;
  private TaskEditorDialog taskEditor;
  private Concept root;

  public TreeListener() {
    root = new Concept("root");
  }

  public void setEnvironment(Environment e) {
    environment = e;
    conceptEditor = new ConceptEditorDialog(environment);
    conceptEditor.setListener(this);
    conceptEditor.setRootConcept(root);
    ruleEditor = new RuleEditorDialog(environment);
    ruleEditor.setListener(this);
    ruleEditor.setRootConcept(root);
    episodeEditor = new EpisodeEditorDialog(environment);
    episodeEditor.setListener(this);
    episodeEditor.setRootConcept(root);
    taskEditor = new TaskEditorDialog(environment);
    taskEditor.setListener(this);
    taskEditor.setRootConcept(root);
  }

  public Environment getEnvironment() {
    return environment;
  }

  public Concept getRootConcept() {
    return root;
  }

  public void setRootConcept(Concept c) {
    conceptEditor.setRootConcept(c);
    ruleEditor.setRootConcept(c);
    episodeEditor.setRootConcept(c);
    taskEditor.setRootConcept(c);
  }
  /**
   * Only used for <code>Concept</code>s
   * @param parentConceptId
   */
  public void newConceptInstanceOf(String parentConceptId) {
  	System.out.println("New Concept Instance on: "+parentConceptId);
    if (parentConceptId == null) return;
    int which = environment.whichType(parentConceptId);
    switch (which) {
    	case IConstants.CONCEPT: 
    		conceptEditor.showNewInstanceOf(parentConceptId);
    		break;
    }
  }

  /**
   * Only used for <code>Concept</code>s
   * Ignores others
   * @param parentConceptId
   */
  public void newConceptSubOf(String parentConceptId) {
    if (parentConceptId == null) return;
    int which = environment.whichType(parentConceptId);
    switch (which) {
    	case IConstants.CONCEPT: 
    		conceptEditor.showNewSubOf(parentConceptId);
    		break;
    }
  }
  
  public void newRule(String parentConceptId) {
  	System.out.println("New ProcessRule "+parentConceptId);
    if (parentConceptId == null) return;
  	ruleEditor.showNewInstance(parentConceptId);
  }
  public void newModel(String parentConceptId) {
  	System.out.println("New Model "+parentConceptId);
    if (parentConceptId == null) return;
  	episodeEditor.showNewInstance(parentConceptId);
  }
  public void newTask(String parentConceptId) {
  	System.out.println("New Task "+parentConceptId);
    if (parentConceptId == null) return;
  	taskEditor.showNewInstance(parentConceptId);
  }

  /**
   * Selection could be one of:
   * <li><code>Concept</code></li>
   * <li><code>ProcessRule</code></li>
   * <li><code>Model</code></li>
   * <li><code>Episode</code></li>
   * <li><code>Task</code></li>
   * @param conceptId
   */
  public void displaySelection(String conceptId) {
    if (conceptId == null) return;
    int type = environment.whichType(conceptId);
    switch(type) {
    	case IConstants.CONCEPT: conceptEditor.showSelf(conceptId); break;
    	case IConstants.MODEL: episodeEditor.showSelf(conceptId); break;
    	case IConstants.EPISODE: episodeEditor.showSelf(conceptId); break;
    	case IConstants.PROCESS_RULE: ruleEditor.showSelf(conceptId); break;
    	case IConstants.TASK: taskEditor.showSelf(conceptId); break;
    }
  }
  
}