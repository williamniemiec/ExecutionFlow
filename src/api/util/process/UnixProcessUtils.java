package api.util.process;

import java.io.IOException;

class UnixProcessUtils extends ProcessUtils {
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void forceKillProcessWithPid(long pid) throws IOException {
		runtime.exec("kill -9 " +pid);
	}
}
