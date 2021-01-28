package api.jdb;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import api.util.ArgumentFile;
import api.util.StringUtils;

/**
 * Simple API for JBD (Java debugger).
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @see			https://github.com/williamniemiec/jdb-api
 */
public class JDB {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Process process;
	private ProcessBuilder processBuilder;
	private JDBInput in;
	private JDBOutput out;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	/**
	 * Creates API for JDB.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path that will be used in JDB
	 * @param		srcPath Source path that will be used in JDB
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	private JDB(Path workingDirectory, String classPath, String srcPath, 
				String classSignature, String classArgs) {
		if (classPath == null)
			throw new IllegalStateException("Class path cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path cannot be empty");

		classArgs = (classArgs == null) ? "" : classArgs;
		classSignature = (classSignature == null) ? "" : classSignature;
		
		processBuilder = new ProcessBuilder(
			"jdb",
				"-sourcepath", srcPath.replaceAll("\\s", "%20"),
				"-classpath", classPath.replaceAll("\\s", "%20"),
				classSignature, 
				classArgs
		);
		
		if (workingDirectory != null)
			processBuilder.directory(workingDirectory.toFile());
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	public static class Builder	{
		
		private Path argumentFile; 
		private Path workingDirectory;
		private List<Path> classPath;
		private List<Path> srcPath;
		private String classSignature;
		private String classArgs;
		
		public Builder argumentFile(Path argumentFile) {
			this.argumentFile = argumentFile;
			
			return this;
		}
		
		public Builder workingDirectory(Path workingDirectory) {
			this.workingDirectory = workingDirectory;
			
			return this;
		}
		
		public Builder classPath(List<Path> classPath) {
			this.classPath = classPath;
			
			return this;
		}
		
		public Builder srcPath(List<Path> srcPath) {
			this.srcPath = srcPath;
			
			return this;
		}
		
		public Builder classSignature(String classSignature) {
			this.classSignature = classSignature;
			
			return this;
		}
		
		public Builder classArgs(String classArgs) {
			this.classArgs = classArgs;
			
			return this;
		}
		
		/**
		 * Creates {@link JDB} with provided information. It is 
		 * necessary to provide all required fields. The required fields 
		 * are: <br />
		 * <ul>
		 * 	<li>Class path</li>
		 * 	<li>Source path</li>
		 * </ul>
		 * 
		 * @return		JDB with provided information
		 * 
		 * @throws		IllegalArgumentException If any required field is null
		 */
		public JDB build() {
			if (classPath == null)
				classPath = new ArrayList<>();
			
			if (srcPath == null)
				srcPath = new ArrayList<>();
			
			createArgumentFileFromClassPath();
			
			if (argumentFile == null) {
				return new JDB(
						workingDirectory, 
						StringUtils.implode(relativizePaths(classPath), ";"), 
						StringUtils.implode(relativizePaths(srcPath), ";"), 
						classSignature, 
						classArgs
				);
			}
			else {
				return new JDB(
						workingDirectory, 
						"@" + argumentFile, 
						StringUtils.implode(relativizePaths(srcPath), ";"), 
						classSignature, 
						classArgs
				);
			}
		}
		
		private void createArgumentFileFromClassPath() {
			try {
				argumentFile = ArgumentFile.createArgumentFile(
						Path.of(System.getProperty("java.io.tmpdir")), 
						"argfile-jdb.txt", 
						classPath
				);
			} 
			catch (IOException e) {
				argumentFile = null;
			}
		}	
		
		private List<Path> relativizePaths(List<Path> paths) {
			if (paths == null) 
				return new ArrayList<>();
			
			List<Path> relativizedClassPaths = new ArrayList<>();
			Path relativizedPath;
			
			for (int i = 0; i < paths.size(); i++) {
				if (paths.get(i).isAbsolute())
					relativizedPath = workingDirectory.relativize(paths.get(i));
				else
					relativizedPath = paths.get(i);
					
				relativizedClassPaths.add(i, relativizedPath);
			}
			
			return relativizedClassPaths;
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Initializes JDB in a new process.
	 * 
	 * @return		Itself to allow chained calls
	 * 
	 * @throws		IOException If JDB cannot be initialized 
	 */
	public JDB run() throws IOException {
		initializeJDB();
		onShutdown();
		
		return this;
	}

	private void initializeJDB() throws IOException {
		process = processBuilder.start();
		out = new JDBOutput(process);
		in = new JDBInput(process);
	}

	private void onShutdown() {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			    	if (in != null)
			    		in.close();
			    	
			    	if (out != null)
			    		out.close();
			    	
			    	if (process != null)
			    		process.destroyForcibly();
			    }
			});
		}
		catch (IllegalStateException e) {
		}
	}

	public void quit() throws InterruptedException {
		stopStreams();
		process.destroy();
		process.waitFor();
	}
	
	private void stopStreams() {
		in.close();
		out.close();
	}
	
	public void forceQuit() {
		stopStreams();
		process.destroyForcibly();
	}
	
	/**
	 * Sends a command to JDB. After calling this method, you must to call
	 * {@link #read()} for JDB to process the command.
	 * 
	 * @param		command Command that will be sent to JDB
	 */
	public JDB send(String command) {
		if (in == null)
			return this;
		
		in.send(command);
		
		return this;
	}
	
	/**
	 * Sends commands to JDB. After calling this method, you must to call
	 * {@link #read()} for JDB to process these commands.
	 * 
	 * @param		commands Commands that will be sent to JDB
	 */
	public JDB send(String... commands) {
		if (in == null)
			return this;
		
		in.send(commands);
		
		return this;
	}
	
	/**
	 * Reads JDB output. This method will block until some input is
	 * available, an I/O error occurs, or the end of the stream is reached.
	 * 
	 * @return		JDB output
	 * 
	 * @throws		IOException If it cannot read JDB output
	 */
	public String read() {
		try {
			return out.read();
		} 
		catch (IOException e) {
			return "";
		}
	}
	
	/**
	 * Reads all available JDB output. This method will not block if no output is
	 * available.
	 * 
	 * @return		List of read JDB output
	 * 
	 * @throws		IOException If it cannot read JDB output
	 */
	public List<String> readAll() throws IOException {
		return out.readAll();
	}
	
	/**
	 * Checks if there is an output available. 
	 * 
	 * @return		True if {@link #read()} is guaranteed not to block if
	 * called; otherwise, returns false
	 */
	public boolean isReady() {
		return out.isReady();
	}
	
	/**
	 * Causes the current thread to block until JDB is finished.
	 * 
	 * @throws		InterruptedException If the current thread is interrupted 
	 * by another thread while it is waiting
	 */
	public void waitFor() throws InterruptedException {
		process.waitFor();
	}
	
	public boolean isRunning() {
		return (process != null) && process.isAlive();
	}
}
