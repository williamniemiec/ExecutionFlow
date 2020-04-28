package executionFlow.core;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import executionFlow.ExecutionFlow;
import executionFlow.info.ClassMethodInfo;
import executionFlow.info.CollectorInfo;


/**
 * Computes test path from the available techniques.<br/>
 * <h1>Available techniques</h1>
 * <ul>
 * 		<li>CheapCoverage - Analyzing via class byte code</li>
 * 		<li>JDB - Analyzing code via debugging</li>
 * </ul>
 */
public class TestPathManager 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private String appPath;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * Computes test path from the available techniques.<br/>
	 * <h1>Available techniques</h1>
	 * <ul>
	 * 		<li>CheapCoverage - Analyzing via class byte code</li>
	 * 		<li>JDB - Analyzing code via debugging</li>
	 * </ul>
	 */
	public TestPathManager()
	{
		this.appPath = getProjectPath();
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Computes test path from a list of collectors using {@link CheapCoverage}.
	 *  
	 * @param collectors List of collectors
	 * @return Test paths obtained from this list
	 * @throws Throwable If an error occurs
	 */
	public List<List<Integer>> testPath_cc(List<CollectorInfo> collectors) throws Throwable
	{
		List<List<Integer>> tp_cc = new ArrayList<>();
		
		for (CollectorInfo collector : collectors) {
			CheapCoverage.loadClass(collector.getMethodInfo().getClassPath());
			tp_cc.add(CheapCoverage.getTestPath(collector.getMethodInfo(), collector.getConstructorInfo()));
		}
		
		return tp_cc;
	}
	
	/**
	 * Computes test path of a method using {@link JDB}.
	 * 
	 * @param collector Information about this method
	 * @return Test path for this method
	 * @throws Throwable If an error occurs
	 */
	public List<List<Integer>> testPath_jdb(CollectorInfo collector, int lastLineTestMethod) throws Throwable
	{
		List<List<Integer>> tp_jdb = new ArrayList<>();
		
		ClassMethodInfo cmi = collector.getMethodInfo();
		JDB md = new JDB(appPath, cmi.getClassPath(), lastLineTestMethod);
		
		tp_jdb = md.getTestPaths(cmi);
		
		return tp_jdb;
	}
	
	/**
	 * Merges test paths obtained from {@link CheapCoverage} and {@link JDB}. This 
	 * method is used to ensure that the test path obtained from jdb contains the 
	 * correct ending. 
	 * 
	 * @param tp_cc Test path obtained from {@link CheapCoverage}
	 * @param tp_jdb Test path obtained from {@link JDB}
	 * @return Merged test path
	 */
	public List<List<Integer>> merge_cc_jdb(List<List<Integer>> tp_cc, List<List<Integer>> tp_jdb)
	{
		List<List<Integer>> testPaths = new ArrayList<>();
		
		// Only needs to compare the end of each test path
		for (int i=0; i<tp_jdb.size(); i++) {
			List<Integer> tp_jdb_merge = tp_jdb.get(i);
			List<Integer> tp_cc_merge = tp_cc.get(i);
			
			if (tp_jdb_merge.size() > 0) {
				Integer jdb_last = tp_jdb_merge.get(tp_jdb_merge.size()-1);
				Integer cc_last = tp_cc_merge.get(tp_cc_merge.size()-1);
				
				if (!jdb_last.equals(cc_last)) {
					tp_jdb_merge.remove(tp_jdb_merge.size()-1);	// Removes last element
				}
			}
			
			// Saves result
			testPaths.add(tp_jdb_merge);
		}
		
		return testPaths;
	}
	
	/**
	 * Returns project root path, based on class {@link ExecutionFlow} location.
	 * 
	 * @return Project root path
	 */
	private String getProjectPath()
	{
		String projectPath = "";
		
		try {
			projectPath = new File(ExecutionFlow.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			projectPath = new File(projectPath+"../").getParent();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return projectPath;
	}
}
