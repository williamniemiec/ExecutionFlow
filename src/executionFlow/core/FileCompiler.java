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
		//this.file = new File(filepath);
	}
	
	public static String compile(String fileToCompile, String outputDir)
	{
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
		int compilationResult =	compiler.run(null, null, null, new String[] {"-d", outputDir, fileToCompile});
		if(compilationResult == 0){
			System.out.println("Compilation is successful");
		}else{
			System.out.println("Compilation Failed");
		}
		
		return outputDir+"/"+fileToCompile.split("\\.")[0]+".class";
	}
	
	public static void main(String[] args) 
	{
		//FileCompiler fc = new FileCompiler("test_else.java");
		//FileCompiler.compile("src\\executionFlow\\ExecutionFlow.java");
		FileCompiler.compile("test_else.java", null);
	}
}
