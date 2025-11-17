/*
 *  Copyright (C) 2005  Jack Park,
 * 	mail : jackpark@gmail.com
 *
 *  Apache 2 License
 */
package org.nex.tinytsc.engine;
import java.io.Serializable;
/**
 * <p>Title: TinyTSC</p>
 * <p>Description: Small "The Scholar's Companion(r)"</p>
 * <p>Copyright: Copyright (c) 2005, Jack Park</p>
 * <p>Company: NexistGroup</p>
 * @author Jack Park
 * @version 1.0
 *
 * When used in a <code>ProcessRule</code>, a tuple looks like<br>
 *   subject = *x
 *   predicate = foo
 *   as in foo(*x)
 * or
 *   subject = *x
 *   predicate contains
 *   object = *y
 *   as in contains(*x, *y)
 *
 * When used in a <code>Episode</code, a tuple looks like<br>
 *   subject = bar
 *   predicate = foo
 *   as in foo(bar)
 * or
 *   subject = foo
 *   predicate contains
 *   object = jelly
 *   as in contains(foo, jelly) 
 */

public class Sentence implements Serializable {
  public int type = 0;
  public String subject = "";
  public String predicate = "";
  /**
   * For thenSay and thenCreate, we will use <code>object</code>
   */
  public String object = "";
  public String objectB = "";
  /**
   * <p>Truth allows us to, e.g. kill an <code>actor</code> in a
   * <code>thenActor</code> slot in a <code>ProcessRule</code></p>
   * <p>All <code>actors</code>, <code>relations</code>, and <code>states</code>
   * are assumed to be <code>true</code> until some <code>ProcessRule</code>
   * makes them <code>false</code>, in which case, they <em>disappear</code>
   * from the <code>Model</code>
   */
  public boolean truth = true;

  /**
   * <p>For future reference. We may yet explore
   * <code>Markov Logic Networks</code> with these
   * <code>Sentence</code>s</p>
   */
  public double probability = 0;

  public Sentence() {
  }

  /**
   * Make a full copy of this <code>Sentence</code>
   * @return
   */
  public Sentence copy() {
    Sentence result = new Sentence();
    result.subject = subject;
    result.predicate = predicate;
    result.object = object;
    result.objectB = objectB;
    result.truth = true;
    return result;
  }

  public boolean equals(Object o) {
    if (o instanceof Sentence) {
      Sentence t = (Sentence)o;
      return (t.subject.equals(subject) &&
              t.predicate.equals(predicate) &&
              t.object.equals(object) &&
              t.objectB.equals(objectB));
    }
    return false;
  }

  /**
   * <p>Similarity requires same <code>predicate</code> and
   * same number of objects used, regardless of the values
   * in the objects.</p>
   * <p>This is looking at the nature of the <em>sentence</em>.
   * e.g.<br>
   * hasColor(Car, Blue) is similar to hasColor(Horse, Brown)
   * but is not similar to hasWeight(Car, 2000) or to actor(Joe)</p>
   *
   * @param t
   * @return
   */
  public boolean isSimilar(Sentence t) {
    if (t.predicate.equals(predicate)) {
        return (countUsedObjects(t) == countUsedObjects(this));
    }
    return false;
  }

  /**
   * Simple string equality
   * TODO : maybe we need to make room for case insensitivity?
   * TODO : maybe we should be checking isA - that's pretty hairy here
   * <p>Let's get real here: this should read pred.isA(pred)
   * 	which means we are forced to do a database query here.
   * 	Which really means, we should never call this method but,
   * 	instead, use an external call,e.g. in the inference engine for isA</p>
   * @param pred
   * @return
   */
  public boolean samePredicate(String pred) {
    return pred.equals(predicate);
  }

  int countUsedObjects(Sentence t) {
    int count = 0;
    if (!t.object.equals("")) count++;
    if (!t.objectB.equals("")) count++;
    return count;
  }

  public String toString() {
    return "("+predicate+"("+subject+" | "+object+")";
  }

  public String toXML() {
    String space = "    ";
    StringBuffer buf = new StringBuffer(space+"<sentence>\n");
    if (!predicate.equals(""))
      buf.append(space+"  <predicate>"+predicate+"</predicate>\n");
    if (!subject.equals(""))
      buf.append(space+"  <subject>"+subject+"</subject>\n");
    if (!object.equals(""))
      buf.append(space+"  <object>"+object+"</object>\n");
    if (!objectB.equals(""))
      buf.append(space+"  <objectB>"+object+"</objectB>\n");
    buf.append(space+"  <truth>"+Boolean.toString(truth)+"</truth>\n");
    buf.append(space+"</sentence>\n");
    return buf.toString();
  }
}