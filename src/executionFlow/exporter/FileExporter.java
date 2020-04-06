package executionFlow.exporter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
	private String classPath;
	private static final String DIRNAME = "testPaths";
	
	
	
	// Map<method's signature, id> // Associa a cada assinatura de metodo um id
	// !!! Deve ser zerado ao trocar o diretorio / nome da classe !!!!
	//Map<String, Integer> signatureId = new HashMap<>();
	// Armazena assinatura do metodo e lista de ids (necessario para garantir que mais de um teste possa testar um mesmo metodo)
	//Map<String, List<Integer>> signatureId = new HashMap<>();
	
	
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
		//System.out.println(classPaths);
		// Clear folders that will stores test paths of the methods
		//prepareExport();
		prepareExport();
		
		boolean alreadyExists;
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
			
			// Create file
			Path p = Paths.get(DIRNAME+"/"+folderPath+"/"+folderName);
			
			try {
				Files.createDirectories(p);
				File f = new File(p.toFile(), getTestPathName(p, signatures));
				alreadyExists = f.exists();
				
				BufferedWriter bfw = new BufferedWriter(new FileWriter(f, true));
				
				if (!alreadyExists) {
					bfw.write(signatures.getTestMethodSignature());
					bfw.newLine();
				}
				
				bfw.write(e.getValue().toString());
				bfw.newLine();
				bfw.close();
				
				System.out.println("Writing file "+f.getName()+" in "+f.getAbsolutePath());
			} catch (IOException e1) {
				System.out.println("Error: "+e1.getMessage());
			}
		}
		
		System.out.println("Test paths have been successfully generated!");
		System.out.println("Location: "+new File(DIRNAME).getAbsolutePath());
		System.out.println();
		
	}
	
	private void prepareExport()
	{
		//generateMethodSignaturesIds();
		
		boolean alreadyExists;
		boolean deleteFile = false;
		
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();
			
			String testMethodSignature_testPath = signatures.getTestMethodSignature();
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
			
			// Delete files that will be rewritten
			File dir = new File(DIRNAME+"/"+folderPath+"/"+folderName);
			
			if (!dir.exists()) {
				return;
			}
			
			String[] files = dir.list();
			File f;
			
			for (String filename : files) {
				f = new File(dir, filename);
				
				try (BufferedReader br = new BufferedReader(new FileReader(f))) {
					String testMethodSignature_file = br.readLine();
					
					if (testMethodSignature_file.equals(testMethodSignature_testPath)) {
						deleteFile = true;
						
					}
				} catch(IOException e1) {
					e1.printStackTrace();
				}
				
				if (deleteFile) {
					deleteFile = false;
					f.getAbsoluteFile().delete();
				}
			}
			
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
		int id = 1;
		
		String testMethodSignature_testPath = signatures.getTestMethodSignature();
		File f = new File(path.toFile(), "TP_"+id+".txt");
		
		
		while (f.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(f))) {
				String testMethodSignature_file = br.readLine();
				
				if (testMethodSignature_file.equals(testMethodSignature_testPath)) {
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
	private void generateMethodSignaturesIds()
	{
		int lastInsertedId;
		List<Integer> ids;
		
		for (Map.Entry<SignaturesInfo, List<Integer>> e : classPaths.entrySet()) {
			SignaturesInfo signatures = e.getKey();
		
			if (signatureId.containsKey(signatures.getMethodSignature())) {
				//System.out.println("TEM");
				
				ids = signatureId.get(signatures.getMethodSignature());
				lastInsertedId = ids.get(ids.size()-1); 
				System.out.println("lastInsertedId "+lastInsertedId);
				ids.add(lastInsertedId+1);
				System.out.println(ids);
				System.out.println(signatureId);
				System.out.println();
			} else {
				//System.out.println("NAO TEM");
				ids = new ArrayList<>();
				ids.add(1);
				signatureId.put(signatures.getMethodSignature(), ids);
			}
		}
		
	}
	*/
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
	
	/*
	private void clearDir(String path)
	{
		File dir = new File(path);
		
		try {
			Files.walk(dir.toPath())
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.forEach(File::delete);
		} catch (IOException e) { }
	}
	
	private void deleteFile(Path dir, String filename)
	{
		//File dir = new File(path);
		
		try {
			Files.walk(dir)
			.sorted(Comparator.reverseOrder())
			.map(Path::toFile)
			.filter(f -> f.getName().equals(filename))
			.forEach(File::delete);
		} catch (IOException e) { }
	}
	*/
}
