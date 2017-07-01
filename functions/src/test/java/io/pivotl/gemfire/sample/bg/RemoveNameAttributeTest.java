package io.pivotl.gemfire.sample.bg;

import junit.framework.TestCase;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
@SuppressWarnings("rawtypes")
public class RemoveNameAttributeTest extends TestCase {

    private ClientCache cache;
    private String locatorHost = "localhost";
    private Integer locatorPort = 41111;
    private Region personRegion;

    @Test
    public void testFunctionCall() {
        getCache();
        getRegion();
        executeSplitAttributeFucntion(personRegion);
    }

    public void executeSplitAttributeFucntion(Region personRegion) {
        System.out.println("Executing Function:");
        Execution execution = FunctionService.onRegion(personRegion);
        execution.execute("RemoveNameAttributeFunction");
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
        personRegion = cache.getRegion("Person");
        if (personRegion == null) {
            ClientRegionFactory crf = cache.createClientRegionFactory(ClientRegionShortcut.PROXY);
            personRegion = crf.create("Person");
        }
    }

}
