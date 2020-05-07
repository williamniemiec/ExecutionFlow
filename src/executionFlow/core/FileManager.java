package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager 
{
	private String filename;
	private File inputFile;
	private File originalFile; 
	
	
	public FileManager(String srcFilename)
	{
		inputFile = new File(srcFilename);
		originalFile = new File(srcFilename+".original"); 
		this.filename = inputFile.getName().split("\\.")[0];
		
//		System.out.println("inputFile: "+inputFile);
//		System.out.println("originalFile: "+originalFile);
//		System.out.println("filename: "+filename);
	}
	
	
	public FileManager revert()
	{
		inputFile.delete();
		originalFile.renameTo(inputFile);
		
		return this;
	}
	
	public FileManager parseFile()
	{
//		System.out.println(inputFile.getAbsolutePath());
		
		createBackupFile();
		
		// Parses file
		FileParser fp = new FileParser(inputFile.getAbsolutePath(), inputFile.getParent(), filename+"_parsed");
		File out = new File(fp.parseFile());
		//inputFile =  new File(fp.parseFile());
		// Changes parsed file name to the same as received filename
		
		inputFile.delete();
		out.renameTo(inputFile);
		
		return this;
	}
	
	public FileManager compileFile(String classOutput, String classPackage) throws Exception
	{
//		System.out.println(classOutput);
//		System.out.println("!!!!"+classPackage);
		int packageFolders = classPackage.split("\\.").length;
//		System.out.println(packageFolders);
		Path file = Paths.get(classOutput);
		for (int i=0; i<packageFolders; i++) {
			file = file.getParent();
		}
		
//		System.out.println("Compile "+inputFile.getAbsolutePath());
		
		//classOutput = classOutput + "\\" + classPackage.replace(".", "\\");
//		System.out.println("FileToCompile: "+inputFile);
//		System.out.println("OutputDir: "+file.toString());
		// Compiles parsed file
		FileCompiler.compile(inputFile, file.toString());
		
		return this;
	}
	
	private void createBackupFile()
	{
		try {
			Files.copy(
				inputFile.toPath(), 
				originalFile.toPath(), 
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
