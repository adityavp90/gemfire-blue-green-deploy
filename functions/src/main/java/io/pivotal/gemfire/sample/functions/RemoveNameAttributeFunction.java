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
public class RemoveNameAttributeFunction implements Function {
	
	private static final Logger logger = LogService.getLogger();
	private Cache cache;
	
	@Override
	public void execute(FunctionContext fc) {
		RegionFunctionContext rfc = (RegionFunctionContext) fc;
		Region r = PartitionRegionHelper.getLocalDataForContext(rfc);

		this.cache = (Cache) r.getRegionService();
		Set<Integer> regionKeys = r.keySet();
		for (Object o : regionKeys) {
			PdxInstance instance = (PdxInstance) r.get(o);
			
			if(instance.getField("name") != null){
				PdxInstance newInstance = updatePdxInstance(instance);
				r.put(newInstance.getField("id"), newInstance);
			}

		}		
		
	}
	
	private PdxInstance updatePdxInstance(PdxInstance instance){
		PdxInstanceFactory factory = this.cache.createPdxInstanceFactory(instance.getClassName());
		PdxInstanceImpl impl = (PdxInstanceImpl) instance;
		
		String name = (String) instance.getField("name");
			
		for (PdxField field : impl.getPdxType().getFields()) {
			String fieldName = field.getFieldName();
			if(!fieldName.equals("name")){
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
		}
		PdxInstance newInstance = factory.create();
		return newInstance;
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
