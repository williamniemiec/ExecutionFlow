package api.jdb;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Responsible for handling JDB input.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
class JDBInput {
	
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
	public JDBInput(Process jdbProcess) {
		this.input = new PrintWriter(
				new BufferedWriter(
						new OutputStreamWriter(
								jdbProcess.getOutputStream())), 
				true
		);
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
	 * @throws		IllegalStateException If input is closed
	 */
	public void send(String command) {
		if (input == null)
			throw new IllegalStateException("Input is closed");
		
		input.println(command);
	}
	
	/**
	 * Sends commands to JDB. After calling this method, you must to call
	 * {@link JDBOutput#read()} for JDB to process these commands.
	 * 
	 * @param		command Commands that will be sent to JDB
	 * 
	 * @throws		IllegalStateException If input is closed
	 */
	public void send(String... commands) {
		if (input == null)
			throw new IllegalStateException("Input is closed");
		
		for (String command : commands) {
		    input.println(command);
		}
	}
	
	/**
	 * Closes JDB input.
	 */
	public void close() {
		if (input == null)
			return;
		
		input.close();
		input = null;
	}
}