/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.database;

import java.io.*;

import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.engine.Episode;
import org.nex.tinytsc.engine.Task;
import org.nex.tinytsc.xml.ConceptPullParser;
import org.nex.tinytsc.engine.Model;
//import org.nex.tinytsc.IDatastore;
import org.nex.tinytsc.DatastoreException;
//jdbm.jar
import jdbm.btree.BTree;
import jdbm.helper.MRU;
import jdbm.helper.ObjectCache;
import jdbm.helper.StringComparator;
import jdbm.recman.RecordManager;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;
//log4j.jar
import org.apache.log4j.Logger;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 *
 * We will create 3 databases:
 * <li>Concepts</li>
 * <li>Rules</li>
 * <li>Episodes</li>
 */

public class JDBMDatabase /*implements IDatastore*/ {
  private Environment environment;
  private ConceptPullParser parser;
  //TODO add LRUCaches to speed things up
  private RecordManager conceptManager = null;
  private RecordManager ruleManager = null;
  private RecordManager episodeManager = null;
  private RecordManager taskManager = null;
  private RecordManager modelManager = null;
  private BTree conceptTree = null;
  private BTree ruleTree = null;
  private BTree episodeTree = null;
  private BTree taskTree = null;
  private BTree modelTree = null;
  private Tuple tuple = new Tuple();
  private TupleBrowser  browser = null;
  private String directory;
  final String tableName = "data";
  final String conceptsDatabaseName = "Concepts";
  final String rulesDatabaseName = "Rules";
  final String episodesDatabaseName = "Episodes";
  final String tasksDatabaseName = "Tasks";
  final String modelsDatabaseName = "Models";

  /**
   * Basic constructor
   * @param baseDir -- where the tables go
   * @param env
   */
  public JDBMDatabase(String baseDir, Environment env) {
	  this.environment = env;
	  this.parser = new ConceptPullParser(environment);
    this.directory = baseDir;
  }


  //----------------------------------DATABASE
  // putting objects
  // if we don't commit each transaction,
  // memory use grows
  public void putConcept(String key, Concept w) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.putConcept "+key);
      environment.logDebug("PutConcept\n"+w.toXML());
      conceptTree.insert(key, w.toXML(), true);
      conceptManager.commit();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void putRule(String key, ProcessRule p) throws DatastoreException {
    try {
        environment.logDebug("PutRule\n"+p.toXML());
      System.out.println("JDBMDatabase.putRule "+key);
      ruleTree.insert(key, p.toXML(), true);
      ruleManager.commit();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void putEpisode(String key, Episode t) throws DatastoreException {
    try {
        environment.logDebug("PutEpisode\n"+t.toXML());
      System.out.println("JDBMDatabase.putEpisode "+key);
      episodeTree.insert(key, t.toXML(), true);
      episodeManager.commit();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void putTask(String key, Task t) throws DatastoreException {
    try {
        environment.logDebug("PutTask\n"+t.toXML());
      System.out.println("JDBMDatabase.putTask "+key);
      taskTree.insert(key, t.toXML(), true);
      taskManager.commit();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void putModel(String key, Model t) throws DatastoreException {
    try {
        environment.logDebug("PutModel\n"+t.toXML());
      System.out.println("JDBMDatabase.putModel "+key);
      modelTree.insert(key, t.toXML(), true);
      modelManager.commit();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  // getting objects
  public Concept getConcept(String key) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.getConcept "+key);
      browser = conceptTree.browse(key);
      Concept result = null;
      String xml;
      while (browser.getNext(tuple)) {
        String k = (String) tuple.getKey();
        System.out.println("JDBMDatabase.getConceptX "+key+" "+k);
        if (k.equals(key)) {
        	xml = (String)tuple.getValue();
            environment.logDebug("GetConcept "+key+"\n"+xml);
            System.out.println("JDBMDatabase.getConceptY "+key+" "+xml);
        	parser.parse(xml);
          result = parser.getConcept();
          result.setDatabase(this);
        }
        else {
          break;
        }
      }
      return result;
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }

  public ProcessRule getRule(String key) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.getRule "+key);
      browser = ruleTree.browse(key);
      ProcessRule result = null;
      String xml;
      while (browser.getNext(tuple)) {
        String k = (String) tuple.getKey();
        if (k.equals(key)) {
        	xml = (String)tuple.getValue();
            environment.logDebug("GetRule "+key+"\n"+xml);
        	parser.parse(xml);
          result = parser.getRule();
        }
        else {
          break;
        }
      }
      return result;
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public Episode getEpisode(String key) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.getEpisode "+key);
      browser = episodeTree.browse(key);
      Episode result = null;
      String xml;
      while (browser.getNext(tuple)) {
        String k = (String) tuple.getKey();
        if (k.equals(key)) {
        	xml = (String) tuple.getValue();
            environment.logDebug("GetEpisode "+key+"\n"+xml);
        	parser.parse(xml);
            result = parser.getEpisode();
        }
        else {
          break;
        }
      }
      return result;
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public Task getTask(String key) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.getTask "+key);
      browser = taskTree.browse(key);
      Task result = null;
      String xml;
      while (browser.getNext(tuple)) {
        String k = (String) tuple.getKey();
        if (k.equals(key)) {
        	xml = (String)tuple.getValue();
            environment.logDebug("GetTask "+key+"\n"+xml);
        	parser.parse(xml);
            result = parser.getTask();
        }
        else {
          break;
        }
      }
      return result;
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public Model getModel(String key) throws DatastoreException {
    try {
      System.out.println("JDBMDatabase.getModel "+key);
      browser = modelTree.browse(key);
      Model result = null;
      String xml;
      while (browser.getNext(tuple)) {
        String k = (String) tuple.getKey();
        System.out.println("JDBMDatabase.getModelX "+key+" "+k);
        if (k.equals(key)) {
        	xml = (String)tuple.getValue();
            environment.logDebug("DB-GetModel "+key+"\n"+xml);
        	parser.parse(xml);
            result = parser.getModel();
        }
        else {
          break;
        }
      }
      return result;
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }

  public int getConceptCount() throws DatastoreException {
      int result = 0;
      startConceptsIterator();
      Tuple t = new Tuple();
      while (getNextObject(t))
        result++;
      return result;
  }
  public int getRuleCount() throws DatastoreException {
      int result = 0;
      startRulesIterator();
      Tuple t = new Tuple();
      while (getNextObject(t))
        result++;
      return result;
  }
  public int getEpisodeCount() throws DatastoreException {
      int result = 0;
      startEpisodesIterator();
      Tuple t = new Tuple();
      while (getNextObject(t))
        result++;
      return result;
  }
  public int getModelCount() throws DatastoreException {
      int result = 0;
      startModelsIterator();
      Tuple t = new Tuple();
      while (getNextObject(t))
        result++;
      return result;
  }
  public int getTaskCount() throws DatastoreException {
      int result = 0;
      startTasksIterator();
      Tuple t = new Tuple();
      while (getNextObject(t))
        result++;
      return result;
  }
  //browsing objects
  public void startConceptsIterator() throws DatastoreException {
    try {
      browser = conceptTree.browse();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void startRulesIterator() throws DatastoreException {
    try {
      browser = ruleTree.browse();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void startEpisodesIterator() throws DatastoreException {
    try {
      browser = episodeTree.browse();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void startTasksIterator() throws DatastoreException {
    try {
      browser = taskTree.browse();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void startModelsIterator() throws DatastoreException {
    try {
      browser = modelTree.browse();
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  /**
   * Depends on which <em>iterator</em> is started
   * @param tuple
   * @return <code>true</code> if tuple is filled
   * @throws DatastoreException
   */
  public boolean getNextObject(Tuple t) throws DatastoreException {
    try {
      return browser.getNext(t);
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }

  public void close() throws DatastoreException {
    try {
      if (conceptManager != null) {
        conceptManager.commit();
        conceptManager.close();
      }
      if (ruleManager != null) {
        ruleManager.commit();
        ruleManager.close();
      }
      if (episodeManager != null) {
        episodeManager.commit();
        episodeManager.close();
      }
      if (taskManager != null) {
        taskManager.commit();
        taskManager.close();
      }
      if (modelManager != null) {
        modelManager.commit();
        modelManager.close();
      }
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }
  public void open() throws DatastoreException {
    try {
      //concepts
      conceptManager = new RecordManager(directory + "/" + conceptsDatabaseName);
      ObjectCache wordCache = new ObjectCache(conceptManager, new MRU(100));
      long conceptRecid = conceptManager.getNamedObject(tableName);
      if (conceptRecid != 0) {
        System.out.println("Concept exists");
        // already exists
        conceptTree = BTree.load(conceptManager, wordCache, conceptRecid);
      }
      else {
        System.out.println("Concept not exists");
        // does not exist
        conceptTree = new BTree(conceptManager, wordCache, new StringComparator());
        conceptManager.setNamedObject(tableName, conceptTree.getRecid());
      }
      //rules
      ruleManager = new RecordManager(directory + "/" + rulesDatabaseName);
      ObjectCache pairCache = new ObjectCache(ruleManager, new MRU(100));
      long ruleRecid = ruleManager.getNamedObject(tableName);
      if (ruleRecid != 0) {
        System.out.println("ProcessRule exists");
        // already exists
        ruleTree = BTree.load(ruleManager, pairCache, ruleRecid);
      }
      else {
        System.out.println("ProcessRule not exists");
        // does not exist
        ruleTree = new BTree(ruleManager, pairCache, new StringComparator());
        ruleManager.setNamedObject(tableName, ruleTree.getRecid());
      }
      //episodes
      episodeManager = new RecordManager(directory + "/" + episodesDatabaseName);
      ObjectCache episodeCache = new ObjectCache(episodeManager, new MRU(100));
      long episodeRecid = episodeManager.getNamedObject(tableName);
      if (episodeRecid != 0) {
        System.out.println("Episode exists");
        // already exists
        episodeTree = BTree.load(episodeManager, episodeCache, episodeRecid);
      }
      else {
        System.out.println("Episode not exists");
        // does not exist
        episodeTree = new BTree(episodeManager, episodeCache,
                                new StringComparator());
        episodeManager.setNamedObject(tableName, episodeTree.getRecid());
      }
      //tasks
      taskManager = new RecordManager(directory + "/" + tasksDatabaseName);
      ObjectCache taskCache = new ObjectCache(taskManager, new MRU(100));
      long taskRecid = taskManager.getNamedObject(tableName);
      if (episodeRecid != 0) {
        System.out.println("Task exists");
        // already exists
        taskTree = BTree.load(taskManager, taskCache, taskRecid);
      }
      else {
        System.out.println("Task not exists");
        // does not exist
        taskTree = new BTree(taskManager, taskCache, new StringComparator());
        taskManager.setNamedObject(tableName, taskTree.getRecid());
      }
      //models
      modelManager = new RecordManager(directory + "/" + modelsDatabaseName);
      ObjectCache modelCache = new ObjectCache(modelManager, new MRU(100));
      long modelRecid = modelManager.getNamedObject(tableName);
      if (episodeRecid != 0) {
        System.out.println("Model exists");
        // already exists
        modelTree = BTree.load(modelManager, modelCache, modelRecid);
      }
      else {
        System.out.println("Model not exists");
        // does not exist
        modelTree = new BTree(modelManager, modelCache, new StringComparator());
        modelManager.setNamedObject(tableName, modelTree.getRecid());
      }
    } catch (IOException e) {
      throw new DatastoreException(e);
    }
  }

  protected void finalize() throws Throwable {
      close();
  }

}
