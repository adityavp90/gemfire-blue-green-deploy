package io.pivotal.gemfire.sample.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.logging.log4j.Logger;


@SuppressWarnings("rawtypes")
public class SplitAttributeFunction implements Function {
	
	private static final Logger logger = LogService.getLogger();
	
	@Override
	public void execute(FunctionContext fc) {
		RegionFunctionContext rfc = (RegionFunctionContext) fc;
		Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

		Cache c = (Cache) r.getRegionService();
		Set<Integer> regionKeys = r.keySet();
		for (Object o : regionKeys) {
			PdxInstance instance = (PdxInstance) r.get(o);
			PdxInstanceFactory factory = c.createPdxInstanceFactory(instance.getClassName());
			PdxInstanceImpl impl = (PdxInstanceImpl) instance;
			
			String name = (String) instance.getField("name");
			
			for (PdxField field : impl.getPdxType().getFields()) {
				String fieldName = field.getFieldName();
				Object fieldValue = instance.getField(fieldName);
				switch (field.getFieldType()) {
				case STRING:
					factory.writeString(fieldName, (String) fieldValue);
					break;
				case INT:
					factory.writeInt(fieldName, (int) fieldValue);
					break;
				case DOUBLE:
					factory.writeDouble(fieldName, (double) fieldValue);
					break;
				default:
					factory.writeObject(fieldName, fieldValue);
				}
			}

			factory.writeString("firstName", name.split(" ")[0]);
			factory.writeString("lastName", name.split(" ")[1]);
			PdxInstance newInstance = factory.create();
			r.put(instance.getField("id"), newInstance);

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
