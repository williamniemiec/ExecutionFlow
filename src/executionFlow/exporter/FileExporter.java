package executionFlow.exporter;

import java.util.List;
import java.util.Map;

import executionFlow.info.SignaturesInfo;


public class FileExporter implements ExporterExecutionFlow 
{
	private Map<SignaturesInfo, List<Integer>> classPaths;
	
	
	public FileExporter(Map<SignaturesInfo, List<Integer>> classPaths)
	{
		this.classPaths = classPaths;
	}
	
	
	@Override
	public void export() 
	{
		
	}

}
