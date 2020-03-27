package executionFlow;

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


public class RT 
{
	private static final MethodHandle PROBE;
	private static List<Integer> path = new ArrayList<>();
	private static int lastAddLine = 0;
	
	static {
		try {
			PROBE = lookup().findStatic(RT.class, "probe", methodType(void.class, String.class, int.class));
		} catch (NoSuchMethodException | IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}

	
	@SuppressWarnings("unused")
	private static void probe(String method, int line) 
	{
		if (!method.contains("<init>") && line != lastAddLine) {	// Não considera linhas do construtor
			path.add(line);
			lastAddLine = line;
		}
	}

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
	}
}
