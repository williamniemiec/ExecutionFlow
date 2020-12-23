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
public class Checkpoint {
	
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
	public Checkpoint(Path location, String name) {
		checkpointFile = location.resolve(name + ".checkpoint");
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
	public void enable() throws IOException {
		prepareCheckpoint();
		checkpointFileThread = createCheckpoint();
		checkpointFileThread.start();
	}

	private Thread createCheckpoint() {
		Runnable r = () -> {
			try (FileReader fr = new FileReader(checkpointFile.toFile())) {
				while (!end) {
					try {
						Thread.sleep(200);
					} 
					catch (InterruptedException e) {
					}
				}
			} 
			catch (FileNotFoundException e) {
			} 
			catch (IOException e) {
			}
		};
		
		return new Thread(r);
	}

	private void prepareCheckpoint() throws IOException {
		end = false;
		
		try {
			Files.deleteIfExists(checkpointFile);
			Files.createFile(checkpointFile);
		} 
		catch(FileAlreadyExistsException e) {
		}
	}
	
	/**
	 * Disables checkpoint.
	 * 
	 * @throws 		IOException If an error occurs when deleting the checkpoint
	 * file
	 * 
	 * @implSpec	It will disable and delete the checkpoint file
	 */
	public void disable() throws IOException {
		if (checkpointFileThread == null)
			return;
		
		disableCheckpoint();
		destroyCheckpoint();
	}

	private void destroyCheckpoint() throws IOException {
		try {
			Thread.sleep(50);
			checkpointFileThread.join();
			delete();
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void disableCheckpoint() {
		end = true;
	}
	
	/**
	 * Checks if a checkpoint was created.
	 * 
	 * @return		True if a checkpoint was created; false otherwise
	 */
	public boolean exists() {
		return Files.exists(checkpointFile);
	}
	
	/**
	 * Checks if the checkpoint is active.
	 * 
	 * @return		True if the checkpoint is running; false otherwise
	 * 
	 * @implSpec	It will try to delete the checkpoint file. If it cannot do
	 * this, it means that the checkpoint is active; otherwise, the checkpoint
	 * is active, so it must recreate the checkpoint file that was deleted
	 */
	public boolean isEnabled() {
		if (!exists()) 
			return false;
		
		boolean enabled = false;
		
		// Tries to delete the checkpoint file. If no exception is thrown, it 
		// means that the checkpoint is not active; otherwise, it is active
		try {
			Files.delete(checkpointFile);
			
			// Restores deleted checkpoint file
			Files.createFile(checkpointFile);
		} 
		catch (SecurityException | IOException e) {
			enabled = true;
		}
		
		return enabled;
	}
	
	/**
	 * Deletes the checkpoint file.
	 * 
	 * @throws		IOException If the checkpoint is active
	 */
	public void delete() throws IOException {
		Files.deleteIfExists(checkpointFile);
	}
}
