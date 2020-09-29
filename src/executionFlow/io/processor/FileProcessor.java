package executionFlow.io.processor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import executionFlow.info.CollectorInfo;
import executionFlow.io.FileEncoding;


/**
 * A file processor will add or replace some code to an existing code if some
 * condition(s) is(are) met. Who will define these conditions will be the
 * classes that implement this class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.1.0
 * @since		2.0.0
 */
public abstract class FileProcessor implements Serializable 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 400L;
	
	protected FileEncoding encode = FileEncoding.UTF_8;
	protected transient Path file;
	protected transient Path outputDir;
	protected String outputFilename;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Processes the file, adding some code to an existing code if some 
	 * conditions are met.
	 * 
	 * @return		Location of parsed file
	 * 
	 * @throws		IOException If it cannot parse the file
	 */
	public abstract String processFile() throws IOException;
	
	/**
	 * Processes the file, adding some code to an existing code if some 
	 * conditions are met.
	 * 
	 * @param		collectors Information about all invoked collected
	 * 
	 * @return		Location of parsed file
	 * 
	 * @throws		IOException If it cannot parse the file
	 */
	public String processFile(Map<Integer, List<CollectorInfo>> collectors) throws IOException
	{
		return processFile();
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	/**
	 * Gets file encoding.
	 * 
	 * @return		File encoding
	 */
	public FileEncoding getEncoding()
	{
		return encode;
	}
	
	/**
	 * Sets file encoding.
	 * 
	 * @param		encoding File encoding
	 */
	public void setEncoding(FileEncoding encode)
	{
		this.encode = encode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos)
	{
		try {
			oos.defaultWriteObject();
			oos.writeUTF(file.toAbsolutePath().toString());
			oos.writeUTF(outputDir.toAbsolutePath().toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois)
	{
		try {
			ois.defaultReadObject();
			this.file = Path.of(ois.readUTF());
			this.outputDir = Path.of(ois.readUTF());
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
