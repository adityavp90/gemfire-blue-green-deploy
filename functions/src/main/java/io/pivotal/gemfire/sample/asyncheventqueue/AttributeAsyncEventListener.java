package io.pivotal.gemfire.sample.asyncheventqueue;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Declarable;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.asyncqueue.AsyncEvent;
import org.apache.geode.cache.asyncqueue.AsyncEventListener;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.pdx.PdxInstance;

import java.util.List;
import java.util.Properties;

/**
 * Created by Charlie Black on 6/30/17.
 */
public class AttributeAsyncEventListener implements AsyncEventListener, Declarable {
    private Cache cache = CacheFactory.getAnyInstance();
    private LogWriter logger = cache.getLogger();
    private Object callback = 1;

    @Override
    public boolean processEvents(List<AsyncEvent> list) {

        try {
            if (FunctionService.isRegistered("EventModifierFunction")) {
                list.forEach(item -> {
                    if (item.getCallbackArgument() == null) {
                        Region region = item.getRegion();
                        Execution execution = FunctionService.onRegion(region).withArgs(item.getDeserializedValue());
                        ResultCollector collector = execution.execute("EventModifierFunction");
                        List<PdxInstance> regionResults = (List<PdxInstance>) collector.getResult();
                        PdxInstance pdxInstance = regionResults.get(0);
                        region.put(item.getKey(), pdxInstance, callback);
                    }
                });
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return true;
    }

    @Override
    public void close() {

    }

    @Override
    public void init(Properties properties) {

    }
}
