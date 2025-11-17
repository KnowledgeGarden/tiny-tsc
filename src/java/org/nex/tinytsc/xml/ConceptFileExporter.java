/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.xml;
import java.io.*;
import java.util.*;

import org.nex.persist.text.TextFileHandler;
import org.nex.tinytsc.engine.*;
import org.nex.tinytsc.database.JDBMDatabase;
import org.nex.tinytsc.DatastoreException;
import org.nex.tinytsc.api.IExporterListener;

import jdbm.helper.Tuple;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */
public class ConceptFileExporter {
	/**
	 * Since exporting is threaded, we notify the host when done.
	 * This facilitates coordination of multiple exports.
	 * An alternative is to create multiple instances of
	 * <code>ConceptFileExporter</code>
	 */
	private IExporterListener host;
	private Environment environment;
	private TextFileHandler handler = new TextFileHandler();
        private PrintWriter out;
        private JDBMDatabase database;

	/**
	 * Constructed on an as-needed basis
	 * @param environment
	 * @param host can be <code>null</code>
	 */
	public ConceptFileExporter(Environment e, IExporterListener h) {
		super();
		environment = e;
		host = h;
                database = e.getDatabase();
	}

        /**
         *
         * @param root Concept
         * @param isExpand boolean if <code>true</code> expand taxonomy
         */
        public void export(Concept root, boolean isExpand) {
		new ConceptExporter(root);
	}

        /**
         * Will export either {@link Episode} or {@link Model}
         * @param root Episode
         * @param isExpand boolean if <code>true</code> expand envisionment
         */
        public void export(Episode root, boolean isExpand) {
		new ModelExporter(root, isExpand);
	}

        public void exportRulesComplete(List rules) {
          if (rules != null && rules.size() > 0) {
            startXML();
            Iterator itr = rules.iterator();
            while (itr.hasNext())
              out.print(((ProcessRule)itr.next()).toXML());
            finishXML();
          }
        }

        /**
         * <p>
         * For use where other elements are being exported,
         * User must call startXML and finishXML
         * </p>
         * @param rules List
         */
        public void exportRules(List rules) {
          if (rules != null && rules.size() > 0) {
            Iterator itr = rules.iterator();
            while (itr.hasNext())
              out.print(((ProcessRule)itr.next()).toXML());
          }
        }
        public void export(ProcessRule rule) {
          //TODO
        }

        public void exportTasksComplete(List tasks) {
          if (tasks != null && tasks.size() > 0) {
            startXML();
            Iterator itr = tasks.iterator();
            while (itr.hasNext())
              out.print(((Task)itr.next()).toXML());
            finishXML();
          }
        }
        public void exportTasks(List tasks) {
          if (tasks != null && tasks.size() > 0) {
            Iterator itr = tasks.iterator();
            while (itr.hasNext())
              out.print(((Task)itr.next()).toXML());
          }
        }
        public void export(Task task) {
          //TODO
        }

	class ModelExporter extends Thread {
		Episode root;
                boolean expand;

		public ModelExporter(Episode r, boolean isExpand) {
			root = r;
                        expand = isExpand;
			start();
		}

		public void run() {
                  out = saveAs();
                  if (out != null)
                    processModels();
                  if (host != null)
                    host.exportDone();
		}

                void processModels() {
                  startXML();
                  //TODO ripple down episodes
                  finishXML();
                }
	}

	class ConceptExporter extends Thread {
		Concept root;

		public ConceptExporter(Concept r) {
			root = r;
			start();
		}

		public void run() {
                  out = saveAs();
                  if (out != null)
                    processConcepts();
                  if (host != null)
                    host.exportDone();
		}
                void processConcepts() {
                  startXML();
                  //TODO ripple down objects
                  finishXML();
                }
	}

        public void exportDatabase() {
          out = saveAs();
          if (out != null)
            new DatabaseExporter();

        }

        class DatabaseExporter extends Thread {

          public DatabaseExporter() {
            start();
          }
          public void run() {
            startXML();
            try {
              //Concepts
              Tuple t = new Tuple();
              String key;
              database.startConceptsIterator();
              System.out.println("Exporting Concepts");
              while (database.getNextObject(t)) {
                key = (String)t.getKey();
                out.print(database.getConcept(key).toXML());
              }
              //Rules
              database.startRulesIterator();
              System.out.println("Exporting Rules");
              while (database.getNextObject(t)) {
                key = (String)t.getKey();
                out.print(database.getRule(key).toXML());
              }
              //Models
              database.startModelsIterator();
              System.out.println("Exporting Models");
              while (database.getNextObject(t)) {
                key = (String)t.getKey();
                out.print(database.getModel(key).toXML());
              }
              //Episodes
              database.startEpisodesIterator();
              System.out.println("Exporting Episodes");
              while (database.getNextObject(t)) {
                key = (String)t.getKey();
                out.print(database.getEpisode(key).toXML());
              }
              //Tasks
              database.startTasksIterator();
              System.out.println("Exporting Tasks");
              while (database.getNextObject(t)) {
                key = (String)t.getKey();
                out.print(database.getTask(key).toXML());
              }
            } catch (DatastoreException e) {
              e.printStackTrace();
            }
            finishXML();
          }
        }
        public void startXML() {
          if (out != null) {
            System.out.println("Starting Export");
            out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.print("<database>\n");
          }
        }
        /**
         * Ends saving the export
         */
        public void finishXML() {
          if (out != null) {
            System.out.println("Ending Export");
            out.print("</database>\n");
            try {
              out.flush();
              out.close();
            }
            catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
        /**
         * <p>
         * Get a File to save into, then return a PrintWriter for that file
         * </p>
         * @return PrintWriter
         */
        PrintWriter saveAs() {
          File f = handler._saveAs();
          if (f != null) {
            try {
              FileOutputStream fos = new FileOutputStream(f);
              BufferedOutputStream bos = new BufferedOutputStream(fos);
              PrintWriter pw = new PrintWriter(bos);
              return pw;
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
          return null;
        }
}
