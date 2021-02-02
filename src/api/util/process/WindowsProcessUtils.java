package api.util.process;

import java.io.IOException;

class WindowsProcessUtils extends ProcessUtils {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void forceKillProcessWithPid(long pid) throws IOException {
		runtime.exec("taskkill /F /PID" + pid);
	}
}
