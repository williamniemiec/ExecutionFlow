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
	
	
	public FileManager(String filename)
	{
		this.filename = filename;
		inputFile = new File(filename);
		originalFile = new File(filename+".original"); 
	}
	
	
	public FileManager revert()
	{
		inputFile.delete();
		originalFile.renameTo(inputFile);
		
		return this;
	}
	
	public FileManager parseFile()
	{
		// Parses file
		FileParser fp = new FileParser("test_else.java", null, "test_else_parsed");
		File out = new File(fp.parseFile());
		
		// Changes parsed file name to the same as received filename
		inputFile.delete();
		out.renameTo(inputFile);
		
		return this;
	}
	
	public FileManager compileFile(String classOutput)
	{
		// Compiles parsed file
		FileCompiler.compile(filename, classOutput);
		
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
