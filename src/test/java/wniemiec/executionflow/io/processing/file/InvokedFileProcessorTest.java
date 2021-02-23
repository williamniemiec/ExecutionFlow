package wniemiec.executionflow.io.processing.file;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.FileEncoding;

class InvokedFileProcessorTest extends FileProcessorTest {

private String filename;
	
	@BeforeEach
	void clean() {
		filename = "";
		InvokedFileProcessor.clearMapping();
	}
	
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
		withFilename("invoked");

		assertMappingIs(Map.ofEntries(
				Map.entry(3, 2),
				Map.entry(4, 3),
				Map.entry(5, 4),
				Map.entry(8, 8),
				Map.entry(7, 6),
				//Map.entry(9, 10),
				Map.entry(14, 13),
				Map.entry(15, 14),
				Map.entry(17, 16),
				Map.entry(18, 17),
				//Map.entry(19, 20),
				Map.entry(20, 21),
				//Map.entry(21, 23),
				Map.entry(23, 24),
				//Map.entry(24, 25),
				//Map.entry(27, 29),
				//Map.entry(28, 29),
				Map.entry(29, 30),
				Map.entry(30, 31),
				Map.entry(32, 33),
				Map.entry(33, 34),
				Map.entry(34, 35),
				Map.entry(35, 37),
				Map.entry(37, 38),
				//Map.entry(38, 39),
//				Map.entry(41, 43),
				Map.entry(43, 44),
				Map.entry(44, 45),
				//Map.entry(47, 49),
				///Map.entry(48, 49),
//				Map.entry(50, 53),
				Map.entry(52, 54),
//				Map.entry(53, 55),
				Map.entry(55, 56),
				Map.entry(56, 57),
				Map.entry(57, 58),
//				Map.entry(58, 59),
				Map.entry(62, 61),
				Map.entry(63, 62),
//				Map.entry(64, 63),
				Map.entry(66, 67),
				Map.entry(67, 68),
//				Map.entry(68, 69),
				Map.entry(73, 72),
				Map.entry(74, 73),
//				Map.entry(75, 79),
				Map.entry(77, 80),
				Map.entry(78, 81),
				Map.entry(79, 85),
//				Map.entry(80, 86),
				Map.entry(82, 87),
//				Map.entry(83, 88),
				Map.entry(85, 89),
				Map.entry(86, 90),
				Map.entry(88, 96),
//				Map.entry(89, 97),
				Map.entry(91, 98),
				Map.entry(92, 99),
				Map.entry(93, 100),
//				Map.entry(94, 101),
				Map.entry(96, 102),
				Map.entry(98, 103),
				Map.entry(99, 104),
				Map.entry(100, 106),
				Map.entry(101, 107),
				Map.entry(102, 109),
				Map.entry(103, 110),
				Map.entry(106, 112),
//				Map.entry(107, 113),
				Map.entry(109, 114),
				Map.entry(111, 115),
//				Map.entry(112, 116),
				Map.entry(114, 117),
				Map.entry(116, 118),
//				Map.entry(117, 119),
				Map.entry(119, 120),
				Map.entry(121, 121),
				Map.entry(124, 123),
				Map.entry(126, 124),
//				Map.entry(127, 125),
				Map.entry(129, 126),
				Map.entry(130, 127),
				Map.entry(132, 128),
				Map.entry(133, 129),
				Map.entry(134, 130),
				Map.entry(135, 131),
				Map.entry(136, 132),
				Map.entry(137, 133),
				Map.entry(138, 134),
				Map.entry(139, 135),
				Map.entry(140, 136),
				Map.entry(141, 137),
				Map.entry(142, 138),
				Map.entry(143, 139),
				Map.entry(144, 140),
				Map.entry(145, 141),
				Map.entry(146, 142),
				Map.entry(147, 143),
				Map.entry(148, 144),
				Map.entry(149, 145),
				Map.entry(150, 146),
				Map.entry(151, 147),
				Map.entry(152, 148),
				Map.entry(153, 149),
				Map.entry(154, 150),
				Map.entry(155, 151),
				Map.entry(156, 152),
				Map.entry(157, 153),
				Map.entry(158, 154),
				Map.entry(159, 155),
//				Map.entry(160, 156),
				Map.entry(162, 157),
//				Map.entry(163, 158),
				Map.entry(165, 159),
				Map.entry(166, 160),
				Map.entry(168, 161),
				Map.entry(169, 162),
				Map.entry(170, 163),
				Map.entry(171, 164),
				Map.entry(172, 165),
				Map.entry(173, 166),
				Map.entry(174, 168),
				Map.entry(175, 169),
//				Map.entry(176, 170),
				Map.entry(178, 171),
//				Map.entry(179, 172),
				Map.entry(181, 173),
				Map.entry(182, 174),
				Map.entry(184, 175),
				Map.entry(185, 176),
				Map.entry(186, 177),
				Map.entry(187, 179),
				Map.entry(188, 180),
//				Map.entry(189, 181),
//				Map.entry(190, 182),
				Map.entry(192, 183),
				Map.entry(193, 184),
				Map.entry(194, 185),
				Map.entry(195, 186),
//				Map.entry(196, 187),
				Map.entry(198, 188)
//				Map.entry(199, 189),
//				Map.entry(200, 190)
		));
	}
	
	//-----------------------------------------------------------------------
	//		Methods
	//-----------------------------------------------------------------------
	private void withFilename(String filename) {
		this.filename = filename;
	}
	
	private void assertMappingIs(Map<Integer, Integer> expectedMapping) throws IOException {
		InvokedFileProcessor processor = new InvokedFileProcessor.Builder()
				.targetFile(getTestFile(filename))
				.outputDir(getTempFolder())
				.outputFilename(filename + "-output")
				.build();
		
		Path out = Path.of(processor.processFile());
		
		for (String line : readLinesFrom(out)) {
			System.out.println(line);
		}
		
		Assertions.assertEquals(expectedMapping, processor.getMapping());
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
