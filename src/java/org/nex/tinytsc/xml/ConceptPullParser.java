/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.xml;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Task;
import org.nex.tinytsc.engine.Episode;
import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.engine.Model;
import org.nex.tinytsc.engine.Sentence;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.DatastoreException;
import org.nex.tinytsc.api.IConstants;
//xpp.jar
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


//log4j.jar
import org.apache.log4j.Logger;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */
public class ConceptPullParser {
        private Environment environment;
        private Concept theConcept;
        private Model theModel;
        private Episode theEpisode;
        private ProcessRule theRule;
        private Task theTask;
        private Sentence theSentence;

    public Concept getConcept() {
    	return theConcept;
    }
    public Model getModel() {
    	return theModel;
    }
    public Episode getEpisode() {
    	return theEpisode;
    }
    public Task getTask() {
    	return theTask;
    }
    public ProcessRule getRule() {
    	return theRule;
    }
	/**
	 *
	 */
	public ConceptPullParser(Environment e) {
		super();
		environment = e;
	}

	public  void parse(File inFile) {
	    try {

	      FileInputStream is = new FileInputStream(inFile);
	      BufferedReader in = new BufferedReader(new InputStreamReader(is));
	      parse(in);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	environment.logError(e.getMessage(), e);
	    }
	}
	
	public void parse(String xml) {
		environment.logDebug("Parsing\n"+xml);
		//initialize
		//theConcept = null; theRule = null; theEpisode = null; theTask = null; theModel = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
		    BufferedReader in = new BufferedReader(new InputStreamReader(bis));
		    parse(in);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	environment.logError(e.getMessage(), e);
	    }
	}
	
	public void parse(Reader in) throws Exception {
	      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	      factory.setNamespaceAware(false);
	      XmlPullParser xpp = factory.newPullParser();
	      xpp.setInput(in);
	      String temp = null;
	      String text = null;
	      String name = null;
          String otherSlotName = null;
	      String value = null;
          String id = null;
          String ruleId = null;
          String nodeId = null;
	      Map<String, Object> attributes = null;
	      List<Object> theList = null;
	      boolean isList = false;
          boolean isSlot = false;
          boolean isModel = false;
          boolean isRule = false;
          boolean isEpisode = false;
          boolean isNextEpisode = false;
          boolean isPreviousEpisode = false;
          boolean isTask = false;
          boolean isConcept = false;
          boolean isThenSay = false;
          boolean isIfActors = false;
          boolean isIfNotActors = false;
          boolean isIfRelations = false;
          boolean isIfNotRelations = false;
          boolean isIfStates = false;
          boolean isIfNotStates = false;
          boolean isThenActors = false;
          boolean isThenRelates = false;
          boolean isThenStates = false;
          boolean isThenCreate = false;
          boolean isThenConjecture = false;
          boolean isInstanceOf = false; // priviledged slot
          boolean isTransitiveClosure = false;
          boolean isRules = false;
          boolean isEpisodes = false;
          //isOther means just put the slot name/List value pair into the concept
          // as a property key/value pair
          boolean isOtherSlot = false;
          StringTokenizer tok = null;
	      int eventType = xpp.getEventType();
	      environment.logDebug("EVENT "+eventType);      
	      boolean isStop = false;
	      while (! (isStop || eventType == XmlPullParser.END_DOCUMENT)) {

	        if (eventType == XmlPullParser.START_DOCUMENT) {
	          System.out.println("Start document");
	        }
	        else if (eventType == XmlPullParser.END_DOCUMENT) {
	          System.out.println("End document");
	        }
	        else if (eventType == XmlPullParser.START_TAG) {
		        temp = xpp.getName();
		        environment.logDebug("XXX "+temp);
		        attributes = getAttributes(xpp);
		        if (attributes != null) {
		          name = (String) attributes.get(IConstants._NAME);
		          value = (String) attributes.get(IConstants._VALUE);
	              id = (String) attributes.get("id");
		        } else {
		          name = null;
		          value = null;
		        }
	        	System.out.println("Start tag " + temp);
		        environment.logDebug("Start tag " + temp+" | "+name);
		        if (temp.equalsIgnoreCase(IConstants._CONCEPT)) {
	                    isConcept = true;
	                    // make a new Concept
	                    theConcept = new Concept(id);
	            } else if (temp.equalsIgnoreCase(IConstants._RULE)) {
	            	  // make a new ProcessRule
	                theRule = new ProcessRule(id);
	                isRule = true;
	            } else if (temp.equalsIgnoreCase(IConstants._EPISODE)) {
	            	  // make a new Episode
	                theEpisode = new Episode(id);
	                isEpisode = true;
	            } else if (temp.equalsIgnoreCase(IConstants._MODEL)) {
	            	  // make a new Model
	                theModel = new Model(id);
	                isModel = true;
	            } else if (temp.equalsIgnoreCase(IConstants._TASK)) {
	            	  // make a new Task
	                theTask = new Task(id);
	                isTask = true;
	                environment.logDebug("NewTask "+theTask);
	            } else if (temp.equalsIgnoreCase(IConstants._SLOT)) {
	                isSlot = true;
	                //we already have the slot name
	                //There is an issue here:
	                // if it's one of the non-slot slots, e.g. hasInstances, sentence, etc,
	                // we don't make a new slot.
	                // otherwise we do, but we do that JustInTime
	                environment.logDebug("GotSlot "+name);
	                if (name != null && name.equals(IConstants._IF_ACTORS) || name != null && name.equals(IConstants._ACTORS))
		                  isIfActors = true;
		            else if (name != null && name.equals(IConstants._IF_NOT_ACTORS))
		                  isIfNotActors = true;
		            else if (name != null && name.equals(IConstants._IF_RELATIONS) ||
		            		name != null && name.equals(IConstants._RELATIONS))
		                  isIfRelations = true;
		            else if (name != null && name.equals(IConstants._IF_NOT_RELATIONS))
		                  isIfNotRelations = true;
		            else if (name != null && name.equals(IConstants._IF_STATES) || name != null && name.equals(IConstants._STATES))
		                  isIfStates = true;
		            else if (name != null && name.equals(IConstants._IF_NOT_STATES))
		                  isIfNotStates = true;
		            else if (name != null && name.equals(IConstants._THEN_CREATE))
		                  isThenCreate = true;
		            else if (name != null && name.equals(IConstants._THEN_ACTORS))
		                  isThenActors = true;
		            else if (name != null && name.equals(IConstants._THEN_RELATIONS))
		                  isThenRelates = true;
		            else if (name != null && name.equals(IConstants._THEN_STATES))
		                  isThenStates = true;
		            else if (name != null && name.equals(IConstants._THEN_SAY))
		                  isThenSay = true;
		            else if (name != null && name.equals(IConstants._THEN_CONJECTURE))
		                  isThenConjecture = true;
		            else if (name != null && name.equals(IConstants.INSTANCE_OF))
		                  isInstanceOf = true;
		            else if (name != null && name.equals(IConstants.TRANSITIVE_CLOSURE))
		                	isTransitiveClosure = true;
		            else if (name != null && name.equals(IConstants.RULES))
		                	isRules = true;
		            else if (name != null && name.equals(IConstants.EPISODES))
		                	isEpisodes = true;
		            else if (name != null) {
		                  isOtherSlot = true;
		                  otherSlotName = name;
		            }
	            } else if (temp.equalsIgnoreCase(IConstants._SENTENCE)) {
	            	  // make a new Sentence
	                theSentence = new Sentence();
	                environment.logDebug("NewSentence");
	            }
	        }
	        else if (eventType == XmlPullParser.END_TAG) {
		        temp = xpp.getName();
	          environment.logDebug("End tag " + temp + " // " + text+
        			  isIfActors+" "+isIfRelations+" "+isIfStates);
              if (temp.equalsIgnoreCase(IConstants._CONCEPT)) {
                isConcept = false;
  
              } else if (temp.equalsIgnoreCase(IConstants._RULE)) {
                isRule = false;
                
                isInstanceOf = false;
                isOtherSlot = false;
              } else if (temp.equalsIgnoreCase(IConstants._EPISODE)) {
                isEpisode = false;
              
                isInstanceOf = false;
              } else if (temp.equalsIgnoreCase(IConstants._MODEL)) {
            	  isModel = false;              
              } else if (temp.equalsIgnoreCase(IConstants._TASK)) {
                isTask = false;
                //This marks the end of a parse of an object

               /* try {
                  System.out.println(theTask.toXML());
                  environment.importTask(theTask);
                } catch (DatastoreException x) {
                  throw new RuntimeException(x);
                }
                theTask = null;*/
              } else if (temp.equals(IConstants.INSTANCE_OF)) {
            	  if (isConcept)
            		  theConcept.setInstanceOf(text);
            	  else if (isTask)
            		  theTask.setInstanceOf(text);
            	  else if (isEpisode)
            		  theEpisode.setInstanceOf(text);
            	  else if (isModel)
            		  theModel.setInstanceOf(text);
            	  else if (isRule)
            		  theRule.setInstanceOf(text);
            	  
              } else if (temp.equals(IConstants.TRANSITIVE_CLOSURE)) {
                  	isTransitiveClosure = false;
                    tok = new StringTokenizer(text);
                    while (tok.hasMoreTokens())
                    	theConcept.addTransitiveClosure(tok.nextToken());
              } else if (temp.equals(IConstants.RULES))
                  	isRules = true;
              else if (temp.equals(IConstants.EPISODES)) {
                  	isEpisodes = true;
              } else if (temp.equals(IConstants.SUB_OF)) {
            	  environment.logDebug("SUBOF "+text);
                  tok = new StringTokenizer(text);
                  while (tok.hasMoreTokens())
                  	theConcept.addSubOf(tok.nextToken());

              } else if (temp.equals(IConstants.HAS_SUBS)) {
                  tok = new StringTokenizer(text);
                  while (tok.hasMoreTokens())
                  	theConcept.addSubClass(tok.nextToken());

              } else if (temp
            		  .equals(IConstants.HAS_INSTANCES)) {
                  tok = new StringTokenizer(text);
                  while (tok.hasMoreTokens())
                  	theConcept.addInstance(tok.nextToken());

              } else if (temp.equalsIgnoreCase(IConstants._SLOT)) {
                isSlot = false;
                // now deal with the slot
                if (isOtherSlot) {
                  //these are not qp slots
                  if (isConcept)
                    theConcept.putProperty(otherSlotName,theList);
                }  //qp slots were dealt with in their sentences
                isIfActors = false;
                isIfNotActors = false;
                isIfRelations = false;
	            isIfNotRelations = false;
	            isIfStates = false;
	            isIfNotStates = false;
	            isThenCreate = false;
	            isThenActors = false;
	            isThenRelates = false;
	            isThenStates = false;
	            isThenSay = false;
	            isThenConjecture = false;
	            isOtherSlot = false;
              } else if (temp.equalsIgnoreCase(IConstants._SENTENCE)) {
            	  environment.logDebug("SENT+ "+theSentence.toXML()+
            			  isIfActors+" "+isIfRelations+" "+isIfStates);
                if (isModel) {
                  if (isIfActors)
                    theModel.addActor(theSentence);
                  else if (isIfRelations)
                    theModel.addRelation(theSentence);
                  else if (isIfStates)
                    theModel.addState(theSentence);
            	  environment.logDebug("SENT++ "+theModel.toXML());
            	  environment.logDebug("MODEL++ "+theModel.getActors()+" | "+
            			  theModel.getRelations()+" | "+
            			  theModel.getStates());
                } else if (isEpisode) {
                  if (isIfActors)
                    theEpisode.addActor(theSentence);
                  else if (isIfRelations)
                    theEpisode.addRelation(theSentence);
                  else if (isIfStates)
                    theEpisode.addState(theSentence);
            	  environment.logDebug("SENT++ "+theEpisode.toXML());
               } else if (isRule) {
                  if (isIfActors)
                    theRule.addIfActor(theSentence);
                  else if (isIfRelations)
                    theRule.addIfRelation(theSentence);
                  else if (isIfStates)
                    theRule.addIfState(theSentence);
                  else if (isIfNotActors)
                    theRule.addIfNotActor(theSentence);
                  else if (isIfNotRelations)
                    theRule.addIfNotRelation(theSentence);
                  else if (isIfNotStates)
                    theRule.addIfNotState(theSentence);
                  else if (isThenActors)
                   theRule.addThenActor(theSentence);
                 else if (isThenRelates)
                   theRule.addThenRelation(theSentence);
                 else if (isThenStates)
                   theRule.addThenState(theSentence);
                 else if (isThenConjecture)
                   theRule.addThenConjecture(theSentence);
                 else if (isThenSay) {
                   theRule.addThenSay(text);
                   isThenSay = false;
                 }
                  

            	 theSentence = null;
                } else if (temp.equalsIgnoreCase(IConstants._DATABASE)) {
                  //we're done
                  environment.finishImport();
                }
                

              } else if (temp.equalsIgnoreCase(IConstants._PREDICATE)) {
            	  // this is bad news
            	  // for some reason, the <sentence> tag is not firing
            	  if (theSentence == null)
            		  theSentence = new Sentence();
                theSentence.predicate = text;
              } else if (temp.equalsIgnoreCase(IConstants._SUBJECT)) {
                //might be more than one value
            	  environment.logDebug("SUBJECT! "+text+" "+theSentence);
                theSentence.subject = text;
                
              } else if (temp.equalsIgnoreCase(IConstants._TRUTH)) {
                boolean truth = text.equals("true");
                theSentence.truth = truth;
              } else if (temp.equalsIgnoreCase(IConstants._NEXT_EPIODE)) {
                isNextEpisode = false;
                environment.logDebug("ParseNextEpisode "+theModel+" | "+theEpisode+"\n"+
                		isModel+" "+ruleId+" "+nodeId);
                ;
                if (isModel) {
                	environment.logDebug("PRE "+theModel.toXML());
                  theModel.addNextEpisode(ruleId,nodeId);
              	environment.logDebug("POST "+theModel.toXML());
                } else if (isEpisode)
                  theEpisode.addNextEpisode(ruleId,nodeId);
                ruleId = nodeId = null;
              } else if (temp.equalsIgnoreCase(IConstants._PREVIOUS_EPISODE)) {
                isPreviousEpisode = false;
               if (isEpisode)
                  theEpisode.addPreviousEpisode(ruleId,nodeId);
                ruleId = nodeId = null;
              } else if (temp.equalsIgnoreCase(IConstants._MECHANISM)) {
                ruleId = text;
              } else if (temp.equalsIgnoreCase(IConstants._NODE)) {
                nodeId = text;
              } else if (temp.equalsIgnoreCase(IConstants._TYPE)) {
                theTask.setTaskType(text);
              } else if (temp.equalsIgnoreCase(IConstants._OBJECT)) {
            	  theSentence.object = text;
            	  environment.logDebug("OBJECT! "+text+" "+theSentence);
              } else if (temp.equalsIgnoreCase(IConstants._OBJECT_B)) {
            	  environment.logDebug("ObjectB! "+text);
            	  theSentence.objectB = text;
              } else if (temp.equalsIgnoreCase(IConstants._EXPERIMENT)) {
                Model m = environment.getModel(text);
                //could be null!!!!
                environment.logDebug("ProcessingTask "+m+" | "+theTask);
                if (theTask != null)
                	theTask.setModel(m);
              } else if (temp.equalsIgnoreCase(IConstants._VALUE)) {

                //value might have a list of space-delimited symbols
                tok = new StringTokenizer(text);
                theList = new ArrayList<Object>();
                while (tok.hasMoreTokens())
                  theList.add(tok.nextToken());
              } else if (temp.equalsIgnoreCase(IConstants._MY_MECHANISM)) {
                theEpisode.setMechanism(text);
              } else if (temp.equalsIgnoreCase(IConstants._NAME)) {
                if (isConcept)
                  theConcept.setName(text);
                else if (isEpisode)
                  theEpisode.setName(text);
                else if (isModel)
                  theModel.setName(text);
                else if (isRule)
                  theRule.setName(text);
              } else if (temp.equalsIgnoreCase(IConstants._COMMENT)) {
                if (isConcept)
                  theConcept.setComment(text);
                else if (isEpisode)
                  theEpisode.setComment(text);
                else if (isModel)
                  theModel.setComment(text);
                else if (isRule)
                  theRule.setComment(text);
              } else if (temp.equalsIgnoreCase(IConstants._PRIORITY)) {
            	  environment.logDebug("Priority "+text);
            	  if (!text.equals("")) {
            		  int prx = Integer.parseInt(text);
            		  theTask.setPriority(prx);
            	  }
              }

              
	        }
	        else if (eventType == XmlPullParser.TEXT) {
	                environment.logDebug("Text "+id+" // "+xpp.getText());
	          text = xpp.getText().trim();
	        }
	        else if (eventType == XmlPullParser.CDSECT) {
	          text = xpp.getText().trim();
	        }
	        eventType = xpp.next();
	        environment.logDebug("EVENT1 "+eventType); 
	      } //end while
	    
	  }

	  /**
	   * Return null if no attributes
	   */
	  Map<String, Object> getAttributes(XmlPullParser p) {
	    Map<String, Object> result = null;
	    int count = p.getAttributeCount();
	    if (count > 0) {
	      result = new HashMap<String, Object>();
	      String name = null;
	      for (int i = 0; i < count; i++) {
	        name = p.getAttributeName(i);
	        result.put(name, p.getAttributeValue(i));
	      }
	    }
	    return result;
	  }

}
