package wniemiec.app.executionflow.io.processing.file;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.io.FileEncoding;

class InvokedFileProcessorTest extends FileProcessorTest {

	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void clean() {
		InvokedFileProcessor.clearMapping();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testFullBuilder() {
		new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.outputFileExtension("txt")
				.encoding(FileEncoding.UTF_8)
				.build();
	}
	
	@Test
	void testMinimumBuilder() {
		new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.build();
	}
	
	@Test
	void testEmptyBuilder() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullTargetFile() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(null)
				.outputDir(getTempFolder())
				.outputFilename("invoked-output")
				.build();
		});
	}
	
	
	@Test
	void testBuilderWithNullOutputDirectory() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(null)
				.outputFilename("invoked-output")
				.build();
		});
	}
	
	@Test
	void testBuilderWithNullOutputFilename() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			new InvokedFileProcessor.Builder()
				.targetFile(getTestFile("invoked"))
				.outputDir(getTempFolder())
				.outputFilename(null)
				.build();
		});
	}
	
	@Test
	void testProcessing() throws Exception {
		testProcessorOnFile("invoked");
	}
	
	@Test
	void testMapping() throws Exception {
		doProcessing("invoked");

		assertMappingContains(Map.ofEntries(
				Map.entry(0, List.of(0)),
				Map.entry(1, List.of(2)),
				Map.entry(2, List.of(3)),
				Map.entry(3, List.of(4)),
				Map.entry(5, List.of(6)),
				Map.entry(9, List.of(8)),
				Map.entry(11, List.of(11)),
				Map.entry(12, List.of(13)),
				Map.entry(13, List.of(14)),
				Map.entry(15, List.of(15)),
				Map.entry(16, List.of(16)),
				Map.entry(18, List.of(17)),
				Map.entry(19, List.of(19)),
				Map.entry(20, List.of(20)),
				Map.entry(22, List.of(22)),
				Map.entry(23, List.of(22)),
				Map.entry(25, List.of(25)),
				Map.entry(26, List.of(27)),
				Map.entry(27, List.of(28)),
				Map.entry(29, List.of(28)),
				Map.entry(31, List.of(31)),
				Map.entry(32, List.of(33)),
				Map.entry(33, List.of(34)),
				Map.entry(35, List.of(35)),
				Map.entry(36, List.of(37)),
				Map.entry(38, List.of(37)),
				Map.entry(39, List.of(37)),
				Map.entry(41, List.of(40)),
				Map.entry(42, List.of(42)),
				Map.entry(43, List.of(43)),
				Map.entry(45, List.of(45)),
				Map.entry(46, List.of(47)),
				Map.entry(47, List.of(49)),
				Map.entry(49, List.of(51)),
				Map.entry(50, List.of(51)),
				Map.entry(52, List.of(53)),
				Map.entry(53, List.of(53)),
				Map.entry(55, List.of(55)),
				Map.entry(56, List.of(57)),
				Map.entry(57, List.of(58)),
				Map.entry(58, List.of(60)),
				Map.entry(59, List.of(61)),
				Map.entry(61, List.of(61)),
				Map.entry(62, List.of(61)),
				Map.entry(65, List.of(62)),
				Map.entry(66, List.of(64)),
				Map.entry(67, List.of(65)),
				Map.entry(69, List.of(65)),
				Map.entry(70, List.of(65)),
				Map.entry(72, List.of(65)),
				Map.entry(74, List.of(68)),
				Map.entry(75, List.of(70)),
				Map.entry(76, List.of(71)),
				Map.entry(78, List.of(73)),
				Map.entry(79, List.of(75)),
				Map.entry(80, List.of(77)),
				Map.entry(82, List.of(78)),
				Map.entry(83, List.of(79)),
				Map.entry(85, List.of(80)),
				Map.entry(86, List.of(81)),
				Map.entry(88, List.of(82)),
				Map.entry(90, List.of(85)),
				Map.entry(91, List.of(87)),
				Map.entry(92, List.of(88)),
				Map.entry(93, List.of(90)),
				Map.entry(94, List.of(91)),
				Map.entry(95, List.of(92)),
				Map.entry(96, List.of(93)),
				Map.entry(97, List.of(94)),
				Map.entry(98, List.of(95)),
				Map.entry(99, List.of(96)),
				Map.entry(100, List.of(97)),
				Map.entry(101, List.of(98)),
				Map.entry(102, List.of(99)),
				Map.entry(103, List.of(100)),
				Map.entry(104, List.of(101)),
				Map.entry(105, List.of(102)),
				Map.entry(107, List.of(104)),
				Map.entry(108, List.of(106)),
				Map.entry(109, List.of(107)),
				Map.entry(110, List.of(109)),
				Map.entry(111, List.of(110)),
				Map.entry(112, List.of(112)),
				Map.entry(113, List.of(113)),
				Map.entry(114, List.of(114)),
				Map.entry(115, List.of(115)),
				Map.entry(116, List.of(116)),
				Map.entry(118, List.of(118)),
				Map.entry(119, List.of(118)),
				Map.entry(121, List.of(120))
		));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void assertMappingContains(Map<Integer, List<Integer>> expectedMapping) 
			throws IOException {
		Map<Integer, List<Integer>> obtained = InvokedFileProcessor.getMapping();

		for (Map.Entry<Integer, List<Integer>> m : expectedMapping.entrySet()) {
			Assertions.assertTrue(
					obtained.containsKey(m.getKey()),
					"Obtained map does not have a key " + m.getKey()
			);
			List<Integer> obtainedLineMapping = obtained.get(m.getKey());
			
			for (Integer lineMapping : m.getValue()) {
				Assertions.assertTrue(
						obtainedLineMapping.contains(lineMapping), 
						"Obtained map does not have this line mapping: " + lineMapping
				);
			}
		}
	}
	
	@Override
	protected FileProcessor getFileProcessor(String filename) {
		return new InvokedFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
	}
}
