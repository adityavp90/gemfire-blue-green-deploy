package io.pivotal.gemfire.sample.cachewriter;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.partition.PartitionRegionHelper;
import org.apache.geode.cache.util.CacheListenerAdapter;
import org.apache.geode.cache.util.CacheWriterAdapter;
import org.apache.geode.internal.cache.EntryEventImpl;
import org.apache.geode.internal.logging.LogService;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxInstanceFactory;
import org.apache.geode.pdx.WritablePdxInstance;
import org.apache.geode.pdx.internal.PdxField;
import org.apache.geode.pdx.internal.PdxInstanceImpl;
import org.apache.geode.pdx.internal.WritablePdxInstanceImpl;
import org.apache.logging.log4j.Logger;

import io.pivotal.gemfire.sample.temp.entity.Person;



@SuppressWarnings("rawtypes")
public class AttributeSyncCacheWriter extends CacheWriterAdapter<Integer,Person> {

	private static final Logger logger = LogService.getLogger();
	private static String RECURSING = "recursing";

	public void beforeCreate(EntryEvent event) {

		logger.warn("Old Entry: " + event.getOldValue() + "\n" + "New Entry: " + event.getNewValue());

		logger.warn("Callback Argument Present: " + event.isCallbackArgumentAvailable());
		logger.warn("Callback Argument: " + event.getCallbackArgument());

		if (event.getCallbackArgument() == null) {
			EntryEventImpl eei = (EntryEventImpl) event;				
			
			Cache c = (Cache) event.getRegion().getRegionService();
			PdxInstanceImpl impl = (PdxInstanceImpl) event.getNewValue();
			PdxInstanceFactory factory = c.createPdxInstanceFactory(impl.getClassName());
			
			String name = impl.getField("name").toString();

			for (PdxField field : impl.getPdxType().getFields()) {
				String fieldName = field.getFieldName();
				Object fieldValue = impl.getField(fieldName);
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
			PdxInstance updatedInstance = factory.create();
		
			eei.setNewValue(updatedInstance);
			eei.makeSerializedNewValue();
		}
	}

}
