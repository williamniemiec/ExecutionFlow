package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.App;
import wniemiec.executionflow.io.processing.file.ProcessorType;
import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;

class InvokedProcessingManagerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path srcPath;
	private Path binPath;
	private String pkg;
	private FileProcessingManager fileProcessingManager;
	private FilesProcessingManager filesProcessingManager;
	private InvokedProcessingManager invokedProcessingManager;
	
	
	//-------------------------------------------------------------------------
	//		Constructors
	//-------------------------------------------------------------------------
	InvokedProcessingManagerTest() throws ClassNotFoundException, IOException {
		srcPath = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "resources", "wniemiec",
						"executionflow", "io", "processing", "manager", 
						"fileprocessing.java")
		);
		
		binPath = App.getCurrentProjectRoot().resolve(
				Path.of(".", "target", "test-classes", "wniemiec", 
						"executionflow", "io", "processing", "manager", 
						"fileprocessing.class")
		);
		
		pkg = "wniemiec.executionflow.io.processing.manager";
		
		fileProcessingManager = createFileProcessingManager();
		filesProcessingManager = new FilesProcessingManager(
				ProcessorType.INVOKED
		);
		
		Logger.setLevel(LogLevel.WARNING);
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void prepare() throws ClassNotFoundException, IOException {
		invokedProcessingManager = new InvokedProcessingManager(filesProcessingManager);
	}
	
	@AfterEach
	void restore() throws IOException {
		invokedProcessingManager.restoreInvokedOriginalFiles();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testProcessAndCompile() throws ClassNotFoundException, IOException {
		FileTime binFileTime = Files.getLastModifiedTime(binPath);
		FileTime srcFileTime = Files.getLastModifiedTime(srcPath);
		
		invokedProcessingManager.processAndCompile(fileProcessingManager);
		
		Assertions.assertNotEquals(
				binFileTime, 
				Files.getLastModifiedTime(binPath)
		);
		
		Assertions.assertNotEquals(
				srcFileTime, 
				Files.getLastModifiedTime(srcPath)
		);
	}
	
	@Test
	void testProcessAndCompileWithNullFileProcessingManager() 
			throws ClassNotFoundException, IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			invokedProcessingManager.processAndCompile(null);			
		});
	}
	
	@Test
	void testProcessAndCompileWithFilesProcessingManagerDestroyed() 
			throws ClassNotFoundException, IOException {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			invokedProcessingManager.destroyInvokedFilesManager();
			invokedProcessingManager.processAndCompile(fileProcessingManager);			
		});
	}
	
	@Test
	void testDestroy() {
		invokedProcessingManager.destroyInvokedFilesManager();
		
		Assertions.assertFalse(invokedProcessingManager.isInvokedFilesManagerInitialized());
	}
	
	@Test
	void testDeleteBackup() throws IOException {
		invokedProcessingManager.processAndCompile(fileProcessingManager);
		invokedProcessingManager.restoreInvokedOriginalFile(fileProcessingManager);
		
		Assertions.assertTrue(invokedProcessingManager.hasBackupFiles());
		invokedProcessingManager.deleteBackupFiles();
		Assertions.assertFalse(invokedProcessingManager.hasBackupFiles());
	}
	
	@Test
	void testDeleteBackupWithFilesProcessingManagerDestroyed() 
			throws ClassNotFoundException, IOException {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			invokedProcessingManager.destroyInvokedFilesManager();
			invokedProcessingManager.deleteBackupFiles();
		});
	}
	
	@Test
	void testRestoreNullInvokedOriginalFile() 
			throws ClassNotFoundException, IOException {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			invokedProcessingManager.restoreInvokedOriginalFile(null);	
		});
	}
	
	@Test
	void testRestoreInvokedOriginalFileWithFilesProcessingManagerDestroyed() 
			throws ClassNotFoundException, IOException {
		Assertions.assertThrows(IllegalStateException.class, () -> {
			invokedProcessingManager.destroyInvokedFilesManager();
			invokedProcessingManager.restoreInvokedOriginalFile(fileProcessingManager);	
		});
	}
	
		
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private FileProcessingManager createFileProcessingManager() {
		return new FileProcessingManager.Builder()
				.srcPath(srcPath)
				.binPath(binPath)
				.filePackage(pkg)
				.backupExtensionName("bkp")
				.fileProcessorFactory(new InvokedFileProcessorFactory())
				.build();
	}
}
