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
	 * @param		classPath Class path that will be used in JDB
	 * @param		srcPath Source path that will be used in JDB
	 * @param		classSignature Class signature to begin debugging
	 * @param		args Arguments passed to the main() method of classSignature
	 */
	public JDB(Path workingDirectory, String classPath, String srcPath, String classSignature, String classArgs)
	{
		if (classPath == null)
			throw new IllegalStateException("Class path list cannot be empty");
		
		if (srcPath == null)
			throw new IllegalStateException("Source path list cannot be empty");

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
	public JDB(Path workingDirectory, List<Path> classPath, List<Path> srcPath, String classSignature, String classArgs)
	{
		this(
				workingDirectory, 
				DataUtil.implode(classPath, ";"), 
				DataUtil.implode(srcPath, ";"), 
				classSignature, 
				classArgs
		);
	}
	
	/**
	 * Creates API for JDB.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 */
	public JDB(Path workingDirectory, List<Path> classPath, List<Path> srcPath)
	{
		this(
				workingDirectory, 
				DataUtil.implode(classPath, ";"), 
				DataUtil.implode(srcPath, ";"),
				null,
				null
		);
	}
	
	/**
	 * Creates API for JDB.
	 * 
	 * @param		workingDirectory Directory that will serve as a reference
	 * for the execution of the process
	 * @param		classPathArgumentFile Argument file containing class path 
	 * list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 */
	public JDB(Path workingDirectory, Path classPathArgumentFile, List<Path> srcPath)
	{
		this(
				workingDirectory, 
				"@" + classPathArgumentFile, 
				DataUtil.implode(srcPath, ";"),
				null,
				null
		);
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
	public JDB(List<Path> classPath, List<Path> srcPath, String classSignature, String classArgs)
	{
		this(
				null, 
				DataUtil.implode(srcPath, ";"), 
				DataUtil.implode(srcPath, ";"),
				classSignature,
				classArgs
		);
	}
	
	/**
	 * Creates API for JDB. Using this constructor, working directory will be
	 * current directory.
	 * 
	 * @param		classPath Class path list that will be used in JDB
	 * @param		srcPath Source path list that will be used in JDB
	 */
	public JDB(List<Path> classPath, List<Path> srcPath)
	{
		this(
				null, 
				DataUtil.implode(srcPath, ";"), 
				DataUtil.implode(srcPath, ";"),
				null,
				null
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
	 */
	public JDB start() throws IOException
	{
		process = processBuilder.start();
		out = new JDBOutput();
		in = new JDBInput();
		
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
		catch (IllegalStateException e)
		{}

		
		return this;
	}

	/**
	 * Terminates JDB.
	 */
	public void quit()
	{
		in.close();
		out.close();
		process.destroy();
		
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
	
	
	//-------------------------------------------------------------------------
	//		Inner classes
	//-------------------------------------------------------------------------
	/**
	 * Responsible for handling JDB inputs.
	 * 
	 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
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
					new OutputStreamWriter(process.getOutputStream())), true);
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
