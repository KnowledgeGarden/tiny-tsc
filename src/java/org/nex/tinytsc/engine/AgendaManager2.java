/**
 * 
 */
package org.nex.tinytsc.engine;
import java.util.*;
//log4j.jar
import org.apache.log4j.Logger;
import org.nex.tinytsc.api.Identifiable;

/**
 * 
 */
public class AgendaManager2 {
	private Logger log = Logger.getLogger(AgendaManager.class);
	/**
	 * A DynamicAgenda is a List<Task>
	 * Key: taskType
	 * Value: Map<String , List<Task>>
	 * 	Key:objectType
	 *  Value List<Task> which is sorted on priority
	 */
	private Map<String, Map<String, List<Task>>> _agendas;
	/**
	 * Key = ClassId
	 * Value = List
	 * 	First value is the Object to be updated, following are Agendas (List<Task> which contain that object
	 * 		Update the object
	 * 		Then, sort those lists
	 * We use this to simplify updating by ClassId
	 */
	private Map<String, List<Object>> _classes;
	/**
	 * Identifiable could be extensible: it generalizes the agenda to
	 * handle all kinds of objects
	 * <code>object</code> could be one of:
	 * <li>Task</li>
	 * <li>Episode</li>
	 * <li>Model</li>
	 * <li>Concept</li>
	 * It is up to the <code>IAgent</code>
	 * to know what to do with it.
	 */
	 private Identifiable object = null;

	
	
	
	/**
	 * 
	 */
	public AgendaManager2() {
		_agendas = new HashMap<String, Map<String, List<Task>>>();
		_classes = new HashMap<String, List<Object>>();
	}

	//////////////////////
	// API
	//////////////////////

	/**
	 * Add a given task to its agenda
	 * @param t
	 * @param priority
	 * @param c
	 * @param identifiableType
	 */
	public void addTask(String taskType , int priority, Identifiable c, int identifiableType) {
		Task t = new Task(taskType, priority);
		t.setObject(c);
		synchronized(_agendas) {
			List s = findOrCreateAgenda(taskType, identifiableType);
			s.add(t);
			Collections.sort(s);
		}
	}
	

	
	/**
	 * <p>Return highest priority {@link Task} of a given {@code taskType}</cr>
	 * 	This is distructive - it removes the object</p>
	 * @param taskType
	 * @param identifiableType
	 * @return can return {@code null
	 */
	public Task takeTask(String taskType, int identifiableType) {
		Task result = null;
		synchronized(_agendas) {
			Map<String,List<Task>> foo = _agendas.get(taskType);  // note: might return null
			if (foo != null) {
				List<Task> s = foo.get(Integer.toString(identifiableType));
				result = (Task)s.remove(0);
			}
		}
		return result;
	}

	/**
	 * Does not remove task from Agenda
	 * @param taskType
	 * @param identifiableType
	 * @return
	 */
	public Task getTask(String taskType, int identifiableType) {
		Task result = null;
		synchronized(_agendas) {
			Map<String,List<Task>> foo = _agendas.get(taskType);  // note: might return null
			if (foo != null) {
				List<Task> s = foo.get(Integer.toString(identifiableType));
				result = (Task)s.get(0);
			}
		}
		return result;
	}

	public void updatePriority(String objectId, int increment) {
		
	}
	//////////////////////
	// Support
	//////////////////////
	
	private List<Task> findOrCreateAgenda(String taskType, int identifiableType) {
		List<Task> result = null;
		Map<String, List<Task>> foo = _agendas.get(taskType);
		if (foo == null) {
			result = new ArrayList<Task>();
			foo = new HashMap<String, List<Task>>();
			Map<String, List<Task>> bar = new HashMap<String, List<Task>>();
			foo.put(Integer.toString(identifiableType), result);
			_agendas.put(taskType, foo);
		}
		return result;	
	}
		
	
}
