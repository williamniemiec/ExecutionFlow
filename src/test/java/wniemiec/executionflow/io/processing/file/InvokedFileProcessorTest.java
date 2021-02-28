package wniemiec.executionflow.io.processing.file;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.FileEncoding;

class InvokedFileProcessorTest extends FileProcessorTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String filename;
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void clean() {
		filename = "";
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
	void testProcessing() throws IOException {
		testProcessorOnFile("invoked");
	}
	
	@Test
	void testMapping() throws IOException {
		doProcessing("invoked");

		assertMappingIs(Map.ofEntries(
				Map.entry(3, 2),
				Map.entry(4, 3),
				Map.entry(5, 4),
				Map.entry(7, 6),
				Map.entry(14, 13),
				Map.entry(15, 14),
				Map.entry(17, 16),
				Map.entry(18, 17),
				Map.entry(20, 21),
				Map.entry(23, 24),
				Map.entry(29, 30),
				Map.entry(30, 31),
				Map.entry(32, 33),
				Map.entry(33, 34),
				Map.entry(34, 35),
				Map.entry(35, 37),
				Map.entry(37, 38),
				Map.entry(43, 44),
				Map.entry(52, 54),
				Map.entry(55, 56),
				Map.entry(56, 57),
				Map.entry(57, 58),
				Map.entry(62, 61),
				Map.entry(63, 62),
				Map.entry(66, 67),
				Map.entry(67, 68),
				Map.entry(73, 72),
				Map.entry(74, 73),
				Map.entry(77, 80),
				Map.entry(78, 81),
				Map.entry(80, 87),
				Map.entry(83, 89),
				Map.entry(84, 90),
				Map.entry(85, 91),
				Map.entry(88, 93),
				Map.entry(90, 94),
				Map.entry(91, 95),
				Map.entry(92, 97),
				Map.entry(93, 98),
				Map.entry(94, 100),
				Map.entry(95, 101),
				Map.entry(98, 103),
				Map.entry(101, 105),
				Map.entry(103, 106),
				Map.entry(106, 108),
				Map.entry(108, 109),
				Map.entry(113, 112),
				Map.entry(116, 114),
				Map.entry(118, 115),
				Map.entry(121, 117),
				Map.entry(122, 118),
				Map.entry(124, 119),
				Map.entry(125, 120),
				Map.entry(126, 121),
				Map.entry(127, 122),
				Map.entry(128, 123),
				Map.entry(129, 124),
				Map.entry(130, 125),
				Map.entry(131, 126),
				Map.entry(132, 127),
				Map.entry(133, 128),
				Map.entry(134, 129),
				Map.entry(135, 130),
				Map.entry(136, 131),
				Map.entry(137, 132),
				Map.entry(138, 133),
				Map.entry(139, 134),
				Map.entry(140, 135),
				Map.entry(141, 136),
				Map.entry(142, 137),
				Map.entry(143, 138),
				Map.entry(144, 139),
				Map.entry(145, 140),
				Map.entry(146, 141),
				Map.entry(147, 142),
				Map.entry(148, 143),
				Map.entry(149, 144),
				Map.entry(150, 145),
				Map.entry(151, 146),
				Map.entry(154, 148),
				Map.entry(157, 150),
				Map.entry(158, 151),
				Map.entry(160, 152),
				Map.entry(161, 153),
				Map.entry(162, 154),
				Map.entry(163, 155),
				Map.entry(164, 156),
				Map.entry(165, 157),
				Map.entry(166, 159),
				Map.entry(167, 160),
				Map.entry(170, 162),
				Map.entry(173, 164),
				Map.entry(174, 165),
				Map.entry(176, 166),
				Map.entry(177, 167),
				Map.entry(178, 168),
				Map.entry(179, 170),
				Map.entry(180, 171),
				Map.entry(184, 174),
				Map.entry(185, 175),
				Map.entry(186, 176),
				Map.entry(187, 177),
				Map.entry(190, 179)
		));
	}
	
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void assertMappingIs(Map<Integer, Integer> expectedMapping) 
			throws IOException {
		Assertions.assertEquals(
				new TreeMap<Integer, Integer>(expectedMapping), 
				new TreeMap<Integer, Integer>(InvokedFileProcessor.getMapping())
		);
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
