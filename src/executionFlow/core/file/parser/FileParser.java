package executionFlow.core.file.parser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

import executionFlow.core.file.FileEncoding;


/**
 * A file parser will add some code to an existing code if some conditions are 
 * met. Who will define these conditions will be the classes that implement
 * this class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.4
 */
public abstract class FileParser implements Serializable 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 105L;
	
	protected FileEncoding encode = FileEncoding.UTF_8;
	protected transient Path file;
	protected transient Path outputDir;
	protected String outputFilename;
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Parses file, adding some code to an existing code if some conditions are 
	 * met
	 * 
	 * @return		Location of parsed file
	 * @throws		IOException If it cannot parse the file
	 */
	public abstract String parseFile() throws IOException;
	
	
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
