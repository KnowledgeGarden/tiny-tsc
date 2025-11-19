/**
 * 
 */
package devtests;

import org.nex.tinytsc.api.IConstants;
import org.nex.tinytsc.engine.AgendaManager2;
import org.nex.tinytsc.engine.Concept;
import org.nex.tinytsc.engine.Task;
import java.util.*;

/**
 * 
 */
public class AgendaTest1 extends BaseTestClass {
	private int ObjectType = IConstants.CONCEPT;
	private String TaskType = IConstants.FILLIN_NEXT_EPISODE;
	private String TaskType2 = IConstants.FIND_EPISODE;
	private String AgendaName = "testing";
	private AgendaManager2 agenda;
	/**
	 * 
	 */
	public AgendaTest1() {
		agenda = environment.getAgendaFactory().getOrCreateAgenda(AgendaName);
		Concept o1 = new Concept("101");
		Concept o2 = new Concept("102");
		Concept o3 = new Concept("103");
		agenda.addTask(TaskType, 30, o1, ObjectType);
		agenda.addTask(TaskType2, 20, o2, ObjectType);
		agenda.addTask(TaskType, 10, o3, ObjectType);
		List<Task> tx = agenda.getTaskList(AgendaName, ObjectType);
		// See what sorting did
		printTaskList(tx);
	}

	void printTaskList(List<Task> tx) {
		int len = 0;
		if (tx != null) {
			len = tx.size();
			for (int i=0;i<len;i++) 
				System.out.println("X "+tx.get(i).getPriority()+ " ");
			System.out.println("");
		}
	
	}
}
