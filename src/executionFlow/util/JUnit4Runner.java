package executionFlow.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Responsible for executing JUnit 4 tests.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class JUnit4Runner
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private int totalTests;
	private Process process;
	private ProcessBuilder processBuilder;
	private BufferedReader output;
	private BufferedReader outputError;
	private boolean stopped;
	private boolean displayVersion;
	
	private JUnit4Runner(Path workingDirectory, String classPath, String classSignature, 
			boolean displayVersion)
	{
		this.displayVersion = displayVersion;
		
		processBuilder =  new ProcessBuilder(
			"java", 
				"-cp", classPath, 
				"org.junit.runner.JUnitCore", classSignature
		);
			
		processBuilder.directory(workingDirectory.toFile());
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	public static class Builder
	{
		private Path argumentFile; 
		private Path workingDirectory;
		private List<Path> classPath;
		private String classSignature;
		private boolean displayVersion;
		
		public Builder argumentFile(Path argumentFile)
		{
			this.argumentFile = argumentFile;
			return this;
		}
		
		public Builder workingDirectory(Path workingDirectory)
		{
			this.workingDirectory = workingDirectory;
			return this;
		}
		
		public Builder classPath(List<Path> classPath)
		{
			this.classPath = classPath;
			return this;
		}
		
		public Builder classSignature(String classSignature)
		{
			this.classSignature = classSignature;
			return this;
		}
		
		public Builder displayVersion(boolean displayVersion)
		{
			this.displayVersion = displayVersion;
			return this;
		}
		
		public JUnit4Runner build()
		{
			if (classPath == null)
				classPath = new ArrayList<>();

			if (argumentFile == null) {
				return new JUnit4Runner(
						workingDirectory, 
						DataUtil.implode(classPath, ";"),
						classSignature,
						displayVersion
				);
			}
			else {
				return new JUnit4Runner(
						workingDirectory, 
						"@" + argumentFile, 
						classSignature,
						displayVersion
				);
			}
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------	
	public void run() throws IOException, InterruptedException
	{	
		initializeCLI();	
		checkErrors();
		parseCLI();
		closeCLI();
	}

	private void closeCLI() throws IOException, InterruptedException {
		if (stopped)
			return;
		
		output.close();
		outputError.close();
		process.waitFor();
	}

	private void parseCLI() throws IOException {
		String line;
		boolean fatalError = false;
		
		while (!fatalError && !stopped && (line = output.readLine()) != null) {
			boolean error = checkErrors();
			
			if (line.contains("OK (")) {
				totalTests = Integer.valueOf(extractNumbers(line));
			}
			else if (line.contains("JUnit version") && displayVersion) {
				System.out.println(line);
			}
			else if (!error){
				System.out.println(line);
			}
			
			System.out.flush();
			
			fatalError = line.equals("FAILURES!!!");
		}
	}
	
	private String extractNumbers(String line) {
		Pattern patternNumbers = Pattern.compile("[0-9]+");
		Matcher m = patternNumbers.matcher(line);
		
		if (!m.find())
			return "";
		
		return m.group();
	}

	private boolean checkErrors() throws IOException {
		boolean error = false;
		String line;
		
		while (outputError.ready() && (line = outputError.readLine()) != null) {
			System.err.println(line);
			error = true;
		}
		
		return error;
	}
	
	private void initializeCLI() throws IOException, InterruptedException {
		process = processBuilder.start();
		Thread.sleep(3000);
		
		stopped = false;			
		totalTests = 0;
		
		output = new BufferedReader(new InputStreamReader(process.getInputStream()));
		outputError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	}

	public void quit() throws IOException
	{
		stopped = true;
		process.destroyForcibly();
		output.close();
		outputError.close();
	}
	
	public boolean isRunning()
	{
		return process != null && process.isAlive();
	}
	
	public int getTotalTests()
	{
		return totalTests;
	}
}
