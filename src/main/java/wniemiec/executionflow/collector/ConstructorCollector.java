package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

/**
 * Responsible for collect constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		7.0.0
 * @since		7.0.0
 */
public class ConstructorCollector extends InvokedCollector {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static ConstructorCollector instance;
	
	/**
	 * Stores information about collected constructor.<hr/>
	 * <ul>
	 * 	<li><b>Key(with arguments):</b>		
	 * 	<code>invocationLine + classSignature[arg1,arg2,...]</code></li>
	 * 	<li><b>Key(without arguments):</b>	
	 * 	<code>invocationLine + classSignature[]</code></li>
	 * 	<li><b>Value:</b> Informations about the constructor</li>
	 * </ul>
	 */
	protected volatile Map<Integer, TestedInvoked> constructorCollector;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private ConstructorCollector() {
		constructorCollector = new LinkedHashMap<>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static ConstructorCollector getInstance() {
		if (instance == null)
			instance = new ConstructorCollector();
		
		return instance;
	}
	
	@Override
	public void collect(TestedInvoked testedInvoked) {
		if (wasConstructorCollected(testedInvoked.getTestedInvoked()))
			return;
		
		putInConstructorCollection(testedInvoked.getTestedInvoked(), testedInvoked);
	}
	
	private boolean wasConstructorCollected(Invoked constructor) {
		return constructorCollector.containsKey(constructor.getInvocationLine());
	}
	
	private void putInConstructorCollection(Invoked constructor, 
											TestedInvoked testedInvoked) {
		constructorCollector.put(
				constructor.getInvocationLine(),
				testedInvoked
		);
	}
	
	@Override
	public Set<TestedInvoked> getAllCollectedInvoked() {
		return new HashSet<>(constructorCollector.values());
	}
	
	@Override
	public void reset() {
		constructorCollector.clear();
	}

	@Override
	public void updateInvocationLines(Map<Integer, Integer> mapping, 
									  Path testMethodSrcFile) {
		updateInvokedInvocationLines(
				mapping, 
				testMethodSrcFile, 
				constructorCollector.values()
		);
	}

	@Override
	public String toString() {
		return "ConstructorCollector [constructorCollector=" + constructorCollector + "]";
	}
}
