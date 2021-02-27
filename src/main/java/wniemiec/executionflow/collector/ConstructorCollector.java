package wniemiec.executionflow.collector;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;

public class ConstructorCollector extends InvokedCollector {

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
	
	private ConstructorCollector() {
		constructorCollector = new LinkedHashMap<>();
	}
	
	public static ConstructorCollector getInstance() {
		if (instance == null)
			instance = new ConstructorCollector();
		
		return instance;
	}
	
	@Override
	public void storeCollector(Invoked constructor, Invoked testMethod) {
		if (constructorCollector.containsKey(constructor.getInvocationLine()))
			return;
		
		constructorCollector.put(
				constructor.getInvocationLine(),
				new TestedInvoked(constructor, testMethod)
		);
	}
	
	public Set<TestedInvoked> getCollectorSet() {
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
}
