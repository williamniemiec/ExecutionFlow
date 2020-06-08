package executionFlow.runtime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import executionFlow.ExecutionFlow;


/**
 * Responsible for transactions. It will basically create a new file and will
 * keep it open until the program ends or the transaction ends.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		1.5
 * @since		1.5
 */
public class Transaction
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path transaction;
	private Thread transactionFileThread;
	private boolean end;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public Transaction(String name)
	{
		transaction = new File(ExecutionFlow.getAppRootPath(), name+".transaction").toPath();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public void end() throws IOException
	{
			end = true;
			
			try {
				Thread.sleep(50);
				transactionFileThread.join();
				delete();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}
	
	public void start() throws IOException
	{
		Files.createFile(transaction);
		
		Runnable r = () -> {
				try (FileReader fr = new FileReader(transaction.toFile())) {
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
		
		transactionFileThread = new Thread(r);
		transactionFileThread.start();
	}
	
	public boolean exists()
	{
		return Files.exists(transaction);
	}
	
	public boolean isActive()
	{
		if (!exists()) 
			return false;
		
		boolean response = false;

		try {
			Files.delete(transaction);
			Files.createFile(transaction);
		} catch (SecurityException | IOException e) {
			response = true;
		}
		
		return response;
	}
	
	public void delete() throws IOException
	{
		if (Files.exists(transaction)) {
			Files.delete(transaction);
		}
	}
}
