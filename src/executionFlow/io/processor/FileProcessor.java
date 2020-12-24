package executionFlow.io.processor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import executionFlow.io.FileEncoding;
import executionFlow.util.FileUtil;
import executionFlow.util.formatter.JavaIndenter;
import executionFlow.util.logger.LogLevel;
import executionFlow.util.logger.Logger;

/**
 * A file processor will add or replace some code to an existing code if some
 * condition(s) is(are) met. Who will define these conditions will be the
 * classes that implement this class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		2.0.0
 */
public abstract class FileProcessor implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 400L;
	protected FileEncoding encoding = FileEncoding.UTF_8;
	protected transient Path file;
	protected transient Path outputFile;
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
	public final String processFile() throws IOException {
		if (file == null)
			return "";
		
		List<String> sourceCode = FileUtil.readLines(file, encoding.getStandardCharset());
		
		sourceCode = doProcessing(sourceCode);
		
		FileUtil.writeLines(sourceCode, outputFile, encoding.getStandardCharset());

		dump(sourceCode);
		
		return outputFile.toString();
	}
	
	protected abstract List<String> doProcessing(List<String> sourceCode);
	
	protected void dump(List<String> sourceCode) {
		if (Logger.getLevel() != LogLevel.DEBUG)
			return;
		
		JavaIndenter indenter = new JavaIndenter();
		List<String> formatedFile = indenter.format(sourceCode);

		Logger.debug(this.getClass(), "Processed file");
		FileUtil.printFileWithLines(formatedFile);
		
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters & Setters
	//-------------------------------------------------------------------------
	public FileEncoding getEncoding() {
		return encoding;
	}

	public void setEncoding(FileEncoding encode) {
		this.encoding = encode;
	}
	
	
	//-------------------------------------------------------------------------
	//		Serialization and deserialization methods
	//-------------------------------------------------------------------------
	private void writeObject(ObjectOutputStream oos) {
		try {
			oos.defaultWriteObject();
			oos.writeUTF(file.toAbsolutePath().toString());
			oos.writeUTF(outputFile.toAbsolutePath().toString());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream ois) {
		try {
			ois.defaultReadObject();
			this.file = Path.of(ois.readUTF());
			this.outputFile = Path.of(ois.readUTF());
		} 
		catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
}
