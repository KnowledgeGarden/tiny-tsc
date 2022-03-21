/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.engine;
import java.io.*;

import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.api.Identifiable;
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class Task implements Serializable, Identifiable {
  private final int type = IConstants.TASK;
  public static final String UNKNOWN = "UNK";
  public static final String FILLIN_NEXT_EPISODE = "fillinNextEp";
  public static final String PUBLISH_EPISODE = "publishEp";
  public static final String FIND_EPISODE = "findEp";
  private String id="";
  private String instanceOf="";
  /**
   * taskTypes will be, e.g.
   * <li>FILLIN_NEXT_EPISODE</li>
   * <li>PUBLISH_EPISODE</LI>
   */
  private String taskType = Task.UNKNOWN;
  private int priority = 100;
  /**
   * <code>object</code> could be one of:
   * <li>Task</li>
   * <li>Episode</li>
   * <li>Model</li>
   * <li>Concept</li>
   * It is up to the <code>IAgent</code>
   * to know what to do with it.
   */
  private Identifiable object = null;
  private Model model = null;
  //TODO
  public Task() {
  }
  public Task(String _id) {
    id = _id;
  }
  public Task(String type, int p) {
    this.taskType = type;
    this.priority = p;
  }

  public int getNodeType() {
    return type;
  }

  public void setId(String _id) {
    this.id = _id;
  }
  public String getId() {
    return this.id;
  }
  public void setInstanceOf(String io) {
    instanceOf = io;
  }
  public String getInstanceOf() {
    return instanceOf;
  }
  public String getTaskType() {
    return taskType;
  }
  public void setTaskType(String type) {
    taskType = type;
  }
  public int getPriority() {
    return priority;
  }
  public void setPriority(int p) {
    priority = p;
  }

  public void setModel(Model m) {
    model = m;
  }

  public Model getModel() {
    return model;
  }

  public void setObject(Identifiable c) {
  	System.out.println("Task setting object "+c);
    object = c;
  }

  public Identifiable getObject() {
    return object;
  }

  public String toXML() {
    StringBuffer buf = new StringBuffer("<task id=\"" + id + "\">\n");
    if (!instanceOf.equals(""))
      buf.append("  <slot name=\"instanceOf\"><value>" + instanceOf + "</value></slot>\n");
    buf.append("  <type>"+taskType+"</type>\n");
    if (object != null)
      buf.append("  <object>"+object.getId()+"</object>\n");
    if (model != null)
    	buf.append("  <experiment>"+model.getId()+"</experiment>\n");
    buf.append("</task>\n");
    return buf.toString();
  }
}
