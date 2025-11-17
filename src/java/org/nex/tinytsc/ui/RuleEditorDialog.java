/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.ui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Environment;
import org.nex.tinytsc.engine.ProcessRule;
import org.nex.tinytsc.DatastoreException;

/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 */

public class RuleEditorDialog extends JDialog {
	Environment environment;
	boolean isEditable = false;
	String instanceParentId;
  BorderLayout borderLayout1 = new BorderLayout();
  RuleEditorPanel ruleEditorPanel1;
  JPanel jPanel1 = new JPanel();
  FlowLayout flowLayout1 = new FlowLayout();
  JLabel jLabel1 = new JLabel();
  JTextField idField = new JTextField();
  JPanel jPanel2 = new JPanel();
  FlowLayout flowLayout2 = new FlowLayout();
  JButton cancelButton = new JButton();
  JButton okButton = new JButton();

  public RuleEditorDialog(Environment env) throws HeadlessException {
	  environment = env;
    try {
    	ruleEditorPanel1 = new RuleEditorPanel(environment);
      jbInit();
      ruleEditorPanel1.setHost(this);

    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public void setListener(TreeListener e) {
    ruleEditorPanel1.setListener(e);
  }

  public void setRootConcept(Concept c) {
    ruleEditorPanel1.setRootConcept(c);
  }

  private void jbInit() throws Exception {
    this.setTitle("ProcessRule Editor");
    this.getContentPane().setLayout(borderLayout1);
    this.setSize(800,800);
    jPanel1.setLayout(flowLayout1);
    jLabel1.setText("ProcessRule Identity: ");
    idField.setPreferredSize(new Dimension(200, 21));
    idField.setText("");
    jPanel2.setLayout(flowLayout2);
    flowLayout2.setHgap(20);
    flowLayout2.setVgap(5);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new RuleEditorDialog_cancelButton_actionAdapter(this));
    okButton.setText("OK");
    okButton.addActionListener(new RuleEditorDialog_okButton_actionAdapter(this));
    this.getContentPane().add(ruleEditorPanel1, BorderLayout.CENTER);
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel1, null);
    jPanel1.add(idField, null);
    this.getContentPane().add(jPanel2,  BorderLayout.SOUTH);
    jPanel2.add(okButton, null);
    jPanel2.add(cancelButton, null);
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }

  public void setRuleId(String id) {
  	idField.setText(id);
  }


  public void showSelf(String ruleId) {
    ruleEditorPanel1.setRuleId(ruleId);
    this.idField.setText(ruleId);
    setVisible(true);
  }

  /**
   * Called from <code>TreeListener</code> to create a new
   * <code>ProcessRule</code>
   * Note: <code>Episodes</code> are <em>never</em> created here.
   * @param parentId
   */
  public void showNewInstance(String parentId) {
  	if (parentId == null || parentId.equals("")) return;
  	ruleEditorPanel1.newInstance(parentId);
  	idField.setText("");
  	isEditable = true;
  	instanceParentId = parentId;
  	setVisible(true);
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (idField.getText().equals("")) {
    	JOptionPane.showMessageDialog(null, "Model not identified yet");
    	return;
    }
//    System.out.println("RED 1");
    if (isEditable) {
//        System.out.println("RED 2");
	    if (ruleEditorPanel1.getIsDirty()) {
//	        System.out.println("RED 3");
	    	ProcessRule ep = ruleEditorPanel1.getRule();
//	        System.out.println("RED 4 "+ep);
	    	ep.setId(idField.getText());
                try {
                  environment.putRule(ep);
                } catch (DatastoreException x) {
                  x.printStackTrace();
                }
	    	Concept parent = environment.getConcept(instanceParentId);
	    	if (parent == null)
	    		throw new RuntimeException("ProcessRule Missing instanceParent: "+instanceParentId);
	    	parent.addProperty("hasInstances", idField.getText());
	    }
    }
    setVisible(false);
  }

  void cancelButton_actionPerformed(ActionEvent e) {
    setVisible(false);
  }


}

class RuleEditorDialog_okButton_actionAdapter implements java.awt.event.ActionListener {
  RuleEditorDialog adaptee;

  RuleEditorDialog_okButton_actionAdapter(RuleEditorDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.okButton_actionPerformed(e);
  }
}

class RuleEditorDialog_cancelButton_actionAdapter implements java.awt.event.ActionListener {
  RuleEditorDialog adaptee;

  RuleEditorDialog_cancelButton_actionAdapter(RuleEditorDialog adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cancelButton_actionPerformed(e);
  }
}
