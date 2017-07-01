package io.pivotal.gemfire.sample.cachewriter;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.Logger;

import java.util.List;

@SuppressWarnings("rawtypes")
public class AttributeSyncCacheWriter extends CacheWriterAdapter {

    private static final Logger logger = LogService.getLogger();
    private Cache cache;
    private static String RECURSING = "recursing";

    public void beforeCreate(EntryEvent event) {
        executeEventModifier(event);
    }

    public void beforeUpdate(EntryEvent event) {
        executeEventModifier(event);
    }

    private void executeEventModifier(EntryEvent event) {
        this.cache = (Cache) event.getRegion().getRegionService();
        if (FunctionService.isRegistered("EventModifierFunction")) {
            Execution execution = FunctionService.onRegion(event.getRegion()).withArgs(event.getNewValue());
            ResultCollector collector = execution.execute("EventModifierFunction");
            List<PdxInstance> regionResults = (List<PdxInstance>) collector.getResult();
            PdxInstance pdxInstance = regionResults.get(0);

            ((EntryEventImpl) event).setNewValue(pdxInstance);
            ((EntryEventImpl) event).makeSerializedNewValue();
        }
    }

}


