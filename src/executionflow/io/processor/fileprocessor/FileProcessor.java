package executionflow.io.processor.fileprocessor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;

import executionflow.io.FileEncoding;
import util.io.console.ConsoleFilePrinter;
import util.io.manager.TextFileManager;
import util.io.processor.JavaCodeIndenter;
import util.logger.LogLevel;
import util.logger.Logger;

/**
 * A file processor will add or replace some code to an existing code if some
 * condition(s) is(are) met. Who will define these conditions will be the
 * classes that implement this class.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.5
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
		
		List<String> sourceCode = readLinesOf(file);
		
		sourceCode = doProcessing(sourceCode);
		
		writeLinesInOutputFile(sourceCode);

		dump(sourceCode);
		
		return outputFile.toString();
	}

	private List<String> readLinesOf(Path file) throws IOException {
		TextFileManager txtFileManager = new TextFileManager(
				file, 
				encoding.getStandardCharset()
		);
		
		return txtFileManager.readLines();
	}
	
	protected abstract List<String> doProcessing(List<String> sourceCode);
	
	private void writeLinesInOutputFile(List<String> lines) throws IOException {
		TextFileManager outFileManager = new TextFileManager(
				outputFile, 
				encoding.getStandardCharset()
		);
		
		outFileManager.writeLines(lines);
	}
	
	protected void dump(List<String> sourceCode) {
		if (Logger.getLevel() != LogLevel.DEBUG)
			return;
		
		JavaCodeIndenter indenter = new JavaCodeIndenter();
		List<String> indentedFile = indenter.indent(sourceCode);

		Logger.debug(this.getClass(), "Processed file");
		ConsoleFilePrinter.printFileWithLines(indentedFile);
		
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
