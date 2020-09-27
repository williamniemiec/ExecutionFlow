package executionFlow.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;


/**
 * A checkpoint is a class and method marker, and it is used when you want  
 * a piece of code, even if executed by several independent processes, to be
 * executed only once. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class Checkpoint
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path checkpointFile;
	private Thread checkpointFileThread;
	private volatile boolean end;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * A checkpoint is used when it is necessary to mark a class or method to 
	 * see if it is being executed more than once.
	 * 
	 * @param		location Path where checkpoint will be created
	 * @param		name Checkpoint name
	 */
	public Checkpoint(Path location, String name)
	{
		checkpointFile = Path.of(location.toAbsolutePath().toString(), name+".checkpoint");
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
		end = false;
		
		try {
			Files.deleteIfExists(checkpointFile);
			Files.createFile(checkpointFile);
		} 
		catch(FileAlreadyExistsException e) {}
		
		Runnable r = () -> {
			try (FileReader fr = new FileReader(checkpointFile.toFile())) {
				while (!end) {
					try {
						Thread.sleep(200);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		};
		
		checkpointFileThread = new Thread(r);
		checkpointFileThread.start();
	}
	
	/**
	 * Disables checkpoint.
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
		} 
		catch (InterruptedException e) {
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
		} 
		catch (SecurityException | IOException e) {
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
