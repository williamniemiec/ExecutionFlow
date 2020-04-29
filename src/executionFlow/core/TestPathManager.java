package executionFlow.core;

import java.util.ArrayList;
import java.util.List;

import executionFlow.info.CollectorInfo;


/**
 * Computes test path from the available techniques.<br />
 * <h1>Available techniques</h1>
 * <ul>
 * 		<li>CheapCoverage - Analyzing via class byte code</li>
 * 		<li>JDB - Analyzing code via debugging</li>
 * </ul>
 */
public class TestPathManager 
{
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
		
		JDB md = new JDB(collector.getMethodInfo().getClassPath(), lastLineTestMethod);
		
		tp_jdb = md.getTestPaths(collector.getMethodInfo());
		
		return tp_jdb;
	}
	
	/**
	 * Merges test paths obtained from {@link CheapCoverage} and {@link JDB}. This 
	 * method is used to ensure that the test path obtained from JDB contains the 
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
			
			if (tp_cc_merge.size() == 0) {
				// Saves result
				testPaths.add(tp_cc_merge);
			} else if (tp_jdb_merge.size() > 0) {
				Integer jdb_last = tp_jdb_merge.get(tp_jdb_merge.size()-1);
				Integer cc_last = tp_cc_merge.get(tp_cc_merge.size()-1);
				
				if (!jdb_last.equals(cc_last)) {
					tp_jdb_merge.remove(tp_jdb_merge.size()-1);	// Removes last element
				}
				
				// Saves result
				testPaths.add(tp_jdb_merge);
			}
			
		}
		
		return testPaths;
	}
}
