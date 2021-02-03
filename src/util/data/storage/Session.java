package util.data.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Session manager that stores information obtained at run time. It allows 
 * different processes and threads to have access to this information. There
 * are two types of session:
 * <ul>
 * 	<li>Normal - disk storage</li>
 * 	<li>Shared - storage in class data memory</li> 
 * </ul>
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 */
public class Session {
	
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private volatile Map<String, Object> content;
	private File sessionFile;
	private static volatile Map<String, Object> sharedContent = new HashMap<>();
	
	
	//-----------------------------------------------------------------------
	//		Constructor
	//-----------------------------------------------------------------------
	/**
	 * Creates a session that stores information obtained at run time.
	 * 
	 * @param		name Session name
	 * @param		directory Location where the session will be saved
	 * 
	 * @throws		IllegalArgumentException If name is null or empty or if
	 * directory is null
	 */
	public Session(String name, File directory)	{
		if ((name == null) || name.isBlank())
			throw new IllegalArgumentException("Name cannot be empty");
		
		if (directory == null)
			throw new IllegalArgumentException("Directory cannot be null");
		
		this.sessionFile = new File(directory, name + ".bin");
		this.content = new HashMap<>();
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	/**
	 * Stores data in the current session.
	 * 
	 * @param		key Identifier so that it is possible to retrieve data
	 * @param		value Data to be stored
	 * 
	 * @throws		IOException If an error occurred while storing the session
	 * 
	 * @implNote	To allow different processes and threads to have access to 
	 * session data, it is stored on disk
	 * 
	 * @throws		IllegalArgumentException If key or value is null
	 */
	public synchronized void save(String key, Object value) throws IOException {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (value == null)
			throw new IllegalArgumentException("Value cannot be null");
		
		if (exists()) {
			load();
			sessionFile.delete();			
		}
		
		sessionFile.createNewFile();
		
		content.put(key, value);
		
		store();
	}

	/**
	 * Gets data from the current session.
	 * 
	 * @param		key Information identifier 
	 * 
	 * @return		Stored information of null if there is no data stored with 
	 * the provided identifier
	 * 
	 * @implNote	It is read from class data memory
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 * @throws		IllegalStateException If session does not exist. For a 
	 * session to exist, something must be saved in it
	 * @throws		IllegalArgumentException If key is null
	 */
	public Object read(String key) throws IOException {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (!exists())
			throw new IllegalStateException("Session does not exist");
		
		load();
		
		return content.get(key);
	}
	
	/**
	 * Removes data from the current session.
	 * 
	 * @param		key Information identifier to be removed
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 * @throws		IllegalStateException If session does not exist. For a 
	 * session to exist, something must be saved in it
	 * @throws		IllegalArgumentException If key is null
	 */
	public void remove(String key) throws IOException {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (!exists())
			throw new IllegalStateException("Session does not exist");
		
		load();
		
		if (!content.containsKey(key))
			return;
		
		content.remove(key);
		
		store();
	}
	
	/**
	 * Checks  if there is data stored in the session with the specified key.
	 * 
	 * @param		key Identifier
	 * 
	 * @return		True if the key is associated with any data
	 * 
	 * @throws		IllegalArgumentException If key is null
	 * @throws		IllegalStateException If session does not exist. For a 
	 * session to exist, something must be saved in it
	 */
	public boolean hasKey(String key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (!exists())
			throw new IllegalStateException("Session does not exist");
		
		return content.containsKey(key);
	}
	
	/**
	 * Checks if a stored session exists.
	 * 
	 * @return		True if exists; false otherwise
	 */
	public boolean exists() {
		return sessionFile.exists();
	}
	
	/**
	 * Deletes the session.
	 * 
	 * @return		True if session has been destroyed or if it does not exist;
	 * false otherwise
	 */
	public synchronized boolean destroy() {
		if (!sessionFile.exists())
			return true;
		
		return sessionFile.delete();
	}
	
	/**
	 * Loads stored session.
	 * 
	 * @throws		IOException If an error occurred while retrieving the session
	 */
	@SuppressWarnings("unchecked")
	private void load() throws IOException {
		if (!exists())
			return;

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sessionFile))) {
			content = (Map<String, Object>)ois.readObject();
		}		
		catch (ClassNotFoundException e) {
		}
	}
	
	private void store() throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(sessionFile))) {
			oos.writeObject(content);
			oos.flush();
		}
	}
	
	/**
	 * Stores data in the current session.
	 * 
	 * @param		key Identifier so that it is possible to retrieve data
	 * @param		value Data to be stored
	 * 
	 * @throws		IOException If an error occurred while storing the session
	 * 
	 * @implNote	To allow different processes and threads to have access to 
	 * session data, it is stored in class data memory
	 * 
	 * @throws		IllegalArgumentException If key or value is null
	 */
	public static synchronized void saveShared(String key, Object value) 
			throws IOException {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (value == null)
			throw new IllegalArgumentException("Value cannot be null");
		
		sharedContent.put(key, value);
	}
	
	/**
	 * Gets data from the current session.
	 * 
	 * @param		key Information identifier 
	 * 
	 * @return		Stored information of null if there is no data stored with 
	 * the provided identifier
	 * 
	 * @implNote	It is read from class data memory
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 * @throws		IllegalStateException If session does not exist. For a 
	 * session to exist, something must be saved in it
	 * @throws		IllegalArgumentException If key is null
	 */
	public static Object readShared(String key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		if (!sharedContent.containsKey(key))
			throw new IllegalStateException("Key not found");
		
		return sharedContent.get(key);
	}
	
	/**
	 * Checks  if there is data stored in the session with the specified key.
	 * 
	 * @param		key Identifier
	 * 
	 * @return		True if the key is associated with any data
	 * 
	 * @throws		IllegalArgumentException If key is null
	 */
	public static boolean hasKeyShared(String key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");
		
		return sharedContent.containsKey(key);
	}
	
	/**
	 * Removes data from the current session.
	 * 
	 * @param		key Information identifier to be removed
	 * 
	 * @throws		IOException If an error occurred while loading the session
	 * @throws		IllegalStateException If session does not exist. For a 
	 * session to exist, something must be saved in it
	 * @throws		IllegalArgumentException If key is null
	 */
	public static void removeShared(String key) {
		if (key == null)
			throw new IllegalArgumentException("Key cannot be null");

		if (!sharedContent.containsKey(key))
			return;
		
		sharedContent.remove(key);
	}
}
