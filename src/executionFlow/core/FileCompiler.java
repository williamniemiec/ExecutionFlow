package executionFlow.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class FileCompiler 
{
	private File file;
	
	public FileCompiler(String filepath)
	{
		System.out.println("FP: "+filepath);
		//this.file = new File(filepath);
	}
	
	/**
	 * 
	 * @param fileToCompile Absolute path of source file to be compiled
	 * @param outputDir
	 * @return
	 * @throws Exception 
	 */
	public static String compile(File fileToCompile, String outputDir) throws Exception
	{
//		System.out.println("output: "+outputDir);
		//String fileToCompile = "test_else.java";
		//String[] args = new String[3];
		/*
		List<String> args = new ArrayList<>();
		if (outputDir != null) {
			args.add("-d");
			args.add("bin");
		}
		
		args.add(fileToCompile);
		*/
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		int compilationResult =	compiler.run(null, null, null, new String[] {"-d", outputDir, fileToCompile.getAbsolutePath()});
		if(compilationResult != 0) {
			throw new Exception("Compilation Failed");
		}
		//System.out.println("FILE TO COMPILE: "+fileToCompile);
//		System.out.println("COMPILED "+outputDir+"\\"+fileToCompile.getName().split("\\.")[0]+".class");
		
		return outputDir+"\\"+fileToCompile.getName().split("\\.")[0]+".class";
	}
}
