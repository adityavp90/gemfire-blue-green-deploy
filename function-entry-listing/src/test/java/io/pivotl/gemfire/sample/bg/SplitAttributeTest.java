package io.pivotl.gemfire.sample.bg;

import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.junit.Test;

import io.pivotal.gemfire.sample.temp.entity.Person;
import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class SplitAttributeTest extends TestCase {

	private ClientCache cache;
	private String locatorHost = "localhost";
	private Integer locatorPort = 41111;
	private Region<Integer, Person> personRegion;
	
	@Test
	public void testFunctionCall() {
		System.out.println("In Before");
		getCache();
		getRegion();
		System.out.println("In Test Attribute");
		executeSplitAttributeFucntion(personRegion);
	}

	public void executeSplitAttributeFucntion(Region personRegion) {
		Execution execution = FunctionService.onRegion(personRegion);		
		ResultCollector collector = execution.execute("SplitAttributeFunction");
		List<String> regionResults = (List<String>) collector.getResult();
		System.out.println("Entries for region: " + personRegion.getName());
		for (String entry : regionResults) {
			System.out.println(" - " + entry);
		}
	}

	private void getCache() {
		ClientCacheFactory ccf = new ClientCacheFactory();

		ccf.addPoolLocator(locatorHost, locatorPort);

		ccf.setPdxPersistent(true);
		ccf.setPdxReadSerialized(false);
		ccf.setPdxSerializer(new ReflectionBasedAutoSerializer("io.pivotal.gemfire.sample.temp.entity.*"));

		this.cache = ccf.create();
		System.out.println(this.cache.getName());
	}

	private void getRegion() {
		ClientRegionFactory<Integer, Person> crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		this.personRegion = crf.create("Person");

	}

}
