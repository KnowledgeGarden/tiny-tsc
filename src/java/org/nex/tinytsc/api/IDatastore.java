/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.api;

import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.engine.Task;
import org.nex.tinytsc.engine.Episode;
import org.nex.tinytsc.engine.Model;
import org.nex.tinytsc.DatastoreException;
import org.nex.tinytsc.engine.Concept;

//jdm
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 *
 * Not used until we resolve the issue about a Sentence for getNextObject()
 */

public interface IDatastore {

  public void putConcept(String key, Concept w) throws DatastoreException;
  public void putRule(String key, ProcessRule p) throws DatastoreException;
  public void putEpisode(String key, Episode t) throws DatastoreException;
  public void putTask(String key, Task t) throws DatastoreException;
  public void putModel(String key, Model t) throws DatastoreException;
  public Concept getConcept(String key) throws DatastoreException;

  public ProcessRule getRule(String key) throws DatastoreException;
  public Episode getEpisode(String key) throws DatastoreException;
  public Task getTask(String key) throws DatastoreException;
  public Model getModel(String key) throws DatastoreException;
  public int getConceptCount() throws DatastoreException;
  public int getRuleCount() throws DatastoreException;
  public int getEpisodeCount() throws DatastoreException;
  public int getModelCount() throws DatastoreException;
  public int getTaskCount() throws DatastoreException;

  public void startConceptsIterator() throws DatastoreException;
  public void startRulesIterator() throws DatastoreException;
  public void startEpisodesIterator() throws DatastoreException;
  public void startTasksIterator() throws DatastoreException;
  public void startModelsIterator() throws DatastoreException;
  /**
   * Depends on which <em>iterator</em> is started
   * @param tuple
   * @return <code>true</code> if tuple is filled
   * @throws DatastoreException
   */
//  public boolean getNextObject(Sentence t) throws DatastoreException;

}