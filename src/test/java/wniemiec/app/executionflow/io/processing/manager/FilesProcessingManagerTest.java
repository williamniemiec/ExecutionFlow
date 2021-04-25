package wniemiec.app.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.App;
import wniemiec.app.executionflow.io.processing.file.ProcessorType;
import wniemiec.app.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;

class FilesProcessingManagerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Path srcPath;
	private Path binPath;
	private String pkg;
	private FileProcessingManager fileProcessingManager;
	private FilesProcessingManager processingManager;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	FilesProcessingManagerTest() {
		srcPath = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles", "wniemiec", "app",
						"executionflow", "io", "processing", "manager", 
						"fileprocessing.java")
		);
		
		binPath = App.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles", "wniemiec", "app", 
						"executionflow","io", "processing", "manager", 
						"fileprocessing.class")
		);
		
		pkg = "auxfiles.wniemiec.app.executionflow.io.processing.manager";
		fileProcessingManager = createFileProcessingManager();
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void prepare() throws ClassNotFoundException, IOException {
		processingManager = new FilesProcessingManager(
				ProcessorType.INVOKED
		);
	}
	
	@AfterEach
	void clean() {
		processingManager.restoreAll();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testProcessing() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		
		Assertions.assertTrue(processingManager.wasProcessed(fileProcessingManager));
	}
	
	@Test
	void testCompilation() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		processingManager.compile(fileProcessingManager);
		
		Assertions.assertTrue(processingManager.wasCompiled(fileProcessingManager));
	}
	
	@Test
	void testCreateBackup() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		processingManager.compile(fileProcessingManager);
		processingManager.createBackup();
		
		Assertions.assertTrue(processingManager.hasBackupStored());
		
		processingManager.deleteBackup();
	}
	
	@Test
	void testRestoreFromBackup() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		processingManager.compile(fileProcessingManager);
		processingManager.createBackup();
		
		Assertions.assertTrue(processingManager.loadBackup());
		
		processingManager.deleteBackup();
	}
	
	@Test
	void testRemoveProcessedFile() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		Assertions.assertTrue(processingManager.wasProcessed(fileProcessingManager));
		
		processingManager.remove(fileProcessingManager);
		Assertions.assertFalse(processingManager.wasProcessed(fileProcessingManager));
	}
	
	@Test
	void testRemoveCompiledFile() 
			throws ClassNotFoundException, Exception {
		processingManager.compile(fileProcessingManager);
		Assertions.assertTrue(processingManager.wasCompiled(fileProcessingManager));
		
		processingManager.remove(fileProcessingManager);
		Assertions.assertFalse(processingManager.wasCompiled(fileProcessingManager));
	}
	
	@Test
	void testRemoveBackup() 
			throws ClassNotFoundException, Exception {
		processingManager.process(fileProcessingManager);
		processingManager.createBackup();
		processingManager.deleteBackup();
		
		Assertions.assertFalse(processingManager.hasBackupStored());
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
