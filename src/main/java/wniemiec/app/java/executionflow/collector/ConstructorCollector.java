package wniemiec.app.java.executionflow.collector;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import wniemiec.app.java.executionflow.invoked.Invoked;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.app.java.executionflow.user.User;

/**
 * Responsible for collect constructors.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
		storeConstructorCollector();
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
	
	private void storeConstructorCollector() {
		try {
			User.storeConstructorCollector(constructorCollector);
		} 
		catch (IOException e) {
		}
	}
	
	@Override
	public Set<TestedInvoked> getAllCollectedInvoked() {
		return new HashSet<>(getConstructorCollection());
	}
	
	private Collection<TestedInvoked> getConstructorCollection() {
		try {
			constructorCollector = User.getConstructorCollector();
			
			if (constructorCollector == null)
				constructorCollector = new HashMap<>();
			
			return constructorCollector.values();
		} 
		catch (IOException e) {
			return new ArrayList<>();
		}
	}
	
	@Override
	public void reset() {
		constructorCollector.clear();
		User.resetConstructorCollector();
	}

	@Override
	public void updateInvocationLines(Map<Integer, List<Integer>> mapping, 
	  		   						  Path testMethodSrcFile) {
		updateInvokedInvocationLines(
				mapping, 
				testMethodSrcFile, 
				getConstructorCollection()
		);
		
		storeConstructorCollector();
	}

	@Override
	public String toString() {
		return "ConstructorCollector [constructorCollector=" + constructorCollector + "]";
	}
}
