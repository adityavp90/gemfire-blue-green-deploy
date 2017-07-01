package io.pivotal.gemfire.sample.functions;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.WritablePdxInstance;


@SuppressWarnings("rawtypes")
public class EventModifierFunction implements Function {

    private Cache cache = CacheFactory.getAnyInstance();

    @Override
    public void execute(FunctionContext fc) {
        //CMB : this function needed to be changed to be more generic so it can be used by the writer and the AEQ
        RegionFunctionContext rfc = (RegionFunctionContext) fc;
        PdxInstance event = (PdxInstance) rfc.getArguments();
        Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

        this.cache = (Cache) r.getRegionService();

        event = eventModifier(event);
        rfc.getResultSender().lastResult(event);
    }

    private PdxInstance eventModifier(PdxInstance pdxInstance) {
        PdxInstance returnValue;

        if (pdxInstance.hasField("firstName") && pdxInstance.hasField("name")) {
            // CMB : We have the case where the PDX instance has both Fields???  "Should not happen".
            // When could it happen - when we have ignore-unread-fields set to true - default.
            // How did the old code work???  Maybe didn't do a get - change - put?
            WritablePdxInstance writablePdxInstance = pdxInstance.createWriter();
            if (pdxInstance.getField("name") != null) {
                String name = pdxInstance.getField("name").toString();
                writablePdxInstance.setField("firstName", name.split(" ")[0]);
                writablePdxInstance.setField("lastName", name.split(" ")[1]);
            } else {
                writablePdxInstance.setField("name", pdxInstance.getField("firstName") + " " + pdxInstance.getField("lastName"));
            }
            returnValue = writablePdxInstance;
        } else {
            returnValue = handleOtherPdxType(pdxInstance);
        }
        return returnValue;
    }

    private PdxInstance handleOtherPdxType(PdxInstance pdxInstance) {
        PdxInstanceFactory factory = cache.createPdxInstanceFactory(pdxInstance.getClassName());

        pdxInstance.getFieldNames().forEach(name -> {
            Object value = pdxInstance.getField(name);
            if (value instanceof Integer) {
                factory.writeInt(name, (Integer) value);
            } else if (value instanceof Double) {
                factory.writeDouble(name, (Double) value);
            } else if (value instanceof String) {
                factory.writeString(name, (String) value);
            } else {
                factory.writeObject(name, value);
            }
        });
        if (pdxInstance.getField("name") != null) {
            String name = pdxInstance.getField("name").toString();
            factory.writeString("firstName", name.split(" ")[0]);
            factory.writeString("lastName", name.split(" ")[1]);
        } else {
            factory.writeString("name", pdxInstance.getField("firstName") + " " + pdxInstance.getField("lastName"));
        }
        return factory.create();
    }


    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    public boolean hasResult() {
        return true;
    }

    public boolean isHA() {
        return false;
    }

    public boolean optimizeForWrite() {
        return false;
    }

}
