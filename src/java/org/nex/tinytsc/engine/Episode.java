/*
d *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.engine;
import java.io.*;
import java.util.*;

import org.nex.tinytsc.api.IActorCarrier;
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

public class Episode implements Serializable, IActorCarrier, Identifiable {
  private final int type = IConstants.EPISODE;
  private String id="";
  private String name = "";
  private String comment = "";
  private String instanceOf = "";
  private List<Sentence> actors = null;
  private List<Sentence> relations = null;
  private List<Sentence> states = null;
  private Model root = null;
  private boolean isModel = false;
  /**
   * Key = ruleId that fired to make nextEpisode
   * Value = episodeId
   */
  private Map<String, String> nextEpisodes = new HashMap<String, String>();
  /**
   * Key = ruleId that brought us here -- looping, etc, means many rules
   * Value = episodeId
   */
  private Map<String, String> previousEpisodes = new HashMap<String, String>();
  /** <code>ProcessRule</code> id that fired */
  private String mechanism= ""; //TODO to be removed - it was the ruleId

  public Episode() {}

  public Episode(Model m) {
  	this.root = m;
  }

  public void setModel(Model m) {
  	this.root = m;
  }
  public Model getModel() {
  	return root;
  }
  public int getNodeType() {
    return type;
  }

  public void setIsModel() {
    isModel = true;
  }
  public Episode(String _id) {
    this.id = _id;
  }
  public void setId(String _id) {
    this.id = _id;
  }
  public void setInstanceOf(String io) {
    instanceOf = io;
  }
  public String getInstanceOf() {
    return instanceOf;
  }
  public String getId() {
    return this.id;
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

  public Map<String, String> getPreviousEpisodes() {
    return this.previousEpisodes;
  }

  public Map<String, String> getNextEpisodes() {
    return this.nextEpisodes;
  }

  public String getPreviousEpisodeId(String ruleId) {
    return previousEpisodes.get(ruleId);
  }

  public void setMechanism(String ruleId) {
    this.mechanism = ruleId;
  }
  public String getMechanism() {
    return this.mechanism;
  }
  public void addActor(Sentence t) {
    if (actors==null) actors = new ArrayList<Sentence>();
    actors.add(t);
  }
  public void removeActor(Sentence t) {
  	actors.remove(t);
  }
  public List<Sentence> getActors() {
    return actors;
  }
  public void setActors(List<Sentence> actors) {
    this.actors = actors;
  }
  
  /**
   * <p>We ask if a {@code ProcessRule} has already fired on this episode,
   * in which case, we do not want it to fire again. Return {@code false}
   * if there is no record of it firing already.</p>
   * <p>We look only at {@code nextEpisodes} because those are created
   * by specific rule firings</p>
   * @param ruleId
   * @return
   */
  public boolean ruleHasFired(String ruleId) {
    return nextEpisodes.containsKey(ruleId);
  }

  public void addRelation(Sentence t) {
    if (t == null) return;
    if (relations==null) relations = new ArrayList<Sentence>();
    relations.add(t);
    System.out.println("ADDRLNS: "+t);
  }
  public void setRelations(List<Sentence> relns) {
    this.relations = relns;
  }
  public void removeRelation(Sentence t) {
  	relations.remove(t);
  }
  public void addRelations(List<Sentence> l) {
    if (relations == null)
      relations = l;
    else
      relations.addAll(l);
  }
  public List<Sentence> getRelations() {
    return relations;
  }

  public void addState(Sentence t) {
    if (states==null) states = new ArrayList<Sentence>();
    states.add(t);
//    System.out.println("ADDSTAS: "+t);
  }
  public void removeState(Sentence t) {
  	states.remove(t);
  }
  public void addStates(List l) {
    if (states == null)
      states = l;
    else
      states.addAll(l);
  }
  public void setStates(List stas) {
    this.states = stas;
  }

  public List<Sentence> getStates() {
    return states;
  }

  public void addNextEpisode(String ruleId, String episodeId) {
    this.nextEpisodes.put(ruleId,episodeId);
  }

  public Iterator<String> listNextEpisodeRuleIds() {
    return nextEpisodes.keySet().iterator();
  }

  /**
   * There is just <em>one</em> nextEpisode per individual <code>ProcessRule</code>
   * firing.
   * @return
   */
  public Iterator<String> listNextEpisodeIds() {
    return nextEpisodes.values().iterator();
  }

  public void addPreviousEpisode(String ruleId, String episodeId) {
    this.previousEpisodes.put(ruleId,episodeId);
  }

  public Iterator listPreviousEpisodeRuleIds() {
    return previousEpisodes.keySet().iterator();
  }

  public Iterator<String> listPreviousEpisodeIds() {
  	return previousEpisodes.values().iterator();
  }

  /**
   * <p>Compare other to this. A <code>superset</code> is
   * found if other is the same as this, but with more stuff</p>
   * <p>NOTE: this does not compare ID. It should. FIXED.</p>
   * @param other
   * @return -1 if no compare, 0 if absolute same, 1 if other might be superset
   */
  public int compareEpisode(Episode other) {
	  System.out.println("CompareEpisodes "+other);
    int result = -1; // default no comparison
    if (this.id.equals(other.getId()))
    	return 0; //THEY ARE ONE AND THE SAME EPISODE
    if ((actors != null) && (actors.size() <= other.getActors().size())) {
      if (other.getActors().containsAll(actors)) {
        if ((relations != null) && (other.getRelations() != null) &&
             other.getRelations().containsAll(relations)) {
            if ((states != null) && (other.getStates() != null) &&
                other.getStates().containsAll(states))
              result = 1;
              // same but possibly a superset where other contains all of me,
            
        }
      }
    }
    return result;
  }

  public boolean equals(Object o) {
    boolean tf = true;
    if (o instanceof Episode) {
      Episode e = (Episode)o;
      if (e.getId().equals(getId())) {
        if (!e.getNextEpisodes().equals(getNextEpisodes()))
            return false;
        if (!e.getPreviousEpisodes().equals(getPreviousEpisodes()))
            return false;
        if ((e.getActors() == null) && (getActors() == null)) {
            //do nothing -- very unlikely case
        } else if ((e.getActors() != null) && (getActors() != null))
            tf = getActors().equals(e.getActors());
        if (tf) {
            if ((e.getRelations() == null) && (getRelations() == null)) {
              //doNothing
            } else if ((e.getRelations() != null) && (getRelations() != null))
                 tf =  getRelations().equals(e.getRelations());
            if (tf) {
              if ((e.getStates() == null) && (getStates() == null))
                        return true;
              if ((e.getStates() != null) && (getStates() != null))
                      return getStates().equals(e.getStates());
          }
        }
      }
    }
    return false;
  }

  public String toXML() {
    StringBuffer buf = new StringBuffer();
    try {
      if (isModel)
        buf.append("<model id=\"" + id + "\">\n");
      else
        buf.append("<episode id=\"" + id + "\">\n");
      if (!name.equals(""))
        buf.append("  <name>" + name + "</name>\n");
      if (!comment.equals(""))
        buf.append("  <comment>" + comment + "</comment>\n");
      if (this.instanceOf != null && !this.instanceOf.equals(""))
      	buf.append("  <instanceOf>"+instanceOf+"</instanceOf>\n");
      if (!isModel && mechanism != null && !mechanism.equals(""))
        buf.append("  <myMechanism>" + mechanism + "</myMechanism>\n"); //TODO to be removed
      int len;
      if (actors != null) {
        len = actors.size();
        buf.append("  <slot name=\""+IConstants._ACTORS+"\">\n");
        for (int i = 0; i < len; i++)
          buf.append( (actors.get(i)).toXML());
        buf.append("  </slot>\n");
      }
      if (relations != null) {
        len = relations.size();
        buf.append("  <slot name=\""+IConstants._RELATIONS+"\">\n");
        for (int i = 0; i < len; i++)
          buf.append( (relations.get(i)).toXML());
        buf.append("  </slot>\n");
      }
      if (states != null) {
        len = states.size();
        buf.append("  <slot name=\""+IConstants._STATES+"\">\n");
        for (int i = 0; i < len; i++)
          buf.append( (states.get(i)).toXML());
        buf.append("  </slot>\n");
      }
      Iterator<String> itr = nextEpisodes.keySet().iterator();
      String n;
      while (itr.hasNext()) {
        n = itr.next();
        buf.append("  <nextEpisode>\n");
        buf.append("    <mechanism>"+n+"</mechanism>\n");
        buf.append("    <node>"+(String)nextEpisodes.get(n)+"</node>\n");
        buf.append("  </nextEpisode>\n");
      }
      itr = previousEpisodes.keySet().iterator();
      while (itr.hasNext()) {
        n = itr.next();
        buf.append("  <previousEpisode>\n");
        buf.append("    <mechanism>"+n+"</mechanism>\n");
        buf.append("    <node>"+(String)previousEpisodes.get(n)+"</node>\n");
        buf.append("  </previousEpisode>\n");
      }
      if (isModel)
        buf.append("</model>\n");
      else
        buf.append("</episode>\n");
    } catch (Exception e) {
      System.out.println("Failure in episode "+id+" "+e.getMessage());
    }
    return buf.toString();
  }
}
