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

import executionFlow.ConsoleOutput;
import executionFlow.info.SignaturesInfo;


/**
 * Exports computed test path to a file.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.0
 */
public class FileExporter implements ExporterExecutionFlow 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String dirName;

	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Exports computed test paths from collected invokers, where an invoker
	 * can be a method or a constructor.
	 * 
	 * @param		dirName Name of the directory
	 */
	public FileExporter(String dirName)
	{
		this.dirName = dirName;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void export(Map<SignaturesInfo, List<List<Integer>>> classTestPaths) 
	{
		try {
			// Removes test path folders that will be overwritten (avoids 
			// creating duplicate files)
			prepareExport(classTestPaths);
		
			for (Map.Entry<SignaturesInfo, List<List<Integer>>> e : classTestPaths.entrySet()) {
				SignaturesInfo signatures = e.getKey();
	
				
				// Gets save path
				Path savePath = Paths.get(getSavePath(signatures.getInvokerSignature()));
				
				// Writes test paths in the file
				writeFile(e.getValue(), savePath, signatures.getTestMethodSignature());
			}

			ConsoleOutput.showInfo("Test paths have been successfully exported!");
			ConsoleOutput.showInfo("Location: "+new File(dirName).getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns path where are test paths of an invoker.
	 * 
	 * @param		invokerSignature Invoker signature
	 * 
	 * @return		Path where are test paths of an invoker.
	 */
	private String getSavePath(String invokerSignature)
	{
		String[] signatureFields = invokerSignature.split("\\.");
		String folderPath = getFolderPath(signatureFields);
		String folderName = getFolderName(signatureFields);
		
		
		return dirName+"/"+folderPath+"/"+folderName;
	}
	
	/**
	 * Generates folder's path based on invoker's signature.
	 * 
	 * @param		signatureFields Fields of the signature of the invoker
	 * 
	 * @return		Folder's path
	 */
	private String getFolderPath(String[] signatureFields)
	{
		String folderPath = "";
		StringBuilder sb = new StringBuilder();
		
		
		for (int i=0; i<signatureFields.length-2; i++) {
			sb.append(signatureFields[i]);
			sb.append("/");
		}
		
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length()-1);	// Removes last slash
			folderPath = sb.toString();
		}
		
		return folderPath;
	}
	
	/**
	 * Generates folder's name based on invoker's signature.
	 * 
	 * @param		signatureFields Fields of the signature of the invoker
	 * 
	 * @return		Folder's name
	 */
	private String getFolderName(String[] signatureFields)
	{
		// Extracts class name
		String className = signatureFields[signatureFields.length-2];
		
		// Extracts invoker name with parameters
		String invokerName = signatureFields[signatureFields.length-1];
		
		
		return className+"."+invokerName;
	}
	
	/**
	 * Writes test paths of an invoker in a file.
	 * 
	 * @param		testPaths Test paths of the invoker
	 * @param		savePath Location where the file will be saved
	 * @param		testMethodSignature Signature of the test method the invoker is in
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
		}
			
		bfw.newLine();
		bfw.close();
		
		ConsoleOutput.showInfo("Writing file "+f.getName()+" in "+f.getAbsolutePath());
	}
	
	/**
	 * Removes test path folders that will be overwritten.
	 * 
	 * @param		classTestPaths Collected test paths
	 * 
	 * @throws		IOException If any test path file to be removed is in use
	 */
	private void prepareExport(Map<SignaturesInfo, List<List<Integer>>> classTestPaths) throws IOException
	{
		for (SignaturesInfo signatures : classTestPaths.keySet()) {	
			// Gets save path
			Path savePath = Paths.get(getSavePath(signatures.getInvokerSignature()));
			
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
				
				if (willBeOverwritten(testPathFile, signatures.getTestMethodSignature())) {
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
