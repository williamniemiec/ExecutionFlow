package util.task;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

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
	private ReentrantLock lock;
	private Condition condLock;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * A checkpoint is used when it is necessary to mark a class or method to 
	 * see if it is being executed more than once.
	 * 
	 * @param		directory Directory where checkpoint will be created
	 * @param		name Checkpoint name
	 * 
	 * @throws		IllegalArgumentException If directory is null or if name is
	 * null or empty
	 */
	public Checkpoint(Path directory, String name) {
		if (directory == null)
			throw new IllegalArgumentException("Directory cannot be null");
		
		if ((name == null) || name.isBlank())
			throw new IllegalArgumentException("Name cannot be empty");
		
		checkpointFile = directory.resolve(name + ".checkpoint");
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
		createCheckpoint();
		startCheckpoint();
	}
	
	private void prepareCheckpoint() throws IOException {
		lock = new ReentrantLock();
		condLock = lock.newCondition();
		
		try {
			Files.deleteIfExists(checkpointFile);
			Files.createFile(checkpointFile);
		} 
		catch(FileAlreadyExistsException e) {
		}
	}

	private void createCheckpoint() {
		Runnable r = () -> {
			try (FileReader fr = new FileReader(checkpointFile.toFile())) {
				lock.lock();
				condLock.await();
				lock.unlock();
			}
			catch (IOException | InterruptedException e) {
			}
		};
		
		checkpointFileThread = new Thread(r);
	}
	
	private void startCheckpoint() {
		checkpointFileThread.start();
		
		try {
			Thread.sleep(200);
		} 
		catch (InterruptedException e) {
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
		lock.lock();
		condLock.signalAll();
		lock.unlock();
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
