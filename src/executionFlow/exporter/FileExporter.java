package executionFlow.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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
	//private String classPath;
	private static final String DIRNAME = "testPaths";

	
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
		
		// Ex: TestClass.fibonacci(int)
		
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
		
		if (!alreadyExists) {
			bfw.write(testMethodSignature);
			bfw.newLine();
		}
		
		bfw.write(testPaths.toString());
		bfw.newLine();
		bfw.close();
		
		System.out.println("Writing file "+f.getName()+" in "+f.getAbsolutePath());
	}
	
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

			// Delete files that will be rewritten
			//Path savePath = Paths.get(getSavePath(signatures.getMethodSignature()));
			File dir = savePath.toFile();
			
			// If dir does not exist, it is all ready
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
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String testMethodSignature_file = br.readLine();
			
			if (testMethodSignature_file.equals(testMethodSignature)) {
				response = true;
				
			}
		}
		
		return response;
	}
	
	/**
	 * Returns the file's name that the test path file should have.
	 * 
	 * @param path Path where the test path files are located
	 * @return Name that the test path file should have
	 */
	private String getTestPathName(Path path, String testMethodSignature)
	{
		int id = 1;
		
		File f = new File(path.toFile(), "TP_"+id+".txt");
		
		
		while (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String testMethodSignature_file = br.readLine();
				
				if (testMethodSignature_file.equals(testMethodSignature)) {
					return "TP_"+id+".txt";
				}			
			} catch(IOException e1) {
				e1.printStackTrace();
			}
			
			id++;
			f = new File(path.toFile(), "TP_"+id+".txt");
		}
		
		return "TP_"+id+".txt";
	}
	
	/*
	public String findClassFilePath(String classSignature)
	{
		Path root = Paths.get(System.getProperty("user.dir"));
		
		// Extracts class name
		String[] signatureFields = classSignature.split("\\.");
		String className = signatureFields[signatureFields.length-2];
		
		
		try {
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			        if (file.toString().endsWith(className+".java")) {
			        	classPath = file.toString();
			        }
			        
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return classPath;
	}
	*/
}
