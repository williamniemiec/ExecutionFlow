package executionFlow.core;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import executionFlow.MethodExecutionFlow;
import executionFlow.info.ClassConstructorInfo;
import executionFlow.info.ClassMethodInfo;

public class TestPath {
	private String appPath;
	
	public TestPath()
	{
		try {
			appPath = new File(MethodExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		appPath = new File(appPath+"../").getParent();
		
	}
	
	public List<Integer> getTestPath(ClassMethodInfo methodInfo, ClassConstructorInfo constructorInfo) throws Throwable
	{		
		List<Integer> methodPath_cc;
		List<Integer> methodPath_jdb;
		
		MethodDebugger methodDebugger = new MethodDebugger(appPath, methodInfo.getClassPath());
		CheapCoverage.loadClass(methodInfo.getClassPath());
		
		methodPath_cc = CheapCoverage.getTestPath(methodInfo, constructorInfo);
		methodPath_jdb = methodDebugger.getTestPath(methodInfo, constructorInfo);
		
		System.out.println("cc: "+methodPath_cc);
		System.out.println("jdb: "+methodPath_jdb);
		
		if (methodPath_jdb.size() > 0) {
			int lastVisitedLine = methodPath_jdb.get(methodPath_jdb.size()-1);
			
			if (lastVisitedLine != methodPath_cc.get(methodPath_cc.size()-1)) {
				methodPath_jdb.remove(methodPath_jdb.size()-1);
			}
		}
		
		return methodPath_jdb;
	}
}
