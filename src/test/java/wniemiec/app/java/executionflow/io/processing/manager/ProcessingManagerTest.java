package wniemiec.app.java.executionflow.io.processing.manager;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.app.java.ExecutionFlow;
import wniemiec.app.java.executionflow.invoked.Invoked;
import wniemiec.app.java.executionflow.invoked.TestedInvoked;
import wniemiec.app.java.executionflow.io.processing.manager.ProcessingManager;
import wniemiec.io.java.Consolex;
import wniemiec.io.java.LogLevel;

class ProcessingManagerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private ProcessingManager processingManager;
	private final Invoked testedMethod;
	private final Invoked testMethod;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	ProcessingManagerTest() {
		final Path resourcesSrc = ExecutionFlow.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles", "wniemiec", "app",
						"java", "executionflow", "io", "processing", "manager")
		);
		final Path resourcesBin = ExecutionFlow.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles", "wniemiec", "app",
						"java", "executionflow", "io", "processing", "manager")
		);
		
		processingManager = ProcessingManager.getInstance();
		
		testedMethod = new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("testedinvoked.java"))
				.binPath(resourcesBin.resolve("testedinvoked.class"))
				.signature("auxfiles.wniemiec.app.java.executionflow.io.processing.manager.testedinvoked.m3(int)")
				.invocationLine(9)
				.build();
		
		testMethod = new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("testmethod.java"))
				.binPath(resourcesBin.resolve("testmethod.class"))
				.signature("auxfiles.wniemiec.app.java.executionflow.io.processing.manager.testmethod.method1()")
				.build();
		
		Consolex.setLoggerLevel(LogLevel.WARNING);
	}
	
	@AfterEach
	void clean() {
		processingManager.restoreOriginalFilesFromTestMethod();
		processingManager.restoreOriginalFilesFromInvoked();
		processingManager.deleteBackupFilesOfPreprocessingOfTestMethod();
		processingManager.deleteBackupFilesOfProcessingOfTestMethod();
		processingManager.deleteBackupFilesOfProcessingOfInvoked();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testDoPreprocessing() throws Exception {
		processingManager.initializeManagers();
		processingManager.doPreprocessingInTestMethod(testMethod);
		
		Assertions.assertTrue(processingManager.wasPreprocessingDoneSuccessfully());
		
		processingManager.restoreOriginalFilesFromTestMethod();
	}
	
	@Test
	void testDoProcessing() throws Exception {
		processingManager.initializeManagers();
		processingManager.doProcessingInTestedInvoked(new TestedInvoked(testedMethod, testMethod));
		
		Assertions.assertFalse(processingManager.wasPreprocessingDoneSuccessfully());
		
		processingManager.restoreOriginalFilesFromInvoked();
	}
	
	@Test
	void testRestoreOriginalFilesFromTestMethod() throws Exception {
		processingManager.initializeManagers();
		processingManager.doPreprocessingInTestMethod(testMethod);		
		
		Assertions.assertTrue(processingManager.restoreOriginalFilesFromTestMethod());
	}
	
	@Test
	void testRestoreOriginalFilesFromTestedInvoked() throws Exception {
		processingManager.initializeManagers();
		processingManager.doProcessingInTestedInvoked(
				new TestedInvoked(testedMethod, testMethod)
		);
		
		Assertions.assertTrue(processingManager.restoreOriginalFilesFromInvoked());
	}
	
	@Test
	void testResetLastProcessing() throws Exception {
		processingManager.initializeManagers();
		processingManager.doPreprocessingInTestMethod(testMethod);
		
		
		byte[] testMethodSrcFileContent = Files.readAllBytes(testMethod.getSrcPath());
		byte[] testMethodBinFileContent = Files.readAllBytes(testMethod.getBinPath());
		byte[] testedInvokedSrcFileContent = Files.readAllBytes(testedMethod.getSrcPath());
		byte[] testedInvokedBinFileContent = Files.readAllBytes(testedMethod.getBinPath());
		processingManager.doProcessingInTestedInvoked(
				new TestedInvoked(testedMethod, testMethod)
		);
		
		processingManager.undoLastProcessing();
		
		Assertions.assertArrayEquals(
				testMethodSrcFileContent, 
				Files.readAllBytes(testMethod.getSrcPath())
		);
		Assertions.assertArrayEquals(
				testMethodBinFileContent, 
				Files.readAllBytes(testMethod.getBinPath())
		);
		Assertions.assertArrayEquals(
				testedInvokedSrcFileContent, 
				Files.readAllBytes(testedMethod.getSrcPath())
		);
		Assertions.assertArrayEquals(
				testedInvokedBinFileContent, 
				Files.readAllBytes(testedMethod.getBinPath())
		);
	}
}
