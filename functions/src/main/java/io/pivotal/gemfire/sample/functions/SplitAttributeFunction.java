package io.pivotal.gemfire.sample.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;

import java.util.Set;

@SuppressWarnings("rawtypes")
public class SplitAttributeFunction implements Function {

    @Override
    public void execute(FunctionContext fc) {
        RegionFunctionContext rfc = (RegionFunctionContext) fc;
        Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

        Set<Integer> regionKeys = r.keySet();
        for (Object o : regionKeys) {
            r.put(o, r.get(o));
        }
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    public boolean hasResult() {
        return false;
    }

    public boolean isHA() {
        return false;
    }

    public boolean optimizeForWrite() {
        return false;
    }

}
