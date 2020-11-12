package executionFlow.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import executionFlow.ExecutionFlow;
import executionFlow.util.Logger;
import executionFlow.util.DataUtil;
import executionFlow.util.Pair;


/**
 * Exports computed test path to a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		3.0.0
 * @since		1.0
 */
public class FileExporter implements ExporterExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private boolean isConstructor;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Exports computed test paths from collected invoked, where an invoked
	 * can be a method or a constructor.
	 * 
	 * @param		dirName Directory name
	 * @param		isConstructor If the invoked is a constructor
	 */
	public FileExporter(String dirName, boolean isConstructor)
	{
		this.dirName = dirName;
		this.isConstructor = isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) 
	{
		if (classTestPaths == null || classTestPaths.isEmpty())
			return;
		
		try {
			// Removes test path folders that will be overwritten (avoids 
			// creating duplicate files)
			prepareExport(classTestPaths);
		
			for (Map.Entry<Pair<String, String>, List<List<Integer>>> e : classTestPaths.entrySet()) {
				Pair<String, String> signatures = e.getKey();
	
				
				// Gets save path
				Path savePath = Paths.get(ExecutionFlow.getCurrentProjectRoot().toString(), dirName,
					DataUtil.generateDirectoryPath(signatures.second, isConstructor));
				
				// Writes test paths in the file
				writeFile(e.getValue(), savePath, signatures.first);
			}

			Logger.info("Test paths have been successfully exported!");
			Logger.info("Location: "+new File(ExecutionFlow.getAppRootPath().toFile(), dirName).getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes test paths of an invoked to a file.
	 * 
	 * @param		testPaths Test paths of the invoked
	 * @param		savePath Location where the file will be saved
	 * @param		testMethodSignature Signature of the test method the invoked is in
	 * 
	 * @throws		IOException If it is not possible to write some test path file
	 */
	private void writeFile(List<List<Integer>> testPaths, Path savePath, String testMethodSignature) throws IOException
	{
		boolean alreadyExists;
		File f = new File(savePath.toFile(), getTestPathName(savePath, testMethodSignature));
		
		Files.createDirectories(savePath);
		alreadyExists = f.exists();
		
		BufferedWriter bfw = new BufferedWriter(new FileWriter(f, true));
		
		// If the file does not exist, writes test method signature on it
		if (!alreadyExists) {
			bfw.write(testMethodSignature);
			bfw.newLine();
		}
		
		for (List<Integer> testPath : testPaths) {
			bfw.write(testPath.toString());		// Writes test path in the file	
			bfw.newLine();
		}
			
		bfw.newLine();
		bfw.close();
		
		Logger.info("Writing file "+f.getName()+" in "+f.getAbsolutePath());
	}
	
	/**
	 * Removes test path folders that will be overwritten.
	 * 
	 * @param		classTestPaths Collected test paths
	 * 
	 * @throws		IOException If any test path file to be removed is in use
	 */
	private void prepareExport(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) throws IOException
	{
		for (Pair<String, String> signatures : classTestPaths.keySet()) {	
			// Gets save path
			Path savePath = Paths.get(ExecutionFlow.getAppRootPath().toString(), dirName,
					DataUtil.generateDirectoryPath(signatures.second, isConstructor));
			
			File dir = savePath.toFile(), testPathFile;
			String[] files;
			
			
			// If the save path does not exist nothing will be overwritten
			if (!dir.exists())
				return;
			
			
			// Else removes files that will be overwritten
			files = dir.list();
			
			// Searches by files that will be overwritten and delete them 
			for (String filename : files) {
				testPathFile = new File(dir, filename);
				
				if (willBeOverwritten(testPathFile, signatures.first)) {
					testPathFile.getAbsoluteFile().delete();
				}
			}
		}
	}
	
	/**
	 * Checks if a test path file will be overwritten.
	 * 
	 * @param		file Test path file
	 * @param		testMethodSignature Signature of the test method the method is in
	 * 
	 * @return		True if the file will be overwritten; otherwise, returns false
	 * 
	 * @throws		IOException If it is not possible to read the file
	 */
	private boolean willBeOverwritten(File file, String testMethodSignature) throws IOException
	{
		boolean response = false;
		BufferedReader br = new BufferedReader(new FileReader(file));
		String testMethodSignature_file = br.readLine();

		
		br.close();
		
		// If the file has the same test method signature it will be overwritten
		if (testMethodSignature_file.equals(testMethodSignature))
			response = true;	
		
		return response;
	}
	
	/**
	 * Returns the file name that the test path file should have.
	 * 
	 * @param		path Path where the test path files are located
	 * 
	 * @return		Name that the test path file should have
	 * 
	 * @throws		IOException If it cannot find file in the provided path
	 */
	private String getTestPathName(Path path, String testMethodSignature) throws IOException
	{		
		int id = 1;
		
		// Starts trying with TP_1.txt
		File f = new File(path.toFile(), "TP_"+id+".txt");
		
		
		// It the name is already in use, if the file has the same test method 
		// signature
		while (f.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String testMethodSignature_file = br.readLine();
			
			
			br.close();
				
			// If the file has the same test method signature, test path will 
			// belong to it
			if (testMethodSignature_file.equals(testMethodSignature)) {
				return "TP_"+id+".txt";
			}			

			// Else try to generate another file name 
			id++;
			f = new File(path.toFile(), "TP_"+id+".txt");
		}
		
		return "TP_"+id+".txt";
	}
}
