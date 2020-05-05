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
		
		// Changes parsed file name to the same as received filename
		//inputFile.delete();
		//out.renameTo(inputFile);
		
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
