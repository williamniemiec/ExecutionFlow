package wniemiec.app.executionflow.io.processing.manager;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.app.executionflow.collector.ConstructorCollector;
import wniemiec.app.executionflow.collector.InvokedCollector;
import wniemiec.app.executionflow.collector.MethodCollector;
import wniemiec.app.executionflow.invoked.Invoked;
import wniemiec.app.executionflow.invoked.TestedInvoked;

class CollectorProcessingManagerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Invoked testedMethod;
	private final Invoked testMethod;
	private static InvokedCollector methodCollector;
	private static InvokedCollector constructorCollector;
	private final Map<Integer, List<Integer>> mapping; 
	private final CollectorProcessingManager collectorManager;
		
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	CollectorProcessingManagerTest() {
		methodCollector = MethodCollector.getInstance();
		constructorCollector = ConstructorCollector.getInstance();
		
		testedMethod = new Invoked.Builder()
				.binPath(Path.of("foo/bar/testedmethod.class"))
				.srcPath(Path.of("foo/bar/testedmethod.java"))
				.signature("foo.SomeClass.method(int)")
				.invocationLine(5)
				.build();
		
		testMethod = new Invoked.Builder()
				.binPath(Path.of("foo/bar/testmethod.class"))
				.srcPath(Path.of("foo/bar/testmethod.java"))
				.signature("foo.ClassName.testMethod()")
				.build();
		
		
		
		collectorManager = CollectorProcessingManager.getInstance(Set.of(
				methodCollector,
				constructorCollector
		));
		
		mapping = Map.ofEntries(
				Map.entry(1, List.of(1)),
				Map.entry(2, List.of(3)),
				Map.entry(3, List.of(4)),
				Map.entry(4, List.of(5))
		);
		
		methodCollector.collect(new TestedInvoked(testedMethod, testMethod));
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void clean() {
		methodCollector.reset();
		constructorCollector.reset();
		methodCollector.collect(new TestedInvoked(testedMethod, testMethod));
	}
	
	@AfterEach
	void restoreDefaultValues() {
		testedMethod.setInvocationLine(5);
		collectorManager.reset();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testGetInstanceWithNullCollectors() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectorProcessingManager.getInstance(null);
		});
	}
	
	@Test
	void testGetInstanceWithoutCollectors() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CollectorProcessingManager.getInstance(Set.of());
		});
	}
	
	@Test
	void testUpdateCollectorsFromMapping() {
		collectorManager.updateCollectorsFromMapping(
				mapping, 
				testMethod.getSrcPath(),
				testedMethod.getSrcPath() 
		);
		
		Assertions.assertEquals(4, getFirstCollectedTestedMethod().getInvocationLine());
	}
	
	@Test
	void testUpdateCollectorsFromNullMapping() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			collectorManager.updateCollectorsFromMapping(
					null, 
					testMethod.getSrcPath(),
					testedMethod.getSrcPath() 
			);
		});
	}
	
	@Test
	void testUpdateCollectorsFromMappingWithNullTestMethodSrcPath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			collectorManager.updateCollectorsFromMapping(
					mapping, 
					null,
					testedMethod.getSrcPath() 
			);
		});
	}
	
	@Test
	void testUpdateCollectorsFromMappingWithNullTestedInvokedSrcPath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			collectorManager.updateCollectorsFromMapping(
					mapping, 
					testMethod.getSrcPath(),
					null 
			);
		});
	}
	
	@Test
	void testUpdateCollectorsFromMappingWithTestedInvokedOutsideTestMethod() {
		collectorManager.updateCollectorsFromMapping(
				mapping, 
				testMethod.getSrcPath(), 
				testedMethod.getSrcPath()
		);
		
		Assertions.assertEquals(4, getFirstCollectedTestedMethod().getInvocationLine());
		
		collectorManager.updateCollectorsFromMapping(
				Map.of(99, List.of(4)), 
				testMethod.getSrcPath(), 
				testedMethod.getSrcPath()
		);
		
		Assertions.assertEquals(4, getFirstCollectedTestedMethod().getInvocationLine());
	}
	
	@Test
	void testUpdateCollectorsFromMappingWithTestedInvokedInsideTestMethod() {
		collectorManager.updateCollectorsFromMapping(
				mapping, 
				testMethod.getSrcPath(), 
				testMethod.getSrcPath()
		);
		
		Assertions.assertEquals(4, getFirstCollectedTestedMethod().getInvocationLine());
		
		collectorManager.updateCollectorsFromMapping(
				Map.of(99, List.of(4)), 
				testMethod.getSrcPath(), 
				testMethod.getSrcPath()
		);
		
		Assertions.assertEquals(99, getFirstCollectedTestedMethod().getInvocationLine());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private Invoked getFirstCollectedTestedMethod() {
		return methodCollector
					.getAllCollectedInvoked()
					.iterator()
					.next()
					.getTestedInvoked();
	}
}
