/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.engine;
import java.util.*;

import org.nex.tinytsc.api.IActorCarrier;
import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.api.Identifiable;

import java.io.*;
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 *
 * TODO add ifRun ifNotRun thenRun
 */

public class ProcessRule implements Serializable, IActorCarrier, Identifiable {
  private final int type = IConstants.PROCESS_RULE;
  private String id="";
  private String instanceOf = "";
  private List<String> subOf = null;
  private String name = "";
  private String comment = "";
  private List<Sentence> ifActors = null;
  private List<Sentence> ifNotActors = null;
  private List<Sentence> ifRelations = null;
  private List<Sentence> ifNotRelations = null;
  private List<Sentence> ifStates = null;
  private List<Sentence> ifNotStates = null;
  private List<Sentence> ifRuns = null; 		//TODO
  private List<Sentence> ifNotRuns = null;	//TODO
  private List<Sentence> thenRuns = null;  	//TODO
  private List<Sentence> thenActors = null;
  private List<Sentence> thenRelations = null;
  private List<Sentence> thenStates = null;
  private List<Sentence> thenCreates = null;
  private List<Sentence> thenConjectures = null; //takes <sentence>
  private List<String> thenSay = null; //takes String
  boolean isTerminate = false;

  public ProcessRule() {}

  public ProcessRule(String _id) {
    this.id = _id;
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
  public void addSubOf(String so) {
    if (subOf == null) subOf = new ArrayList<String>();
    subOf.add(so);
  }
  public List<String> getSubOf() {
    return subOf;
  }
  public void setName(String _name) {
    this.name = _name;
  }
  public String getName() {
    return this.name;
  }
  public void setComment(String _comment) {
    this.comment = _comment;
  }
  public String getComment() {
    return this.comment;
  }

  public void setIsTerminate(boolean f) {
    this.isTerminate = f;
  }

  public boolean getIsTerminate() {
    return this.isTerminate;
  }

  public void addIfActor(Sentence t) {
    if (ifActors==null) ifActors = new ArrayList<Sentence>();
    ifActors.add(t);
  }
  public void setIfActors(List<Sentence> l) {
  	ifActors = l;
  }
  public void removeIfActor(Sentence t) {
  	ifActors.remove(t);
  }
  public List<Sentence> getIfActors() {
    return ifActors;
  }
  public void addIfNotActor(Sentence t) {
    if (ifNotActors==null) ifNotActors = new ArrayList<Sentence>();
    ifNotActors.add(t);
  }
  public void setIfNotActors(List<Sentence> l) {
  	ifNotActors = l;
  }
  public void removeIfNotActor(Sentence t) {
  	ifNotActors.remove(t);
  }
  public List<Sentence> getIfNotActors() {
    return ifNotActors;
  }

  /**
   * Return all the actors in this <code>ProcessRule</code>
   * @return
   */
  public List<Sentence> getActors() {
    if (ifNotActors == null)
      return ifActors;
    List<Sentence> result = ifActors;
    result.addAll(ifNotActors);
    return result;
  }

  public void addThenActor(Sentence t) {
    if (thenActors==null) thenActors = new ArrayList<Sentence>();
    thenActors.add(t);
  }
  public void setThenActors(List<Sentence> l) {
  	thenActors = l;
  }
  public void removeThenActor(Sentence t) {
  	thenActors.remove(t);
  }
  public List<Sentence> getThenActors() {
    return thenActors;
  }

  public void addIfRelation(Sentence t) {
    if (ifRelations==null) ifRelations = new ArrayList<Sentence>();
    ifRelations.add(t);
  }
  public void setIfRelation(List<Sentence> l) {
  	ifRelations = l;
  }
  public void removeIfRelation(Sentence t) {
  	ifRelations.remove(t);
  }
  public List<Sentence> getIfRelations() {
    return ifRelations;
  }
  public void addIfNotRelation(Sentence t) {
    if (ifNotRelations==null) ifNotRelations = new ArrayList<Sentence>();
    ifNotRelations.add(t);
  }
  public void setIfNotRelation(List<Sentence> l) {
  	ifNotRelations = l;
  }
  public void removeIfNotRelation(Sentence t) {
  	ifNotRelations.remove(t);
  }
  public List<Sentence> getIfNotRelations() {
    return ifNotRelations;
  }
  public void addThenRelation(Sentence t) {
    if (thenRelations==null) thenRelations = new ArrayList<Sentence>();
    thenRelations.add(t);
  }
  public void setThenRelation(List<Sentence> l) {
  	thenRelations = l;
  }
  public void removeThenRelation(Sentence t) {
  	thenRelations.remove(t);
  }
  public List<Sentence> getThenRelations() {
    return thenRelations;
  }

  public void addIfState(Sentence t) {
    if (ifStates==null) ifStates = new ArrayList<Sentence>();
    ifStates.add(t);
  }
  public void setIfState(List<Sentence> l) {
  	ifStates = l;
  }
  public void removeIfState(Sentence t) {
  	ifStates.remove(t);
  }
  public List<Sentence> getIfStates() {
    return ifStates;
  }
  public void addIfNotState(Sentence t) {
    if (ifNotStates==null) ifNotStates = new ArrayList<Sentence>();
    ifNotStates.add(t);
  }
  public void setIfNotState(List<Sentence> l) {
  	ifNotStates = l;
  }
  public void removeIfNotState(Sentence t) {
  	ifNotStates.remove(t);
  }
  public List<Sentence> getIfNotStates() {
    return ifNotStates;
  }
  public void addThenState(Sentence t) {
    if (thenStates==null) thenStates = new ArrayList<Sentence>();
    thenStates.add(t);
  }
  public void setThenState(List<Sentence> l) {
  	thenStates = l;
  }
  public void removeThenState(Sentence t) {
  	thenStates.remove(t);
  }
  public List<Sentence> getThenStates() {
    return thenStates;
  }
  public void addThenConjecture(Sentence t) {
    if (thenConjectures==null) thenConjectures = new ArrayList<Sentence>();
    thenConjectures.add(t);
  }
  public void setThenConjecture(List<Sentence> l) {
  	thenConjectures = l;
  }
  public void removeThenConjecture(Sentence t) {
  	thenConjectures.remove(t);
  }
  public List<Sentence> getThenConjectures() {
    return thenConjectures;
  }

  /**
   * Sentence.object is the message
   * @param t
   */
  public void addThenSay(String t) {
    if (thenSay==null) thenSay = new ArrayList<String>();
    thenSay.add(t);
  }
  public void setThenSay(List<String> l) {
  	thenSay = l;
  }

  public void removeThenSay(String t) {
  	thenSay.remove(t);
  }
  /**
   * List of <code>String</code>s
   * @return
   */
  public List<String> getThenSays() {
    return thenSay;
  }
  /**
   * A <code>thenCreate</code>
   * takes only the <code>String</code> value
   * which will be the name of a new binding.
   * e.g. (legacy tsc notation)
   *    thenCreate	( *act.c.t-cell )
   * is followed by
   *    thenActors	( ( act.c.t-cell ( *act.c.t-cell ) true ) )
   * @param t
   */
  public void addThenCreates(Sentence t) {
    if (thenCreates==null) thenCreates = new ArrayList<Sentence>();
    thenCreates.add(t);
  }
  public void setThenCreates(List<Sentence> l) {
  	thenCreates = l;
  }
  public void removeThenCreate(Sentence t) {
  	thenCreates.remove(t);
  }
  /**
   * List of <code>Sentence</code>s
   * where Sentence.object is the value
   * @return
   */
  public List<Sentence> getThenCreates() {
    return thenCreates;
  }
  public String toXML() {
    StringBuffer buf = new StringBuffer("<rule id=\""+id+"\">\n");
    if (!name.equals(""))
      buf.append("  <name>"+name+"</name>\n");
    if (!comment.equals(""))
      buf.append("  <comment>"+comment+"</comment>\n");
    if (this.instanceOf != null && !this.instanceOf.equals(""))
    	buf.append("  <instanceOf>"+instanceOf+"</instanceOf>\n");
    int len;
    if (subOf != null) {
      len = subOf.size();
      buf.append("  <slot name=\"subOf\"><value>\n  ");
      for (int i=0;i<len;i++)
        buf.append(subOf.get(i)+" ");
      buf.append("\n  </value></slot>\n");
    }
    if (ifActors != null) {
      len = ifActors.size();
      buf.append("  <slot name=\""+IConstants._IF_ACTORS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifActors.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (ifNotActors != null) {
      len = ifNotActors.size();
      buf.append("  <slot name=\""+IConstants._IF_NOT_ACTORS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifNotActors.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (ifRelations != null) {
      len = ifRelations.size();
      buf.append("  <slot name=\""+IConstants._IF_RELATIONS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifRelations.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (ifNotRelations != null) {
      len = ifNotRelations.size();
      buf.append("  <slot name=\""+IConstants._IF_NOT_RELATIONS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifNotRelations.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (ifStates != null) {
      len = ifStates.size();
      buf.append("  <slot name=\""+IConstants._IF_STATES+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifStates.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (ifNotStates != null) {
      len = ifNotStates.size();
      buf.append("  <slot name=\""+IConstants._IF_NOT_STATES+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(ifNotStates.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (thenCreates != null) {
      len = thenCreates.size();
      buf.append("  <slot name=\""+IConstants._THEN_CREATE+"\">\n");
      for (int i=0;i<len;i++)
        buf.append("    "+thenCreates.get(i).object+" ");
      buf.append("\n  </slot>\n");
    }
    if (thenActors != null) {
      len = thenActors.size();
      buf.append("  <slot name=\""+IConstants._THEN_ACTORS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(thenActors.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (thenRelations != null) {
      len = thenRelations.size();
      buf.append("  <slot name=\""+IConstants._THEN_RELATIONS+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(thenRelations.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (thenStates != null) {
      len = thenStates.size();
      buf.append("  <slot name=\""+IConstants._THEN_STATES+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(thenStates.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (thenSay != null) {
      buf.append("  <slot name=\""+IConstants._THEN_SAY+"\"><value>\n");
      buf.append("    "+thenSay.get(0)+"\n");
      buf.append("  </value></slot>\n");
    }
    if (thenConjectures != null) {
      len = thenConjectures.size();
      buf.append("  <slot name=\""+IConstants._THEN_CONJECTURE+"\">\n");
      for (int i=0;i<len;i++)
        buf.append(thenConjectures.get(i).toXML());
      buf.append("  </slot>\n");
    }
    if (isTerminate)			//TODO pull parser doesn't know about this yet
      buf.append("  <isTerminate/>\n");
    buf.append("</rule>\n");
    return buf.toString();
  }

  @Override
  public String toString() {
	  return this.id;
  }
}
