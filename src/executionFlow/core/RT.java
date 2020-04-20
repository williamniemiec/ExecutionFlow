package executionFlow.core;

import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.ArrayList;
import java.util.List;


/**
 * Helper class for {@link CheapCoverage}. It will be responsible for storing
 * the test path.
 * 
 * Modified from {@link https://github.com/forax/cheapcoverage}
 */
public class RT 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static final MethodHandle PROBE;
	private static List<Integer> path = new ArrayList<>();
	private static int lastAddLine = 0;
	private static String lastAddMethod;
	
	static {
		try {
			PROBE = lookup().findStatic(RT.class, "probe", methodType(void.class, String.class, int.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	public static CallSite bsm(Lookup lookup, String name, MethodType type, String method, int line) 
	{
		return new ConstantCallSite(insertArguments(PROBE, 0, method, line));
	}
	
	public static List<Integer> getExecutionPath() { return path; }
	
	public static void clearExecutionPath() 
	{
		if (path != null)
			path.clear(); 
		
		lastAddLine = 0;
		lastAddMethod = null;
	}
	
	@SuppressWarnings("unused")
	private static void probe(String method, int line) 
	{
//		if (line == -1) {System.out.println(lastAddLine); return;};
		
		// Ignores constructor lines
		if (!method.contains("<init>") && !method.contains("<clinit>") && !method.contains("preClinit") && line != lastAddLine) {	 
			if (lastAddMethod == null) { lastAddMethod = method; }
			
			// Ignores internal calls in test path
			if (!method.equals(lastAddMethod)) { return; }
				
			path.add(line);
			lastAddLine = line;
		}
	}
}
