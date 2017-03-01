package io.pivotal.gemfire.sample.listeners;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("rawtypes")
public class AttributeSyncListener extends CacheListenerAdapter {
	
	private static final Logger logger = LogService.getLogger();
	
	
	public void afterCreate(EntryEvent event){
		
		logger.warn("Old Entry: " + event.getOldValue() + "\n"
				+ "New Entry: " + event.getNewValue());
		
		Cache c = (Cache) event.getRegion().getRegionService();
		
		Region re =  event.getRegion();
		Region r = PartitionRegionHelper.getLocalData(re);
		
		PdxInstance instance = (PdxInstance) r.get(event.getKey());
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
		
		logger.warn("New Instance: " + newInstance);
		r.put(instance.getField("id"), newInstance);
	}
	
}
	