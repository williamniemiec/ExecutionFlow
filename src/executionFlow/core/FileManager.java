package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
		System.out.println(inputFile.getAbsolutePath());
		// Parses file
		FileParser fp = new FileParser(inputFile.getAbsolutePath(), inputFile.getParent(), filename+"_parsed");
		File out = new File(fp.parseFile());
		//inputFile =  new File(fp.parseFile());
		// Changes parsed file name to the same as received filename
		createBackupFile();
		inputFile.delete();
		out.renameTo(inputFile);
		
		return this;
	}
	
	public FileManager compileFile(String classOutput, String classPackage)
	{
		System.out.println("Compile "+inputFile.getAbsolutePath());
		
		classOutput = classOutput + "\\" + classPackage.replace(".", "\\");
		
		// Compiles parsed file
		FileCompiler.compile(inputFile.getAbsolutePath(), classOutput);
		
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
