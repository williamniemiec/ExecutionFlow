package wniemiec.app.java.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.io.processing.file.ProcessorType;
import wniemiec.app.java.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.app.java.executionflow.io.processing.manager.FileProcessingManager;
import wniemiec.app.java.executionflow.io.processing.manager.FilesProcessingManager;
import wniemiec.app.java.executionflow.io.processing.manager.InvokedProcessingManager;
import wniemiec.io.java.Consolex;
import wniemiec.io.java.LogLevel;

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
		srcPath = ExecutionFlow.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles", "wniemiec", 
						"app", "java", "executionflow", "io", "processing", "manager", 
						"fileprocessing.java")
		);
		
		binPath = ExecutionFlow.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles", "wniemiec", "app", "java",
						"executionflow", "io", "processing", "manager", 
						"fileprocessing.class")
		);
		
		pkg = "auxfiles.wniemiec.app.java.executionflow.io.processing.manager";
		
		fileProcessingManager = createFileProcessingManager();
		filesProcessingManager = new FilesProcessingManager(
				ProcessorType.INVOKED
		);
		
		Consolex.setLoggerLevel(LogLevel.WARNING);
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
	void testProcessAndCompile() throws ClassNotFoundException, Exception {
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
	void testDeleteBackup() throws Exception {
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
