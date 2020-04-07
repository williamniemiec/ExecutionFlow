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
	private static final String DIRNAME = "testPaths";

	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * Exports test paths of a method to a file.
	 * 
	 * @param classPaths All tested test paths of a class
	 */
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
		try {
			// Removes test path folders that will be overwritten (avoid creating duplicate files)
			prepareExport();
		
			for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
				SignaturesInfo signatures = e.getKey();
	
				// Gets save path
				Path savePath = Paths.get(getSavePath(signatures.getMethodSignature()));
				
				// Writes test paths in the file
				writeFile(e.getValue(), savePath, signatures.getTestMethodSignature());
			}
			
			System.out.println("Test paths have been successfully generated!");
			System.out.println("Location: "+new File(DIRNAME).getAbsolutePath());
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns path where are test paths of a method.
	 * 
	 * @param methodSignature Signature of the method
	 * @return Path where are test paths of a method.
	 */
	private String getSavePath(String methodSignature)
	{
		String[] signatureFields = methodSignature.split("\\.");
		
		String folderPath = getFolderPath(signatureFields);
		String folderName = getFolderName(signatureFields);
		
		return DIRNAME+"/"+folderPath+"/"+folderName;
	}
	
	/**
	 * Generates folder's path based on method's signature.
	 * 
	 * @param signatureFields Fields of the signature of the method
	 * @return Folder's path
	 */
	private String getFolderPath(String[] signatureFields)
	{
		// Extracts folder path
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
	 * Generates folder's name based on method's signature.
	 * 
	 * @param signatureFields Fields of the signature of the method
	 * @return Folder's name
	 */
	private String getFolderName(String[] signatureFields)
	{
		// Extracts class name
		String className = signatureFields[signatureFields.length-2];
		
		// Extracts method name with parameters
		String methodName = signatureFields[signatureFields.length-1];
		
		return className+"."+methodName;
	}
	
	/**
	 * Writes test paths of a method in a file.
	 * 
	 * @param testPaths Test paths of the method
	 * @param savePath Location where the file will be saved
	 * @param testMethodSignature Signature of the test method the method is in
	 * @throws IOException If it is not possible to write some test path file
	 */
	private void writeFile(List<Integer> testPaths, Path savePath, String testMethodSignature) throws IOException
	{
		boolean alreadyExists;
		
		Files.createDirectories(savePath);
		File f = new File(savePath.toFile(), getTestPathName(savePath, testMethodSignature));
		alreadyExists = f.exists();
		
		BufferedWriter bfw = new BufferedWriter(new FileWriter(f, true));
		
		// If the file does not exist, writes test method signature on it
		if (!alreadyExists) {
			bfw.write(testMethodSignature);
			bfw.newLine();
		}
		
		bfw.write(testPaths.toString());		// Writes test path in the file
		bfw.newLine();
		bfw.close();
		
		System.out.println("Writing file "+f.getName()+" in "+f.getAbsolutePath());
	}
	
	/**
	 * Removes test path folders that will be overwritten.
	 *  
	 * @throws IOException If any test path file to be removed is in use
	 */
	private void prepareExport() throws IOException
	{
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();			
			
			// Gets save path
			Path savePath = Paths.get(getSavePath(signatures.getMethodSignature()));
			File dir = savePath.toFile();
			
			// If the save path does not exist nothing will be overwritten
			if (!dir.exists()) {
				return;
			}
			
			// Else removes files that will be overwritten
			String[] files = dir.list();
			File testPathFile;
			
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
	 * @param file Test path file
	 * @param testMethodSignature Signature of the test method the method is in
	 * @return True if the file will be overwritten; otherwise, returns false
	 * @throws IOException If it is not possible to read the file
	 */
	private boolean willBeOverwritten(File file, String testMethodSignature) throws IOException
	{
		boolean response = false;
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String testMethodSignature_file = br.readLine();
		br.close();
		
		// If the file has the same test method signature it will be overwritten
		if (testMethodSignature_file.equals(testMethodSignature)) {
			response = true;
		}		
		
		return response;
	}
	
	/**
	 * Returns the file name that the test path file should have.
	 * 
	 * @param path Path where the test path files are located
	 * @return Name that the test path file should have
	 * @throws IOException If it cannot find file in the provided path
	 */
	private String getTestPathName(Path path, String testMethodSignature) throws IOException
	{
		int id = 1;
		
		// Starts trying with TP_1.txt
		File f = new File(path.toFile(), "TP_"+id+".txt");
		
		// It the name is already in use, if the file has the same test method signature
		while (f.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String testMethodSignature_file = br.readLine();
			br.close();
				
			// If the file has the same test method signature, test path will belong to it
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
