package executionFlow.exporter.testpath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import executionFlow.ExecutionFlow;
import executionFlow.util.DataUtil;
import executionFlow.util.Pair;
import executionFlow.util.logger.Logger;


/**
 * Exports computed test path to a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.0
 */
public class FileExporter implements TestPathExporter 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;
	private boolean isConstructor;
	private Path exportFile;

	
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
			removeConflictingExportFiles(classTestPaths);
		
			for (Map.Entry<Pair<String, String>, List<List<Integer>>> e : classTestPaths.entrySet()) {
				Pair<String, String> signatures = e.getKey();
				List<List<Integer>> testPaths = e.getValue();
				String testMethodSignature = signatures.first;
				String invokedSignature = signatures.second;
	

				this.exportFile = generateDirectoryFromSignature(invokedSignature);
				
				testPaths = removeEmptyTestPaths(testPaths);
				storeExportFile(testPaths, testMethodSignature);
			}

			Logger.info("Test paths have been successfully exported!");
			Logger.info(
					"Location: " 
					+ new File(
							ExecutionFlow.getAppRootPath().toFile(), 
							dirName
					).getAbsolutePath()
			);
		} catch (IOException e) {
			Logger.error("Error while exporting test paths: " + e.getMessage());
		}
	}



	private Path generateDirectoryFromSignature(String invokedSignature) {
		return Paths.get(
				ExecutionFlow.getCurrentProjectRoot().toString(), 
				dirName,
				DataUtil.generateDirectoryPathFromSignature(invokedSignature, isConstructor)
		);
	}
	
	private void storeExportFile(List<List<Integer>> testPaths, String testMethodSignature) 
			throws IOException
	{
		if (testPaths.isEmpty())
			return;				
		
		File testPathExportFile = new File(
				exportFile.toFile(), 
				getTestPathName(exportFile, testMethodSignature)
		);
		
		try (BufferedWriter bfw = new BufferedWriter(new FileWriter(testPathExportFile, true))) {
			Files.createDirectories(exportFile);
			
			writeTestMethodSignature(testMethodSignature, testPathExportFile, bfw);
			writeTestPaths(testPaths, bfw);
			
			bfw.newLine();
		}
		
		Logger.info(
				"Writing file " + testPathExportFile.getName() 
				+ " in " + testPathExportFile.getAbsolutePath()
		);
	}


	private void writeTestPaths(List<List<Integer>> testPaths, BufferedWriter bfw) throws IOException {
		for (List<Integer> testPath : testPaths) {
			bfw.write(testPath.toString());		// Writes test path in the file	
			bfw.newLine();
		}
	}


	private void writeTestMethodSignature(String testMethodSignature, File testPathExportFile, BufferedWriter bfw)
			throws IOException {
		// If the file does not exist, writes test method signature on it
		if (!testPathExportFile.exists()) {
			bfw.write(testMethodSignature);
			bfw.newLine();
		}
	}
	
	private List<List<Integer>> removeEmptyTestPaths(List<List<Integer>> testPaths) 
	{
		List<List<Integer>> tps = new ArrayList<>();
		
		for (List<Integer> tp : testPaths) {
			if (!tp.isEmpty())
				tps.add(tp);
		}
		
		return tps;
	}

	/**
	 * Removes test path export files that will be overwritten.
	 * 
	 * @param		classTestPaths Collected test paths
	 * 
	 * @throws		IOException If any test path file to be removed is in use
	 */
	private void removeConflictingExportFiles(Map<Pair<String, String>, List<List<Integer>>> classTestPaths) 
			throws IOException
	{
		// Removes test path folders that will be overwritten (avoids 
		// creating duplicate files)
		for (Pair<String, String> signatures : classTestPaths.keySet()) {
			String testMethodSignature = signatures.first;
			String invokedSignature = signatures.second;
			
			File testPathExportDirectory = generateDirectoryFromSignature(invokedSignature).toFile();
			
			removeTestPathExportFileWithTestMethodSignature(
					testPathExportDirectory, 
					testMethodSignature
			);
		}
	}
	
	private void removeTestPathExportFileWithTestMethodSignature(File testPathExportDirectory,
			String testMethodSignature) throws IOException {
		if (!testPathExportDirectory.exists())
			return;

		for (String filename : testPathExportDirectory.list()) {
			File testPathFile = new File(testPathExportDirectory, filename);
			
			if (extractTestMethodSignatureFromExportFile(testPathFile).equals(testMethodSignature)) {
				testPathFile.getAbsoluteFile().delete();
			}
		}
	}


	private String extractTestMethodSignatureFromExportFile(File file) throws IOException
	{
		String testMethodSignature = "";
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			testMethodSignature = br.readLine();
		}

		return testMethodSignature;
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
		File f = new File(path.toFile(), "TP_" + id + ".txt");
		
		
		// It the name is already in use, if the file has the same test method 
		// signature
		while (f.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String testMethodSignature_file = br.readLine();
			
			
			br.close();
				
			// If the file has the same test method signature, test path will 
			// belong to it
			if (testMethodSignature_file.equals(testMethodSignature)) {
				return "TP_" + id + ".txt";
			}			

			// Else try to generate another file name 
			id++;
			f = new File(path.toFile(), "TP_" + id + ".txt");
		}
		
		return "TP_"+id+".txt";
	}
}
