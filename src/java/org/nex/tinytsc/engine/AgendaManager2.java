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
	 * A DynamicAgenda is a SortedSet<Task>
	 */
	private Map<String, Map<String, SortedSet>> _agendas;
	/**
	 * Key = ClassId
	 * Value = Task which contains that ClassId
	 * We use this to simplify updating by ClassId
	 */
	private Map<String, Task> _classes;
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
		_agendas = new HashMap<String, Map<String,SortedSet>>();
		_classes = new HashMap<String, Task>();
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
			SortedSet s = findOrCreateAgenda(taskType);
			s.add(t);
		}
	}
	

	
	/**
	 * Return highest priority {@link Task} of a given {@code taskType}
	 * @param taskType
	 * @param identifiableType
	 * @return can return {@code null
	 */
	public Task getTask(String taskType, int identifiableType) {
		Task result = null;
		synchronized(_agendas) {
			Map<String,SortedSet> foo = _agendas.get(taskType);  // note: might return null
			if (foo != null) {
				SortedSet s = foo.get(Integer.toString(identifiableType));
				result = (Task)s.last();
			}
		}
		return result;
	}
	
	//////////////////////
	// Support
	//////////////////////
	
	private SortedSet findOrCreateAgenda(String taskType, int identifiableType) {
		SortedSet result = null;
		Map<String,SortedSet> foo = _agendas.get(taskType);
		if (result == null) {
			result = new TreeSet(new MyComparator<T>());
			foo = new HashMap<String, SortedSet>();
			foo.put(Integer.toString(identifiableType), result);
			_agendas.put(taskType, foo);
		}
		return result;	
	}
	
	private class MyComparator<T> implements Comparator<T> {
		@Override
		public int compare(T source, T target) {
			Task _a = (Task)source;
			Task _b = (Task)target;
			String _typeA = _a.getTaskType();
			String _typeB = _b.getTaskType();
			if (!_typeA.equals(_typeB))
					return -1;
			return 0;
		}
		
	}
	
	
}
