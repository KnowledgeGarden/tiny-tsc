/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc;
import java.util.*;
import java.io.*;

import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.Episode;
import org.nex.persist.text.TextFileHandler;
import org.nex.tinytsc.engine.Sentence;
import org.nex.tinytsc.engine.Model;
import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Task;
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 *
 * To make this work, _BIO.Loader was cleaned up and all the files
 * it calls were cleaned up. e.g. instance.of --> instanceOf
 * and all instances where dangling comments were in lines, they got a "\" to mark them
 * plus, any lack of spaces between parens and other tokens in sentences
 * were fixed, e.g. foo) became foo )
 * Legacy TSC used sentences, e.g. ( abuts ( a b ) true )
 * TSC now uses Tuples, e.g. ( abuts ( a b ) )
 */

public class LegacyImporter {
  final String TASK = "agendaTask";
  final String MODEL = "model";
  final String RULE = "processRule";

  private Environment environment;
  private TextFileHandler handler = new TextFileHandler();
  /**
   * These are the Con ConObjects made here
   */
  private Map<String, Con> ConObject = new HashMap<String, Con>();

  private Map<String, Concept> MyConcepts = new HashMap<String, Concept>();
  private Map<String, Task> MyTasks = new HashMap<String, Task>();
 // private Map<String, Episode> MyEpisodes = new HashMap<String, Episode>();
  private Map<String, Model> MyModels = new HashMap<String, Model>();
  private Map<String, ProcessRule> MyRules = new HashMap<String, ProcessRule>();

  private File loader;
  private String directory;
  /** state variables */
  private Con workingCon = null;
  private Sentence workingSentence = null;
  private List<Object> sentences;

  public LegacyImporter(Environment e) {
    this.environment = e;
  }

  /**
   * Open a file to import a legacy TSC KB
   */
  public void startImport() {
    loader = handler.openFile("TSC Loader");
    if (loader != null) {
      try {
        directory = loader.getCanonicalPath();
        //strip off filename
        int where = directory.lastIndexOf("/");
        if (where == -1) {
          where = directory.lastIndexOf("\\");
          if (where == -1)
            throw new RuntimeException("Can't find directory in "+directory);
        }
        directory = directory.substring(0,where+1);
        System.out.println("Loading from directory: "+directory);
        //tested and works!
        parseLoader();
        // parsing will fill the objects HashMap
        // when done, dump everything into the database
        storeAll();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Convert all the Con objects into TSC objects and store them
   * fillin the inverseSlot
   */
  void storeAll() throws Exception {
    System.out.println("Total number of Con objects: "+ConObject.size());
    environment.logDebug("AllConcepts\n"+ConObject);
    Iterator<String> itr = ConObject.keySet().iterator();
    Con c; 
    String n;
    while (itr.hasNext()) {
      n = itr.next();
      environment.logDebug("MessingWith "+n);
      if (n != null) {
    	  c = ConObject.get(n);
    	  resolveInverseSlots(c);
          convertCon(c);
          
      }
    }

    finishTasks();
    itr = MyConcepts.keySet().iterator();
    Concept cx;
    while (itr.hasNext()) {
    	n = itr.next();
    	cx = this.MyConcepts.get(n);
        //if (cx.getInstanceOf().equals(""))
        environment.getDatabase().putConcept(n, cx);
    }
    itr = MyTasks.keySet().iterator();
    Task tx;
    while (itr.hasNext()) {
    	n = itr.next();
    	tx = this.MyTasks.get(n);
        environment.getDatabase().putTask(n, tx);
    }
    itr = MyModels.keySet().iterator();
    Model mx;
    while (itr.hasNext()) {
    	n = itr.next();
    	mx = this.MyModels.get(n);
        environment.getDatabase().putModel(n, mx);
    }
    itr = MyRules.keySet().iterator();
    ProcessRule rx;
    while (itr.hasNext()) {
    	n = itr.next();
    	rx = this.MyRules.get(n);
        environment.getDatabase().putRule(n, rx);
    }


    environment.finishImport();
  }

  void resolveInverseSlots(Con c) {
	  if (c == null) return;
	  System.out.println("ResolvingInverseSlots "+c);
    Iterator<String> itr = c.listSlotNames();
    Con slot, other;
    String n, x;
    List<Object> vals, invVals;
    if (!c.instanceOf.equals("")) {
    	x = c.instanceOf;
      System.out.println("****IO : "+x);
      if (x != null) {
    	  other = new Con(x); //ConObject.get(c.instanceOf);
    	  System.out.println("****Other : "+other);
      
    	  vals = new ArrayList<Object>();
    	  vals. add(c.id);
    	  System.out.println("****Vals : "+vals);
    	  other.addSlot("hasInstances",vals);
      }
    }
    while (itr.hasNext()) {
      n = itr.next();
      System.out.println("^^^^Getting "+n+" on "+c.id);
      // get the slot itself
      slot = ConObject.get(n);
      if (slot != null) {
        invVals = slot.getSlotValue("inverseSlot");
        if (invVals != null) {
          // this Con has a slotType which has an inverseValue
          // this means that some other Con, one of this slot's values
          // has a named inverseSlot that should hold thisCon as a value
          int len = invVals.size();
          System.out.println("  Got: "+len);
          vals = c.getSlotValue(n);
          int len2 = vals.size();
          for (int i = 0; i < len; i++) {
            n = (String) invVals.get(i); // inverseSlotType
            System.out.println("     Got: "+n);
            for (int j = 0; j < len2; j++) {
              other = (Con) ConObject.get( (String) vals.get(j));
              if (other != null) {
                System.out.println("       Got: "+other.id);
                List<Object> l = new ArrayList<Object>();
                l.add(c.id);
                other.addSlot(n, l);
              }
            }
          }
        }
      }
    }
  }
  private List<Con> tasks = new ArrayList<Con>();

  void finishTasks() {
    try {
      int len = tasks.size();
      System.out.println("FinishingTasks "+len);
      for (int i = 0; i < len; i++)
        convertConcept(tasks.get(i));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   * TODO: ripple through and check all slots. If a slotType has an inverseSlot,
   * then go deal with the inverseSlot. If it's already handled, ignore, otherwise
   * @param c
   */
  void convertCon(Con c) {
	  environment.logDebug("ConvertCon "+c);
    if (c.instanceOf.equals(""))
      convertConcept(c);
    else if (c.instanceOf.equals(TASK))
      convertTask(c);
    else if (c.instanceOf.equals(RULE))
      convertRule(c);
    else if (c.instanceOf.equals(MODEL))
      convertModel(c);
    else
      tasks.add(c);
  }

  void convertTask(Con c) {
	  System.out.println("CT "+c);
    Task tsk = new Task(c.id);
    if (!c.instanceOf.equals("")) {
      tsk.setInstanceOf(c.instanceOf);
    }
    MyTasks.put(c.id, tsk);
    Concept pr = MyConcepts.get(TASK);
    if (pr == null) {
    	pr = new Concept(TASK);
    	pr.setDatabase(environment.getDatabase());
    	MyConcepts.put(TASK, pr);
    }
    pr.addInstance(c.id);
    if (c.getPriority() != null)
    	tsk.setPriority(Integer.parseInt(c.getPriority()));
    Iterator<String> itr = c.listSlotNames();
    String n, v;
    List l;
    while (itr.hasNext()) {
      n = itr.next();
      l = c.getSlotValue(n);
      int len = l.size();
      for (int i = 0; i < len; i++) {
        if (n.equals("topic")) {
          //ignore
        } else if (n.equals("doTask")) {
          v = (String)l.get(i);
          if (v.startsWith("fillin"))
            tsk.setTaskType(IConstants.FILLIN_NEXT_EPISODE);
          else if (v.startsWith("publish"))
            tsk.setTaskType(IConstants.PUBLISH_EPISODE);
        } else if (n.equals("onConcept")) {
          String x = (String)l.get(i);
          System.out.println("Getting model "+x);
          /**
           * NOTE: it's entirely possible that the model has been loaded but not processed yet
           * That's because we are storing concepts in a Map which does not necessarily return them
           * in the order they are loaded.
           * If we don't get a model from the database, e.g. myExperiment
           * we must go process it now
           */
          
          Model m = environment.getModel(x);
          if (m == null) {
        	Con mx = this.ConObject.get(x);
        	if (mx == null)
                throw new RuntimeException("Missing Model: "+x);
        	System.out.println("GotModel "+mx);
        	resolveInverseSlots(mx);
            convertCon(mx);
            m = environment.getModel(x);
          }
          // concept MUST be compiled before task
          tsk.setObject(m);
          // we are importing and the model is the episode
          tsk.setModel( (Model) tsk.getObject());
        }
      }
    }
    try {
      environment.importTask(tsk);
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
    environment.addTask(tsk);
    System.out.println(tsk.toXML());
  }
  void convertRule(Con c) {
	  System.out.println("CR "+c);
    ProcessRule r = new ProcessRule(c.id);
    if (!c.instanceOf.equals("")) {
      r.setInstanceOf(c.instanceOf);
    }
    MyRules.put(c.id, r);
    Concept pr = MyConcepts.get(RULE);
    if (pr == null) {
    	pr = new Concept(RULE);
    	pr.setDatabase(environment.getDatabase());
    	MyConcepts.put(RULE, pr);
    }
    pr.addInstance(c.id);
    Iterator<String> itr = c.listSlotNames();
    String n;
    List l;
    Sentence t;
    while (itr.hasNext()) {
      n = itr.next();
      l = c.getSlotValue(n);
      int len = l.size();
      for (int i=0;i<len;i++) {
        if (n.equals("instanceOf"))
          r.setInstanceOf((String)l.get(i));
        else if (n.equals("subOf"))
          r.addSubOf((String)l.get(i));
        else if (n.equals("context") ||
                   n.equals("myCreator") ||
                   n.equals("level")) {
          //do nothing for now
        }  else if (n.equals("thenCreate")) {
            Sentence x = new Sentence();
            x.object = (String)l.get(i);
        	r.addThenCreates(x);
        } else if (n.equals("thenSay")) {
        	String sx = (String)l.get(i);
        	sx = sx.trim();
        	//remove leading and trailing quotes, if any
        	if (sx.startsWith("\""))
        		sx = sx.substring(1).trim();
        	if (sx.endsWith("\""))
        		sx = sx.substring(0, (sx.length()-1)).trim();
        	r.addThenSay(sx);
        } else if (n.equals("thenTerminate"))
          r.setIsTerminate(true);
        else {
          System.out.println("R# "+n);
          t = (Sentence) l.get(i);
          if (n.equals("ifActors"))
            r.addIfActor(t);
          else if (n.equals("ifNotActors"))
            r.addIfNotActor(t);
          else if (n.equals("ifRelations"))
            r.addIfRelation(t);
          else if (n.equals("ifNotRelations"))
            r.addIfNotRelation(t);
          else if (n.equals("ifStates"))
            r.addIfState(t);
          else if (n.equals("ifNotStates"))
            r.addIfNotState(t);
          else if (n.equals("thenActors"))
            r.addThenActor(t);
          else if (n.equals("thenRelations"))
            r.addThenRelation(t);
          else if (n.equals("thenStates"))
            r.addThenState(t);
          else if (n.equals("thenTerminate"))
            r.setIsTerminate(true);
          else if (n.equals("thenConjecture"))
            r.addThenConjecture(t);
        }
        //TODO: ifRun, ifNotRun, thenRun
      }
    }
    try {
      environment.importRule(r);
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
     System.out.println(r.toXML());
  }
  void convertModel(Con c) {
	  System.out.println("CM "+c);
    Model m = new Model(c.id);
    if (!c.instanceOf.equals("")) {
      m.setInstanceOf(c.instanceOf);
    }
    MyModels.put(c.id, m);
    Concept pr = MyConcepts.get(MODEL);
    if (pr == null) {
    	pr = new Concept(MODEL);
    	pr.setDatabase(environment.getDatabase());
    	MyConcepts.put(MODEL, pr);
    }
    pr.addInstance(c.id);    
    Iterator<String> itr = c.listSlotNames();
    environment.logDebug("IMPORT MODEL slot names "+c.getSlotNames());
    String n;
    List l;
    Sentence t;
    while (itr.hasNext()) {
      //for each slot name
      n = itr.next();
      l = c.getSlotValue(n);
      int len = l.size();
//      System.out.println("## "+l+" "+n);
      for (int i=0;i<len;i++) {
        if (n.equals("instanceOf"))
          m.setInstanceOf((String)l.get(i));
        else if (n.equals("context") ||
                 n.equals("myCreator") ||
                 n.equals("(") ||
                 n.equals(")")){
          //doNothing
        } else {
          System.out.println("#M "+n);
          t = (Sentence) l.get(i);
          environment.logDebug("IMPORT MODEL slots "+n+" "+t);
          if (n.equals("actors"))
            m.addActor(t);
          else if (n.equals("relations"))
            m.addRelation(t);
          else if (n.equals("states"))
            m.addState(t);
        }
      }
    }
    try {
      environment.importModel(m);
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
     System.out.println(m.toXML());
  }
  void convertConcept(Con c) {
	  environment.logDebug("ConvertConcept "+c);
    Concept con = MyConcepts.get(c.id);
    if (con == null) {
    	con = new Concept(c.id);
    	con.setDatabase(environment.getDatabase());
    	MyConcepts.put(c.id, con);
    }
    Concept parent;
    String io = c.instanceOf;
	  environment.logDebug("ConvertConcept-1 "+io);
    if (!io.equals("")) {
      //con.addProperty("instanceOf",c.instanceOf);
      con.setInstanceOf(io);
      parent = MyConcepts.get(io);
      if (parent == null) {
    	  parent = new Concept(io);
    	  parent.setDatabase(environment.getDatabase());
    	  MyConcepts.put(io, parent);
      }
      parent.addInstance(c.id);
	  environment.logDebug("ConvertConcept-2 "+parent.toXML());
    }
    Iterator<String> itr = c.listSlotNames();
    String n, pid;
    List l;
    while (itr.hasNext()) {
      n = itr.next();
      l = c.getSlotValue(n);
	  System.out.println("CCCXXX "+n +" "+l);
      int len = l.size();
      
      for (int i=0;i<len;i++) {
    	  if (n.equals("subOf")) {
    		  environment.logDebug("IMPORT "+c.id+" "+n);
    		  pid = (String)l.get(i);
    		  con.addSubOf(pid);
    		  parent = MyConcepts.get(pid);
    		  if (parent == null) {
    			  //not made yet, so make a shell
    			  parent = new Concept(pid);
    			  parent.setDatabase(environment.getDatabase());
    			  MyConcepts.put(pid, parent);
    		  }
    		  parent.addSubClass(c.id);
    		  environment.logDebug("IMPORT+ "+parent.toXML());
    	  }
    	  else if (n.equals("subOf")) {
    		  environment.logDebug("IMPORT "+c.id+" "+n);
    		  pid = (String)l.get(i);
    		  con.setInstanceOf(pid);
    		  parent = MyConcepts.get(pid);
    		  if (parent == null) {
    			  //not made yet, so make a shell
    			  parent = new Concept(pid);
    			  parent.setDatabase(environment.getDatabase());
    			  MyConcepts.put(pid, parent);
    		  }
    		  parent.addInstance(c.id);
    		  environment.logDebug("IMPORT+ "+parent.toXML());
    	  }
    	  else 
    	  //TODO need "else" here?
        con.addProperty(n,(String)l.get(i));
      }
    }
    try {
      environment.importConcept(con);
    } catch (DatastoreException x) {
      x.printStackTrace();
    }
     System.out.println(con.toXML());
  }
  
  /**
   * Read a file, line by line
   */
  void parseLoader() {
    TextFileHandler loadHandler = new TextFileHandler();
    String line = loadHandler.readFirstLine(this.loader).trim();
    while (line != null) {
      System.out.println("Loader got: "+line);
      parseLoaderLine(line);
      line = loadHandler.readNextLine();
    }
  }
  
  /**
   * Deal with any line which doesn't start with a comment
   * If the line starts with "xload", then load that file
   * @param line
   */
  void parseLoaderLine(String line) {
    String str = line.trim();
    if (line.startsWith("\\")) return;
    if (line.startsWith("xload")) {
      str = str.substring("xload".length()).trim();
      parseKB(str);
    }
  }

  /**
   * Wait for rightCurlyBracket to close out a block comment
   */
  private boolean isLeftCurlyBracket = false;
  private boolean isSentence = false;
  
  String fixConceptLine(String line) {
	  String result = "";
	  if (line.startsWith("c:"))
		  result = cleanLine(line.substring("c:".length()).trim());
	  else if (line.startsWith("+c:"))
		  result = cleanLine(line.substring("+c:".length()).trim());
	  return result;
  }
  /**
   * <p>Workhorse for parsing KB data<p>
   * <p>In a legacy KB, everything is either a concept or a slot of some kind</p>
   * @param line
   */
  void parseLine(String line) {
    String str = line.trim();
    String name;
    //comment
    if (str.startsWith("\\")) return;
    if (isSentence)
      parseSentence(cleanLine(str));
    // another kind of block comment is comment: comment;
    //look for block comment end
    else if (isLeftCurlyBracket &&
               ( str.startsWith("}") ||
                 str.startsWith("comment;")) ) {
      isLeftCurlyBracket = false;
    } else if (isLeftCurlyBracket)
      return;
    // block comment start
    else if (str.startsWith("{") ||
             str.startsWith("comment:")) {
      isLeftCurlyBracket = true;
    }
    //start a concept
    else if (str.startsWith("c:") ||
             str.startsWith("+c:")) {
      name = fixConceptLine(str);
      System.out.println("Got Concept: "+name+ " | "+workingCon);
      if (workingCon == null)
        workingCon = new Con(name);
      else if (!workingCon.id.equals(name)) {
        // not the same, but already in the cache
        workingCon = ConObject.get(name);
        // see if we already have this puppy
        if (workingCon==null)
          workingCon = new Con(name);
      }
      //make sure workingCon is already set in the map
      if (workingCon != null) {
        this.ConObject.put(workingCon.id, workingCon);
      }
    }
    else //otherwise, it's a slot
      parseSlot(cleanLine(str));
  }

  boolean haveConcept() {
    return (workingCon != null);
  }

  /**
   * Lines which are slots can have multiple values separated by whitespace.
   * e.g.
   *   inverseValue   notAttachedTo disconnected<br>
   * Lines which are QP  lines include <em>sentences</em> enclosed in <code>( )</code
   * e.g.
   *   	ifActors  ( ( antibody ( *antibody ) true )
                    ( bacterium ( *bacterium ) true )
                    ( macrophage ( *macrophage ) true ) )
   * and
   *     ifRelations ( ( binds ( *antibody *bacterium )  true ) )
   *
   *
   * @param line
   */
  void parseSlot(String line) {
	  if (line.equals("")) return;
	  environment.logDebug("ParseSlot "+line); 
	if (workingCon == null) return; // somebody killed it
    if (line.startsWith("instanceOf"))
      parseInstanceOf(line.substring("instanceOf".length()));
    else if (line.startsWith("thenTerminate"))
      parseTerminate(line.substring("thenTerminate".length()));
    else if (line.startsWith("ifActors") ||
             line.startsWith("ifRelations") ||
             line.startsWith("ifStates") ||
             line.startsWith("ifNotActors") ||
             line.startsWith("ifNotRelations") ||
             line.startsWith("ifNotStates") ||
             line.startsWith("thenActors") ||
             line.startsWith("thenRelations") ||
             line.startsWith("thenStates") ||
             line.startsWith("actors") ||
             line.startsWith("relations") ||
             line.startsWith("states"))
      parseSentence(line);
    else if (line.startsWith("thenCreate"))
      parseCreate(line.substring("thenCreate".length()));
    else if (line.startsWith("thenSay"))
        parseSay(line.substring("thenSay".length()));
    else
      parseOtherSlot(line);
  }

  /**
   * If instanceOf, look for these and convert to
   * agendatask  --> task
   * episode     --> episode
   * SENSE.RULE  --> rule
   * process.rule--> rule
   * experiment --> ignore this
   * @param line
   */
  void parseInstanceOf(String line) {
    environment.logDebug("Parse instanceOf: "+workingCon.id+" | "+line);
    StringTokenizer toks = new StringTokenizer(line);
    String v;
    if (toks.hasMoreTokens()) {
      v = toks.nextToken();
      if (v.equals("experiment")) {
        throw new RuntimeException("Experiment in "+workingCon.id);
      } // filter out old popsim.rule
//      if (!v.equals("popsim.rule")) { popsim.rule removed
        if (v.equals("agendatask")) {
          workingCon.instanceOf = TASK;
        } else if (v.equals("episode"))
          workingCon.instanceOf = MODEL;
        else if (v.equals("sense.rule") ||
                 v.equals("process.rule"))
          workingCon.instanceOf = RULE;
        else
          workingCon.instanceOf = v;
//      }
    }
    System.out.println("ConInstance "+workingCon.toString()); // debug
  }


  void parseSay(String line) {
    System.out.println("ParseSay: "+line);
    String x = line.trim();
    if (x.startsWith("\""))
      x = x.substring(1).trim();
    if (x.endsWith("\""))
      x = x.substring(0,x.length()-1).trim();
    List l = new ArrayList();
    l.add(x);
    workingCon.addSlot("thenSay",l);
  }

  void parseCreate(String line) {
    System.out.println("ParseCreate: " + line);
    StringTokenizer toks = new StringTokenizer(line);
    String v; // a binding name
    List<Object> l = new ArrayList<Object>();
    while (toks.hasMoreTokens()) {
      v = toks.nextToken();
      if (!v.equals("(") && !v.equals(")"))
        l.add(v);
    }
    workingCon.addSlot("thenCreate",l);
  }

  void parseTerminate(String line) {
    System.out.println("ParseTerminate: "+line);
    List<Object> l = new ArrayList<Object>();
    l.add(line.trim()); // we don't care what it says
    workingCon.addSlot("thenTerminate",l);
  }

  /**
   * Other slots are any slots which are not sentence types
   * Watch for
   *   disjointFrom
   *   inverseRelation
   * @param line
   */
  void parseOtherSlot(String line) {
	  if (line.equals("")) return;
    environment.logDebug("ParseOtherSlot: "+line);
    StringTokenizer toks = new StringTokenizer(line);
    String slotName, inverseSlotName;
    String v;
    List vals = new ArrayList();
    //this dealing with inverseSlot deals ONLY at the slot definitions
    //When, later, some concept has such a slot, e.g. subOf, then
    //we need to ask if that slotType has an inverseSlot, and deal with it
    //at that time.
    if (toks.hasMoreTokens()) {
      slotName = toks.nextToken();
      if (slotName.equals("inverseSlot")) {
        // an inverseSlotType
        v = toks.nextToken();
        Con s = ConObject.get(v);
        if (s==null)
          s = new Con(v);
        vals.add(workingCon.id);
        s.addSlot("inverseSlot",vals);
        ConObject.put(v,s);
        vals = new ArrayList();
        vals.add(v);
      } else if (slotName.equals("synonym")) {
          v = toks.nextToken();
          Con s = ConObject.get(v);
          if (s==null)
            s = new Con(v);
          vals.add(workingCon.id);
          s.addSlot("synonym",vals);
          ConObject.put(v,s);
          vals = new ArrayList();
          vals.add(v);
      } else if (slotName.equals("priority")) {
    	  v = toks.nextToken();
    	  workingCon.setPriority(v);
      } else if (slotName.equals("subOf")) {
    	  while (toks.hasMoreTokens())
    	  	workingCon.addSlotValue("subOf", toks.nextToken());
      } else if (slotName.equals("instanceOf")) {
    	  while (toks.hasMoreTokens())
    		  workingCon.addSlotValue("subOf", toks.nextToken());
      } else {
    	  //TODO this is an odd slot, e.g. "abuts"
        while(toks.hasMoreTokens())
          vals.add(toks.nextToken());
      }
      workingCon.addSlot(slotName,vals);
    }
  }


  private String sentenceSlotType;
  private StringTokenizer sentenceTokenizer;
  /**
   * Second leftParen in a slot starts leftParen count.
   * Increment on leftParen, decrement on rightParen
   * Sentence ends when leftParenCount = 0
   */
  private int leftParenCount = 0;
  /**
   * Runs until out of sentences, then turns off isSentenceEnclosure
   *
   * Very sensitive to spaces between parens and other tokens.
   * e.g.
   *   Macrophage1) will blow this, should be Macrophage1 )
   * @param line
   */
  void parseSentence(String line) {
    System.out.println("ParseSentence: "+ leftParenCount +" "+line);
    sentenceTokenizer = new StringTokenizer(line);
    String token;
    if (!isSentence) {
      // get sentenceSlotType
      if (sentenceTokenizer.hasMoreTokens()) {
        sentenceSlotType = sentenceTokenizer.nextToken();
        System.out.println("SentenceSlotType: " + sentenceSlotType);
        // grab the starting paren
        token = sentenceTokenizer.nextToken();
        isSentence = true;
        sentences = new ArrayList<Object>();
      }
    }
    workingSentence = new Sentence();
    int innerCount = 0;
    boolean didSentence = false;
    while(sentenceTokenizer.hasMoreTokens()) {
      token = sentenceTokenizer.nextToken();
      System.out.println("    Token: "+leftParenCount+" "+token);
      if (token.equals("(")) {
        leftParenCount++;
      } else if (token.equals(")")) {
        if (leftParenCount == 0) {
          //condition of dangling closing paren
          System.out.println("SentenceSlotType2: " + sentenceSlotType);
          workingCon.addSlot(sentenceSlotType,sentences);
          sentences = null;
          isSentence = false;
          break;
        }
        leftParenCount--;
        if (leftParenCount == 0)
          break; // done with this sentence
      } else {
        didSentence = true;
        System.out.println("-Sentence: "+leftParenCount+" "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
        if (!token.equals("true") && token.equals("false")) {
                workingSentence.truth = false;
              System.out.println("--Sentence: "+leftParenCount+" "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
        } else if (leftParenCount == 1) {
          if (!token.equals("true"))
            workingSentence.predicate = token;
            System.out.println("---Sentence: "+leftParenCount+" "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
        } else if (++innerCount == 1) {
          workingSentence.subject = token;
          System.out.println("----Sentence: "+leftParenCount+" "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
        } else
          workingSentence.object = token;
        System.out.println("-----Sentence: "+leftParenCount+" "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
      }
    }
    if (didSentence)
      sentences.add(workingSentence);
    System.out.println("####Sentence: "+workingSentence.predicate+" "+workingSentence.subject+" "+workingSentence.object+" "+workingSentence.truth);
    if (sentenceTokenizer.hasMoreTokens()) {
      token = sentenceTokenizer.nextToken();
      isSentence = false;
      workingCon.addSlot(sentenceSlotType,sentences);
      sentences = null;
    }
    System.out.println(workingCon.toString());
//    System.out.println(" SentenceEnd: "+leftParenCount);
  }
  /**
   * Look for comment marks out in the line
   * @param line
   * @return
   */
  String cleanLine(String line) {
    String result = line;
    int where = result.indexOf("\\");
    if (where > -1)
      result = result.substring(0,where).trim();
    return result;
  }
  void parseKB(String fileName) {
    System.out.println("Parsing kb: "+fileName);
    File f = new File(directory+fileName);
      if (!f.exists())
        throw new RuntimeException("Can't find file "+directory+fileName);

    String line = handler.readFirstLine(f).trim();
    while (line != null) {
      parseLine(line);
      line = handler.readNextLine();
    }
  }

  ////////////////////////////////////////
  // inner class to make Legacy TSC concepts
  // built during parsing
  ////////////////////////////////////////
  class Con {
    String id;
    String instanceOf = "";
    String priority = null;
    //Some slots take strings, some take Sentence objects
    Map <String, List<Object>> slots= new HashMap<String, List<Object>>();

    public Con(String name) {
      id = name;
    }

    public void setPriority(String p) {
    	priority = p;
    }
    
    /**
     * 
     * @return can return {@code null}
     */
    public String getPriority() {
    	return priority;
    }
    public void addSlot(String key, List<Object> values) {
      List x = (List)slots.get(key);
      if (x == null)
        slots.put(key,values);
      else {
        int len = values.size();
        for (int i=0;i<len;i++)
          if (!x.contains(values.get(i)))
            x.add(values.get(i));
        slots.put(key,x);
      }
    }
    
    public void addSlotValue(String key, String val) {
        List x = (List)slots.get(key);
        if (x == null) x = new ArrayList();
        if (!x.contains(val))
        	x.add(val);
        slots.put(key,x);
    }

    public List getSlotValue(String key) {
      return (List)slots.get(key);
    }

    boolean hasSlotValue(String key, Object val) {
      List v = getSlotValue(key);
      if (v != null)
        return v.contains(val);
      return false;
    }
    public Iterator<String> listSlotNames() {
    	System.out.println("listing slots from "+id);
      return slots.keySet().iterator();
    }
    
    public Set<String> getSlotNames() {
    	return slots.keySet();
    }

    //debugging
    public String toString() {
      StringBuffer buf = new StringBuffer("c: "+id+"\n");
      if (!instanceOf.equals(""))
        buf.append("  instanceOf "+instanceOf+"\n");
      Iterator itr = listSlotNames();
      String n;
      List l;
      Sentence t;
      Object o;
      while (itr.hasNext()) {
        n = (String)itr.next();
        buf.append("  "+n+" ");
        l = getSlotValue(n);
        if (l != null) {
          int len = l.size();
          for (int i=0;i<len;i++) {
            o = l.get(i);
            if (o instanceof String)
              buf.append((String)l.get(i)+" ");
            else {
              t = (Sentence)o;
              buf.append("("+t.predicate+"("+t.subject);
              if (!t.object.equals(""))
                buf.append(" "+t.object);
              buf.append(")");
              if (t.truth)
                buf.append("true");
              else
                buf.append("false");
              buf.append(")");
            }
          }
//          buf.append("\n");
        }
        buf.append("\n");
      }
      return buf.toString();
    }
  }
}
