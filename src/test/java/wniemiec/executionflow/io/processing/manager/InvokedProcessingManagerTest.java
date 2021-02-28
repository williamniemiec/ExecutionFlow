package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.processing.file.ProcessorType;
import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;

class InvokedProcessingManagerTest {

	private Path srcPath = Path.of(".", "src", "test", "resources", "wniemiec", 
			"executionflow", "io", "processing", "manager", "fileprocessing.java");
	private Path binPath = Path.of(".", "target", "test-classes", "wniemiec", 
			"executionflow", "io", "processing", "manager", "fileprocessing.class");
	private String pkg = "wniemiec.executionflow.io.processing.manager";
	private FileProcessingManager fileProcessingManager;
	private FilesProcessingManager filesProcessingManager;
	private InvokedProcessingManager invokedProcessingManager;
	
	InvokedProcessingManagerTest() throws ClassNotFoundException, IOException {
		fileProcessingManager = createFileProcessingManager();
		filesProcessingManager = new FilesProcessingManager(
				ProcessorType.INVOKED
		);
		
		Logger.setLevel(LogLevel.WARNING);
	}
	
	@BeforeEach
	void prepare() throws ClassNotFoundException, IOException {
		invokedProcessingManager = new InvokedProcessingManager(filesProcessingManager);
	}
	
	@AfterEach
	void restore() throws IOException {
		invokedProcessingManager.restoreInvokedOriginalFiles();
	}
	
	@Test
	void testProcessAndCompile() throws ClassNotFoundException, IOException {
		invokedProcessingManager.processAndCompile(fileProcessingManager);
		
		assertSrcFileIsProcessed();
		assertSrcFileIsCompiledAfterProcessing();
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
			invokedProcessingManager.processAndCompile(fileProcessingManager);
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
	
	private void assertSrcFileIsCompiledAfterProcessing() throws IOException {
		FileTime binFileTime = Files.getLastModifiedTime(binPath);
		
		fileProcessingManager.processFile(false);
		fileProcessingManager.compileFile();
		
		Assertions.assertNotEquals(
				binFileTime, 
				Files.getLastModifiedTime(binPath)
		);
	}

	private void assertSrcFileIsProcessed() throws IOException {
		FileTime srcFileTime = Files.getLastModifiedTime(srcPath);
		
		fileProcessingManager.processFile(false);
		
		Assertions.assertNotEquals(
				srcFileTime, 
				Files.getLastModifiedTime(srcPath)
		);
	}
	
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