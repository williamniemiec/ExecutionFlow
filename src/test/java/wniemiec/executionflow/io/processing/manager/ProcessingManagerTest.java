package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.App;
import wniemiec.executionflow.invoked.Invoked;
import wniemiec.executionflow.invoked.TestedInvoked;
import wniemiec.util.logger.LogLevel;
import wniemiec.util.logger.Logger;

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
		final Path resourcesSrc = App.getCurrentProjectRoot().resolve(
				Path.of(".", "src", "test", "java", "auxfiles", "wniemiec", 
						"executionflow", "io", "processing", "manager")
		);
		final Path resourcesBin = App.getAppTargetPath().resolve(
				Path.of("test-classes", "auxfiles", "wniemiec", "executionflow",
						"io", "processing", "manager")
		);
		
		processingManager = ProcessingManager.getInstance();
		
		testedMethod = new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("testedinvoked.java"))
				.binPath(resourcesBin.resolve("testedinvoked.class"))
				.signature("auxfiles.wniemiec.executionflow.io.processing.manager.testedinvoked.m3(int)")
				.invocationLine(9)
				.build();
		
		testMethod = new Invoked.Builder()
				.srcPath(resourcesSrc.resolve("testmethod.java"))
				.binPath(resourcesBin.resolve("testmethod.class"))
				.signature("auxfiles.wniemiec.executionflow.io.processing.manager.testmethod.method1()")
				.build();
		
		Logger.setLevel(LogLevel.WARNING);
	}
	
	@AfterEach
	void clean() {
		processingManager.deleteBackupFilesOfPreprocessingOfTestMethod();
		processingManager.deleteBackupFilesOfProcessingOfTestMethod();
		processingManager.deleteBackupFilesOfProcessingOfInvoked();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testDoPreprocessing() throws IOException {
		processingManager.initializeManagers();
		processingManager.doPreprocessingInTestMethod(testMethod);
		
		Assertions.assertTrue(processingManager.wasPreprocessingDoneSuccessfully());
		
		processingManager.restoreOriginalFilesFromTestMethod();
	}
	
	@Test
	void testDoProcessing() throws IOException {
		processingManager.initializeManagers();
		processingManager.doProcessingInTestedInvoked(new TestedInvoked(testedMethod, testMethod));
		
		Assertions.assertTrue(processingManager.wasPreprocessingDoneSuccessfully());
		
		processingManager.restoreOriginalFilesFromInvoked();
	}
	
	@Test
	void testRestoreOriginalFilesFromTestMethod() throws IOException {
		processingManager.initializeManagers();
		processingManager.doPreprocessingInTestMethod(testMethod);		
		
		Assertions.assertTrue(processingManager.restoreOriginalFilesFromTestMethod());
	}
	
	@Test
	void testRestoreOriginalFilesFromTestedInvoked() throws IOException {
		processingManager.initializeManagers();
		processingManager.doProcessingInTestedInvoked(
				new TestedInvoked(testedMethod, testMethod)
		);
		
		Assertions.assertTrue(processingManager.restoreOriginalFilesFromInvoked());
	}
	
	@Test
	void testResetLastProcessing() throws IOException {
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
