package executionFlow.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import executionFlow.ExecutionFlow;


/**
 * Responsible for checkpoints. A checkpoint is a class and method marker, and
 * it is used when you want to know if a method or class is being executed more
 * than once, allowing the program to behave in the first execution different 
 * from the others. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class Checkpoint
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path checkpointFile;
	private Thread checkpointFileThread;
	private boolean end;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * A checkpoint is used when it is necessary to mark a class or method to 
	 * see if it is being executed more than once.
	 * 
	 * @param		name Checkpoint name
	 */
	public Checkpoint(String name)
	{
		checkpointFile = new File(ExecutionFlow.getAppRootPath(), name+".checkpoint").toPath();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Starts a new checkpoint.
	 * 
	 * @throws		IOException If checkpoint file cannot be created
	 * 
	 * @implSpec	It will basically create a new file and will keep it 
	 * open until the program ends or the checkpoint is disabled
	 */
	public void enable() throws IOException
	{
		Files.createFile(checkpointFile);
		
		Runnable r = () -> {
			try (FileReader fr = new FileReader(checkpointFile.toFile())) {
				while (!end) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		checkpointFileThread = new Thread(r);
		checkpointFileThread.start();
	}
	
	/**
	 * Defines the end of a checkpoint.
	 * 
	 * @throws 		IOException If an error occurs when deleting the checkpoint
	 * file
	 * 
	 * @implSpec	It will disable and delete the checkpoint file
	 */
	public void disable() throws IOException
	{
		end = true;
		
		try {
			Thread.sleep(50);
			checkpointFileThread.join();
			delete();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if a checkpoint was created.
	 * 
	 * @return If a checkpoint was created
	 */
	public boolean exists()
	{
		return Files.exists(checkpointFile);
	}
	
	/**
	 * Checks if the checkpoint is in execution.
	 * 
	 * @return		If the checkpoint is in execution
	 * 
	 * @implSpec	It will try to delete the checkpoint file. If it cannot do
	 * this, it means that the checkpoint is active; otherwise, the checkpoint
	 * is active, so it must recreate the checkpoint file that was deleted
	 */
	public boolean isActive()
	{
		// If the checkpoint does not exist, it is not active
		if (!exists()) 
			return false;
		
		boolean response = false;
		
		// Tries to delete the checkpoint file. If no exception is thrown, it 
		// means that the checkpoint is not active; otherwise, it is active
		try {
			Files.delete(checkpointFile);
			
			// Restores deleted checkpoint file
			Files.createFile(checkpointFile);
		} catch (SecurityException | IOException e) {
			response = true;
		}
		
		return response;
	}
	
	/**
	 * Deletes the checkpoint file.
	 * 
	 * @throws		IOException If the checkpoint is active
	 */
	public void delete() throws IOException
	{
		if (Files.exists(checkpointFile)) {
			Files.delete(checkpointFile);
		}
	}
}
