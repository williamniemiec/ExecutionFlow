package executionFlow.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
	private String classPath;
	private static final String DIRNAME = "testPaths";

	Map<String, Integer> signatureId = new HashMap<>();
	
	
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
		
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();

			//----------------------------
			// Extracts class name
			String[] signatureFields = signatures.getMethodSignature().split("\\.");
			String className = signatureFields[signatureFields.length-2];
			
			// Extracts method name with parameters
			String methodName = signatureFields[signatureFields.length-1];
			
			// Ex: TestClass.fibonacci(int)
			String folderName = className+"."+methodName;
			
			// Extracts folder path
			String folderPath = "";
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<signatureFields.length-2; i++) {
				sb.append(signatureFields[i]);
				sb.append("/");
			}
			if (sb.length() > 0)
				sb.deleteCharAt(sb.length()-1);	// Removes last slash
			
			folderPath = sb.toString();
			
			System.out.println(folderPath);
			System.out.println(folderName);
			
			// Create file
			Path p = Paths.get(DIRNAME+"/"+folderPath+"/"+folderName);
			
			try {
				Files.createDirectories(p);
				File f = new File(p.toFile(), getTestPathName(p, signatures));
				BufferedWriter bfw = new BufferedWriter(new FileWriter(f));
				bfw.write(signatures.getTestMethodSignature());
				bfw.newLine();
				bfw.write(e.getValue().toString());
				bfw.newLine();
				bfw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			
			System.out.println();	// New line
		}
	}
	

	/**
	 * Returns the file's name that the test path file should have.
	 * 
	 * @param path Path where the test path files are located
	 * @return Name that the test path file should have
	 */
	private String getTestPathName(Path path, SignaturesInfo signatures)
	{
		int id = 0;
		boolean alreadyExists = true;
		File f;
		signatures.getTestMethodSignature();
		signatures.getMethodSignature();
		
		while (alreadyExists)
		{
			id++;
			f = new File(path.toFile(), "TP_"+id+".txt");
			alreadyExists = f.exists();
		}
		
		return "TP_"+id+".txt";
	}
	
	
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
}
