package wniemiec.executionflow.io.processing.manager;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.collector.ConstructorCollector;
import wniemiec.executionflow.collector.InvokedCollector;
import wniemiec.executionflow.collector.MethodCollector;
import wniemiec.executionflow.invoked.Invoked;

class CollectorProcessingManagerTest {

	private final Invoked testedMethod;
	private final Invoked testMethod;
	private final InvokedCollector methodCollector;
	private final InvokedCollector constructorCollector;
	private final Map<Integer, Integer> mapping; 
	private final CollectorProcessingManager collectorManager;
		
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
				Map.entry(1, 1),
				Map.entry(3, 2),
				Map.entry(4, 3),
				Map.entry(5, 4)
		);
		
		methodCollector.storeCollector(testedMethod, testMethod);
	}
	
	@AfterEach
	void restoreDefaultValues() {
		testedMethod.setInvocationLine(5);
		collectorManager.reset();
	}
	
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
		
		Assertions.assertEquals(4, testedMethod.getInvocationLine());
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
		
		Assertions.assertEquals(4, testedMethod.getInvocationLine());
		
		collectorManager.updateCollectorsFromMapping(
				Map.of(4, 99), 
				testMethod.getSrcPath(), 
				testedMethod.getSrcPath()
		);
		
		Assertions.assertEquals(4, testedMethod.getInvocationLine());
	}
	
	@Test
	void testUpdateCollectorsFromMappingWithTestedInvokedInsideTestMethod() {
		collectorManager.updateCollectorsFromMapping(
				mapping, 
				testMethod.getSrcPath(), 
				testMethod.getSrcPath()
		);
		
		Assertions.assertEquals(4, testedMethod.getInvocationLine());
		
		collectorManager.updateCollectorsFromMapping(
				Map.of(4, 99), 
				testMethod.getSrcPath(), 
				testMethod.getSrcPath()
		);
		
		Assertions.assertEquals(99, testedMethod.getInvocationLine());
	}
}
