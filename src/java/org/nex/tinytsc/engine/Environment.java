/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.engine;
import java.io.IOException;
import java.util.*;

import org.nex.tinytsc.database.JDBMDatabase;
import org.nex.tinytsc.ui.MainFrame;
import org.nex.tinytsc.DatastoreException;
import org.nex.tinytsc.LinkManager;
//import org.nex.tinytsc.agents.IAgent;
import org.nex.tinytsc.agents.FillinNextEpisodeAgent;
import org.nex.tinytsc.agents.PublishEpisodeAgent;
import org.nex.tinytsc.api.IAgent;
import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.api.Identifiable;

//jdbm.jar
import jdbm.helper.Tuple;

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
 * <p><code>Environment</code> takes care of <em>everything</em><br>
 * We hand each <code>agent</code> the <code>Environment</code>
 * from which they have access to the <code>agenda</code> and <code>database</code></p>
 * <p>Environment is shared by many different <code>IAgent</code>s</p>
 *
 * TODO:
 *   Add caches for each object
 */
public class Environment {

  public static final String UNNAMED = "Unnamed";
  private Logger log = Logger.getLogger(Environment.class);
  private JDBMDatabase database;
  private MainFrame host;
  private LinkManager taxManager;
  private Timer timer;
  private Long curIdNum = new Long(System.currentTimeMillis());
//  private AgendaManager agenda;
//  private List agents = new ArrayList();
  private Map agents = new HashMap();
  //for now
  private FillinNextEpisodeAgent fillinAgent = new FillinNextEpisodeAgent();
  private PublishEpisodeAgent publishAgent = new PublishEpisodeAgent();

  private Model workingModel = null;
  /**
   * Cache for named {@link Episode} objects
   * being built by the QP engines.
   */
  private Map episodes = new HashMap();

  private Model currentModel = null;

  public Environment(Hashtable properties/*, MainFrame h*/) throws DatastoreException {
    //host = h;
    String databaseDirectory=(String)properties.get("DatabasePath");
    //locate databaseDirectory in the root install directory
    //TODO: change to a different system that sends in absolute path
    java.io.File f = new java.io.File(".");
    try {
      databaseDirectory = f.getCanonicalPath()+databaseDirectory;
    } catch (Exception x) {
      x.printStackTrace();
    }
    database = new JDBMDatabase(databaseDirectory, this);
    database.open();
    taxManager = new LinkManager(this);
//    agenda = new AgendaManager();
    fillinAgent.initialize(this);
    publishAgent.initialize(this);
    //fill the agenda with Tasks
//    database.startTasksIterator();

//    Sentence t = new Sentence();
//    try {
//      while (database.getNextObject(t)) {
//        addTask( (Task) t.getValue());
//      }
//    } catch (BadTaskException x) {
//      x.printStackTrace();
//    }
    timer = new Timer();
    // seed concept
    if (getConcept("root") == null) {
    	Concept r = new Concept("root");
        System.out.println("ROOT "+r.toXML());
    	putConcept(r);
    }
    //debug
    System.out.println("ROOTX "+getConcept("root"));
    
  }

  public void setHost(MainFrame h) {
	  this.host = h;
	  say("Environment ready.");
  }
  
  public LinkManager getTaxonomyManager() {
	  return taxManager;
  }
  
  /////////////////////////////
  // importing support
  /////////////////////////////
  public void importConcept(Concept c) throws DatastoreException {
    database.putConcept(c.getId(),c);
  }

  public void importRule(ProcessRule c) throws DatastoreException {
    database.putRule(c.getId(),c);
  }

  public void importModel(Model c) throws DatastoreException {
	  System.out.println("ImportModel "+c);
    database.putModel(c.getId(),c);
  }
  public void importTask(Task c) throws DatastoreException {
    database.putTask(c.getId(),c);
  }

  public void importEpisode(Episode c) throws DatastoreException {
    database.putEpisode(c.getId(),c);
  }

  /**
   * Just setup some displays and statistics
   */
  public void finishImport() {
  	/*Model m = null;
  	//just send in the first model
        Tuple t = new Tuple();
        try {
          database.startModelsIterator();
          if (database.getNextObject(t))
            m = database.getModel((String)t.getKey());
          System.out.println("Finish Import " + t.getKey()+" "+m+" "+host);
          host.displayConceptRoot(database.getConcept("root"), m);
        } catch (DatastoreException x) {
          x.printStackTrace();
        }*/
        host.updateStatistics();
  }




  public JDBMDatabase getDatabase() {
    return database;
  }

//  public AgendaManager getAgenda() {
//    return agenda;
//  }

  /**
   * Support routine for an unnamed {@link Model}
   * @return  Model
   */
  public Model newModel() {
    return newModel(Environment.UNNAMED);
  }

  /**
   * Callback from <code>FillinNextEpisodeAgent</code>
   * @param m
   */
  public void setCurrentModel(Model m) {
    System.out.println("SCM 1");
    this.currentModel = m;
    currentModel.setEnvironment(this);
    System.out.println("SCM 2");
    say("Setting currentModel: "+m.getId());
    System.out.println("SCM 3");
  }
  /**
   * Callback from when an episode is sent here from <code>PublishEpisodeAgent</code>
   */
  public void incrementNodeCount() {
    if (currentModel != null) currentModel.incrementNodeCount();
  }
  /**
   * Support routine for a named {@link Model}
   * @param name
   * @return  Model
   */
  public Model newModel(String name) {
      return new Model(name);
  }

  /**
   * Utility to return a new named {@link Task}
   * @return Task
   */
  public Task newTask() {
    return newTask("T"+nextId());
  }
  /**
   * Utility routine to return a new {@link Task} of a given name
   * @param name String
   * @return Task
   */
  public Task newTask(String name) {
    return new Task(name);
  }
  /**
   * <p>
   * We are bypassing the AgendaManager for now. Just toss the
   * new <code>Task</code> at the appropriate <code>IAgent</code>
   * and ignore <code>priority</code> for now.
   * </p>
   * <p>
   * NOTE: this is NOT extensible easily -- need to return to
   * {@link AgendaManager}
   * }
   * @param newTask
   */
  public synchronized void addTask(Task newTask) {
    say("Environment Add Task start");
//    synchronized(agents) {
      say("Adding task "+newTask.getId()+" "+newTask.getTaskType());
      String tt = newTask.getTaskType();
      if (tt.equals(IConstants.FILLIN_NEXT_EPISODE))
        this.fillinAgent.addTask(newTask);
      else if (tt.equals(IConstants.PUBLISH_EPISODE))
        this.publishAgent.addTask(newTask);
      else
        say("Environment doesn't know how to handle taskType: "+tt);

      go();
/**
      IAgent agent = (IAgent)agents.get(newTask.getTaskType());
      if (agent == null)
        throw new RuntimeException("Missing Agent for "+newTask.getTaskType());
      agent.addTask(newTask);
 */
//    }
  }

  public void startTasks() {
    Iterator itr = agents.keySet().iterator();
    String n;
    while (itr.hasNext()) {
      n = (String)itr.next();
      say("Starting: "+n);
      ((IAgent)agents.get(n)).go();
    }
  }

  /**
   * Allows user to stop tasks from executing
   */
  public void waitTasks() {
    Iterator itr = agents.keySet().iterator();
    String n;
    while (itr.hasNext()) {
      n = (String)itr.next();
      say("Waiting: "+n);
      ((IAgent)agents.get(n)).idle();
    }
  }
  /**
   * Return a list of <em>id</em> values for each {@link Task}
   * @return  List
   */
  public List<String> listTasks() {
    List<String> result = new ArrayList<String>();
    try {
      database.startTasksIterator();
      Tuple t = new Tuple();
      while (database.getNextObject(t))
        result.add((String)t.getKey());
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
    return result;
  }

  public List<String> listModelIds() {
    List<String> result = new ArrayList<String>();
    try {
      database.startModelsIterator();
      Tuple t = new Tuple();
      while (database.getNextObject(t))
        result.add((String)t.getKey());
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
    return result;
  }
  /**
   * Support routine for a new <code>Episode</code>
   * @param  root    Model
   * @return  Episode does not return <code>null</code>
   */
  public Episode newEpisode(Model root) {
    Episode result = new Episode("E" + nextId());
    result.setInstanceOf("episode");
    result.setModel(root);
    return result;
  }

  /**
   * <p>
   * Return a <code>String</code> id for an
   * <em>existential</em> binding.
   * </p>
   * @return  String
   */
  public String newExistentialId() {
    return "EX"+Long.toString(nextId());
  }
  /**
   * We need access to and control of <code>agents</code>
   * @param taskType  String
   * @param agent     IAgent
   */
  public void registerAgent(String taskType, IAgent agent) {
    synchronized(agents) {
      Object o = agents.get(taskType);
      if (o != null)
        throw new RuntimeException(taskType+" already registered");
      agents.put(taskType, agent);
    }
  }

  /**
   * Returns every {@link ProcessRule} in the database
   * @return List
   */
  public List<ProcessRule> getAllRules() {
    synchronized(database) {
      List<ProcessRule> result = new ArrayList();
      try {
        database.startRulesIterator();
        Tuple t = new Tuple();
        while (database.getNextObject(t))
          result.add(database.getRule((String)t.getKey()));
      } catch (DatastoreException x) {
        x.printStackTrace();
      }
      return result;
    }
/*
    try {
      synchronized (database) {
        List result = new ArrayList();
        Sentence t = new Sentence();
        database.startRulesIterator();
        while(database.getNextObject(t))
          result.add((ProcessRule) t.getValue());
        return result;
      }
    } catch (DatastoreException e) {
      throw new RuntimeException(e.getMessage());
    }
*/
  }
  /**
   * <p>
   * Cache an <code>Episode</code> during envisionment building
   * <code>FillinNextEpisodeAgent</code> caches its result
   * </p>
   * @param e
   */
  public void cacheEpisode(Episode e) {
    synchronized(episodes) {
      episodes.put(e.getId(),e);
    }
  }

  /**
   * <p>
   * Grab a cached <code>Episode</code> during envisionment building
   * <code>PublishEpisodeAgent</code> grabs a cached <code>Episode</code>
   * </p>
   * @param id
   * @return
   */
  public Episode uncacheEpisode(String id) {
    synchronized(episodes) {
      return (Episode)episodes.get(id);
    }
  }



  public int getConceptCount() throws DatastoreException {
    synchronized(database) {
      return database.getConceptCount();
    }
  }
  public int getRuleCount() throws DatastoreException {
    synchronized(database) {
      return database.getRuleCount();
    }
  }
  public int getTaskCount() throws DatastoreException {
    synchronized (database) {
      return database.getTaskCount();
    }
  }
  public int getModelCount() throws DatastoreException {
    synchronized(database) {
      return database.getModelCount();
    }
  }
  public int getEpisodeCount() throws DatastoreException {
    synchronized(database) {
      return database.getEpisodeCount();
    }
  }
  public Concept getConcept(String id) {
    synchronized(database) {
      try {
        return database.getConcept(id);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }


  public ProcessRule getRule(String id) {
    synchronized(database) {
      try {
        return database.getRule(id);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
  public Task getTask(String id) {
    synchronized(database) {
      try {
        return database.getTask(id);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
  public Episode getEpisode(String id) {
    synchronized(database) {
      try {
        return database.getEpisode(id);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
  public Model getModel(String id) {
    synchronized(database) {
      try {
        return database.getModel(id);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  /**
   * Temp holding place for fetched object
   */
  private Object identifiableObject = null;

  /**
   * <p>
   * This method satisfies the need to fetch an identifed object from
   * the database when you don't know what type of object it is.
   * </p>
   * @param id String
   * @return Identifiable
   */
  public Identifiable getIdentifiable(String id) {
    int which = this.whichType(id);
    System.out.println("Environment getIdentifiable "+id+" "+which+" "+identifiableObject);
    Identifiable result = null;
    if (identifiableObject != null) {
      switch (which) {
        case IConstants.CONCEPT:
          result = (Concept)identifiableObject;
          break;
        case IConstants.PROCESS_RULE:
          result = (ProcessRule)identifiableObject;
          break;
        case IConstants.EPISODE:
          result = (Episode)identifiableObject;
          break;
        case IConstants.MODEL:
          result = (Model)identifiableObject;
          break;
        case IConstants.TASK:
          result = (Task)identifiableObject;
          break;
      }
    }
    return result;
  }

  public void putConcept(Concept c) throws DatastoreException {
    synchronized(database) {
      try {
        database.putConcept(c.getId(), c);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  public void putRule(ProcessRule c) throws DatastoreException {
    synchronized(database) {
      try {
        database.putRule(c.getId(), c);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  public void putEpisode(Episode c) throws DatastoreException {
    synchronized(database) {
      try {
        database.putEpisode(c.getId(), c);
        incrementNodeCount();
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
    //refresh dashboard
    go();
  }

  public void putTask(Task c) throws DatastoreException {
    synchronized(database) {
      try {
        database.putTask(c.getId(), c);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }
  public void putModel(Model c) throws DatastoreException {
    synchronized(database) {
      try {
        database.putModel(c.getId(), c);
      } catch (DatastoreException e) {
        throw new RuntimeException(e.getMessage());
      }
    }
  }

  /**
   * For any given objectId, return its objectType by fetching it
   * @param objectId
   * @return   int & retain fetched object in <code>identifiableObject</code>
   */
  public int whichType(String objectId) {
  	int result = IConstants.UNUSED;
        try {
          if ((identifiableObject = database.getConcept(objectId)) != null)
            result = IConstants.CONCEPT;
          else if ((identifiableObject = database.getRule(objectId)) != null)
            result = IConstants.PROCESS_RULE;
          else if ((identifiableObject = database.getEpisode(objectId)) != null)
            result = IConstants.EPISODE;
          else if ((identifiableObject = database.getModel(objectId)) != null)
            result = IConstants.MODEL;
          else if ((identifiableObject = database.getTask(objectId)) != null)
            result = IConstants.TASK;
        } catch (DatastoreException x) {
          x.printStackTrace();
        }
  	return result;
  }

  public void close() {
    try {
      database.close();
    } catch (DatastoreException e) {
      e.printStackTrace();
    }
  }
  /**
   * Send a message to the console
   * @param msg
   */
  public synchronized void say(String msg) {
      host.say(msg);
  }

  public Timer getTimer() {
	  return timer;
  }
  ///////////////////////////////////////////
  // Timer for running the dashboard
  ///////////////////////////////////////////

  public MyTask getMyTask() {
	  return new MyTask();
  }
  public class MyTask extends TimerTask {
        public MyTask() {}
        public void run() {
                go();
        }
  }
  /**
   * Collect data and refresh dashboard
   */
  void go() {
//    System.out.println("GO");
    String modelId = "";
    int numEpisodes = 0;
    String fillinEpId =  this.fillinAgent.getCurrentEpId();
    int fillinQueue = this.fillinAgent.getQueueSize();
    String publishEpId = this.publishAgent.getCurrentEpId();
    int publishQueue = this.publishAgent.getQueueSize();
    if (currentModel != null) {
      modelId = currentModel.getId();
      numEpisodes = currentModel.getNodeCount();
    }
    host.refreshDashboard(modelId,numEpisodes,
                          fillinEpId,fillinQueue,
                          publishEpId,
                          publishQueue);
    host.updateStatistics();
  }

  public long nextId() {
    synchronized(curIdNum) {
      long cur = curIdNum.longValue();
      long temp = 0;
      try {
        while ((temp = System.currentTimeMillis()) <= cur) wait(10);
      } catch (Exception e) {}
      curIdNum = new Long(temp);
      return temp;
    }
  }
  
  /////////////////////////
  // Logging support
  /////////////////////////
  public void logDebug(String message) {
	  this.log.debug(message);
  }
  
  public void logError(String message, Exception e) {
	  this.log.error(message, e);
  }
}
