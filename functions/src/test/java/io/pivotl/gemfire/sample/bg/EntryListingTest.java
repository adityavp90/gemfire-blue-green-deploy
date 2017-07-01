package io.pivotl.gemfire.sample.bg;

import junit.framework.TestCase;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class EntryListingTest extends TestCase {

    private ClientCache cache;
    private String locatorHost = "localhost";
    private Integer locatorPort = 41111;
    private Region personRegion;

    //@Test
    public void testFunctionCall() {
        System.out.println("In Before");
        getCache();
        getRegion();
        System.out.println("In Test");
        executeEntryListingFucntion(personRegion);
    }

    public void executeEntryListingFucntion(Region personRegion) {
        Execution execution = FunctionService.onRegion(personRegion);
        ResultCollector collector = execution.execute("EntryListingFunction");
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
        ccf.setPdxReadSerialized(true);

        this.cache = ccf.create();
        System.out.println(this.cache.getName());
    }

    private void getRegion() {
        ClientRegionFactory crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
        this.personRegion = crf.create("Person");

    }

}
