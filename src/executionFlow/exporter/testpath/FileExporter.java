package executionflow.exporter.testpath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionflow.ExecutionFlow;
import executionflow.info.InvokedContainer;
import executionflow.util.DataUtil;
import executionflow.util.logger.Logger;

/**
 * Exports computed test path to a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.0
 */
public class FileExporter implements TestPathExporter {
	
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
	public FileExporter(String dirName, boolean isConstructor) {
		this.dirName = dirName;
		this.isConstructor = isConstructor;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<InvokedContainer, List<List<Integer>>> testPaths) {
		if ((testPaths == null) || testPaths.isEmpty())
			return;
		
		try {
			exportTestPaths(testPaths);
		} 
		catch (IOException e) {
			Logger.error("Error while exporting test paths: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void exportTestPaths(Map<InvokedContainer, List<List<Integer>>> testPaths) 
			throws IOException {
		removeConflictingExportFiles(testPaths.keySet());

		for (Map.Entry<InvokedContainer, List<List<Integer>>> e : testPaths.entrySet()) {
			InvokedContainer invokedContainer = e.getKey();
			List<List<Integer>> tps = e.getValue();

			prepareExportFile(invokedContainer);
						
			tps = removeEmptyTestPaths(tps);
			
			storeExportFile(
					tps, 
					invokedContainer.getTestMethodInfo().getInvokedSignature()
			);
		}

		Logger.info("Test paths have been successfully exported!");
		Logger.info(
				"Location: " 
				+ new File(
						ExecutionFlow.getAppRootPath().toFile(), 
						dirName
				).getAbsolutePath()
		);
	}
	
	/**
	 * Removes test path export files that will be overwritten (avoids 
	 * creating duplicate files).
	 * 
	 * @param		classTestPaths Collected test paths
	 * 
	 * @throws		IOException If any test path file to be removed is in use
	 */
	private void removeConflictingExportFiles(Set<InvokedContainer> invokedContainer) 
			throws IOException {
		for (InvokedContainer container : invokedContainer) {		
			Path testPathExportDirectory = generateDirectoryFromSignature(
					container.getInvokedInfo().getConcreteInvokedSignature()
			);
			
			removeTestPathExportFileWithTestMethodSignature(
					testPathExportDirectory.toFile(), 
					container.getTestMethodInfo().getInvokedSignature()
			);
		}
	}
	
	private Path generateDirectoryFromSignature(String invokedSignature) {
		return Paths.get(
				ExecutionFlow.getCurrentProjectRoot().toString(), 
				dirName,
				DataUtil.generateDirectoryPathFromSignature(invokedSignature, isConstructor)
		);
	}
	
	private void removeTestPathExportFileWithTestMethodSignature(File testPathExportDirectory,
																 String testMethodSignature) 
			throws IOException {
		if (!testPathExportDirectory.exists())
			return;

		for (String filename : testPathExportDirectory.list()) {
			File testPathFile = new File(testPathExportDirectory, filename);
			
			if (extractTestMethodSignatureFromExportFile(testPathFile)
					.equals(testMethodSignature)) {
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
	
	private void prepareExportFile(InvokedContainer invokedContainer) {
		this.exportFile = generateDirectoryFromSignature(
				invokedContainer.getInvokedInfo().getConcreteInvokedSignature()
		);
	}

	private List<List<Integer>> removeEmptyTestPaths(List<List<Integer>> testPaths) {
		List<List<Integer>> tps = new ArrayList<>();
		
		for (List<Integer> tp : testPaths) {
			if (!tp.isEmpty())
				tps.add(tp);
		}
		
		return tps;
	}
	
	private void storeExportFile(List<List<Integer>> testPaths, String testMethodSignature) 
			throws IOException {
		if (testPaths.isEmpty())
			return;				
		
		File testPathExportFile = new File(
				exportFile.toFile(), 
				getTestPathName(exportFile, testMethodSignature)
		);
		
		Files.createDirectories(exportFile);
		
		try (BufferedWriter bfw = new BufferedWriter(new FileWriter(testPathExportFile, true))) {
			
			writeTestMethodSignature(testMethodSignature, testPathExportFile, bfw);
			writeTestPaths(testPaths, bfw);
			
			bfw.newLine();
		}
		
		Logger.info(
				"Writing file " + testPathExportFile.getName() 
				+ " in " + testPathExportFile.getAbsolutePath()
		);
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
	private String getTestPathName(Path path, String testMethodSignature) 
			throws IOException {		
		int id = 1;
		
		// Starts trying with TP_1.txt
		File exportFile = new File(path.toFile(), "TP_" + id + ".txt");
	
		while (exportFile.exists()) {
			if (getTestMethodSignatureFromExportFile(exportFile).equals(testMethodSignature))
				return "TP_" + id + ".txt";			

			id++;
			exportFile = new File(path.toFile(), "TP_" + id + ".txt");
		}
		
		return "TP_" + id + ".txt";
	}

	private String getTestMethodSignatureFromExportFile(File exportFile) 
			throws IOException, FileNotFoundException {
		String testMethodSignature = "";
		
		try (BufferedReader br = new BufferedReader(new FileReader(exportFile))) {
			testMethodSignature = br.readLine();
		}
		
		return testMethodSignature;
	}
	
	private void writeTestMethodSignature(String testMethodSignature, 
										  File testPathExportFile, 
										  BufferedWriter bfw)
			throws IOException {
		if (testPathExportFile.exists())
			return;
		
		bfw.write(testMethodSignature);
		bfw.newLine();
	}

	private void writeTestPaths(List<List<Integer>> testPaths, BufferedWriter bfw) 
			throws IOException {
		for (List<Integer> testPath : testPaths) {
			bfw.write(testPath.toString());	
			bfw.newLine();
		}
	}
}
