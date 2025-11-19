/**
 * 
 */
package devtests;

import java.util.Hashtable;

import org.apache.log4j.PropertyConfigurator;
import org.nex.config.ConfigPullParser;
import org.nex.tinytsc.engine.Environment;

/**
 * 
 */
public class BaseTestClass {
	protected Environment environment;
	/**
	 * 
	 */
	public BaseTestClass() {
	    PropertyConfigurator.configure("logger.properties");
	    ConfigPullParser parser = new ConfigPullParser("tsc-props.xml");
	    Hashtable properties = parser.getProperties();
	    try {
	    	environment = new Environment(properties);
	    } catch (Exception e) {
	    	throw new RuntimeException(e);
	    }
	}

}
