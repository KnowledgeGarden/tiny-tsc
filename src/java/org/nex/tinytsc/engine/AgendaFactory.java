/**
 * 
 */
package org.nex.tinytsc.engine;
import java.util.*;
/**
 * 
 */
public class AgendaFactory {
	/**
	 * Key: agenda name
	 * Value: Agenda for that name
	 */
	private Map<String, AgendaManager2> agendas;
	/**
	 * 
	 */
	public AgendaFactory() {
		agendas = new HashMap<String, AgendaManager2>();
	}

	/**
	 * 
	 * @param name
	 * @return 
	 */
	public AgendaManager2 getOrCreateAgenda(String name) {
		AgendaManager2 result = agendas.get(name);
		if (result == null) {
			result = new AgendaManager2();
			agendas.put(name, result);
		}
		return result;
	}
}
