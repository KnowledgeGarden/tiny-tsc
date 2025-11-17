/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
//import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.nex.tinytsc.engine.Model;
import org.nex.tinytsc.engine.Episode;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.LinkManager;
import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.api.Identifiable;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.engine.Task;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class TreePanel extends JPanel {
  private TreeListener listener;
  private Environment environment;
  private Model initialModel;
  private Concept rootConcept;
  private LinkManager taxManager;
  /**
   * true when a Model, false when Ontology
   */
  private boolean isModel = false;
  /**
   * Slave trees don't open the ConceptEditor
   */
  boolean isSlaveTree = false;
  private ConceptTreeNode root;
  private DefaultTreeModel treeModel;
  private JPopupMenu conceptMenu = new JPopupMenu();
  private JPopupMenu modelMenu = new JPopupMenu();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTree conceptTree = new JTree();
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton refreshButton = new JButton();
  JMenuItem newSubOfItem = new JMenuItem();
  JMenuItem newInstanceOfItem = new JMenuItem();
  JMenuItem xmlItem = new JMenuItem();
  JMenuItem newRuleItem = new JMenuItem();
  JMenuItem newModelItem = new JMenuItem();
  JMenuItem newTaskItem = new JMenuItem();

  public TreePanel(Environment env) {
	  environment = env;
    try {
      jbInit();
      conceptTree.setCellRenderer(new ConceptTreeCellRenderer());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void setListener(TreeListener l) {
    this.listener = l;
	taxManager = environment.getTaxonomyManager();
  }

  public void setIsSlaveTree(boolean t) {
    isSlaveTree = t;
  }

  private void jbInit() throws Exception {
	  System.out.println("TreePanelInit "+environment);
	  taxManager = environment.getTaxonomyManager();
    this.setLayout(borderLayout1);
    conceptTree.addMouseListener(new TreePanel_conceptTree_mouseAdapter(this));
    jPanel1.setLayout(flowLayout1);
    flowLayout1.setAlignment(FlowLayout.RIGHT);
    refreshButton.setBorder(BorderFactory.createRaisedBevelBorder());
    refreshButton.setToolTipText("Refresh the tree");
    refreshButton.setText(" Refresh ");
    refreshButton.addActionListener(new TreePanel_refreshButton_actionAdapter(this));
    newSubOfItem.setText("New SubOf...");
    newSubOfItem.addActionListener(new TreePanel_newSubOfItem_actionAdapter(this));
    newInstanceOfItem.setText("New InstanceOf Item...");
    newInstanceOfItem.addActionListener(new TreePanel_newInstanceOfItem_actionAdapter(this));
    xmlItem.setText("XML View");
    xmlItem.addActionListener(new TreePanel_xmlItem_actionAdapter(this));
    newRuleItem.setText("New ProcessRule...");
    newRuleItem.addActionListener(new TreePanel_newRuleItem_actionAdapter(this));
    newModelItem.setText("New Model...");
    newModelItem.addActionListener(new TreePanel_newModelItem_actionAdapter(this));
    newTaskItem.setText("New Task...");
    newTaskItem.addActionListener(new TreePanel_newTaskItem_actionAdapter(this));
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(refreshButton, null);
    jScrollPane1.getViewport().add(conceptTree, null);
    conceptMenu.add(newSubOfItem);
    conceptMenu.add(newInstanceOfItem);
    conceptMenu.addSeparator();
    conceptMenu.add(xmlItem);
    conceptMenu.addSeparator();
    conceptMenu.add(newRuleItem);
    conceptMenu.add(newModelItem);
    conceptMenu.add(newTaskItem);
  }
  ////////////////////////////////////////////
  // Using JTree for Model presentation
  ////////////////////////////////////////////
  /**
   * Expand the <code>JTree<code> from that <code>Model</code>
   * @param model
   */
  public void setModel(Model m) throws Exception {
    isModel = true;
    this.initialModel = m;
    createModelRootNode(m);
    expandRoot();
  }

  ////////////////////////////////////////////
  // Using JTree for Ontology presentation
  ////////////////////////////////////////////
  /**
   * The root <code>Concept</code> is <code>Root</code>
   * @param c
   */
  public void setRootConcept(Concept c) throws Exception {
    System.out.println("TreePanel setting root "+c.getId());
    isModel = false;
    this.rootConcept = c;
    createConceptRootNode(c);
    expandRoot();
  }

  ////////////////////////////////////////////
  // Support code
  ////////////////////////////////////////////
  void createConceptRootNode(Concept con) throws Exception { 
    int type = IConstants.CONCEPT;
    
    this.root = new ConceptTreeNode(con.getId(),type);
    taxManager.populateConceptTreeRoot(con, root);
    this.treeModel = new DefaultTreeModel(root);
    this.conceptTree.setModel(treeModel);
  }

  void createModelRootNode(Model con) throws Exception { 
	    int type = IConstants.MODEL;
	    
	    this.root = new ConceptTreeNode(con.getId(),type);
	    taxManager.populateModelTreeRoot(con, root);
	    this.treeModel = new DefaultTreeModel(root);
	    this.conceptTree.setModel(treeModel);
	  }

  ConceptTreeNode createNode(String id, int type) {
    return new ConceptTreeNode(id, type);
  }
  /**
   * Balloon the tree
   */
  void expandRoot() {
    if (isModel)
      expandModel();
    else
      expandOntology();
  }

  /**
   * Expand on <code>nextEpisode</code>
   */
  void expandModel() {
    expandEpisode(this.initialModel, this.root);
    int ct = root.getChildCount();
    if (ct > 0) {
    	TreePath p = new TreePath(root);
    	this.conceptTree.expandPath(p);
    }
  }

  void expandEpisode(Episode ep, ConceptTreeNode parent) {
  	if (ep == null) return;
  	System.out.println("Expanding ep "+ep.getId());
  	ConceptTreeNode child;
  	Episode kid;
  	Iterator itr = ep.listNextEpisodeIds();
  	String n;
  	while (itr.hasNext()) {
  		n = (String)itr.next();
  		System.out.println("  Ep child "+n);
  		child = createNode(n, IConstants.EPISODE);
  		parent.add(child);
  		kid = environment.getEpisode(n);
  		if (kid != null)
  			expandEpisode(kid,child);
  		else {
  			System.out.println("Can't find Episode: "+n);
  			//TODO error?
  		}
  	}
  }
  /**
   * Expand along <code>hasSubs</code> and <code>hasInstances</code>
   */
  void expandOntology() {
    expandConcept(this.rootConcept, this.root);
    int ct = root.getChildCount();
    System.out.println("Root has children "+ct);
    if (ct > 0) {
    	TreePath p = new TreePath(root);
    	this.conceptTree.expandPath(p);
    }
  }

  /**
   * If parent has a <code>hasSubs</code> child, return it
   * Otherwise, make a new one, add it, and return it
   * @param parent
   * @return
   */
  ConceptTreeNode getHasSubsNode(ConceptTreeNode parent) {
  	ConceptTreeNode result = null;
  	Enumeration v = parent.children();
  	ConceptTreeNode temp;
  	if (v != null) {
  		while (v.hasMoreElements()) {
  			temp = (ConceptTreeNode)v.nextElement();
  			if ("hasSubs".equals((String)temp.getUserObject())) {
  				result = temp;
  				break;
  			}
  		}
  	}
  	if (result == null) {
  		result = new ConceptTreeNode("hasSubs", IConstants.UNUSED);
  		parent.add(result);
  	}
  	return result;
  }

  /**
   * If parent has a <code>hasInstances</code> child, return it
   * Otherwise, make a new one, add it, and return it
   * @param parent
   * @return
   */
  ConceptTreeNode getHasInstancesNode(ConceptTreeNode parent) {
    System.out.println("Getting hasInstance on "+parent.getUserObject());
  	ConceptTreeNode result = null;
  	Enumeration v = parent.children();
  	ConceptTreeNode temp;
  	if (v != null) {
  		while (v.hasMoreElements()) {
  			temp = (ConceptTreeNode)v.nextElement();
  			if ("hasInstances".equals((String)temp.getUserObject())) {
  				result = temp;
  				break;
  			}
  		}
  	}
  	if (result == null) {
  		result = new ConceptTreeNode("hasInstances",IConstants.UNUSED);
  		parent.add(result);
  	}
  	return result;
  }

  /**
   * We are deliberately <em>not</em> expanding on
   * <li><code>Episode</code></li>
   * <li><code>Model</code></li>
   * <li><code>ProcessRule</code></li>
   * <li><code>Task</code></li>
   * We do present <code>Model</code>s
   * We presume that the Concept passed here is represented
   * as the <code>parent</code>. We only expand on the
   * <code>hasSubs</code> and <code>hasInstances</code>
   * of the <code>Concept</code>
   * @param c
   * @param parent
   */
  void expandConcept(Identifiable c, ConceptTreeNode parent) {
    ConceptTreeNode child, prop, prop2, child2;
    Identifiable kid, kid2;
    Model mKid, mConcept;
    ProcessRule rKid, rConcept = null;
    Task tKid, tConcept = null;
    Concept cConcept = null;
    int type = c.getNodeType();
    switch (type) {
      case IConstants.CONCEPT:
        cConcept = (Concept) c;
        break;
      case IConstants.MODEL:
        mConcept = (Model) c;
        break;
      case IConstants.PROCESS_RULE:
        rConcept = (ProcessRule) c;
        break;
      case IConstants.TASK:
        tConcept = (Task) c;
        break;
        //ignore Episode
    }
    String n;
    int len, len2;
    //////////////////////////////////////////
    // Begin chasing the hasSubs branch
    //////////////////////////////////////////
    java.util.List v2, v;
    if (type == IConstants.CONCEPT) {
      v = (java.util.List) cConcept.getProperty("hasSubs");
      System.out.println("Getting hasSubs on: " + c.getId() + " " + v);
      if (v != null) { //IF Have Subs
        // put the subs and instances as tree nodes into parent
        // and recurse on each of those
        prop = getHasSubsNode(parent);
        len = v.size();
        // for all the subs
        for (int i = 0; i < len; i++) {
          n = (String) v.get(i);
          kid = environment.getConcept(n);
          //if that returns null, look for Task, ProcessRule, etc
          if (kid != null) { // if kid 1
            // else it's a leaf node
            child = createNode(kid.getId(), IConstants.CONCEPT);
            prop.add(child);
            v2 = (java.util.List) ( (Concept) kid).getProperty("hasSubs");
            System.out.println("  Getting hasSubs on: " + kid.getId() + " " +
                               v2);
            if (v2 != null) {
              prop2 = getHasSubsNode(child);
              len2 = v2.size();
              for (int j = 0; j < len2; j++) {
                n = (String) v2.get(j);
                kid2 = environment.getConcept(n);
                if (kid2 != null) {
                  child2 = createNode(n,IConstants.CONCEPT);
                  prop2.add(child2);
                  //	            System.out.println("    Got sub on: "+kid.getId()+" "+n+" "+kid2);
                  expandConcept( (Concept) kid2, child2);
                }
                else
                  processNonConceptNodes(n, prop2);
              }
            }
            v2 = (java.util.List) ( (Concept) kid).getProperty("hasInstances");
            System.out.println("  Getting hasInstances on: " + kid.getId() +
                               " " + v2);
            if (v2 != null) {
              prop2 = getHasInstancesNode(child);
              len2 = v2.size();
              for (int j = 0; j < len2; j++) {
                n = (String) v2.get(j);
                kid2 = environment.getConcept(n);
                if (kid2 != null) {
                  child2 = createNode(n, IConstants.CONCEPT);
                  prop2.add(child2);
                  System.out.println("    Got instance on: "+kid.getId()+" "+n+" "+kid2);
                }
                else
                  processNonConceptNodes(n, prop2);
              }
            }
          }
          else
            processNonConceptNodes(n, prop);
        } // end for
      } //END IF Have Subs
      //////////////////////////////////////////
      // Begin chasing the hasInstances branch
      //////////////////////////////////////////
      v = (java.util.List) cConcept.getProperty("hasInstances");
      System.out.println("Getting hasInstances on: " + c.getId() + " " + v);
      if (v != null) {
        // put the subs and instances as tree nodes into parent
        // and recurse on each of those
        prop = getHasInstancesNode(parent);
        len = v.size();
        // for all the instances -- which don't have children
        // although, I'd bet, somebody will be dumb enough to
        // add children of one sort or another to instances
        // Hah! They're doomed :)
        for (int i = 0; i < len; i++) {
          // for each child that is an instance -- which don't expand
          n = (String) v.get(i);
          kid = environment.getConcept(n);
          //if that returns null, look for Task, ProcessRule, etc
          if (kid != null) {
            System.out.println("  Got Concept " + n);
            // else it's a leaf node
            child = createNode(kid.getId(),IConstants.CONCEPT);
            prop.add(child);
          }
          else
            processNonConceptNodes(n, prop);
        } // end for
      } // end if
    }
  }

  /**
   * These nodes are not instances of <code>Concept</code>
   * and <em>for the time being</em> do not expand
   * @param n
   * @param prop
   */
  void processNonConceptNodes(String n, ConceptTreeNode prop) {
    System.out.println("Processing "+n+" "+prop.getUserObject());
    Identifiable kid;
    ConceptTreeNode child;
    kid = environment.getModel(n);
    // models don't expand
    if (kid != null) {
      System.out.println("  Got Model " + n);
      child = createNode(n,IConstants.MODEL);
      prop.add(child);
    }
    else {
      kid = environment.getRule(n);
      System.out.println("  Got ProcessRule " + n);
      // rules don't expand
      if (kid != null) {
        child = createNode(n,IConstants.PROCESS_RULE);
        prop.add(child);
      }
      else {
        kid = environment.getTask(n);
        System.out.println("  Got Task " + n);
        // tasks don't expand
        if (kid != null) {
          child = createNode(n,IConstants.TASK);
          prop.add(child);
        }
        else {
          System.out.println("Cannot find this object: " + n);
          //TODO error?
        }
      }
    }
  }

  void conceptTree_mouseClicked(MouseEvent e) {
    int modifier = e.getModifiers();
    if((modifier & MouseEvent.BUTTON3_MASK) != 0){
        //show the popup menu
        try {
        conceptMenu.show((Component) e.getSource(), e.getX(), e.getY());
        } catch (Exception es) {
          System.out.println("TreeView popup error: " + es.getMessage());
        }
    }
    if (!isSlaveTree) {
      int count = e.getClickCount();
      if (count > 1) {
        ConceptTreeNode node = getSelectedNode();
        if (node != null)
          listener.displaySelection( (String) node.getUserObject());
      }
    }
  }

  void refreshButton_actionPerformed(ActionEvent e) {
    System.out.println("Refreshing Tree");
    try {
	    if (!isModel) {
	      Concept c = environment.getConcept("root");
	      if (c != null)
	        this.setRootConcept(c);
	    } else {
	    	if (this.initialModel != null)
	    		setModel(environment.getModel(initialModel.getId()));
	    }
    } catch (Exception ex) {
    	environment.logError(ex.getMessage(), ex);
    }
  }

  void newSubOfItem_actionPerformed(ActionEvent e) {
    ConceptTreeNode node = getSelectedNode();
    if (node != null)
      listener.newConceptSubOf((String)node.getUserObject());
  }

  void newInstanceOfItem_actionPerformed(ActionEvent e) {
    ConceptTreeNode node = getSelectedNode();
    if (node != null)
      listener.newConceptInstanceOf((String)node.getUserObject());
  }

  ConceptTreeNode getSelectedNode() {
    ConceptTreeNode result = null;
    TreePath path = this.conceptTree.getAnchorSelectionPath();
    if (path != null) {
      Object obj = path.getLastPathComponent();
      if (obj != null)
        result = (ConceptTreeNode) obj;
      System.out.println("Tree got " + result.getUserObject());
    } else {
      JOptionPane.showMessageDialog(null,"No Tree Node Selected");
    }
    return result;
  }

  public String getSelectedNodeId() {
    ConceptTreeNode result = getSelectedNode();
    if (result == null)
      return null;
    return (String)result.getUserObject();
  }

  void xmlItem_actionPerformed(ActionEvent e) {

  }

  void newModelItem_actionPerformed(ActionEvent e) {
    ConceptTreeNode node = getSelectedNode();
    if (node != null)
      listener.newModel((String)node.getUserObject());
  }

  void newRuleItem_actionPerformed(ActionEvent e) {
    ConceptTreeNode node = getSelectedNode();
    if (node != null)
      listener.newRule((String)node.getUserObject());
  }

  void newTaskItem_actionPerformed(ActionEvent e) {
    ConceptTreeNode node = getSelectedNode();
    if (node != null)
      listener.newTask((String)node.getUserObject());
  }


}

class TreePanel_conceptTree_mouseAdapter extends java.awt.event.MouseAdapter {
  TreePanel adaptee;

  TreePanel_conceptTree_mouseAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.conceptTree_mouseClicked(e);
  }
}

class TreePanel_refreshButton_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_refreshButton_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.refreshButton_actionPerformed(e);
  }
}

class TreePanel_newSubOfItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_newSubOfItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newSubOfItem_actionPerformed(e);
  }
}

class TreePanel_newInstanceOfItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_newInstanceOfItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newInstanceOfItem_actionPerformed(e);
  }
}

class TreePanel_xmlItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_xmlItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.xmlItem_actionPerformed(e);
  }
}

class TreePanel_newModelItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_newModelItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newModelItem_actionPerformed(e);
  }
}

class TreePanel_newRuleItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_newRuleItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newRuleItem_actionPerformed(e);
  }
}

class TreePanel_newTaskItem_actionAdapter implements java.awt.event.ActionListener {
  TreePanel adaptee;

  TreePanel_newTaskItem_actionAdapter(TreePanel adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.newTaskItem_actionPerformed(e);
  }
}