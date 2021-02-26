package wniemiec.executionflow.io.processing.manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.processing.file.factory.InvokedFileProcessorFactory;

class FileProcessingManagerTest {

	private Path srcPath = Path.of(".", "src", "test", "resources", "wniemiec", 
			"executionflow", "io", "processing", "manager", "fileprocessing.java");
	private Path binPath = Path.of(".", "target", "test-classes", "wniemiec", 
			"executionflow", "io", "processing", "manager", "fileprocessing.class");
	private String pkg = "wniemiec.executionflow.io.processing.manager";
	private FileProcessingManager fileProcessingManager;
	private static final boolean AUTO_RESTORE = false;
	
	@BeforeEach
	void clean() throws IOException {
		if (fileProcessingManager == null)
				return;
		
		fileProcessingManager.deleteBinBackupFile();
		fileProcessingManager.deleteSrcBackupFile();
	}
	
	@Test
	void testFullBuilder() {
		new FileProcessingManager.Builder()
				.srcPath(srcPath)
				.binPath(binPath)
				.classPackage(pkg)
				.backupExtensionName("bkp")
				.fileParserFactory(new InvokedFileProcessorFactory())
				.build();
	}
	
	@Test
	void testMinimumBuilder() {
		new FileProcessingManager.Builder()
				.srcPath(srcPath)
				.binPath(binPath)
				.classPackage(pkg)
				.fileParserFactory(new InvokedFileProcessorFactory())
				.build();
	}
	
	@Test
	void testCreateSrcBackupFile() {
		fileProcessingManager = createFileProcessingManager();
		fileProcessingManager.createSrcBackupFile(AUTO_RESTORE);
		
		Assertions.assertTrue(fileProcessingManager.hasSrcBackupStored());
	}
	
	@Test
	void testCreateBinBackupFile() {
		fileProcessingManager = createFileProcessingManager();
		fileProcessingManager.createBinBackupFile(AUTO_RESTORE);
		
		Assertions.assertTrue(fileProcessingManager.hasBinBackupStored());
	}
	
	@Test
	void testProcessFile() throws IOException {
		fileProcessingManager = createFileProcessingManager();
		
		createBackupFiles();
		assertSrcFileIsProcessed();
		restoreBackupFiles();
	}
	
	@Test
	void testProcessAndCompileFile() throws IOException {
		fileProcessingManager = createFileProcessingManager();
		
		createBackupFiles();
		assertSrcFileIsCompiledAfterProcessing();
		restoreBackupFiles();
	}
	
	@Test
	void testDeleteSrcBackupFile() throws IOException {
		fileProcessingManager = createFileProcessingManager();
		fileProcessingManager.createSrcBackupFile(false);
		
		Assertions.assertTrue(fileProcessingManager.hasSrcBackupStored());
		fileProcessingManager.deleteSrcBackupFile();
		Assertions.assertFalse(fileProcessingManager.hasSrcBackupStored());
	}
	
	@Test
	void testDeleteBinBackupFile() throws IOException {
		fileProcessingManager = createFileProcessingManager();
		fileProcessingManager.createBinBackupFile(false);
		
		Assertions.assertTrue(fileProcessingManager.hasBinBackupStored());
		fileProcessingManager.deleteBinBackupFile();
		Assertions.assertFalse(fileProcessingManager.hasBinBackupStored());
	}
	
	@Test
	void testRestoreSrcProcessing() throws IOException {
		fileProcessingManager = createFileProcessingManager();

		createBackupFiles();
		assertSrcFileIsRestoredAfterProcessing();		
		restoreBackupFiles();
	}
	
	@Test
	void testRestoreBinProcessing() throws IOException {
		fileProcessingManager = createFileProcessingManager();
		
		createBackupFiles();
		assertBinFileIsRestoredAfterProcessing();
		restoreBackupFiles();
	}
	
	private void assertSrcFileIsRestoredAfterProcessing() throws IOException {
		List<String> originalSrcFile = Files.readAllLines(srcPath);
		
		fileProcessingManager.processFile(false);
		fileProcessingManager.revertProcessing();
		
		Assertions.assertEquals(
				originalSrcFile, 
				Files.readAllLines(srcPath)
		);
	}
	
	private void assertBinFileIsRestoredAfterProcessing() throws IOException {
		byte[] originalBinFile = Files.readAllBytes(binPath);
		
		fileProcessingManager.processFile(false);
		fileProcessingManager.compileFile();
		fileProcessingManager.revertCompilation();
		
		Assertions.assertArrayEquals(
				originalBinFile,
				Files.readAllBytes(binPath)
		);
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

	private void createBackupFiles() {
		fileProcessingManager.createBinBackupFile(AUTO_RESTORE);
		fileProcessingManager.createSrcBackupFile(AUTO_RESTORE);
	}

	private void assertSrcFileIsProcessed() throws IOException {
		FileTime srcFileTime = Files.getLastModifiedTime(srcPath);
		
		fileProcessingManager.processFile(false);
		
		Assertions.assertNotEquals(
				srcFileTime, 
				Files.getLastModifiedTime(srcPath)
		);
	}

	private void restoreBackupFiles() throws IOException {
		fileProcessingManager.revertCompilation();
		fileProcessingManager.revertProcessing();
	}

	private FileProcessingManager createFileProcessingManager() {
		return new FileProcessingManager.Builder()
				.srcPath(srcPath)
				.binPath(binPath)
				.classPackage(pkg)
				.backupExtensionName("bkp")
				.fileParserFactory(new InvokedFileProcessorFactory())
				.build();
	}
	
	private void assertFileExists(Path file) {
		Assertions.assertTrue(Files.exists(file));
	}
}
