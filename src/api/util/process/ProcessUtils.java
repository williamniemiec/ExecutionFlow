package api.util.process;

import java.io.IOException;

public abstract class ProcessUtils {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	protected Runtime runtime = Runtime.getRuntime();
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public static ProcessUtils getInstance() {
		if (isWindowsOS())
			return new WindowsProcessUtils();
		else
			return new UnixProcessUtils();
	}
		
	public abstract void forceKillProcessWithPid(long pid) throws IOException;
	
	private static boolean isWindowsOS() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}
}
