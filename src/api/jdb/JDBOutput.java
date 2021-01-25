package api.jdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for handling JDB outputs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
class JDBOutput {
	
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
	public JDBOutput(Process jdbProcess) {
		output = new BufferedReader(
				new InputStreamReader(
						jdbProcess.getInputStream()
		));
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
	 */
	public String read() throws IOException {
		return output.readLine();
	}
	
	/**
	 * Checks if there is output available. 
	 * 
	 * @return		True if {@link #read()} is guaranteed not to block if
	 * called; otherwise, returns false
	 */
	public boolean isReady() {
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
	public List<String> readAll() throws IOException {
		List<String> response = new ArrayList<>();
		
		while (output.ready()) {
			response.add(read());
		}
		
		return response;
	}
	
	/**
	 * Closes JDB input.
	 */
	public void close() {
		try {
			output.close();
		} 
		catch (IOException e) {
		}
	}
}
