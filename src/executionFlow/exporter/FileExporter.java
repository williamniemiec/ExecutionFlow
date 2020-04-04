package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;


/**
 * Exports the results to a file.
 */
public class FileExporter implements ExporterExecutionFlow 
{
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private Map<SignaturesInfo, List<Integer>> classPaths;
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	public FileExporter(Map<SignaturesInfo, List<Integer>> classPaths)
	{
		this.classPaths = classPaths;
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	@Override
	public void export() 
	{
		System.out.println("Not implemented");
	}
}
