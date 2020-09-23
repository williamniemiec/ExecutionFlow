package executionFlow.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Simple API for JBD (Java debugger).
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		1.2
 */
public class JDB
{
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
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	public JDB(Path workingDirectory, List<String> classPath, List<String> srcPath, String classSignature, String classArgs)
	{
		if (classPath == null)
			throw new IllegalStateException("Class path list cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path list cannot be empty");

		processBuilder = new ProcessBuilder(
			"jdb",
				"-sourcepath", DataUtil.implode(srcPath, ";"),
				"-classpath", DataUtil.implode(classPath, ";"),
				classSignature, classArgs
		);
		processBuilder.directory(workingDirectory.toFile());
	}
	
	/**
	 * Creates API for JDB.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 */
	public JDB(Path workingDirectory, List<String> classPath, List<String> srcPath)
	{
		if (classPath == null)
			throw new IllegalStateException("Class path list cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path list cannot be empty");

		processBuilder = new ProcessBuilder(
			"jdb",
				"-sourcepath", DataUtil.implode(srcPath, ";"),
				"-classpath", DataUtil.implode(classPath, ";")
		);
		processBuilder.directory(workingDirectory.toFile());
	}
	
	/**
	 * Creates API for JDB.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	public JDB(Path workingDirectory, String classSignature, String args)
	{
		processBuilder = new ProcessBuilder(
			"jdb",	classSignature, args
		);
		processBuilder.directory(workingDirectory.toFile());
	}
	
	/**
	 * Creates API for JDB. Using this constructor, working directory will be
	 * current directory.
	 * 
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	public JDB(List<String> classPath, List<String> srcPath, String classSignature, String classArgs)
	{
		if (classPath == null)
			throw new IllegalStateException("Class path list cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path list cannot be empty");

		processBuilder = new ProcessBuilder(
			"jdb",
				"-sourcepath", DataUtil.implode(srcPath, ";"), 
				"-classpath", DataUtil.implode(classPath, ";")
		);
	}
	
	/**
	 * Creates API for JDB. Using this constructor, working directory will be
	 * current directory.
	 * 
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 */
	public JDB(List<String> classPath, List<String> srcPath)
	{
		if (classPath == null)
			throw new IllegalStateException("Class path list cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path list cannot be empty");

		processBuilder = new ProcessBuilder(
			"jdb",
				"-sourcepath", DataUtil.implode(srcPath, ";"),
				"-classpath", DataUtil.implode(classPath, ";")
		);
	}
	
	/**
	 * Creates API for JDB. Using this constructor, working directory will be
	 * current directory.
	 * 
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	public JDB(String classSignature, String args)
	{
		processBuilder = new ProcessBuilder(
			"jdb",	classSignature, args
		);
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
	 * 
	 * @implNote	If the parent process is terminated, this process will also 
	 * terminate
	 */
	public JDB start() throws IOException
	{
		process = processBuilder.start();
		out = new JDBOutput();
		in = new JDBInput();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() {
		    	in.close();
				out.close();
		    	process.destroyForcibly();
		    }
		});

		return this;
	}

	/**
	 * Ends the process containing JDB.
	 */
	public void quit()
	{
		if (!process.isAlive())
			return;
		
		in.close();
		out.close();
		process.destroyForcibly();
		
		try {
			process.waitFor();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a command to JDB. After calling this method, you must to call
	 * {@link #read()} for JDB to process the command.
	 * 
	 * @param		command Command that will be sent to JDB
	 * 
	 * @apiNote		If {@link #DEBUG} is activated, it will display the 
	 * command executed on the console
	 */
	public JDB send(String command)
	{
		in.send(command);
		
		return this;
	}
	
	/**
	 * Sends commands to JDB. After calling this method, you must to call
	 * {@link #read()} for JDB to process these commands.
	 * 
	 * @param		commands Commands that will be sent to JDB
	 * 
	 * @apiNote		If {@link #DEBUG} is activated, it will display the 
	 * command executed on the console
	 */
	public JDB send(String... commands)
	{
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
	 * 
	 * @apiNote		If {@link #DEBUG} is activated, it will display JDB 
	 * output on the console
	 */
	public String read()
	{
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
	public List<String> readAll() throws IOException
	{
		return out.readAll();
	}
	
	/**
	 * Checks if there is an output available. 
	 * 
	 * @return		True if {@link #read()} is guaranteed not to block if
	 * called; otherwise, returns false
	 */
	public boolean isReady()
	{
		return out.isReady();
	}
	
	/**
	 * Checks whether the JDB process is running.
	 * 
	 * @return		True if the process is running; false otherwise 
	 */
	public boolean isRunning()
	{
		return process.isAlive();
	}
	
	/**
	 * Causes the current thread to wait, if necessary, until the JDB process 
	 * has terminated.
	 * 
	 * @throws		InterruptedException If the current thread is interrupted 
	 * by another thread while it is waiting
	 */
	public void waitFor() throws InterruptedException
	{
		process.waitFor();
	}
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Responsible for handling JDB inputs.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since		1.4
	 */
	private class JDBInput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private PrintWriter input;
		
		
		//---------------------------------------------------------------------
		//		Constructor
		//---------------------------------------------------------------------
		/**
		 * JDB input manager. It should be used in conjunction with 
		 * {@link JDBOutput}.
		 */
		public JDBInput()
		{
			this.input = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(process.getOutputStream())));
		}
		
		
		//---------------------------------------------------------------------
		//		Methods
		//---------------------------------------------------------------------		
		/**
		 * Sends a command to JDB. After calling this method, you must to call
		 * {@link JDBOutput#read()} for JDB to process the command.
		 * 
		 * @param		command Command that will be sent to JDB
		 * 
		 * @apiNote		If {@link #DEBUG} is activated, it will display the 
		 * command executed on the console
		 */
		public void send(String command)
		{
			input.println(command);
			input.flush();
		}
		
		/**
		 * Sends commands to JDB. After calling this method, you must to call
		 * {@link JDBOutput#read()} for JDB to process these commands.
		 * 
		 * @param		command Commands that will be sent to JDB
		 * 
		 * @apiNote		If {@link #DEBUG} is activated, it will display the 
		 * command executed on the console
		 */
		public void send(String... commands)
		{
			for (String command : commands) {
			    input.println(command);
			}
			
			input.flush();
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			input.close();
		}
	}
	
	/**
	 * Responsible for handling JDB outputs.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
	 * @version		2.0.0
	 * @since		1.4
	 */
	private class JDBOutput
	{
		//---------------------------------------------------------------------
		//		Attributes
		//---------------------------------------------------------------------
		private BufferedReader output;
		
        
        //---------------------------------------------------------------------
    	//		Constructor
    	//---------------------------------------------------------------------
        /**
         * JDB output manager. It should be used in conjunction with 
		 * {@link JDBInputt} to be able to send commands. 
         */
		public JDBOutput()
		{
			output = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
		}
		
		
		//---------------------------------------------------------------------
    	//		Methods
    	//---------------------------------------------------------------------
		/**
		 * Reads JDB output. This method will block until some output is
		 * available, an I/O error occurs, or the end of the stream is reached.
		 * 
		 * @return		JDB output
		 * 
		 * @throws		IOException If it cannot read JDB output
		 * 
		 * @apiNote		If {@link #DEBUG} is activated, it will display JDB 
		 * output on the console
		 */
		public String read() throws IOException
		{
			return output.readLine();
		}
		
		/**
		 * Checks if there is output available. 
		 * 
		 * @return		True if {@link #read()} is guaranteed not to block if
		 * called; otherwise, returns false
		 */
		public boolean isReady()
		{
			try {
				return output.ready();
			} 
			catch (IOException e) {
				return false;
			}
		}
		
		/**
		 * Reads all available JDB output. This method will not block if no
		 * output is available.
		 * 
		 * @return		List of read JDB output
		 * 
		 * @throws		IOException If it cannot read JDB output
		 */
		public List<String> readAll() throws IOException
		{
			List<String> response = new ArrayList<>();
			
			
			while (output.ready()) {
				response.add(read());
			}
			
			return response;
		}
		
		/**
		 * Closes JDB input.
		 */
		public void close()
		{
			try {
				output.close();
			} 
			catch (IOException e) { }
		}
	}
}
