package executionflow.io.processor.fileprocessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import executionflow.io.FileEncoding;
import executionflow.util.FileUtils;
import util.io.formatter.JavaIndenter;
import util.logger.LogLevel;
import util.logger.Logger;

/**
 * A file processor will add or replace some code to an existing code if some
 * condition(s) is(are) met. Who will define these conditions will be the
 * classes that implement this class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		2.0.0
 */
public abstract class FileProcessor implements Serializable {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final long serialVersionUID = 600L;
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
		
		List<String> sourceCode = FileUtils.readLines(file, encoding.getStandardCharset());
		
		sourceCode = doProcessing(sourceCode);
		
		FileUtils.writeLines(sourceCode, outputFile, encoding.getStandardCharset());

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
		
		if (formatedFile.size() < 30)
			FileUtils.printFileWithLines(sourceCode);
		else
			FileUtils.printFileWithLines(formatedFile);
		
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
