package io.pivotal.gemfire.sample.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.WritablePdxInstance;


@SuppressWarnings("rawtypes")
public class RemoveNameAttributeFunction implements Function {

    @Override
    public void execute(FunctionContext fc) {
        RegionFunctionContext rfc = (RegionFunctionContext) fc;
        Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

        r.keySet().forEach(o -> {
            PdxInstance instance = (PdxInstance) r.get(o);
            if (instance.getField("name") != null) {
                WritablePdxInstance writablePdxInstance = instance.createWriter();
                writablePdxInstance.setField("name", null);
                r.put(o, writablePdxInstance);
            }
        });
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
